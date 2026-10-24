package com.app.back.springbootmongotest.clase14;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * Implementacion "real" del Notificador: solo escribe en el log.
 *
 * @Component la registra como bean, para que Spring se la inyecte a UsuarioService
 * cuando la aplicacion arranca. En los tests unitarios NO se usa.
 */
@Component
public class NotificadorConsola implements Notificador {

    private static final Logger log = LoggerFactory.getLogger(NotificadorConsola.class);

    @Override
    public void enviar(String destinatario, String mensaje) {
        log.info("Enviando a {}: {}", destinatario, mensaje);
    }
}
