package com.interview.videorentalstore.utils;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;
import org.openapitools.model.Film;


public class ReservationsCostUtilsTest {

    @Test
    public void calculateReservationCost_newRelease1Day_correctCostReturned() {
        final Double cost = ReservationsCostUtils.calculateReservationCost(Film.FilmTypeEnum.NEW_RELEASE, 1);

        assertEquals(40, cost, "Cost returned does not match");
    }

    @Test
    public void calculateReservationCost_newRelease3Days_correctCostReturned() {
        final Double cost = ReservationsCostUtils.calculateReservationCost(Film.FilmTypeEnum.NEW_RELEASE, 3);

        assertEquals(120, cost, "Cost returned does not match");
    }

    @Test
    public void calculateReservationCost_regular5Days_correctCostReturned() {
        final Double cost = ReservationsCostUtils.calculateReservationCost(Film.FilmTypeEnum.REGULAR, 5);

        assertEquals(90, cost, "Cost returned does not match");
    }

    @Test
    public void calculateReservationCost_regular2Days_correctCostReturned() {
        final Double cost = ReservationsCostUtils.calculateReservationCost(Film.FilmTypeEnum.REGULAR, 2);

        assertEquals(30, cost, "Cost returned does not match");
    }

    @Test
    public void calculateReservationCost_old7Days_correctCostReturned() {
        final Double cost = ReservationsCostUtils.calculateReservationCost(Film.FilmTypeEnum.OLD, 7);

        assertEquals(90, cost, "Cost returned does not match");
    }

    @Test
    public void calculateSurchargeCost_newRelease2Days_correctCostReturned() {
        final Double cost = ReservationsCostUtils.calculateSurchargeCost(Film.FilmTypeEnum.NEW_RELEASE, 2);

        assertEquals(80, cost, "Cost returned does not match");
    }

    @Test
    public void calculateSurchargeCost_regular1Days_correctCostReturned() {
        final Double cost = ReservationsCostUtils.calculateSurchargeCost(Film.FilmTypeEnum.REGULAR, 1);

        assertEquals(30, cost, "Cost returned does not match");
    }

    @Test
    public void calculateSurchargeCost_old3Days_correctCostReturned() {
        final Double cost = ReservationsCostUtils.calculateSurchargeCost(Film.FilmTypeEnum.OLD, 3);

        assertEquals(90, cost, "Cost returned does not match");
    }

}
