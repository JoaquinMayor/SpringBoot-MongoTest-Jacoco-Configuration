package com.app.back.springbootmongotest.clase13.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * Entidad que representa un producto del inventario.
 *
 * @Document indica que cada objeto Producto se guarda como un "documento" de MongoDB.
 * El parametro collection = "productos" es el nombre de la coleccion (equivalente a una "tabla").
 */
@Document(collection = "productos")
public class Producto {

    // @Id marca el campo que funciona como identificador unico (_id en Mongo).
    // Si lo dejamos en null, MongoDB lo genera automaticamente al guardar.
    @Id
    private String id;

    private String nombre;

    private Double precio;

    // Constructor vacio: Spring Data lo necesita para crear el objeto al leerlo de la base.
    public Producto() {
    }

    // Constructor de conveniencia para crear productos nuevos (sin id) en el codigo y en los tests.
    public Producto(String nombre, Double precio) {
        this.nombre = nombre;
        this.precio = precio;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public Double getPrecio() {
        return precio;
    }

    public void setPrecio(Double precio) {
        this.precio = precio;
    }
}
