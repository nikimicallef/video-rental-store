package com.interview.videorentalstore.mappers;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.stream.Stream;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.EnumSource;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.NullSource;
import org.openapitools.model.Reservation;

import com.interview.videorentalstore.repositories.models.ReservationDbModel;
import com.interview.videorentalstore.repositories.models.ReservationStatusDbEnum;

import uk.co.jemos.podam.api.PodamFactory;
import uk.co.jemos.podam.api.PodamFactoryImpl;


public class ReservationsMapperTest {

    private static final PodamFactory PODAM_FACTORY = new PodamFactoryImpl();

    @ParameterizedTest
    @NullSource
    @MethodSource("provideActualReturnDate")
    public void convertToApiModel_randomDbModel_correctMapping(Instant actualReturnDate) {
        final ReservationDbModel dbModel = PODAM_FACTORY.manufacturePojo(ReservationDbModel.class);
        dbModel.setActualReturnDate(actualReturnDate);

        final Reservation apiModel = ReservationsMapper.convertToApiModel(dbModel);

        assertEquals(dbModel.getId().toString(), apiModel.getId(), "ID does not match");
        assertEquals(dbModel.getFilmId().toString(), apiModel.getFilmId(), "Film ID does not match");

        final Reservation.StatusEnum expectedStatus = mapDbStatusToApiStatus(dbModel.getStatus());
        assertEquals(expectedStatus, apiModel.getStatus(), "Status does not match");

        assertEquals(dbModel.getReservationDays(), apiModel.getReservationDays(), "Reservation days does not match");

        final LocalDate expectedReservationStartDate = LocalDate.ofInstant(dbModel.getReservationStartDate(), ZoneId.systemDefault());
        assertEquals(expectedReservationStartDate, apiModel.getReservationStartDate(), "Reservation start date does not match");

        assertEquals(dbModel.getCost(), apiModel.getCost(), "Cost does not match");

        final LocalDate expectedExpectedReturnDate = LocalDate.ofInstant(dbModel.getExpectedReturnDate(), ZoneId.systemDefault());
        assertEquals(expectedExpectedReturnDate, apiModel.getExpectedReturnDate(), "Expected return date does not match");

        if (actualReturnDate != null) {
            final LocalDate expectedActualReturnDate = LocalDate.ofInstant(dbModel.getActualReturnDate(), ZoneId.systemDefault());
            assertEquals(expectedActualReturnDate, apiModel.getActualReturnDate(), "Actual return date does not match");
        } else {
            assertNull(apiModel.getActualReturnDate(), "Actual return date is not null");
        }

        assertEquals(dbModel.getSurcharge(), apiModel.getSurcharge(), "Surcharge does not match");
    }

    private static Stream<Arguments> provideActualReturnDate() {
        return Stream.of(
                Arguments.of(Instant.now())
        );
    }

    @ParameterizedTest
    @EnumSource(ReservationStatusDbEnum.class)
    public void convertDbReservationStatusToApiEnum_randomReservationStatus_correctMapping(ReservationStatusDbEnum reservationStatusDbEnum) {
        final Reservation.StatusEnum returnedStatusEnum = ReservationsMapper.convertDbReservationStatusToApiEnum(reservationStatusDbEnum);

        assertEquals(mapDbStatusToApiStatus(reservationStatusDbEnum), returnedStatusEnum, "Reservation status does not match");
    }

    private Reservation.StatusEnum mapDbStatusToApiStatus(ReservationStatusDbEnum reservationStatusDbEnum) {
        return reservationStatusDbEnum == ReservationStatusDbEnum.OPEN ? Reservation.StatusEnum.OPEN
                : (reservationStatusDbEnum == ReservationStatusDbEnum.CLOSED ? Reservation.StatusEnum.CLOSED : Reservation.StatusEnum.SURCHARGE_REQUIRED);
    }
}
