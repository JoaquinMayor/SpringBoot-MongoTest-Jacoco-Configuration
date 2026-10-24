package com.app.back.springbootmongotest.clase13.integration;

import com.app.back.springbootmongotest.clase13.model.Producto;
import com.app.back.springbootmongotest.clase13.repository.ProductoRepository;
import com.app.back.springbootmongotest.clase13.service.ProductoService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.mongodb.MongoDBContainer;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * PRUEBA DE INTEGRACION con @SpringBootTest: servicio + repositorio + MongoDB real.
 *
 * @SpringBootTest
 *   - Carga TODO el contexto de Spring Boot (igual que al arrancar la aplicacion).
 *   - Aca NO hay mocks: el servicio usa el repositorio real, que habla con un Mongo real.
 *   - Verifica que los componentes COLABOREN correctamente entre si.
 *
 * El sufijo "IT" (Integration Test) hace que lo ejecute el plugin Failsafe con "mvn verify",
 * separado de los tests rapidos que corren con "mvn test".
 */
@SpringBootTest
@Testcontainers(disabledWithoutDocker = true)
class ProductoServiceIT {

    // Contenedor real de MongoDB compartido por todos los tests de esta clase.
    @Container
    static MongoDBContainer mongoDB = new MongoDBContainer("mongo:7.0");

    // Conecta Spring al contenedor: la URI se conoce recien cuando el contenedor arranco.
    @DynamicPropertySource
    static void mongoProps(DynamicPropertyRegistry registry) {
        registry.add("spring.mongodb.uri", mongoDB::getReplicaSetUrl);
    }

    @Autowired
    ProductoService productoService;

    @Autowired
    ProductoRepository productoRepository;

    @BeforeEach
    void limpiarBase() {
        productoRepository.deleteAll();
    }

    // Recorrido completo: creacion -> guardado (servicio -> repositorio -> Mongo) -> verificacion.
    @Test
    void debeGuardarProducto() {
        Producto p = new Producto("Mouse", 3500.0);

        Producto guardado = productoService.save(p);

        assertNotNull(guardado.getId());
        // Verificamos directo en la base que el documento realmente quedo persistido.
        assertTrue(productoRepository.findById(guardado.getId()).isPresent());
    }

    @Test
    void debeBuscarPorNombreDesdeElServicio() {
        productoService.save(new Producto("Auriculares", 12000.0));

        Producto encontrado = productoService.buscarPorNombre("Auriculares").orElseThrow();

        assertEquals(12000.0, encontrado.getPrecio());
    }

    @Test
    void laReglaDeNegocioSigueFuncionandoIntegrada() {
        assertThrows(IllegalArgumentException.class,
                () -> productoService.save(new Producto("Gratis", -1.0)));
        // Nada se guardo en la base.
        assertEquals(0, productoRepository.count());
    }
}
