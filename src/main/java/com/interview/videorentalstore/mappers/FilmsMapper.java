package com.interview.videorentalstore.mappers;

import org.openapitools.model.Film;

import com.interview.videorentalstore.repositories.models.FilmDbModel;
import com.interview.videorentalstore.repositories.models.FilmTypeDbEnum;


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

        final Film.FilmTypeEnum filmTypeEnum = convertToApiModelEnum(dbModel.getFilmType());
        apiModel.setFilmType(filmTypeEnum);

        apiModel.setAvailable(dbModel.getAvailable());

        return apiModel;
    }

    /**
     * Converts from a {@link FilmTypeDbEnum} to a {@link Film.FilmTypeEnum}
     *
     * @param dbEnum to convert
     * @return equivalent API enum
     */
    public static Film.FilmTypeEnum convertToApiModelEnum(final FilmTypeDbEnum dbEnum) {
        switch (dbEnum) {
            case NEW_RELEASE:
                return Film.FilmTypeEnum.NEW_RELEASE;
            case REGULAR:
                return Film.FilmTypeEnum.REGULAR;
            case OLD:
                return Film.FilmTypeEnum.OLD;
            default:
                throw new IllegalArgumentException("Film type " + dbEnum.getFilmType() + " not supported");
        }
    }
}
