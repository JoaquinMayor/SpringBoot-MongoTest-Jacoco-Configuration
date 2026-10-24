package com.app.back.springbootmongotest.clase13.unit;

import com.app.back.springbootmongotest.clase13.exception.ProductoNoEncontradoException;
import com.app.back.springbootmongotest.clase13.model.Producto;
import com.app.back.springbootmongotest.clase13.repository.ProductoRepository;
import com.app.back.springbootmongotest.clase13.service.ProductoService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * TEST UNITARIO (solo @Test, sin Spring).
 *
 * - NO se levanta Spring, NO hay base de datos, NO hay Docker.
 * - El repositorio se reemplaza por un "mock" (objeto falso) de Mockito.
 * - Sirve para probar la LOGICA del servicio de forma aislada y muy rapida.
 *
 * Lo incluimos para comparar contra las pruebas de integracion del resto del proyecto.
 */
class ProductoServiceTest {

    private ProductoRepository productoRepository;
    private ProductoService productoService;

    // @BeforeEach: se ejecuta antes de CADA test, asi cada uno arranca "limpio".
    @BeforeEach
    void setUp() {
        // Mockito.mock(...) crea una implementacion falsa: no hace nada salvo lo que le indiquemos.
        productoRepository = Mockito.mock(ProductoRepository.class);
        // Creamos el servicio "a mano" con new: no hay Spring que lo inyecte.
        productoService = new ProductoService(productoRepository);
    }

    @Test
    void debeGuardarCuandoElPrecioEsValido() {
        Producto mouse = new Producto("Mouse", 3500.0);
        // when(...).thenReturn(...): "cuando llamen a save con cualquier Producto, devolve 'mouse'".
        when(productoRepository.save(any(Producto.class))).thenReturn(mouse);

        Producto guardado = productoService.save(mouse);

        assertEquals("Mouse", guardado.getNombre());
        // verify(...): comprueba que el servicio realmente llamo al repositorio.
        verify(productoRepository).save(mouse);
    }

    @Test
    void noDebeGuardarConPrecioNegativo() {
        Producto invalido = new Producto("Teclado", -10.0);

        // assertThrows: el test pasa solo si se lanza la excepcion indicada.
        assertThrows(IllegalArgumentException.class, () -> productoService.save(invalido));
        // never(): el repositorio NO debe haberse usado.
        verify(productoRepository, never()).save(any());
    }

    @Test
    void debeLanzarExcepcionSiNoExisteElId() {
        when(productoRepository.findById("no-existe")).thenReturn(Optional.empty());

        assertThrows(ProductoNoEncontradoException.class, () -> productoService.findById("no-existe"));
    }
}
