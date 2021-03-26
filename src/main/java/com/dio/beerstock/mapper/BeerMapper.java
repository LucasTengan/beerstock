package com.dio.beerstock.mapper;

import com.dio.beerstock.DTO.BeerDTO;
import com.dio.beerstock.entity.Beer;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface BeerMapper {

    BeerMapper INSTANCE = Mappers.getMapper(BeerMapper.class);

    Beer toModel(BeerDTO beerDTO);      // o parâmetro é qual classe eu quero alterar, e o tipo é para qual classe quero
    BeerDTO toDTO(Beer beer);
}
