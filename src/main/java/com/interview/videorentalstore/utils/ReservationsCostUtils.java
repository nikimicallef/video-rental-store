package com.interview.videorentalstore.utils;

import org.openapitools.model.Film;


public class ReservationsCostUtils {

    private static final Double PREMIUM_PRICE = 40.00;
    private static final Double BASIC_PRICE = 30.00;

    /**
     * Calculates the cost of a reservation
     *
     * @param filmType        used to calculate the cost since reservation cost depends on the film type
     * @param reservationDays days the user wants to keep the film for
     * @return reservation cost
     */
    public static Double calculateReservationCost(final Film.FilmTypeEnum filmType, final Integer reservationDays) {
        switch (filmType) {
            case NEW_RELEASE:
                return PREMIUM_PRICE * reservationDays;
            case REGULAR:
                return calculateTotalCostWithBasicPriceForXDays(reservationDays, 3);
            case OLD:
                return calculateTotalCostWithBasicPriceForXDays(reservationDays, 5);
            default:
                throw new IllegalArgumentException("Film type " + filmType + " not supported.");
        }
    }

    /**
     * Calculates the surcharge cost
     *
     * @param filmType used to calculate the surcharge since surcharge cost depends on the film type
     * @param days     for which the user went over their reservation
     * @return surcharge cost
     */
    public static Double calculateSurchargeCost(final Film.FilmTypeEnum filmType, final Integer days) {
        switch (filmType) {
            case NEW_RELEASE:
                return days * PREMIUM_PRICE;
            case REGULAR:
            case OLD:
                return days * BASIC_PRICE;
            default:
                throw new IllegalArgumentException("Film type " + filmType + " not supported.");
        }
    }

    /**
     * Generic method where the price of a film is made up of the basic price X days plus the basic price for each day thereafter
     *
     * @param reservationDays total amount of days the user wants the reservation for
     * @param basicPriceDays  days for which the first base price holds (after which the user is charger on a daily basis)
     * @return total cost
     */
    private static Double calculateTotalCostWithBasicPriceForXDays(final Integer reservationDays, final Integer basicPriceDays) {
        if (reservationDays <= basicPriceDays) {
            return BASIC_PRICE;
        } else {
            return BASIC_PRICE + ((reservationDays - basicPriceDays) * BASIC_PRICE);
        }
    }
}
