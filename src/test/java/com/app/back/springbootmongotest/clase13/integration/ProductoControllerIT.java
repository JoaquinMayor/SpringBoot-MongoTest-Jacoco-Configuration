package com.app.back.springbootmongotest.clase13.integration;

import com.app.back.springbootmongotest.clase13.model.Producto;
import com.app.back.springbootmongotest.clase13.repository.ProductoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.mongodb.MongoDBContainer;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * PRUEBA DE INTEGRACION DE PUNTA A PUNTA (end-to-end) de un endpoint REST.
 *
 * Recorrido: peticion HTTP -> Controller -> Service -> Repository -> MongoDB (contenedor).
 *
 * @SpringBootTest       -> contexto completo, sin mocks.
 * @AutoConfigureMockMvc -> agrega un MockMvc al contexto completo para simular llamadas HTTP.
 *                          (Con @WebMvcTest viene incluido; con @SpringBootTest hay que pedirlo).
 *
 * Diferencia con ProductoControllerWebMvcTest: alla el servicio era un mock;
 * aca todo es real y el dato termina guardado en MongoDB.
 */
@SpringBootTest
@AutoConfigureMockMvc
@Testcontainers(disabledWithoutDocker = true)
class ProductoControllerIT {

    @Container
    static MongoDBContainer mongoDB = new MongoDBContainer("mongo:7.0");

    @DynamicPropertySource
    static void mongoProps(DynamicPropertyRegistry registry) {
        registry.add("spring.mongodb.uri", mongoDB::getReplicaSetUrl);
    }

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ProductoRepository productoRepository;

    @BeforeEach
    void limpiarBase() {
        productoRepository.deleteAll();
    }

    /*
     * Este test verifica:
     *  - que el endpoint POST /productos existe,
     *  - que recibe los datos correctamente,
     *  - que responde como se espera (201),
     *  - y que el producto quedo REALMENTE guardado en MongoDB.
     */
    @Test
    void debeCrearProducto() throws Exception {
        String json = "{\"nombre\":\"Mouse\",\"precio\":3500}";

        mockMvc.perform(post("/productos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists());

        assertTrue(productoRepository.findByNombre("Mouse").isPresent());
    }

    @Test
    void debeObtenerProductoGuardadoEnLaBase() throws Exception {
        Producto guardado = productoRepository.save(new Producto("Monitor", 150000.0));

        mockMvc.perform(get("/productos/{id}", guardado.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nombre").value("Monitor"));
    }

    @Test
    void debeResponder404SiNoExiste() throws Exception {
        mockMvc.perform(get("/productos/{id}", "id-inexistente"))
                .andExpect(status().isNotFound());
    }
}
