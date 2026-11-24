package tests;

import domain.*;
import presentation.CicloJuego;
import presentation.VistaPrincipal;
import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;

/**
 * Pruebas de la física de los choques: el balón no atraviesa la cabeza, el cuerpo ni el pie
 * de los cabezones, ni el travesaño aunque vaya muy rápido. También prueba la cámara lenta
 * de los tiros de poder (el dominio cuenta los tiros y el ciclo del juego va más despacio).
 */
public class FisicaTest {

    private Cancha cancha;
    private Balon balon;
    private Jugador jugador;
    private double suelo;

    /**
     * Crea una cancha, un balón y un jugador de Camerún parado en el piso, mirando a la derecha.
     */
    @Before
    public void setUp() {
        cancha = new Cancha(ConstantesJuego.ANCHO_CANCHA, ConstantesJuego.ALTO_CANCHA);
        suelo = cancha.getNivelPiso() - Jugador.RADIO * 2;
        jugador = new Jugador(1, Pais.CAMERUN.crearPersonaje(), 640, suelo);
        balon = new Balon(100, 100);
    }

    /**
     * Mueve el balón y lo hace chocar con el jugador durante varios pasos.
     * @param pasos cantidad de pasos de 1/60 s
     */
    private void simular(int pasos) {
        for (int i = 0; i < pasos; i++) {
            balon.mover(AyudantePruebas.PASO, cancha);
            jugador.interactuar(balon, null);
        }
    }

    /**
     * Un balón que va rodando hacia el cuerpo (debajo de la cabeza) rebota y no lo atraviesa.
     */
    @Test
    public void noDeberiaAtravesarElCuerpo() {
        balon.reiniciar(560, cancha.getNivelPiso() - Balon.RADIO);
        balon.recibirPatada(700, 0);
        for (int i = 0; i < 60; i++) {
            simular(1);
            assertTrue("el balón quedó dentro o detrás del cuerpo", balon.getX() < jugador.getX());
        }
        assertTrue(balon.getVelocidadX() < 0);
    }

    /**
     * Un balón que llega por detrás también choca con la espalda.
     */
    @Test
    public void noDeberiaAtravesarLaEspalda() {
        balon.reiniciar(720, cancha.getNivelPiso() - Balon.RADIO);
        balon.recibirPatada(-700, 0);
        for (int i = 0; i < 60; i++) {
            simular(1);
            assertTrue(balon.getX() > jugador.getX());
        }
        assertTrue(balon.getVelocidadX() > 0);
    }

    /**
     * Un balón que cae sobre el pie rebota hacia arriba y no se mete en él.
     */
    @Test
    public void noDeberiaAtravesarElPie() {
        double puntaPie = jugador.getX() + Jugador.RADIO / 2.0 + Jugador.LARGO_PIE;
        double xBalon = puntaPie - 2;
        balon.reiniciar(xBalon, cancha.getNivelPiso() - 120);
        balon.recibirPatada(0, 400);
        double topePie = cancha.getNivelPiso() - Jugador.ALTO_PIE;
        boolean reboto = false;
        for (int i = 0; i < 40; i++) {
            simular(1);
            if (Math.abs(balon.getX() - xBalon) < 1) {
                assertTrue("el balón se metió en el pie", balon.getY() + Balon.RADIO <= topePie + 1);
            }
            if (balon.getVelocidadY() < 0) reboto = true;
        }
        assertTrue(reboto);
    }

    /**
     * Si el jugador camina hacia un balón quieto, lo empuja: el balón nunca queda dentro del cuerpo.
     */
    @Test
    public void deberiaEmpujarElBalonAlCaminar() {
        balon.reiniciar(690, cancha.getNivelPiso() - Balon.RADIO);
        for (int i = 0; i < 30; i++) {
            jugador.moverDerecha();
            jugador.mover(AyudantePruebas.PASO, cancha);
            simular(1);
            assertTrue(balon.getX() - Balon.RADIO >= jugador.getX() + Jugador.RADIO / 2.0 - 0.001);
        }
    }

    /**
     * La cabeza saca el balón afuera: después del cabezazo no queda montado sobre ella.
     */
    @Test
    public void deberiaSacarElBalonDeLaCabeza() {
        balon.reiniciar(jugador.getX() + 5, jugador.getY() - 20);
        jugador.interactuar(balon, null);
        double distancia = Math.hypot(balon.getX() - jugador.getX(), balon.getY() - jugador.getY());
        assertTrue(distancia >= Jugador.RADIO + Balon.RADIO - 0.001);
        assertTrue(balon.getVelocidadY() < 0);
    }

