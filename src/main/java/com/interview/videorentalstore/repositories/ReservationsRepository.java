package com.interview.videorentalstore.repositories;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.interview.videorentalstore.repositories.models.ReservationDbModel;


@Repository
public interface ReservationsRepository
        extends CrudRepository<ReservationDbModel, Integer> {

}
