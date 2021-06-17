package com.interview.videorentalstore.mappers;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.openapitools.model.Film;

import com.interview.videorentalstore.repositories.models.FilmDbModel;
import com.interview.videorentalstore.repositories.models.FilmTypeDbEnum;

import uk.co.jemos.podam.api.PodamFactory;
import uk.co.jemos.podam.api.PodamFactoryImpl;


public class FilmsMapperTest {

    private static final PodamFactory PODAM_FACTORY = new PodamFactoryImpl();

    @Test
    public void convertToApiModel_randomDbModel_correctMapping() {
        final FilmDbModel dbModel = PODAM_FACTORY.manufacturePojo(FilmDbModel.class);

        final Film apiModel = FilmsMapper.convertToApiModel(dbModel);

        assertEquals(dbModel.getId().toString(), apiModel.getId(), "ID does not match");
        assertEquals(dbModel.getName(), apiModel.getName(), "Name does not match");

        final Film.FilmTypeEnum expectedFilmType = mapDbFilmTypeToApiFilmType(dbModel.getFilmType());
        assertEquals(expectedFilmType, apiModel.getFilmType(), "Film Type does not match");

        assertEquals(dbModel.getAvailable(), apiModel.getAvailable(), "Availability does not match");
    }

    @ParameterizedTest
    @EnumSource(FilmTypeDbEnum.class)
    public void convertDbFilmTypeToApiEnum_randomFilmType_correctMapping(FilmTypeDbEnum filmTypeDbEnum) {
        final Film.FilmTypeEnum returnedFilmTypeEnum = FilmsMapper.convertDbFilmTypeToApiEnum(filmTypeDbEnum);

        assertEquals(mapDbFilmTypeToApiFilmType(filmTypeDbEnum), returnedFilmTypeEnum, "Film type does not match");
    }

    private Film.FilmTypeEnum mapDbFilmTypeToApiFilmType(FilmTypeDbEnum filmTypeDbEnum) {
        return filmTypeDbEnum == FilmTypeDbEnum.NEW_RELEASE ? Film.FilmTypeEnum.NEW_RELEASE
                : (filmTypeDbEnum == FilmTypeDbEnum.REGULAR ? Film.FilmTypeEnum.REGULAR : Film.FilmTypeEnum.OLD);
    }
}
