package com.app.back.springbootmongotest.clase13.webmvc;

import com.app.back.springbootmongotest.clase13.controller.ProductoController;
import com.app.back.springbootmongotest.clase13.exception.ProductoNoEncontradoException;
import com.app.back.springbootmongotest.clase13.model.Producto;
import com.app.back.springbootmongotest.clase13.service.ProductoService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * TEST DE LA CAPA WEB con @WebMvcTest.
 *
 * @WebMvcTest(ProductoController.class)
 *   - Levanta SOLO la parte web de Spring MVC (controladores, conversion JSON, manejo de errores).
 *   - El parametro indica QUE controlador cargar. Sin parametro carga todos los controladores.
 *   - NO carga servicios ni repositorios, y NO necesita MongoDB ni Docker.
 *   - Configura automaticamente un MockMvc listo para usar.
 *
 * Como el servicio no se carga, hay que darle uno "falso" al controlador con @MockitoBean.
 */
@WebMvcTest(ProductoController.class)
class ProductoControllerWebMvcTest {

    // MockMvc simula peticiones HTTP sin levantar un servidor real (sin abrir puertos).
    @Autowired
    MockMvc mockMvc;

    // @MockitoBean: crea un mock de Mockito y lo registra como bean en el contexto de Spring.
    // Asi el ProductoController recibe este mock en su constructor.
    // (Reemplaza al antiguo @MockBean, que ya no existe en Spring Boot 4).
    @MockitoBean
    ProductoService productoService;

    @Test
    void debeCrearProducto() throws Exception {
        Producto guardado = new Producto("Mouse", 3500.0);
        guardado.setId("abc123");
        when(productoService.save(any(Producto.class))).thenReturn(guardado);

        String json = "{\"nombre\":\"Mouse\",\"precio\":3500}";

        // perform(...)      -> ejecuta la peticion simulada.
        // post("/productos") -> metodo HTTP y ruta.
        // contentType(...)  -> avisa que el cuerpo es JSON (header Content-Type).
        // content(json)     -> el cuerpo de la peticion.
        // andExpect(...)    -> verificaciones sobre la respuesta.
        mockMvc.perform(post("/productos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isCreated())                       // HTTP 201
                .andExpect(jsonPath("$.id").value("abc123"))           // $ = raiz del JSON
                .andExpect(jsonPath("$.nombre").value("Mouse"));
    }

    @Test
    void debeResponder404SiElProductoNoExiste() throws Exception {
        when(productoService.findById("xyz")).thenThrow(new ProductoNoEncontradoException("xyz"));

        mockMvc.perform(get("/productos/{id}", "xyz"))
                .andExpect(status().isNotFound());                     // HTTP 404
    }

    @Test
    void debeBuscarPorNombre() throws Exception {
        when(productoService.buscarPorNombre("Mouse"))
                .thenReturn(Optional.of(new Producto("Mouse", 3500.0)));

        // param("nombre", "Mouse") arma la URL /productos/buscar?nombre=Mouse
        mockMvc.perform(get("/productos/buscar").param("nombre", "Mouse"))
                .andExpect(status().isOk())                            // HTTP 200
                .andExpect(jsonPath("$.precio").value(3500.0));
    }
}
