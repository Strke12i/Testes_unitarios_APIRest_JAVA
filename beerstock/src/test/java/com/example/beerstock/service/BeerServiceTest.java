package com.example.beerstock.service;

import com.example.beerstock.builder.BeerDTOBuilder;
import com.example.beerstock.dto.BeerDTO;
import com.example.beerstock.entity.Beer;
import com.example.beerstock.exception.BeerAlreadyRegisteredException;
import com.example.beerstock.exception.BeerNotFoundException;
import com.example.beerstock.exception.BeerStockExceededException;
import com.example.beerstock.mapper.BeerMapper;
import com.example.beerstock.repository.BeerRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class BeerServiceTest {

    private static final long INVALID_BEER_ID = 1L;

    @Mock
    private BeerRepository beerRepository;

    private BeerMapper beerMapper=BeerMapper.INSTANCE;
    @InjectMocks
    private BeerService beerService;

    @Test
    void whenBeerInformedThenItShouldBeCreated() throws BeerAlreadyRegisteredException {

        BeerDTO expectedBeerDTO= BeerDTOBuilder.builder().build().toBeerDTO();
        Beer expectedSavedBeer=beerMapper.toModel(expectedBeerDTO);

        Mockito.when(beerRepository.findByName(expectedBeerDTO.getName())).thenReturn(Optional.empty());
        Mockito.when(beerRepository.save(expectedSavedBeer)).thenReturn(expectedSavedBeer);

        BeerDTO createdBeerDTO=beerService.createBeer(expectedBeerDTO);

        assertThat(createdBeerDTO.getId(),is(equalTo(expectedBeerDTO.getId())));
        assertThat(createdBeerDTO.getName(),is(equalTo(expectedBeerDTO.getName())));
        assertThat(createdBeerDTO.getQuantity(),is(equalTo(expectedBeerDTO.getQuantity())));


    }

    @Test
    void whenAlreadyRegisteredBeerInformedThenAnExceptionShouldBeThrown(){

        BeerDTO expectedBeerDTO= BeerDTOBuilder.builder().build().toBeerDTO();
        Beer duplicatedBeer=beerMapper.toModel(expectedBeerDTO);

        Mockito.when(beerRepository.findByName(expectedBeerDTO.getName())).thenReturn(Optional.of(duplicatedBeer));

        assertThrows(BeerAlreadyRegisteredException.class,()->beerService.createBeer(expectedBeerDTO));


    }


    @Test
    void whenValidBeerNameIsGivenThenReturnABeer() throws BeerNotFoundException{
        BeerDTO expectedFoundBeerDTO = BeerDTOBuilder.builder().build().toBeerDTO();
        Beer expectedFoundBeer = beerMapper.toModel(expectedFoundBeerDTO);

        Mockito.when(beerRepository.findByName(expectedFoundBeer.getName())).thenReturn(Optional.of(expectedFoundBeer));

        BeerDTO foundBeerDTO=beerService.findByName(expectedFoundBeerDTO.getName());
        assertThat(foundBeerDTO,is(equalTo(expectedFoundBeerDTO)));


    }

    @Test
    void whenNotRegisteredBeerNameIsGivenThenThrowAnException(){
        BeerDTO expectedFoundBeerDTO= BeerDTOBuilder.builder().build().toBeerDTO();

        Mockito.when(beerRepository.findByName(expectedFoundBeerDTO.getName())).thenReturn(Optional.empty());

        assertThrows(BeerNotFoundException.class,()->beerService.findByName(expectedFoundBeerDTO.getName()));
    }

    @Test
    void whenListBeerIsCalledThenReturnAListOfBeers(){
        BeerDTO expectedFoundBeerDTO= BeerDTOBuilder.builder().build().toBeerDTO();
        Beer expectedFoundBeer = beerMapper.toModel(expectedFoundBeerDTO);
        Mockito.when(beerRepository.findAll()).thenReturn(Collections.singletonList(expectedFoundBeer));

        List<BeerDTO> foundListBeersDTO= beerService.listAll();

        assertThat(foundListBeersDTO,is(not(empty())));
        assertThat(foundListBeersDTO.get(0),is(equalTo(expectedFoundBeerDTO)));

    }


    @Test
    void whenListBeerIsCalledThenReturnAnEmptyListOfBeers(){
        Mockito.when(beerRepository.findAll()).thenReturn(Collections.EMPTY_LIST);
        List<BeerDTO> foundListBeersDTO = beerService.listAll();
        assertThat(foundListBeersDTO,is(empty()));
    }

    @Test
    void whenExclusionIsCalledWithValidIdThenABeerShouldBeDeleted() throws BeerNotFoundException{
        BeerDTO exceptedDeletedBeerDTO=BeerDTOBuilder.builder().build().toBeerDTO();
        Beer exceptedDeletedBeer= beerMapper.toModel(exceptedDeletedBeerDTO);
        Mockito.when(beerRepository.findById(exceptedDeletedBeerDTO.getId())).thenReturn(Optional.of(exceptedDeletedBeer));
        beerService.deleteById(exceptedDeletedBeerDTO.getId());

        Mockito.verify(beerRepository,Mockito.times(1)).findById(exceptedDeletedBeerDTO.getId());
        Mockito.verify(beerRepository,Mockito.times(1)).deleteById(exceptedDeletedBeerDTO.getId());

        
    }

    @Test
    void whenIncrementIsCalledThenIncrementBeerStock() throws BeerNotFoundException, BeerStockExceededException {
        BeerDTO expectBeerDTO=BeerDTOBuilder.builder().build().toBeerDTO();
        Beer expectBeer=beerMapper.toModel(expectBeerDTO);
        Mockito.when(beerRepository.findById(expectBeerDTO.getId())).thenReturn(Optional.of(expectBeer));
        int quantityTOIncrement=10;
        int expectedQuantityAfterIncrement=expectBeerDTO.getQuantity()+quantityTOIncrement;

        BeerDTO incrementedBeerDTO = beerService.increment(expectBeerDTO.getId(), quantityTOIncrement);

        assertThat(expectedQuantityAfterIncrement,equalTo(incrementedBeerDTO.getQuantity()));
        assertThat(expectedQuantityAfterIncrement,lessThan(incrementedBeerDTO.getMax()));


    }

    @Test
    void whenIncrementIsGreatherThanMaxThenThrowException(){
        BeerDTO expectedBeerDTO=BeerDTOBuilder.builder().build().toBeerDTO();
        Beer expectedBeer=beerMapper.toModel(expectedBeerDTO);

        Mockito.when(beerRepository.findById(expectedBeerDTO.getId())).thenReturn(Optional.of(expectedBeer));
        int quantityToIncrement=90;
        assertThrows(BeerStockExceededException.class,()->beerService.increment(expectedBeerDTO.getId(),quantityToIncrement));

    }
    @Test
    void whenIncrementIsCalledWithInvalidIdThenThrowException(){

        int quantityToIncrement=10;
        Mockito.when(beerRepository.findById(INVALID_BEER_ID)).thenReturn(Optional.empty());
        assertThrows(BeerNotFoundException.class, () -> beerService.increment(INVALID_BEER_ID, quantityToIncrement));
    }

    @Test
    void whenIncrementAfterSumIsGreatherThanMaxThenThrowException() {
        BeerDTO expectedBeerDTO = BeerDTOBuilder.builder().build().toBeerDTO();
        Beer expectedBeer = beerMapper.toModel(expectedBeerDTO);

        when(beerRepository.findById(expectedBeerDTO.getId())).thenReturn(Optional.of(expectedBeer));

        int quantityToIncrement = 45;
        assertThrows(BeerStockExceededException.class, () -> beerService.increment(expectedBeerDTO.getId(), quantityToIncrement));
    }



}
