package com.example.beerstock.mapper;

import com.example.beerstock.dto.BeerDTO;
import com.example.beerstock.entity.Beer;
import javax.annotation.Generated;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2021-06-25T10:24:25-0300",
    comments = "version: 1.3.1.Final, compiler: javac, environment: Java 11.0.11 (Amazon.com Inc.)"
)
public class BeerMapperImpl implements BeerMapper {

    @Override
    public Beer toModel(BeerDTO beerDTO) {
        if ( beerDTO == null ) {
            return null;
        }

        Beer beer = new Beer();

        beer.setId( beerDTO.getId() );
        beer.setName( beerDTO.getName() );
        beer.setBrand( beerDTO.getBrand() );
        if ( beerDTO.getMax() != null ) {
            beer.setMax( beerDTO.getMax() );
        }
        if ( beerDTO.getQuantity() != null ) {
            beer.setQuantity( beerDTO.getQuantity() );
        }
        beer.setType( beerDTO.getType() );

        return beer;
    }

    @Override
    public BeerDTO toDTO(Beer beer) {
        if ( beer == null ) {
            return null;
        }

        BeerDTO beerDTO = new BeerDTO();

        beerDTO.setId( beer.getId() );
        beerDTO.setName( beer.getName() );
        beerDTO.setBrand( beer.getBrand() );
        beerDTO.setMax( beer.getMax() );
        beerDTO.setQuantity( beer.getQuantity() );
        beerDTO.setType( beer.getType() );

        return beerDTO;
    }
}