    /**
     * Aunque el balón caiga muy rápido, no atraviesa el travesaño (el movimiento va en sub-pasos).
     */
    @Test
    public void noDeberiaAtravesarElTravesanoMuyRapido() {
        Arco arco = cancha.getArcoIzquierdo();
        balon.reiniciar(arco.getX() + arco.getAncho() / 2.0, arco.getY() - 40);
        balon.recibirPatada(0, 4800);
        balon.mover(AyudantePruebas.PASO, cancha);
        assertTrue(balon.getY() < arco.getY());
        assertTrue(balon.getVelocidadY() < 0);
    }

    /**
     * Un tiro muy rápido contra la pared no se sale de la cancha.
     */
    @Test
    public void noDeberiaSalirseDeLaCancha() {
        balon.reiniciar(1200, 300);
        balon.recibirPatada(6000, 0);
        balon.mover(AyudantePruebas.PASO, cancha);
        assertTrue(balon.getX() + Balon.RADIO <= cancha.getAncho());
    }

    /**
     * El pie sigue sirviendo para patear: el balón al alcance sale disparado.
     */
    @Test
    public void deberiaSeguirPateando() {
        Jugador rival = new Jugador(2, Pais.NIGERIA.crearPersonaje(), 1000, suelo);
        balon.reiniciar(jugador.getX() + 45, cancha.getNivelPiso() - Balon.RADIO);
        jugador.patear(balon, rival);
        assertTrue(balon.getVelocidadX() > 0);
        assertTrue(balon.getVelocidadY() < 0);
    }

    /**
     * Cada tiro de poder se cuenta en el balón y en la fachada (la interfaz lo usa para la cámara lenta).
     * @throws DOPOSoccerException si no hay partido
     */
    @Test
    public void deberiaContarLosTirosDePoder() throws DOPOSoccerException {
        DOPOSoccer juego = DOPOSoccer.getInstance();
        juego.iniciarPartido(Pais.CAMERUN, Pais.NIGERIA, Partido.ModoJuego.POR_TIEMPO, 3);
        assertEquals(0, juego.getTirosDePoder());
        balon.lanzarTiro(jugador, Camerun.NOMBRE_TIRO, 1000, 0, false, false);
        assertEquals(1, balon.getTirosLanzados());
        AyudantePruebas.avanzar(juego, Jugador.SEGUNDOS_BARRA + 0.1);
        juego.activarPoder(1);
        for (int i = 0; i < 2000 && juego.getTirosDePoder() == 0; i++) {
            juego.moverJugador(1, Direccion.DERECHA);
            juego.patearJugador(1);
            juego.actualizar(AyudantePruebas.PASO);
        }
        assertEquals(1, juego.getTirosDePoder());
    }

    /**
     * Durante la cámara lenta el juego avanza al 30 % y después vuelve a la velocidad normal.
     */
    @Test
    public void deberiaHacerCamaraLenta() {
        CicloJuego ciclo = new CicloJuego(new VistaVacia());
        assertEquals(0.1, ciclo.tiempoDeJuego(0.1), 1e-9);
        ciclo.iniciarCamaraLenta();
        assertEquals(1, ciclo.getCamaraLenta(), 1e-9);
        assertEquals(0.1 * CicloJuego.FACTOR_CAMARA_LENTA, ciclo.tiempoDeJuego(0.1), 1e-9);
        ciclo.tiempoDeJuego(CicloJuego.DURACION_CAMARA_LENTA);
        assertEquals(0, ciclo.getCamaraLenta(), 1e-9);
        assertEquals(0.1, ciclo.tiempoDeJuego(0.1), 1e-9);
    }

    /**
     * Vista que no hace nada, para probar el ciclo del juego sin ventana.
     */
    private static class VistaVacia implements VistaPrincipal {
        /** No hace nada. */
        public void mostrarInicio() { }
        /** No hace nada. */
        public void mostrarInstrucciones() { }
        /** No hace nada. */
        public void mostrarConfiguracion() { }
        /** No hace nada. */
        public void confirmarConfiguracion() { }
        /** No hace nada. */
        public void iniciarPartido() { }
        /**
         * No hace nada.
         * @param segundos duración del paso
         */
        public void pasoDeJuego(double segundos) { }
        /** No hace nada. */
        public void refrescar() { }
        /** No hace nada. */
        public void detenerCiclo() { }
    }
}
