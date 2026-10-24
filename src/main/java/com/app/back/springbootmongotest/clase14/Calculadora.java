package com.app.back.springbootmongotest.clase14;

/**
 * Clase simple de logica pura (sin dependencias) para practicar tests unitarios con JUnit 5 y Copilot.
 *
 * No es un bean de Spring: en los tests se crea con "new Calculadora()".
 */
public class Calculadora {

    public int sumar(int a, int b) {
        return a + b;
    }

    public int restar(int a, int b) {
        return a - b;
    }

    // A PROPOSITO no tiene test: al abrir el reporte de JaCoCo se ve en rojo (codigo no cubierto).
    // Ejercicio: escribir el test usando Copilot y volver a generar el reporte.
    public int multiplicar(int a, int b) {
        return a * b;
    }

    public int dividir(int dividendo, int divisor) {
        // Caso de error: lo probamos con assertThrows (test negativo).
        if (divisor == 0) {
            throw new ArithmeticException("No se puede dividir por cero");
        }
        return dividendo / divisor;
    }

    // Tiene un "if" y un "for": JaCoCo cuenta sus ramas (branches) y su complejidad ciclomatica.
    public int potencia(int base, int exponente) {
        validarExponente(exponente);
        int resultado = 1;
        for (int i = 0; i < exponente; i++) {
            resultado *= base;
        }
        return resultado;
    }

    // Metodo "package-private" (sin public/private): solo lo ven las clases del MISMO package.
    // Como el test esta en el mismo package (clase14), puede llamarlo directamente.
    void validarExponente(int exponente) {
        if (exponente < 0) {
            throw new IllegalArgumentException("El exponente no puede ser negativo");
        }
    }
}
