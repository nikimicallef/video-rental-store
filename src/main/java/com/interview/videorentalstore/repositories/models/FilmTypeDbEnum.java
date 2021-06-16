package com.interview.videorentalstore.repositories.models;

public enum FilmTypeDbEnum {
    NEW_RELEASE("NEW_RELEASE"),
    REGULAR("REGULAR"),
    OLD("OLD");

    private String filmType;

    FilmTypeDbEnum(final String filmType) {
        this.filmType = filmType;
    }

    public String getFilmType() {
        return filmType;
    }

    public void setFilmType(String filmType) {
        this.filmType = filmType;
    }
}
