package tests;

import domain.*;
import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;

/**
 * Pruebas de las estadísticas del jugador.
 */
public class EstadisticasTest {

    private Estadisticas estadisticas;

    /**
     * Crea estadísticas nuevas antes de cada prueba.
     */
    @Before
    public void setUp() {
        estadisticas = new Estadisticas();
    }

    /**
     * Con survival 1 la vida máxima es 100.
     */
    @Test
    public void deberiaIniciarConVida100() {
        assertEquals(100, estadisticas.getVidaMax(), 0.001);
    }

    /**
     * Con survival 3 la vida máxima es 120 (ejemplo del enunciado).
     */
    @Test
    public void deberiaCalcularVidaConSurvival3() throws DOPOSoccerException {
        estadisticas.mejorar("survival");
        estadisticas.mejorar("survival");
        assertEquals(120, estadisticas.getVidaMax(), 0.001);
    }

    /**
     * Con kick 5 el multiplicador es 1.4 (ejemplo del enunciado).
     */
    @Test
    public void deberiaCalcularMultiplicadorKick5() throws DOPOSoccerException {
        for (int i = 0; i < 4; i++) estadisticas.mejorar("kick");
        assertEquals(1.4, estadisticas.getMultiplicadorKick(), 0.001);
    }

    /**
     * Una estadística no puede pasar de 10.
     */
    @Test
    public void noDeberiaMejorarPasandoDe10() throws DOPOSoccerException {
        for (int i = 0; i < 9; i++) estadisticas.mejorar("speed");
        try {
            estadisticas.mejorar("speed");
            fail("Debía lanzar la excepción");
        } catch (DOPOSoccerException e) {
            assertEquals(DOPOSoccerException.ESTADISTICA_MAXIMA, e.getMessage());
        }
        assertEquals(10, estadisticas.getSpeed());
    }

    /**
     * No se puede mejorar una estadística que no existe.
     */
    @Test(expected = DOPOSoccerException.class)
    public void noDeberiaMejorarEstadisticaInexistente() throws DOPOSoccerException {
        estadisticas.mejorar("fuerza");
    }
}
