package com.cozyquoteforge.pccc.exception;

public class ProductCodeAlreadyExistsException extends RuntimeException {
    public ProductCodeAlreadyExistsException(String code) {
        super("Product code already exists: " + code);
    }
}