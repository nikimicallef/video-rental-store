package com.interview.videorentalstore.repositories;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.interview.videorentalstore.repositories.models.FilmDbModel;


@Repository
public interface FilmsRepository
        extends CrudRepository<FilmDbModel, UUID> {

    @Query("SELECT f FROM FILMS f WHERE (:filmType is null or f.filmType = :filmType)" +
            " and (:available is null or f.available = :available)")
    List<FilmDbModel> findByFilmTypeAndAvailable(String filmType, Boolean available);

}
