package com.app.back.springbootmongotest.clase13.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Se lanza cuando buscamos un producto que no existe.
 *
 * @ResponseStatus(HttpStatus.NOT_FOUND) le dice a Spring MVC que, si esta excepcion
 * llega al controlador sin ser atrapada, responda con un HTTP 404.
 */
@ResponseStatus(HttpStatus.NOT_FOUND)
public class ProductoNoEncontradoException extends RuntimeException {

    public ProductoNoEncontradoException(String id) {
        super("No existe un producto con id " + id);
    }
}
