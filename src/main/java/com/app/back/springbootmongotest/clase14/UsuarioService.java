package com.app.back.springbootmongotest.clase14;

import org.springframework.stereotype.Service;

/**
 * Servicio con logica de negocio y una dependencia externa (Notificador).
 *
 * Aunque es un @Service de Spring, para testearlo de forma UNITARIA no hace falta Spring:
 * alcanza con crearlo con "new" y pasarle un mock del Notificador por el constructor.
 */
@Service
public class UsuarioService {

    // Constante con nombre en lugar de un "numero magico" 18 suelto en el codigo.
    static final int EDAD_MINIMA = 18;

    private final Notificador notificador;

    public UsuarioService(Notificador notificador) {
        this.notificador = notificador;
    }

    public boolean esMayorDeEdad(int edad) {
        if (edad < 0) {
            throw new IllegalArgumentException("La edad no puede ser negativa");
        }
        return edad >= EDAD_MINIMA;
    }

    // Devuelve el email sin espacios y en minusculas, o null si viene vacio.
    public String normalizarEmail(String email) {
        if (email == null || email.isBlank()) {
            return null;
        }
        return email.trim().toLowerCase();
    }

    public void enviarAlerta(String email) {
        String destinatario = normalizarEmail(email);
        if (destinatario == null) {
            throw new IllegalArgumentException("El email es obligatorio");
        }
        notificador.enviar(destinatario, "Alerta enviada");
    }
}
