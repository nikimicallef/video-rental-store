package com.interview.videorentalstore.apis;

import java.util.List;

import org.openapitools.api.FilmsApiController;
import org.openapitools.model.Film;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.NativeWebRequest;

import com.interview.videorentalstore.services.FilmsService;


@Component
public class FilmsRestController
        extends FilmsApiController {

    private static final Logger LOGGER = LoggerFactory.getLogger(FilmsRestController.class);

    private final FilmsService service;

    public FilmsRestController(NativeWebRequest request, FilmsService service) {
        super(request);
        this.service = service;
    }

    @Override
    public ResponseEntity<List<Film>> filmsGet(final String filmType, final Boolean available) {
        LOGGER.info("Entered GET Films endpoint with filmType {} and available {}.", filmType, available);

        final List<Film> allFilms = service.getAllFilms(filmType, available);

        LOGGER.info("Returned {} films.", allFilms.size());

        return ResponseEntity.ok(allFilms);
    }

    @Override
    public ResponseEntity<Film> filmsIdGet(final String id) {
        LOGGER.info("Entered GET film with id {}", id);

        final Film retrievedFilm = service.getFilm(id);

        LOGGER.info("Returned film with id {}" + id);

        return ResponseEntity.ok(retrievedFilm);
    }
}
