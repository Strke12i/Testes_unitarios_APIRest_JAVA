package com.example.beerstock.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class BeerAlreadyRegisteredException extends Exception{

    public BeerAlreadyRegisteredException(String beerName) {
        super(String.format("beer with name %s already register in the system.", beerName));
    }

}
