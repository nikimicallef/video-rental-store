package com.interview.videorentalstore.mappers;

import org.openapitools.model.Film;

import com.interview.videorentalstore.repositories.models.FilmDbModel;


public class FilmsMapper {

    /**
     * Converts from a {@link FilmDbModel} to a {@link Film}
     *
     * @param dbModel DB model to convert
     * @return equivalent API model
     */
    public static Film convertToApiModel(final FilmDbModel dbModel) {
        final Film apiModel = new Film();

        apiModel.setId(dbModel.getId().toString());
        apiModel.setName(dbModel.getName());

        final Film.FilmTypeEnum filmTypeEnum = Film.FilmTypeEnum.fromValue(dbModel.getFilmType());
        apiModel.setFilmType(filmTypeEnum);

        apiModel.setAvailable(dbModel.getAvailable());

        return apiModel;
    }
}
