package com.interview.videorentalstore.mappers;

import java.time.LocalDate;
import java.time.ZoneId;

import org.openapitools.model.Reservation;

import com.interview.videorentalstore.repositories.models.ReservationDbModel;
import com.interview.videorentalstore.repositories.models.ReservationStatusDbEnum;


public class ReservationsMapper {

    /**
     * Converts from a {@link ReservationDbModel} to a {@link Reservation}
     *
     * @param dbModel DB model to convert
     * @return equivalent API model
     */
    public static Reservation convertToApiModel(final ReservationDbModel dbModel) {
        final Reservation apiModel = new Reservation();

        apiModel.setId(dbModel.getId().toString());
        apiModel.setFilmId(dbModel.getFilmId().toString());

        final Reservation.StatusEnum reservationStatusEnum = convertDbReservationStatusToApiEnum(dbModel.getStatus());
        apiModel.setStatus(reservationStatusEnum);

        apiModel.setReservationDays(dbModel.getReservationDays());
        // This time zone should be the client's
        apiModel.setReservationStartDate(LocalDate.ofInstant(dbModel.getReservationStartDate(), ZoneId.systemDefault()));
        apiModel.setCost(dbModel.getCost());
        apiModel.setExpectedReturnDate(LocalDate.ofInstant(dbModel.getExpectedReturnDate(), ZoneId.systemDefault()));

        final LocalDate apiActualReturnDate = dbModel.getActualReturnDate() == null ? null : LocalDate.ofInstant(dbModel.getActualReturnDate(), ZoneId.systemDefault());
        apiModel.setActualReturnDate(apiActualReturnDate);

        apiModel.setSurcharge(dbModel.getSurcharge());

        return apiModel;
    }

    /**
     * Converts from a {@link ReservationStatusDbEnum} to a {@link Reservation.StatusEnum}
     *
     * @param dbEnum to convert
     * @return equivalent API enum
     */
    public static Reservation.StatusEnum convertDbReservationStatusToApiEnum(final ReservationStatusDbEnum dbEnum) {
        switch (dbEnum) {
            case OPEN:
                return Reservation.StatusEnum.OPEN;
            case CLOSED:
                return Reservation.StatusEnum.CLOSED;
            case SURCHARGE_REQUIRED:
                return Reservation.StatusEnum.SURCHARGE_REQUIRED;
            default:
                throw new IllegalArgumentException("Reservation status " + dbEnum.getReservationStatus() + " not supported");
        }
    }
}
