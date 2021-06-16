package com.interview.videorentalstore.repositories.models;

import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;


@Entity(name = "RESERVATIONS")
public class ReservationDbModel {

    @Id
    @GeneratedValue
    private UUID id;
    private UUID filmId;
    private ReservationStatusDbEnum status;
    private Integer reservationDays;
    private Instant reservationStartDate;
    private Double cost;
    private Instant expectedReturnDate;
    private Instant actualReturnDate;
    private Double surcharge;

    public ReservationDbModel() {
    }

    public ReservationDbModel(UUID id, UUID filmId, ReservationStatusDbEnum status, Integer reservationDays, Instant reservationStartDate, Double cost, Instant expectedReturnDate, Instant actualReturnDate, Double surcharge) {
        this.id = id;
        this.filmId = filmId;
        this.status = status;
        this.reservationDays = reservationDays;
        this.reservationStartDate = reservationStartDate;
        this.cost = cost;
        this.expectedReturnDate = expectedReturnDate;
        this.actualReturnDate = actualReturnDate;
        this.surcharge = surcharge;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public UUID getFilmId() {
        return filmId;
    }

    public void setFilmId(UUID filmId) {
        this.filmId = filmId;
    }

    public ReservationStatusDbEnum getStatus() {
        return status;
    }

    public void setStatus(ReservationStatusDbEnum status) {
        this.status = status;
    }

    public Integer getReservationDays() {
        return reservationDays;
    }

    public void setReservationDays(Integer reservationDays) {
        this.reservationDays = reservationDays;
    }

    public Instant getReservationStartDate() {
        return reservationStartDate;
    }

    public void setReservationStartDate(Instant reservationStartDate) {
        this.reservationStartDate = reservationStartDate;
    }

    public Double getCost() {
        return cost;
    }

    public void setCost(Double cost) {
        this.cost = cost;
    }

    public Instant getExpectedReturnDate() {
        return expectedReturnDate;
    }

    public void setExpectedReturnDate(Instant expectedReturnDate) {
        this.expectedReturnDate = expectedReturnDate;
    }

    public Instant getActualReturnDate() {
        return actualReturnDate;
    }

    public void setActualReturnDate(Instant actualReturnDate) {
        this.actualReturnDate = actualReturnDate;
    }

    public Double getSurcharge() {
        return surcharge;
    }

    public void setSurcharge(Double surcharge) {
        this.surcharge = surcharge;
    }

    @Override public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        ReservationDbModel that = (ReservationDbModel) o;
        return Objects.equals(id, that.id) && Objects.equals(filmId, that.filmId) && Objects.equals(status, that.status) && Objects.equals(reservationDays, that.reservationDays) && Objects.equals(reservationStartDate, that.reservationStartDate) && Objects.equals(cost, that.cost) && Objects.equals(expectedReturnDate, that.expectedReturnDate) && Objects.equals(actualReturnDate, that.actualReturnDate) && Objects.equals(surcharge, that.surcharge);
    }

    @Override public int hashCode() {
        return Objects.hash(id, filmId, status, reservationDays, reservationStartDate, cost, expectedReturnDate, actualReturnDate, surcharge);
    }
}
