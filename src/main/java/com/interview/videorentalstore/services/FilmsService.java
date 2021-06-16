package com.interview.videorentalstore.services;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.openapitools.model.Film;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.interview.videorentalstore.exceptions.EntityNotFoundException;
import com.interview.videorentalstore.exceptions.IncorrectRequestException;
import com.interview.videorentalstore.mappers.FilmsMapper;
import com.interview.videorentalstore.repositories.FilmsRepository;
import com.interview.videorentalstore.repositories.models.FilmDbModel;


@Service
public class FilmsService {

    private static final Logger LOGGER = LoggerFactory.getLogger(FilmsService.class);

    private final FilmsRepository repo;

    public FilmsService(FilmsRepository repo) {
        this.repo = repo;
    }

    /**
     * Retrieves all films. Only returns films which match the provided params should these be non-null.
     *
     * @param filmType  returns only films which match this value if this value is non null
     * @param available returns only films which match this value if this value is non null
     * @return all films which match the provided params
     */
    public List<Film> getAllFilms(final String filmType, final Boolean available) {
        if (filmType != null) {
            validateQueryParams(filmType);
        }

        final List<FilmDbModel> retrievedEntities = repo.findByFilmTypeAndAvailable(filmType, available);

        LOGGER.debug("Retrieved {} entities", retrievedEntities.size());

        return retrievedEntities.stream()
                .map(FilmsMapper::convertToApiModel)
                .collect(Collectors.toList());
    }

    /**
     * Returns a film with the given id. Throws {@link EntityNotFoundException} otherwise
     *
     * @param id of the film to retrieve
     * @return film with the provided id
     */
    public Film getFilm(final String id) {
        LOGGER.debug("Entered GET entity service method with id {}", id);

        final FilmDbModel filmDbModel = getFilmDbModel(id);

        return FilmsMapper.convertToApiModel(filmDbModel);
    }

    /**
     * Changes the availability of a film with a given id.
     * The method is NOT idempotent, i.e. if the desired availability status is equal to the current availability status then it will throw an {@link IllegalArgumentException}
     *
     * @param filmId       of the Film whose availability will be changed
     * @param availability the new availability
     * @return the updated Film model
     */
    public Film changeFilmAvailability(final String filmId, final boolean availability) {
        final FilmDbModel film = getFilmDbModel(filmId);

        if (availability == film.getAvailable()) {
            throw new IllegalArgumentException("Film with ID " + filmId + " is already set to available.");
        }

        film.setAvailable(availability);
        final FilmDbModel returnedFilm = repo.save(film);

        return FilmsMapper.convertToApiModel(returnedFilm);
    }

    /**
     * Validates that the film type inputted in the API is valid. If not throws an {@link IncorrectRequestException}
     *
     * @param inputtedFilmType passed in by the user
     */
    private void validateQueryParams(final String inputtedFilmType) {
        try {
            Film.FilmTypeEnum.fromValue(inputtedFilmType);
        } catch (IllegalArgumentException e) {
            throw new IncorrectRequestException("Incorrect data sent in the request URI", List.of("Film type " + inputtedFilmType + " does not exist."));
        }
    }

    /**
     * Returns a film with the given id. Throws {@link EntityNotFoundException} otherwise
     *
     * @param id of the film to retrieve
     * @return film with the provided id
     */
    private FilmDbModel getFilmDbModel(final String id) {
        final UUID uuid;

        try {
            uuid = UUID.fromString(id);
        } catch (IllegalArgumentException e) {
            throw new EntityNotFoundException();
        }

        return repo.findById(uuid).orElseThrow(EntityNotFoundException::new);
    }

}
