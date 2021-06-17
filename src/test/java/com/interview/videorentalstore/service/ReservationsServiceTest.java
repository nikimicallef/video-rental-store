package com.interview.videorentalstore.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import java.time.Instant;
import java.time.Period;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.EnumSource;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.ArgumentCaptor;
import org.openapitools.model.Film;
import org.openapitools.model.Reservation;

import com.interview.videorentalstore.exceptions.EntityNotFoundException;
import com.interview.videorentalstore.exceptions.IncorrectRequestException;
import com.interview.videorentalstore.repositories.ReservationsRepository;
import com.interview.videorentalstore.repositories.models.ReservationDbModel;
import com.interview.videorentalstore.repositories.models.ReservationStatusDbEnum;
import com.interview.videorentalstore.services.FilmsService;
import com.interview.videorentalstore.services.ReservationsService;

import uk.co.jemos.podam.api.PodamFactory;
import uk.co.jemos.podam.api.PodamFactoryImpl;


public class ReservationsServiceTest {

    private static final PodamFactory PODAM_FACTORY = new PodamFactoryImpl();

    private ReservationsRepository repository;
    private ReservationsService reservationsService;
    private FilmsService filmsService;

    @BeforeEach
    public void setUp() {
        this.repository = mock(ReservationsRepository.class);
        this.filmsService = mock(FilmsService.class);
        reservationsService = new ReservationsService(repository, filmsService);
    }

    @AfterEach
    private void afterEach() {
        verifyNoMoreInteractions(repository);
    }

    @Test
    public void createReservation_validInput_reservationSaved() {
        final String filmId = UUID.randomUUID().toString();

        final Reservation reservation = PODAM_FACTORY.manufacturePojo(Reservation.class);
        reservation.setFilmId(filmId);

        final Film filmReferenced = PODAM_FACTORY.manufacturePojo(Film.class);
        filmReferenced.setId(filmId);
        filmReferenced.setAvailable(true);
        when(filmsService.getFilm(filmId)).thenReturn(filmReferenced);

        final ReservationDbModel reservationReturnedFromSave = PODAM_FACTORY.manufacturePojo(ReservationDbModel.class);
        when(repository.save(any(ReservationDbModel.class))).thenReturn(reservationReturnedFromSave);

        when(filmsService.changeFilmAvailability(filmId, false)).thenReturn(PODAM_FACTORY.manufacturePojo(Film.class));

        final Reservation returnedReservation = reservationsService.createReservation(reservation);

        assertEquals(reservationReturnedFromSave.getId().toString(), returnedReservation.getId(), "ID of returned reservation does not match.");

        verify(filmsService, times(2)).getFilm(any(String.class));

        final ArgumentCaptor<ReservationDbModel> argumentCaptor = ArgumentCaptor.forClass(ReservationDbModel.class);
        verify(repository).save(argumentCaptor.capture());

        final ReservationDbModel dbModelSaved = argumentCaptor.getValue();
        assertNull(dbModelSaved.getId(), "ID is not null");
        assertEquals(filmId, dbModelSaved.getFilmId().toString(), "Film ID does not match");
        assertEquals(ReservationStatusDbEnum.OPEN, dbModelSaved.getStatus(), "Status is not set to OPEN");
        assertEquals(reservation.getReservationDays(), dbModelSaved.getReservationDays(), "Reservation days is not equal.");
        assertNotNull(dbModelSaved.getReservationStartDate(), "Reservation start date is null");
        assertNotNull(dbModelSaved.getCost(), "Cost is null");
        assertEquals(dbModelSaved.getReservationStartDate().plus(Period.ofDays(dbModelSaved.getReservationDays())), dbModelSaved.getExpectedReturnDate(), "Expected return date does not match");
        assertNull(dbModelSaved.getActualReturnDate(), "Actual return date is not null");
        assertNull(dbModelSaved.getSurcharge(), "Surcharge is not null");

        verify(filmsService).changeFilmAvailability(any(String.class), any(Boolean.class));
    }

    @ParameterizedTest
    @MethodSource
    public void createReservation_invalidInput_incorrectRequestException(final Integer reservationDays, final String filmId, final Boolean filmFoundInDb, final Boolean available) {
        final Reservation reservation = PODAM_FACTORY.manufacturePojo(Reservation.class);
        reservation.setReservationDays(reservationDays);
        reservation.setFilmId(filmId);

        if (filmFoundInDb != null && filmFoundInDb) {
            // Film ID is valid and we expect one to be in the DB
            final Film film = PODAM_FACTORY.manufacturePojo(Film.class);
            film.available(available);
            when(filmsService.getFilm(filmId)).thenReturn(film);
        } else if (filmFoundInDb != null) {
            // Film ID is valid, but we do not expect one to be in the DB
            when(filmsService.getFilm(filmId)).thenThrow(EntityNotFoundException.class);
        }

        assertThrows(IncorrectRequestException.class, () -> reservationsService.createReservation(reservation));

        if (filmFoundInDb != null) {
            // Film ID is valid so we expect the getFilm to have been executed
            verify(filmsService).getFilm(any(String.class));
        } else {
            verify(filmsService, never()).getFilm(any(String.class));
        }

        verify(repository, never()).save(any(ReservationDbModel.class));
        verify(filmsService, never()).changeFilmAvailability(any(String.class), any(Boolean.class));
    }

