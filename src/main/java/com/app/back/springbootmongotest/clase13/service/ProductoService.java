package com.app.back.springbootmongotest.clase13.service;

import com.app.back.springbootmongotest.clase13.exception.ProductoNoEncontradoException;
import com.app.back.springbootmongotest.clase13.model.Producto;
import com.app.back.springbootmongotest.clase13.repository.ProductoRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

/**
 * Capa de servicio: contiene la logica de negocio y usa el repositorio para persistir.
 *
 * @Service registra esta clase como un "bean" de Spring, para poder inyectarla
 * en el controlador (y en los tests con @Autowired).
 */
@Service
public class ProductoService {

    private final ProductoRepository productoRepository;

    // Inyeccion por constructor: Spring nos pasa el repositorio automaticamente.
    public ProductoService(ProductoRepository productoRepository) {
        this.productoRepository = productoRepository;
    }

    public Producto save(Producto producto) {
        // Regla de negocio simple para tener algo que probar en los tests unitarios.
        if (producto.getPrecio() == null || producto.getPrecio() < 0) {
            throw new IllegalArgumentException("El precio no puede ser nulo ni negativo");
        }
        return productoRepository.save(producto);
    }

    public List<Producto> findAll() {
        return productoRepository.findAll();
    }

    public Producto findById(String id) {
        return productoRepository.findById(id)
                .orElseThrow(() -> new ProductoNoEncontradoException(id));
    }

    public Optional<Producto> buscarPorNombre(String nombre) {
        return productoRepository.findByNombre(nombre);
    }
}
