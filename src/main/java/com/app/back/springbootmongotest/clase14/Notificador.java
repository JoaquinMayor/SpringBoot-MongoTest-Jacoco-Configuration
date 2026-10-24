package com.app.back.springbootmongotest.clase14;

/**
 * Dependencia externa del UsuarioService (por ejemplo, un servicio de email o SMS).
 *
 * Es una interfaz: en produccion se usa NotificadorConsola, y en los tests unitarios
 * la reemplazamos por un mock de Mockito para no enviar nada de verdad.
 */
public interface Notificador {

    void enviar(String destinatario, String mensaje);
}
