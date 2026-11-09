package tests;

import domain.*;
import static org.junit.Assert.*;
import org.junit.Test;

/**
 * Pruebas del partido y de la fachada DOPOSoccer.
 */
public class PartidoTest {

    /**
     * El partido empieza 0 - 0 y sin ganador.
     */
    @Test
    public void deberiaIniciarCeroACero() throws DOPOSoccerException {
        DOPOSoccer juego = new DOPOSoccer();
        juego.iniciarPartido(Camerun.PAIS, Nigeria.PAIS, DOPOSoccer.POR_GOLES, 3);
        Partido p = juego.getPartido();
        assertEquals(0, p.getJugador(1).getGoles());
        assertEquals(0, p.getJugador(2).getGoles());
        assertNull(p.getGanador());
        assertFalse(p.estaTerminado());
    }

    /**
     * En partido a 3 goles, quien llegue a 3 gana y recibe 100 puntos; el otro 25.
     */
    @Test
    public void deberiaGanarPorGolesYRepartirPuntos() throws DOPOSoccerException {
        Partido p = new PartidoPorGoles(CoreaDelSur.PAIS, EstadosUnidos.PAIS, 3);
        for (int i = 0; i < 3; i++) p.getJugador(1).anotarGol();
        p.actualizar(0.016);
        assertTrue(p.estaTerminado());
        assertSame(p.getJugador(1), p.getGanador());
        assertEquals(100, p.getJugador(1).getPuntos());
        assertEquals(25, p.getJugador(2).getPuntos());
    }

    /**
     * Si el tiempo se acaba con empate, entra en muerte súbita y el siguiente gol gana.
     */
    @Test
    public void deberiaEntrarEnMuerteSubita() throws DOPOSoccerException {
        PartidoPorTiempo p = new PartidoPorTiempo(Camerun.PAIS, Nigeria.PAIS, 1);
        for (int i = 0; i < 62; i++) p.actualizar(1);
        assertFalse(p.estaTerminado());
        assertTrue(p.estaEnMuerteSubita());
        p.getJugador(2).anotarGol();
        p.actualizar(0.016);
        assertTrue(p.estaTerminado());
        assertSame(p.getJugador(2), p.getGanador());
    }

    /**
     * Un balón dentro del arco derecho es gol del jugador 1 y todo vuelve al centro.
     */
    @Test
    public void deberiaContarGolYReiniciarPosiciones() throws DOPOSoccerException {
        Partido p = new PartidoPorGoles(Camerun.PAIS, Nigeria.PAIS, 5);
        Cancha c = p.getCancha();
        p.getBalon().reiniciar(c.getAncho() - 20, c.getNivelPiso() - 20);
        p.verificarGol();
        assertEquals(1, p.getJugador(1).getGoles());
        assertEquals(c.getAncho() / 2.0, p.getBalon().getX(), 0.001);
    }

    /**
     * En pausa el tiempo no avanza.
     */
    @Test
    public void noDeberiaAvanzarEnPausa() throws DOPOSoccerException {
        DOPOSoccer juego = new DOPOSoccer();
        juego.iniciarPartido(Camerun.PAIS, Nigeria.PAIS, DOPOSoccer.POR_TIEMPO, 2);
        juego.pausar();
        juego.actualizar(5);
        assertEquals(0, juego.getPartido().getTiempoJugado(), 0.001);
    }

    /**
     * No se puede iniciar un partido con 0 goles.
     */
    @Test(expected = DOPOSoccerException.class)
    public void deberiaLanzarExcepcionConCeroGoles() throws DOPOSoccerException {
        new DOPOSoccer().iniciarPartido(Camerun.PAIS, Nigeria.PAIS, DOPOSoccer.POR_GOLES, 0);
    }

    /**
     * No se puede iniciar un partido con un país que no existe.
     */
    @Test
    public void deberiaLanzarExcepcionConPaisInvalido() {
        try {
            new DOPOSoccer().iniciarPartido("Brasil", Nigeria.PAIS, DOPOSoccer.POR_GOLES, 3);
            fail("Debía lanzar la excepción");
        } catch (DOPOSoccerException e) {
            assertEquals(DOPOSoccerException.PAIS_INVALIDO, e.getMessage());
        }
    }

    /**
     * No se puede mover un jugador que no existe.
     */
    @Test(expected = DOPOSoccerException.class)
    public void deberiaLanzarExcepcionConJugadorInvalido() throws DOPOSoccerException {
        DOPOSoccer juego = new DOPOSoccer();
        juego.iniciarPartido(Camerun.PAIS, Nigeria.PAIS, DOPOSoccer.POR_GOLES, 3);
        juego.moverJugador(3, "derecha");
    }

    /**
     * No se puede mover en una dirección que no existe.
     */
    @Test(expected = DOPOSoccerException.class)
    public void deberiaLanzarExcepcionConDireccionInvalida() throws DOPOSoccerException {
        DOPOSoccer juego = new DOPOSoccer();
        juego.iniciarPartido(Camerun.PAIS, Nigeria.PAIS, DOPOSoccer.POR_GOLES, 3);
        juego.moverJugador(1, "arriba");
    }

    /**
     * Sin partido en curso no se puede jugar.
     */
    @Test(expected = DOPOSoccerException.class)
    public void deberiaLanzarExcepcionSinPartido() throws DOPOSoccerException {
        new DOPOSoccer().patearJugador(1);
    }
}
