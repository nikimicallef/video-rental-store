package com.interview.videorentalstore.repositories;

import java.util.UUID;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.interview.videorentalstore.repositories.models.FilmDbModel;


@Repository
public interface FilmsRepository
        extends CrudRepository<FilmDbModel, UUID> {

}
