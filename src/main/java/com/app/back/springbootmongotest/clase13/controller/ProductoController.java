package com.app.back.springbootmongotest.clase13.controller;

import com.app.back.springbootmongotest.clase13.model.Producto;
import com.app.back.springbootmongotest.clase13.service.ProductoService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Controlador REST que expone los productos por HTTP.
 *
 * @RestController     -> los metodos devuelven datos (JSON), no vistas HTML.
 * @RequestMapping(...) -> todas las rutas de esta clase empiezan con /productos.
 */
@RestController
@RequestMapping("/productos")
public class ProductoController {

    private final ProductoService productoService;

    public ProductoController(ProductoService productoService) {
        this.productoService = productoService;
    }

    // POST /productos  -> crea un producto. @RequestBody convierte el JSON recibido en un Producto.
    // @ResponseStatus(HttpStatus.CREATED) hace que la respuesta sea 201 en lugar de 200.
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Producto crear(@RequestBody Producto producto) {
        return productoService.save(producto);
    }

    // GET /productos -> lista todos los productos.
    @GetMapping
    public List<Producto> listar() {
        return productoService.findAll();
    }

    // GET /productos/{id} -> @PathVariable toma el valor {id} de la URL.
    // Si no existe, el servicio lanza ProductoNoEncontradoException -> 404.
    @GetMapping("/{id}")
    public Producto obtener(@PathVariable String id) {
        return productoService.findById(id);
    }

    // GET /productos/buscar?nombre=Mouse -> @RequestParam toma el parametro "nombre" de la URL.
    // ResponseEntity nos deja elegir el status: 200 con el producto, o 404 si no esta.
    @GetMapping("/buscar")
    public ResponseEntity<Producto> buscarPorNombre(@RequestParam String nombre) {
        return productoService.buscarPorNombre(nombre)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}
