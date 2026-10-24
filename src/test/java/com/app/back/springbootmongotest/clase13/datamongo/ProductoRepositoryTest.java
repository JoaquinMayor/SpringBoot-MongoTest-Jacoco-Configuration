package com.app.back.springbootmongotest.clase13.datamongo;

import com.app.back.springbootmongotest.clase13.model.Producto;
import com.app.back.springbootmongotest.clase13.repository.ProductoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.mongodb.test.autoconfigure.DataMongoTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.mongodb.MongoDBContainer;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * TEST DE LA CAPA DE PERSISTENCIA con @DataMongoTest + Testcontainers.
 *
 * @DataMongoTest
 *   - Levanta SOLO lo necesario para MongoDB: repositorios, MongoTemplate y la conexion.
 *   - NO carga controladores, servicios ni el servidor web -> arranca mas rapido que @SpringBootTest.
 *   - Ideal para probar consultas (findByNombre, findByPrecioLessThan, etc.).
 *
 * @Testcontainers(disabledWithoutDocker = true)
 *   - Activa la extension de JUnit que maneja el ciclo de vida de los campos @Container
 *     (los arranca antes de los tests y los detiene al final).
 *   - disabledWithoutDocker = true: si la maquina no tiene Docker corriendo,
 *     los tests se marcan como "omitidos" en lugar de fallar.
 */
@DataMongoTest
@Testcontainers(disabledWithoutDocker = true)
class ProductoRepositoryTest {

    /*
     * @Container: le indica a Testcontainers que este campo es un contenedor a administrar.
     *   - Es "static" -> se crea UNA sola vez para toda la clase (todos los tests lo comparten).
     *     Si no fuera static, se crearia un contenedor nuevo por cada test (mucho mas lento).
     *
     * new MongoDBContainer("mongo:7.0")
     *   - El parametro es la imagen de Docker a usar (nombre:version).
     *   - Conviene fijar la misma version que usamos en produccion, nunca "latest".
     */
    @Container
    static MongoDBContainer mongoDB = new MongoDBContainer("mongo:7.0");

    /*
     * @DynamicPropertySource: agrega propiedades de configuracion "en caliente",
     * cuando el valor recien se conoce al ejecutar (el puerto del contenedor es aleatorio).
     *   - El metodo debe ser static y recibir un DynamicPropertyRegistry.
     *   - registry.add(nombreDePropiedad, proveedorDelValor)
     *       * "spring.mongodb.uri"     -> propiedad que Spring Boot 4 usa para conectarse a Mongo
     *                                     (en Spring Boot 3 se llamaba "spring.data.mongodb.uri").
     *       * mongoDB::getReplicaSetUrl -> referencia a metodo que devuelve algo como
     *                                     mongodb://localhost:54321/test
     *   - Resultado: Spring se conecta al Mongo del contenedor sin tocar application.properties.
     */
    @DynamicPropertySource
    static void mongoProps(DynamicPropertyRegistry registry) {
        registry.add("spring.mongodb.uri", mongoDB::getReplicaSetUrl);
    }

    // @Autowired: Spring inyecta el repositorio real (conectado al Mongo del contenedor).
    @Autowired
    ProductoRepository productoRepository;

    // Como el contenedor se comparte entre tests, limpiamos la coleccion antes de cada uno.
    @BeforeEach
    void limpiarBase() {
        productoRepository.deleteAll();
    }

    @Test
    void guardarYLeerProducto() {
        Producto p = new Producto("Mouse", 3500.0);

        productoRepository.save(p);

        // Mongo genero el id al guardar.
        assertNotNull(p.getId());
        assertTrue(productoRepository.findById(p.getId()).isPresent());
    }

    @Test
    void debeBuscarPorNombre() {
        productoRepository.save(new Producto("Teclado", 8000.0));

        Optional<Producto> encontrado = productoRepository.findByNombre("Teclado");

        assertTrue(encontrado.isPresent());
        assertEquals(8000.0, encontrado.get().getPrecio());
    }

    @Test
    void debeFiltrarPorPrecioMenorA() {
        productoRepository.save(new Producto("Mouse", 3500.0));
        productoRepository.save(new Producto("Monitor", 150000.0));

        List<Producto> baratos = productoRepository.findByPrecioLessThan(10000.0);

        assertEquals(1, baratos.size());
        assertEquals("Mouse", baratos.get(0).getNombre());
    }
}
