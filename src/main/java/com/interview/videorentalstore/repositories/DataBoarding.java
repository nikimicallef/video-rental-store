package com.interview.videorentalstore.repositories;

import java.time.Instant;
import java.time.Period;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

import com.interview.videorentalstore.repositories.models.FilmDbModel;
import com.interview.videorentalstore.repositories.models.ReservationDbModel;


@Component
public class DataBoarding implements ApplicationListener<ApplicationReadyEvent> {

    @Autowired
    private FilmsRepository filmsRepository;

    @Autowired
    private ReservationsRepository reservationsRepository;

    @Override
    public void onApplicationEvent(ApplicationReadyEvent applicationReadyEvent) {
        // TODO: filmType enum
        final FilmDbModel matrix = filmsRepository.save(new FilmDbModel(null, "Matrix 11", "NEW_RELEASE", false));
        filmsRepository.save(new FilmDbModel(null, "Spider Man", "REGULAR", true));
        filmsRepository.save(new FilmDbModel(null, "Spider Man 2", "REGULAR", true));
        final FilmDbModel outOfAfrica = filmsRepository.save(new FilmDbModel(null, "Out of Africa", "OLD", false));
        filmsRepository.save(new FilmDbModel(null, "The Godfather", "OLD", true));
        filmsRepository.save(new FilmDbModel(null, "Tom and Jerry", "NEW_RELEASE", true));
        filmsRepository.save(new FilmDbModel(null, "The Avengers", "REGULAR", true));

        // TODO: Status enum and cost
        final Instant matrixReservationDate = Instant.now().minus(Period.ofDays(3));
        final int matrixReservationDays = 5;
        reservationsRepository.save(new ReservationDbModel(null, matrix.getId(), "OPEN", matrixReservationDays, matrixReservationDate, 10.00, matrixReservationDate.plus(Period.ofDays(matrixReservationDays)), null, null));

        final Instant outOfAfricaReservationDate = Instant.now().minus(Period.ofDays(7));
        final int outOfAfricaReservationDays = 4;
        reservationsRepository.save(new ReservationDbModel(null, outOfAfrica.getId(), "OPEN", outOfAfricaReservationDays, outOfAfricaReservationDate, 10.00, matrixReservationDate.plus(Period.ofDays(matrixReservationDays)), null, null));

    }
}
