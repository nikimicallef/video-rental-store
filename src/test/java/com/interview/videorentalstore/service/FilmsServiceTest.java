package com.interview.videorentalstore.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

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
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.ArgumentCaptor;
import org.openapitools.model.Film;

import com.interview.videorentalstore.exceptions.EntityNotFoundException;
import com.interview.videorentalstore.exceptions.IncorrectRequestException;
import com.interview.videorentalstore.repositories.FilmsRepository;
import com.interview.videorentalstore.repositories.models.FilmDbModel;
import com.interview.videorentalstore.services.FilmsService;

import uk.co.jemos.podam.api.PodamFactory;
import uk.co.jemos.podam.api.PodamFactoryImpl;


public class FilmsServiceTest {

    private static final PodamFactory PODAM_FACTORY = new PodamFactoryImpl();

    private FilmsRepository repository;
    private FilmsService filmsService;

    @BeforeEach
    public void setUp() {
        this.repository = mock(FilmsRepository.class);
        filmsService = new FilmsService(repository);
    }

    @AfterEach
    private void afterEach() {
        verifyNoMoreInteractions(repository);
    }

    @ParameterizedTest
    @MethodSource
    public void getAllFilms_validParams_filmsReturned(final String filmType, final Boolean available) {
        final List<FilmDbModel> filmsReturnedFromDb = PODAM_FACTORY.manufacturePojo(List.class, FilmDbModel.class);

        when(repository.findByFilmTypeAndAvailable(filmType, available)).thenReturn(filmsReturnedFromDb);

        final List<Film> returnedFilms = filmsService.getAllFilms(filmType, available);

        assertEquals(returnedFilms.size(), filmsReturnedFromDb.size(), "Number of returned films is not as expected.");
        filmsReturnedFromDb.stream().map(FilmDbModel::getId).map(UUID::toString).collect(Collectors.toSet())
                .containsAll(returnedFilms.stream().map(Film::getId).collect(Collectors.toSet()));
        verify(repository).findByFilmTypeAndAvailable(any(), any());
    }

    @Test
    public void getAllFilms_invalidFilmType_incorrectRequestException() {
        final IncorrectRequestException exception = assertThrows(IncorrectRequestException.class, () -> filmsService.getAllFilms("INVALID", true));

        assertEquals(1, exception.getValidationErrors().size(), "Size of validation errors is not 1");
        verify(repository, never()).findByFilmTypeAndAvailable(any(), any());
    }

    @Test
    public void getFilm_validUuid_entityReturned() {
        final UUID uuid = UUID.randomUUID();
        final String id = uuid.toString();

        final FilmDbModel filmInDb = PODAM_FACTORY.manufacturePojo(FilmDbModel.class);

        when(repository.findById(uuid)).thenReturn(Optional.of(filmInDb));

        final Film returnedFilm = filmsService.getFilm(id);

        assertEquals(filmInDb.getId().toString(), returnedFilm.getId(), "IDs of retrieved films not match");
        verify(repository).findById(any(UUID.class));
    }

    @Test
    public void getFilm_invalidUuid_entityNotFoundException() {
        final String id = PODAM_FACTORY.manufacturePojo(String.class);

        assertThrows(EntityNotFoundException.class, () -> filmsService.getFilm(id));

        verify(repository, never()).findById(any(UUID.class));
    }

    @Test
    public void getFilm_entityNotFound_entityNotFoundException() {
        final UUID uuid = UUID.randomUUID();
        final String id = uuid.toString();

        when(repository.findById(uuid)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> filmsService.getFilm(id));

        verify(repository).findById(any(UUID.class));
    }

    @ParameterizedTest
    @CsvSource({ "true,false", "false,true" })
    public void changeFilmAvailability_newAvailability_availabilityChanged(final boolean currentAvailability, final boolean expectedAvailability) {
        final UUID uuid = UUID.randomUUID();
        final String id = uuid.toString();

        final FilmDbModel filmInDb = PODAM_FACTORY.manufacturePojo(FilmDbModel.class);
        filmInDb.setAvailable(currentAvailability);

        when(repository.findById(uuid)).thenReturn(Optional.of(filmInDb));

        final FilmDbModel filmReturnedFromSave = PODAM_FACTORY.manufacturePojo(FilmDbModel.class);
        when(repository.save(filmInDb)).thenReturn(filmReturnedFromSave);

        final Film returnedFilm = filmsService.changeFilmAvailability(id, expectedAvailability);

        verify(repository).findById(any(UUID.class));
        final ArgumentCaptor<FilmDbModel> argumentCaptor = ArgumentCaptor.forClass(FilmDbModel.class);
        verify(repository).save(argumentCaptor.capture());
        assertEquals(expectedAvailability, argumentCaptor.getValue().getAvailable(), "Availability does not match");
        assertEquals(filmReturnedFromSave.getId().toString(), returnedFilm.getId(), "ID of returned film does not match.");
    }

    @ParameterizedTest
    @CsvSource({ "true,true", "false,false" })
    public void changeFilmAvailability_sameAvailability_illegalArgumentException(final boolean currentAvailability, final boolean expectedAvailability) {
        final UUID uuid = UUID.randomUUID();
        final String id = uuid.toString();

        final FilmDbModel filmInDb = PODAM_FACTORY.manufacturePojo(FilmDbModel.class);
        filmInDb.setAvailable(currentAvailability);

        when(repository.findById(uuid)).thenReturn(Optional.of(filmInDb));

        assertThrows(IllegalArgumentException.class, () -> filmsService.changeFilmAvailability(id, expectedAvailability));

        verify(repository).findById(any(UUID.class));
        verify(repository, never()).save(any());
    }

    private static Stream<Arguments> getAllFilms_validParams_filmsReturned() {
        return Stream.of(
                Arguments.of(null, null),
                Arguments.of("NEW_RELEASE", true),
                Arguments.of("REGULAR", false),
                Arguments.of("OLD", true)
        );
    }
}
