package com.interview.videorentalstore.repositories.models;

public enum ReservationStatusDbEnum {
    OPEN("OPEN"),
    CLOSED("CLOSED"),
    SURCHARGE_REQUIRED("SURCHARGE_REQUIRED");

    private String reservationStatus;

    ReservationStatusDbEnum(final String reservationStatus) {
        this.reservationStatus = reservationStatus;
    }

    public String getReservationStatus() {
        return reservationStatus;
    }

    public void setReservationStatus(String reservationStatus) {
        this.reservationStatus = reservationStatus;
    }

}
