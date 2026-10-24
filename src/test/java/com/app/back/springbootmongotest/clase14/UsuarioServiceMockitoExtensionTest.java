package com.app.back.springbootmongotest.clase14;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

/**
 * El MISMO tipo de test que UsuarioServiceTest, pero con las anotaciones de Mockito.
 * Es el estilo que Copilot suele generar, asi que conviene saber leerlo.
 *
 * @ExtendWith(MockitoExtension.class) -> activa Mockito en JUnit 5 (procesa @Mock e @InjectMocks).
 * @Mock                               -> equivale a mock(Notificador.class).
 * @InjectMocks                        -> crea el UsuarioService y le pasa los @Mock por el constructor
 *                                        (equivale a new UsuarioService(notificador)).
 */
@ExtendWith(MockitoExtension.class)
class UsuarioServiceMockitoExtensionTest {

    @Mock
    Notificador notificador;

    @InjectMocks
    UsuarioService service;

    @Test
    void enviarAlertaLlamaUnaSolaVezAlNotificador() {
        service.enviarAlerta("persona@banco.com");

        // times(1): se llamo exactamente una vez.
        verify(notificador, times(1)).enviar("persona@banco.com", "Alerta enviada");
    }

    @Test
    void esMayorDeEdadNoUsaElNotificador() {
        service.esMayorDeEdad(30);

        // never() + anyString(): no se llamo a enviar con NINGUN valor.
        verify(notificador, never()).enviar(anyString(), anyString());
    }
}
