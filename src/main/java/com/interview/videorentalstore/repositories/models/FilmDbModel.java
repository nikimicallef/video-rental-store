package com.interview.videorentalstore.repositories.models;

import java.util.Objects;
import java.util.UUID;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

import org.hibernate.annotations.GenericGenerator;


@Entity(name = "FILMS")
public class FilmDbModel {

    @Id
    @GeneratedValue(generator = "uuid2")
    @GenericGenerator(name = "uuid2", strategy = "org.hibernate.id.UUIDGenerator")
    @Column(name = "id", columnDefinition = "VARCHAR(255)")
    private UUID id;
    private String name;
    private String filmType;
    private Boolean available;

    public FilmDbModel() {
    }

    public FilmDbModel(UUID id, String name, String filmType, Boolean available) {
        this.id = id;
        this.name = name;
        this.filmType = filmType;
        this.available = available;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getFilmType() {
        return filmType;
    }

    public void setFilmType(String filmType) {
        this.filmType = filmType;
    }

    public Boolean getAvailable() {
        return available;
    }

    public void setAvailable(Boolean available) {
        this.available = available;
    }

    @Override public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        FilmDbModel that = (FilmDbModel) o;
        return Objects.equals(id, that.id) && Objects.equals(name, that.name) && Objects.equals(filmType, that.filmType) && Objects.equals(available, that.available);
    }

    @Override public int hashCode() {
        return Objects.hash(id, name, filmType, available);
    }
}