    private static Stream<Arguments> createReservation_invalidInput_incorrectRequestException() {
        return Stream.of(
                Arguments.of(null, "123", null, false),
                Arguments.of(0, "ca4bd7f2-55eb-4649-bca0-de1cd6b391fb", false, false),
                Arguments.of(-1, "ca4bd7f2-55eb-4649-bca0-de1cd6b391fb", true, false)
        );
    }

    @ParameterizedTest
    @ValueSource(strings = { "true", "false" })
    public void returnReservation_validReservation_reservationClosed(final Boolean surcharge) {
        final ReservationDbModel reservationReturnedFromDb = PODAM_FACTORY.manufacturePojo(ReservationDbModel.class);
        reservationReturnedFromDb.setStatus(ReservationStatusDbEnum.OPEN);
        reservationReturnedFromDb.setActualReturnDate(null);
        reservationReturnedFromDb.setSurcharge(null);

        if (surcharge) {
            int reservationDays = 10;
            reservationReturnedFromDb.setReservationDays(reservationDays);
            final Instant reservationStartDate = Instant.now().minus(Period.ofDays(reservationDays + 2));
            reservationReturnedFromDb.setReservationStartDate(reservationStartDate);
            reservationReturnedFromDb.setExpectedReturnDate(reservationStartDate.plus(Period.ofDays(reservationDays)));
        }

        when(repository.findById(reservationReturnedFromDb.getId())).thenReturn(Optional.of(reservationReturnedFromDb));
        when(filmsService.getFilm(reservationReturnedFromDb.getFilmId().toString())).thenReturn(PODAM_FACTORY.manufacturePojo(Film.class));
        when(repository.save(any())).thenReturn(PODAM_FACTORY.manufacturePojo(ReservationDbModel.class));

        reservationsService.returnReservation(reservationReturnedFromDb.getId().toString());

        final ArgumentCaptor<ReservationDbModel> argumentCaptor = ArgumentCaptor.forClass(ReservationDbModel.class);
        verify(repository).save(argumentCaptor.capture());

        assertNotNull(argumentCaptor.getValue().getActualReturnDate(), "Actual return date is null");
        assertNotNull(argumentCaptor.getValue().getSurcharge(), "Surcharge is null");

        if (surcharge) {
            assertNotEquals(0.0, argumentCaptor.getValue().getSurcharge(), "Surcharge is 0.0");
            assertEquals(ReservationStatusDbEnum.SURCHARGE_REQUIRED, argumentCaptor.getValue().getStatus());
        } else {
            assertEquals(0.0, argumentCaptor.getValue().getSurcharge(), "Surcharge is not 0.0");
            assertEquals(ReservationStatusDbEnum.CLOSED, argumentCaptor.getValue().getStatus());
        }

        verify(repository).findById(any(UUID.class));
        verify(filmsService).getFilm(any(String.class));
        verify(filmsService).changeFilmAvailability(any(String.class), any(Boolean.class));
    }

    @ParameterizedTest
    @ValueSource(strings = { "123", "ca4bd7f2-55eb-4649-bca0-de1cd6b391fb" })
    public void returnReservation_invalidReservationId_entityNotFoundException(final String reservationId) {
        if (reservationId.contains("-")) {
            // Reservation ID is valid however we want the find to return nothing
            when(repository.findById(UUID.fromString(reservationId))).thenReturn(Optional.empty());
        }

        assertThrows(EntityNotFoundException.class, () -> reservationsService.returnReservation(reservationId));

        if (reservationId.contains("-")) {
            // Reservation ID is valid so we expect the find to have been executed
            verify(repository).findById(any(UUID.class));
        } else {
            verify(repository, never()).findById(any(UUID.class));
        }

        verify(filmsService, never()).getFilm(any());
        verify(repository, never()).save(any(ReservationDbModel.class));
        verify(filmsService, never()).changeFilmAvailability(any(String.class), any(Boolean.class));
    }

    @ParameterizedTest
    @EnumSource(value = ReservationStatusDbEnum.class, names = { "CLOSED", "SURCHARGE_REQUIRED" })
    public void returnReservation_reservationIsNotOpen_incorrectRequestException(final ReservationStatusDbEnum status) {
        final ReservationDbModel reservationReturnedFromDb = PODAM_FACTORY.manufacturePojo(ReservationDbModel.class);
        reservationReturnedFromDb.setStatus(status);

        when(repository.findById(reservationReturnedFromDb.getId())).thenReturn(Optional.of(reservationReturnedFromDb));

        IncorrectRequestException exception = assertThrows(IncorrectRequestException.class, () -> reservationsService.returnReservation(reservationReturnedFromDb.getId().toString()));

        assertNotNull(exception.getValidationErrors(), "Validation errors should not be null.");

        verify(filmsService, never()).getFilm(any());
        verify(repository).findById(any(UUID.class));
        verify(filmsService, never()).changeFilmAvailability(any(String.class), any(Boolean.class));
    }

    @Test
    public void getAllReservations_validCall_reservationsReturned() {
        final List<ReservationDbModel> reservationsReturnedFromDb = PODAM_FACTORY.manufacturePojo(List.class, ReservationDbModel.class);

        when(repository.findAll()).thenReturn(reservationsReturnedFromDb);

        final List<Reservation> returnedReservations = reservationsService.getAllReservations();

        assertEquals(returnedReservations.size(), reservationsReturnedFromDb.size(), "Number of returned reservations is not as expected.");
        reservationsReturnedFromDb.stream().map(ReservationDbModel::getId).map(UUID::toString).collect(Collectors.toSet())
                .containsAll(returnedReservations.stream().map(Reservation::getId).collect(Collectors.toSet()));
        verify(repository).findAll();
    }
}
