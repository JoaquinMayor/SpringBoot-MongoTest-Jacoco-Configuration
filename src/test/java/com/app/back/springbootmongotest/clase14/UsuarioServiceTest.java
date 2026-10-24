package com.app.back.springbootmongotest.clase14;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;

/**
 * TESTS UNITARIOS de un servicio con una dependencia externa, usando Mockito.
 *
 * El Notificador "de verdad" enviaria emails; en un test unitario eso no se quiere.
 * Por eso lo reemplazamos por un mock: un objeto simulado que no hace nada,
 * pero que RECUERDA como fue llamado para poder verificarlo despues.
 */
class UsuarioServiceTest {

    private Notificador notificador;
    private UsuarioService service;

    @BeforeEach
    void setUp() {
        // mock(Clase.class): crea el objeto simulado.
        notificador = mock(Notificador.class);
        // Aislamiento de dependencias: el servicio recibe el mock por el constructor.
        service = new UsuarioService(notificador);
    }

    // ------------------------------------------------------------------
    // assertTrue / assertFalse
    // ------------------------------------------------------------------

    // Test tipico que sugiere Copilot: una entrada "verdadera" y una "falsa".
    @Test
    void testEsMayorDeEdad() {
        assertTrue(service.esMayorDeEdad(20));
        assertFalse(service.esMayorDeEdad(17));
    }

    // Caso limite: justo en el borde de la condicion (edad >= 18). Copilot muchas veces lo omite.
    @Test
    void deberiaConsiderarMayorDeEdadAQuienTieneExactamente18() {
        assertTrue(service.esMayorDeEdad(UsuarioService.EDAD_MINIMA));
    }

    // Test negativo con un nombre descriptivo que explica la regla de negocio.
    @Test
    void deberiaRechazarEdadNegativa() {
        assertThrows(IllegalArgumentException.class, () -> service.esMayorDeEdad(-1));
    }

    // ------------------------------------------------------------------
    // assertNull / assertNotNull / assertEquals / assertAll
    // ------------------------------------------------------------------

    @Test
    void normalizarEmailDevuelveNullSiEsNulo() {
        assertNull(service.normalizarEmail(null));
    }

    @Test
    void normalizarEmailDevuelveNullSiEstaEnBlanco() {
        assertNull(service.normalizarEmail("   "));
    }

    @Test
    void normalizarEmailQuitaEspaciosYPasaAMinusculas() {
        String resultado = service.normalizarEmail("  Persona@Banco.COM ");

        // assertAll ejecuta TODAS las aserciones aunque alguna falle, y reporta todos los errores juntos.
        // Util cuando varias aserciones verifican el MISMO comportamiento (mantener el foco).
        assertAll(
                () -> assertNotNull(resultado),
                () -> assertEquals("persona@banco.com", resultado)
        );
    }

    // ------------------------------------------------------------------
    // Mockito: verify
    // ------------------------------------------------------------------

    @Test
    void testEnviarAlerta() {
        service.enviarAlerta("persona@banco.com");

        // verify(mock).metodo(parametros): comprueba que se llamo a ese metodo con ESOS parametros.
        verify(notificador).enviar("persona@banco.com", "Alerta enviada");
    }

    @Test
    void enviarAlertaUsaElEmailNormalizado() {
        service.enviarAlerta("  PERSONA@banco.com");

        verify(notificador).enviar("persona@banco.com", "Alerta enviada");
    }

    @Test
    void noDeberiaEnviarAlertaSiElEmailEstaVacio() {
        assertThrows(IllegalArgumentException.class, () -> service.enviarAlerta(""));

        // verifyNoInteractions: el mock no fue usado en absoluto (no se envio nada).
        verifyNoInteractions(notificador);
    }
}
