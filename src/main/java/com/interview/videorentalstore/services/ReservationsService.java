package com.interview.videorentalstore.services;

import java.time.Instant;
import java.time.LocalDate;
import java.time.Period;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import org.openapitools.model.Film;
import org.openapitools.model.Reservation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.interview.videorentalstore.exceptions.EntityNotFoundException;
import com.interview.videorentalstore.exceptions.IncorrectRequestException;
import com.interview.videorentalstore.mappers.ReservationsMapper;
import com.interview.videorentalstore.repositories.ReservationsRepository;
import com.interview.videorentalstore.repositories.models.ReservationDbModel;
import com.interview.videorentalstore.repositories.models.ReservationStatusDbEnum;
import com.interview.videorentalstore.utils.ReservationCostUtils;


@Service
public class ReservationsService {

    private static final Logger LOGGER = LoggerFactory.getLogger(ReservationsService.class);

    private final ReservationsRepository repo;

    private final FilmsService filmsService;

    public ReservationsService(ReservationsRepository repo, FilmsService filmsService) {
        this.repo = repo;
        this.filmsService = filmsService;
    }

    /**
     * Creates a new reservation assuming that the provided details are correct. If not, an {@link IncorrectRequestException} is thrown
     *
     * @param reservation details provided by the user in the API
     * @return the created reservation details
     */
    public Reservation createReservation(final Reservation reservation) {
        validateCreateInput(reservation);

        final Film film = filmsService.getFilm(reservation.getFilmId());
        final Double cost = ReservationCostUtils.calculateReservationCost(film.getFilmType(), reservation.getReservationDays());

        final ReservationDbModel reservationToAdd = new ReservationDbModel(null,
                UUID.fromString(reservation.getFilmId()),
                ReservationStatusDbEnum.OPEN,
                reservation.getReservationDays(),
                Instant.now(),
                cost,
                Instant.now().plus(Period.ofDays(reservation.getReservationDays())),
                null,
                null);

        final ReservationDbModel returnedReservation = repo.save(reservationToAdd);

        filmsService.changeFilmAvailability(reservation.getFilmId(), false);

        return ReservationsMapper.convertToApiModel(returnedReservation);
    }

    /**
     * Returns a film attributed to a reservation.
     * If the provided reservation ID is incorrect a {@link EntityNotFoundException} is thrown.
     * If the specified reservation is already returned then a {@link IncorrectRequestException} is thrown.
     *
     * @param reservationId to return
     * @return returned reservation entity
     */
    public Reservation returnReservation(final String reservationId) {
        final ReservationDbModel reservationDbModel = getReservationDbModel(reservationId);

        if (reservationDbModel.getStatus() != ReservationStatusDbEnum.OPEN) {
            throw new IncorrectRequestException("Reservation with ID " + reservationId + " has already been returned.");
        }

        final Instant actualReturnInstant = Instant.now();
        reservationDbModel.setActualReturnDate(actualReturnInstant);

        final Film film = filmsService.getFilm(reservationDbModel.getFilmId().toString());

        final double surchargeAmount = calculateSurcharge(actualReturnInstant, reservationDbModel.getReservationStartDate(), reservationDbModel.getReservationDays(), film.getFilmType());
        reservationDbModel.setSurcharge(surchargeAmount);
        reservationDbModel.setStatus(surchargeAmount > 0.0 ? ReservationStatusDbEnum.SURCHARGE_REQUIRED : ReservationStatusDbEnum.CLOSED);

        final ReservationDbModel savedReservation = repo.save(reservationDbModel);

        filmsService.changeFilmAvailability(reservationDbModel.getFilmId().toString(), true);

        return ReservationsMapper.convertToApiModel(savedReservation);
    }

    /**
     * Retrieves all reservations in the system
     *
     * @return all reservations in the system
     */
    public List<Reservation> getAllReservations() {
        return StreamSupport.stream(repo.findAll().spliterator(), false)
                .map(ReservationsMapper::convertToApiModel)
                .collect(Collectors.toList());

    }

    /**
     * Calculates the surcharge required to be paid should the user return the film late
     *
     * @param actualReturnInstant     Instant of when the user returned the film
     * @param reservationStartInstant Instant of when the reservation was made
     * @param reservationDays         Expected reservation days
     * @param filmType                attributed to the reservation. Used to calculate surcharge amount.
     * @return surcharge required to be paid. Returns 0.0 if no surcharge is required.
     */
    private double calculateSurcharge(final Instant actualReturnInstant, final Instant reservationStartInstant, final Integer reservationDays, final Film.FilmTypeEnum filmType) {
        final LocalDate reservationStartDate = LocalDate.ofInstant(reservationStartInstant, ZoneId.systemDefault());
        final LocalDate actualReturnDate = LocalDate.ofInstant(actualReturnInstant, ZoneId.systemDefault());

        final int daysBetweenReservationDateAndActualReturnDate = Period.between(reservationStartDate, actualReturnDate).getDays();

        final boolean lateReturn = daysBetweenReservationDateAndActualReturnDate <= reservationDays;

        if (lateReturn) {
            // Returned within right amount of days. No surcharge
            return 0.0;
        } else {
            // Returned film late. Calculating surcharge
            final int surchargeDelta = daysBetweenReservationDateAndActualReturnDate - reservationDays;
            return ReservationCostUtils.calculateSurchargeCost(filmType, surchargeDelta);
        }
    }

    /**
     * Validates that the
     * - reservation days specified are greater than 0
     * - film with the specified ID exists
     * - film with the specified ID is not already reserved
     *
     * @param reservationInput to validate
     */
    private void validateCreateInput(final Reservation reservationInput) {
        final List<String> validationErrors = new ArrayList<>();

        if (reservationInput.getReservationDays() == null || reservationInput.getReservationDays() <= 0) {
            validationErrors.add("Reservation days must be greater than 0");
        }

        UUID filmUuid = null;

        try {
            filmUuid = UUID.fromString(reservationInput.getFilmId());
        } catch (IllegalArgumentException e) {
            validationErrors.add("A film with ID " + reservationInput.getFilmId() + " does not exist.");
        }

        if (filmUuid != null) {
            try {
                final Film filmReferenced = filmsService.getFilm(reservationInput.getFilmId());
                if (!filmReferenced.getAvailable()) {
                    validationErrors.add("The film with ID " + reservationInput.getFilmId() + " is already reserved and therefore it can't be reserved again.");
                }
            } catch (EntityNotFoundException e) {
                validationErrors.add("A film with ID " + reservationInput.getFilmId() + " does not exist.");
            }
        }

        if (!validationErrors.isEmpty()) {
            LOGGER.debug("Input entity invalid");
            throw new IncorrectRequestException("Incorrect data sent in the request body", validationErrors);
        }
    }

    /**
     * Retrieves a reservation from a provided ID. Throws an {@link IllegalArgumentException} if reservation with provided ID is not found.
     *
     * @param reservationId to retrieve
     * @return retrieved reservation
     */
    private ReservationDbModel getReservationDbModel(final String reservationId) {
        final UUID uuid;

        try {
            uuid = UUID.fromString(reservationId);
        } catch (IllegalArgumentException e) {
            throw new EntityNotFoundException();
        }

        return repo.findById(uuid).orElseThrow(EntityNotFoundException::new);
    }
}
