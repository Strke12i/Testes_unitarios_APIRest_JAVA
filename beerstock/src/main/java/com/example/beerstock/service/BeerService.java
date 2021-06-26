package com.example.beerstock.service;

import com.example.beerstock.dto.BeerDTO;
import com.example.beerstock.entity.Beer;
import com.example.beerstock.exception.BeerAlreadyRegisteredException;
import com.example.beerstock.exception.BeerNotFoundException;
import com.example.beerstock.exception.BeerStockExceededException;
import com.example.beerstock.mapper.BeerMapper;
import com.example.beerstock.repository.BeerRepository;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor(onConstructor=@__(@Autowired))
public class BeerService {

    private final BeerRepository beerRepository;
    private final BeerMapper beerMapper=BeerMapper.INSTANCE;


    public BeerDTO createBeer(BeerDTO beerDTO) throws BeerAlreadyRegisteredException{
        verifyIfIsAlreadyRegistered(beerDTO.getName());
        Beer beer=beerMapper.toModel(beerDTO);
        Beer savedBeer=beerRepository.save(beer);
        return beerMapper.toDTO(savedBeer);

    }

    public BeerDTO findByName(String name) throws BeerNotFoundException {

        Beer foundedBeer=beerRepository.findByName(name).orElseThrow(()->
                new BeerNotFoundException(name));
        return beerMapper.toDTO(foundedBeer);

    }

    public List<BeerDTO> listAll(){
        return beerRepository.findAll().stream().
                map(beerMapper::toDTO).collect(Collectors.toList());

    }

    public void deleteById(Long id) throws BeerNotFoundException {
        verifyIfExists(id);
        beerRepository.deleteById(id);
    }

    public BeerDTO increment(Long id, int quantityToIncrement) throws BeerNotFoundException, BeerStockExceededException {
        Beer beerToIncrementStock = verifyIfExists(id);
        int quantityAfterIncrement = quantityToIncrement + beerToIncrementStock.getQuantity();
        if (quantityAfterIncrement <= beerToIncrementStock.getMax()) {
            beerToIncrementStock.setQuantity(beerToIncrementStock.getQuantity() + quantityToIncrement);
            Beer incrementedBeerStock = beerRepository.save(beerToIncrementStock);
            return beerMapper.toDTO(incrementedBeerStock);
        }
        throw new BeerStockExceededException(id, quantityToIncrement);
    }


    private void verifyIfIsAlreadyRegistered(String name) throws BeerAlreadyRegisteredException {
        Optional<Beer> optSavedBeer = beerRepository.findByName(name);
        if (optSavedBeer.isPresent()) {
            throw new BeerAlreadyRegisteredException(name);
        }
    }
    private Beer verifyIfExists(Long id) throws BeerNotFoundException {
        return beerRepository.findById(id)
                .orElseThrow(() -> new BeerNotFoundException(id));
    }
}
