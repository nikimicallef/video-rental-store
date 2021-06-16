package com.interview.videorentalstore.apis;

import java.util.List;

import org.openapitools.api.ReservationsApiController;
import org.openapitools.model.Reservation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.NativeWebRequest;

import com.interview.videorentalstore.services.ReservationsService;


@Component
public class ReservationsRestController
        extends ReservationsApiController {

    private static final Logger LOGGER = LoggerFactory.getLogger(FilmsRestController.class);

    private final ReservationsService service;

    public ReservationsRestController(NativeWebRequest request, ReservationsService service) {
        super(request);
        this.service = service;
    }

    @Override
    public ResponseEntity<Reservation> reservationsIdActionsReturnPost(final String id) {
        LOGGER.info("Entered POST return reservation with id {}", id);

        final Reservation returnedReservation = service.returnReservation(id);

        LOGGER.info("Reservation with id {} returned.", returnedReservation.getId());

        return ResponseEntity.ok(returnedReservation);
    }

    @Override
    public ResponseEntity<Reservation> reservationsPost(final Reservation reservation) {
        LOGGER.info("Entered POST reservation with input body {}", reservation.toString());

        final Reservation createdReservation = service.createReservation(reservation);

        LOGGER.info("Reservation with id {} created.", createdReservation.getId());

        return ResponseEntity.ok(createdReservation);
    }

    @Override public ResponseEntity<List<Reservation>> reservationsGet() {
        LOGGER.info("Entered GET reservations endpoint ");

        final List<Reservation> allReservations = service.getAllReservations();

        LOGGER.info("Returned {} reservations.", allReservations.size());

        return ResponseEntity.ok(allReservations);
    }
}
