package com.app.back.springbootmongotest.clase14;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * TESTS UNITARIOS CON JUNIT 5 de una clase de logica pura.
 *
 * - El test esta en el MISMO package que Calculadora (com.app.back.springbootmongotest.clase14),
 *   pero en src/test/java. Por eso puede usar el metodo package-private validarExponente().
 * - No usa Spring ni Mockito: Calculadora no tiene dependencias.
 * - Cada test sigue el patron AAA: Arrange (preparar), Act (ejecutar), Assert (verificar).
 */
class CalculadoraTest {

    private Calculadora calc;

    // @BeforeEach: se ejecuta antes de CADA test. Inicializa los objetos comunes
    // y garantiza que ningun test dependa de lo que hizo otro.
    @BeforeEach
    void setUp() {
        calc = new Calculadora();
    }

    /*
     * Ejemplo del flujo con Copilot:
     *   1) Nosotros escribimos solo la firma: @Test void testSumar() { }
     *   2) Con el cursor dentro del metodo, Copilot sugiere:  assertEquals(5, calc.sumar(2, 3));
     *   3) Revisamos que el valor esperado (5) sea correcto y aceptamos con Tab.
     */
    @Test
    void testSumar() {
        assertEquals(5, calc.sumar(2, 3));
    }

    // Caso que Copilot suele NO sugerir: numeros negativos. Lo agregamos nosotros.
    @Test
    void testSumaConNumerosNegativos() {
        // Arrange
        int a = -4;
        int b = -6;

        // Act
        int resultado = calc.sumar(a, b);

        // Assert: assertEquals(esperado, actual). El PRIMER parametro es el valor esperado.
        assertEquals(-10, resultado);
    }

    @Test
    void testRestar() {
        assertEquals(1, calc.restar(3, 2));
    }

    @Test
    void testRestarConCeros() {
        assertEquals(0, calc.restar(0, 0));
        assertEquals(7, calc.restar(7, 0));
    }

    // @DisplayName: nombre legible que se muestra en el IDE y en los reportes, en lugar del nombre del metodo.
    @Test
    @DisplayName("Restar un numero mayor da un resultado negativo")
    void restarNumeroMayorDaNegativo() {
        assertEquals(-5, calc.restar(5, 10));
    }

    @Test
    void testDividir() {
        assertEquals(4, calc.dividir(20, 5));
    }

    /*
     * TEST NEGATIVO: no solo probamos lo que "funciona bien", tambien los errores esperados.
     * assertThrows(TipoDeExcepcion.class, () -> codigoQueDebeFallar)
     *   - El segundo parametro es una lambda: JUnit la ejecuta y verifica que lance esa excepcion.
     *   - Devuelve la excepcion, asi podemos revisar tambien el mensaje.
     */
    @Test
    void testDivisionPorCero() {
        ArithmeticException error = assertThrows(ArithmeticException.class, () -> calc.dividir(10, 0));

        assertEquals("No se puede dividir por cero", error.getMessage());
    }

    /*
     * @ParameterizedTest: el MISMO test se ejecuta una vez por cada fila de datos.
     * @CsvSource: cada string es una fila "base, exponente, esperado".
     * Es una forma rapida de subir la cobertura de ramas sin copiar y pegar tests.
     */
    @ParameterizedTest(name = "{0} elevado a {1} = {2}")
    @CsvSource({
            "2, 0, 1",
            "2, 1, 2",
            "2, 10, 1024",
            "-3, 3, -27"
    })
    void testPotencia(int base, int exponente, int esperado) {
        assertEquals(esperado, calc.potencia(base, exponente));
    }

    @Test
    void testPotenciaConExponenteNegativoLanzaExcepcion() {
        assertThrows(IllegalArgumentException.class, () -> calc.potencia(2, -1));
    }

    // Prueba directa de un metodo package-private: posible porque el test comparte el package.
    @Test
    void validarExponenteAceptaCero() {
        assertDoesNotThrow(() -> calc.validarExponente(0));
    }
}
