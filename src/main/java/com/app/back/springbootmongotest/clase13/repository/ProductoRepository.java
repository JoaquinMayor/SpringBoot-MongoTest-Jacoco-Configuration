package com.app.back.springbootmongotest.clase13.repository;

import com.app.back.springbootmongotest.clase13.model.Producto;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.Optional;

/**
 * Repositorio de productos.
 *
 * Al extender MongoRepository<Producto, String> obtenemos GRATIS los metodos
 * save, findById, findAll, deleteById, count, etc.
 *   - Producto -> el tipo de documento que maneja.
 *   - String   -> el tipo del campo @Id.
 *
 * No hace falta implementar esta interfaz: Spring crea la implementacion en tiempo de ejecucion.
 */
public interface ProductoRepository extends MongoRepository<Producto, String> {

    // "Query derivada": Spring lee el nombre del metodo y arma la consulta sola.
    // findByNombre -> { "nombre": ? }
    Optional<Producto> findByNombre(String nombre);

    // findByPrecioLessThan -> { "precio": { $lt: ? } }  (precio menor al valor recibido)
    List<Producto> findByPrecioLessThan(Double precio);
}
