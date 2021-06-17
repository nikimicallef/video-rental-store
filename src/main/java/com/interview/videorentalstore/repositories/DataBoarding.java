package com.interview.videorentalstore.repositories;

import java.time.Instant;
import java.time.Period;

import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

import com.interview.videorentalstore.mappers.FilmsMapper;
import com.interview.videorentalstore.repositories.models.FilmDbModel;
import com.interview.videorentalstore.repositories.models.FilmTypeDbEnum;
import com.interview.videorentalstore.repositories.models.ReservationDbModel;
import com.interview.videorentalstore.repositories.models.ReservationStatusDbEnum;
import com.interview.videorentalstore.utils.ReservationsCostUtils;


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
        // Board films
        final FilmDbModel matrix = filmsRepository.save(new FilmDbModel(null, "Matrix 11", FilmTypeDbEnum.NEW_RELEASE, false));
        filmsRepository.save(new FilmDbModel(null, "Spider Man", FilmTypeDbEnum.REGULAR, true));
        filmsRepository.save(new FilmDbModel(null, "Spider Man 2", FilmTypeDbEnum.REGULAR, true));
        final FilmDbModel outOfAfrica = filmsRepository.save(new FilmDbModel(null, "Out of Africa", FilmTypeDbEnum.OLD, false));
        filmsRepository.save(new FilmDbModel(null, "The Godfather", FilmTypeDbEnum.OLD, true));
        filmsRepository.save(new FilmDbModel(null, "Tom and Jerry", FilmTypeDbEnum.NEW_RELEASE, true));
        filmsRepository.save(new FilmDbModel(null, "The Avengers", FilmTypeDbEnum.REGULAR, true));

        // Board reservations
        final Instant matrixReservationDate = Instant.now().minus(Period.ofDays(3));
        final int matrixReservationDays = 5;
        reservationsRepository.save(new ReservationDbModel(null,
                matrix.getId(),
                ReservationStatusDbEnum.OPEN,
                matrixReservationDays,
                matrixReservationDate,
                ReservationsCostUtils.calculateReservationCost(FilmsMapper.convertDbFilmTypeToApiEnum(matrix.getFilmType()), matrixReservationDays),
                matrixReservationDate.plus(Period.ofDays(matrixReservationDays)),
                null,
                null));

        final Instant outOfAfricaReservationDate = Instant.now().minus(Period.ofDays(7));
        final int outOfAfricaReservationDays = 4;
        reservationsRepository.save(new ReservationDbModel(null,
                outOfAfrica.getId(),
                ReservationStatusDbEnum.OPEN,
                outOfAfricaReservationDays,
                outOfAfricaReservationDate,
                ReservationsCostUtils.calculateReservationCost(FilmsMapper.convertDbFilmTypeToApiEnum(outOfAfrica.getFilmType()), outOfAfricaReservationDays),
                outOfAfricaReservationDate.plus(Period.ofDays(outOfAfricaReservationDays)),
                null,
                null));

    }
}
