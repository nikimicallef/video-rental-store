package com.interview.videorentalstore.repositories;

import java.time.Instant;
import java.time.Period;

import org.openapitools.model.Film;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

import com.interview.videorentalstore.repositories.models.FilmDbModel;
import com.interview.videorentalstore.repositories.models.ReservationDbModel;


@Component
public class DataBoarding
        implements ApplicationListener<ApplicationReadyEvent> {

    private final FilmsRepository filmsRepository;

    private final ReservationsRepository reservationsRepository;

    public DataBoarding(FilmsRepository filmsRepository, ReservationsRepository reservationsRepository) {
        this.filmsRepository = filmsRepository;
        this.reservationsRepository = reservationsRepository;
    }

    @Override
    public void onApplicationEvent(ApplicationReadyEvent applicationReadyEvent) {
        final FilmDbModel matrix = filmsRepository.save(new FilmDbModel(null, "Matrix 11", Film.FilmTypeEnum.NEW_RELEASE.name(), false));
        filmsRepository.save(new FilmDbModel(null, "Spider Man", Film.FilmTypeEnum.REGULAR.name(), true));
        filmsRepository.save(new FilmDbModel(null, "Spider Man 2", Film.FilmTypeEnum.REGULAR.name(), true));
        final FilmDbModel outOfAfrica = filmsRepository.save(new FilmDbModel(null, "Out of Africa", Film.FilmTypeEnum.OLD.name(), false));
        filmsRepository.save(new FilmDbModel(null, "The Godfather", Film.FilmTypeEnum.OLD.name(), true));
        filmsRepository.save(new FilmDbModel(null, "Tom and Jerry", Film.FilmTypeEnum.NEW_RELEASE.name(), true));
        filmsRepository.save(new FilmDbModel(null, "The Avengers", Film.FilmTypeEnum.REGULAR.name(), true));

        // TODO: Status enum and cost
        final Instant matrixReservationDate = Instant.now().minus(Period.ofDays(3));
        final int matrixReservationDays = 5;
        reservationsRepository.save(new ReservationDbModel(null, matrix.getId(), "OPEN", matrixReservationDays, matrixReservationDate, 10.00, matrixReservationDate.plus(Period.ofDays(matrixReservationDays)), null, null));

        final Instant outOfAfricaReservationDate = Instant.now().minus(Period.ofDays(7));
        final int outOfAfricaReservationDays = 4;
        reservationsRepository.save(new ReservationDbModel(null, outOfAfrica.getId(), "OPEN", outOfAfricaReservationDays, outOfAfricaReservationDate, 10.00, matrixReservationDate.plus(Period.ofDays(matrixReservationDays)), null, null));

    }
}
