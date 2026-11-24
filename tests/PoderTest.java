package tests;

import domain.*;
import dto.ComandoDibujo;
import java.io.File;
import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

/**
 * Pruebas de los poderes: barra de poder, aura, los cuatro tiros de poder (Aura Azul, Rayo,
 * Tornado e Ilusión), sus efectos y las reglas del enunciado.
 */
public class PoderTest {

    /** Carpeta temporal para guardar partidas. */
    @Rule
    public TemporaryFolder carpeta = new TemporaryFolder();

    private Partido partido;
    private Jugador j1;
    private Jugador j2;

    /**
     * Partido a 5 goles: Camerún (jugador 1) contra Nigeria (jugador 2).
     * @throws DOPOSoccerException si no se puede crear el partido
     */
    @Before
    public void setUp() throws DOPOSoccerException {
        usarPaises(Pais.CAMERUN, Pais.NIGERIA);
    }

    /**
     * Crea un partido nuevo con los países dados.
     * @param pais1 país del jugador 1
     * @param pais2 país del jugador 2
     * @throws DOPOSoccerException si no se puede crear el partido
     */
    private void usarPaises(Pais pais1, Pais pais2) throws DOPOSoccerException {
        partido = new PartidoPorGoles(pais1, pais2, 5);
        j1 = partido.getJugador(1);
        j2 = partido.getJugador(2);
    }

    /**
     * El jugador 1 lanza su tiro de poder desde un punto, a la altura de la cabeza del rival,
     * con el rival parado en x = 700. Avanza hasta que el tiro golpea o pasan 2 segundos.
     */
    private void tiroContraRival() {
        j2.reiniciar(700, j2.getY());
        j1.aumentarBarra(1);
        assertTrue(j1.activarPoder());
        Balon balon = partido.getBalon();
        balon.reiniciar(400, j2.getY());
        balon.serPateado(j1);
        for (int i = 0; i < 120 && j2.getVida() == j2.getVidaMax(); i++) partido.actualizar(AyudantePruebas.PASO);
    }

    /**
     * La barra se llena en 10 segundos y no pasa de llena.
     */
    @Test
    public void laBarraDeberiaLlenarseEnDiezSegundos() {
        AyudantePruebas.avanzar(partido, 5);
        assertEquals(0.5, j1.getBarraPoder(), 0.02);
        AyudantePruebas.avanzar(partido, 6);
        assertEquals(1.0, j1.getBarraPoder(), 0.0001);
    }

    /**
     * Al recibir un gol, la barra del que lo recibe sube un cuarto de inmediato.
     */
    @Test
    public void recibirUnGolDeberiaSumarUnCuarto() {
        AyudantePruebas.meterEnArco(partido, partido.getCancha().getArcoDerecho());
        assertEquals(1, j1.getGoles());
        assertEquals(0.25, j2.getBarraPoder(), 0.01);
        assertTrue(j1.getBarraPoder() < 0.01);
    }

    /**
     * Con la barra incompleta no se activa; con la barra llena se activa, la barra se vacía
     * y queda el aura. Con el aura no se puede activar otro poder aunque la barra se llene.
     */
    @Test
    public void deberiaActivarSoloConLaBarraLlena() {
        j1.aumentarBarra(0.9);
        assertFalse(j1.activarPoder());
        j1.aumentarBarra(0.1);
        assertTrue(j1.activarPoder());
        assertTrue(j1.tienePoder());
        assertEquals(0, j1.getBarraPoder(), 0.0001);
        j1.aumentarBarra(1);
        assertFalse(j1.activarPoder());
    }

    /**
     * El aura no se acaba con el tiempo: dura hasta tocar el balón.
     */
    @Test
    public void elAuraNoDeberiaTenerLimiteDeTiempo() {
        j1.aumentarBarra(1);
        j1.activarPoder();
        partido.getBalon().reiniciar(640, 100);
        AyudantePruebas.avanzar(partido, 1);
        assertTrue(j1.tienePoder());
    }

    /**
     * Al tocar el balón con el aura sale el tiro de poder y el aura desaparece.
     */
    @Test
    public void tocarElBalonConAuraDeberiaLanzarElTiro() {
        j1.aumentarBarra(1);
        j1.activarPoder();
        Balon balon = partido.getBalon();
        balon.reiniciar(j1.getX() + 40, j1.getY() + Jugador.RADIO);
        j1.patear(balon, j2);
        assertFalse(j1.tienePoder());
        assertTrue(balon.esTiroDe(j1));
        assertEquals(Camerun.NOMBRE_TIRO, balon.getNombreTiro());
        assertEquals(Camerun.VELOCIDAD_TIRO, balon.getVelocidadX(), 0.0001);
    }

    /**
     * Rayo (Camerún): va recto, quita 60 x multiplicadorKick y electrocuta al rival.
     */
    @Test
    public void elRayoDeberiaElectrocutar() {
        tiroContraRival();
        assertEquals(j2.getVidaMax() - 60, j2.getVida(), 0.0001);
        assertEquals(Efecto.ELECTROCUTADO, j2.getEfecto());
        assertFalse(partido.getBalon().tieneTiro());
    }

    /**
     * Electrocutado no puede saltar y se mueve a la mitad de su velocidad.
     */
    @Test
    public void electrocutadoNoDeberiaSaltarYIrALaMitad() {
        j2.recibirEfecto(Efecto.ELECTROCUTADO, 3);
        double x = j2.getX();
        j2.moverIzquierda();
        assertEquals(j2.getVelocidad() * 0.5, x - j2.getX(), 0.0001);
        j2.saltar();
        assertTrue(j2.estaEnSuelo());
    }

    /**
     * El daño del tiro crece con el kick del que lo lanza.
     */
    @Test
    public void elDanioDeberiaCrecerConElKick() throws DOPOSoccerException {
        Estadisticas e = new Estadisticas();
        e.mejorar(Estadistica.KICK);
        j1.setEstadisticas(e);
        tiroContraRival();
        assertEquals(j2.getVidaMax() - 60 * 1.1, j2.getVida(), 0.0001);
    }

    /**
     * Aura Azul (Corea del Sur): va recto (sin gravedad) y empuja al rival hacia su arco.
     */
    @Test
    public void elAuraAzulDeberiaEmpujarHaciaSuArco() throws DOPOSoccerException {
        usarPaises(Pais.COREA_DEL_SUR, Pais.NIGERIA);
        tiroContraRival();
        assertEquals(Efecto.EMPUJE, j2.getEfecto());
        double x = j2.getX();
        AyudantePruebas.avanzar(partido, 0.5);
        assertTrue(j2.getX() > x + 100);
        j2.moverIzquierda();
        assertEquals(Efecto.EMPUJE, j2.getEfecto());
    }

    /**
     * Los tiros rectos (Aura Azul y Rayo) no caen por la gravedad.
     */
    @Test
    public void elTiroRectoNoDeberiaCaer() throws DOPOSoccerException {
        usarPaises(Pais.COREA_DEL_SUR, Pais.NIGERIA);
        j1.aumentarBarra(1);
        j1.activarPoder();
        Balon balon = partido.getBalon();
        balon.reiniciar(400, 300);
        balon.serPateado(j1);
        AyudantePruebas.avanzar(partido, 0.3);
        assertEquals(300, balon.getY(), 0.0001);
    }

    /**
     * Tornado (Nigeria): rebota a ras del suelo y deja al rival inconsciente en el aire.
     */
    @Test
    public void elTornadoDeberiaDejarInconsciente() throws DOPOSoccerException {
        usarPaises(Pais.NIGERIA, Pais.CAMERUN);
        j1.aumentarBarra(1);
        j1.activarPoder();
        Balon balon = partido.getBalon();
        balon.reiniciar(400, partido.getCancha().getNivelPiso() - 60);
        balon.serPateado(j1);
        j2.reiniciar(800, j2.getY());
        double maximo = 0;
        for (int i = 0; i < 120 && j2.getVida() == j2.getVidaMax(); i++) {
            partido.actualizar(AyudantePruebas.PASO);
            maximo = Math.max(maximo, partido.getCancha().getNivelPiso() - balon.getY());
        }
        assertTrue("el tornado va bajito", maximo < 80);
        assertEquals(Efecto.INCONSCIENTE, j2.getEfecto());
        assertFalse(j2.estaEnSuelo());
        double x = j2.getX();
        j2.moverIzquierda();
        assertEquals(x, j2.getX(), 0.0001);
    }

    /**
     * Ilusión (Estados Unidos): salen 7 balones falsos que se ven igual al real
     * y desaparecen cuando termina el efecto.
     */
    @Test
    public void laIlusionDeberiaCrearBalonesFalsos() throws DOPOSoccerException {
        usarPaises(Pais.ESTADOS_UNIDOS, Pais.NIGERIA);
        j1.aumentarBarra(1);
        j1.activarPoder();
        Balon balon = partido.getBalon();
        balon.reiniciar(400, 300);
        balon.serPateado(j1);
        assertEquals(EstadosUnidos.BALONES_FALSOS, balon.getSenuelos().size());
        int balones = 0;
        for (Dibujable d : partido.getDibujables()) {
            ComandoDibujo c = d.aComandoDibujo();
            if (c.forma == ComandoDibujo.Forma.BALON) {
                balones++;
                assertEquals(EstadosUnidos.NOMBRE_TIRO, c.etiqueta);
                assertEquals(2 * Balon.RADIO, c.ancho);
            }
        }
        assertEquals(8, balones);
        AyudantePruebas.avanzar(partido, 2.6);
        assertTrue(balon.getSenuelos().isEmpty());
    }

    /**
     * Confundido, la máquina ve un balón falso en vez del real.
     */
    @Test
    public void confundidaLaMaquinaDeberiaVerUnBalonFalso() throws DOPOSoccerException {
        usarPaises(Pais.ESTADOS_UNIDOS, Pais.NIGERIA);
        partido.ponerMaquina(new MaquinaOfensiva());
        j2 = partido.getJugador(2);
        j1.aumentarBarra(1);
        j1.activarPoder();
        partido.getBalon().reiniciar(400, 300);
        partido.getBalon().serPateado(j1);
        assertSame(partido.getBalon(), partido.getBalonPara(j2));
        j1.getPersonaje().aplicarEfecto(j2);
        assertEquals(Efecto.CONFUSION, j2.getEfecto());
        assertNotSame(partido.getBalon(), partido.getBalonPara(j2));
        assertSame(partido.getBalon(), partido.getBalonPara(j1));
    }

    /**
     * Los efectos no se acumulan: el nuevo reemplaza al anterior.
     */
    @Test
    public void unEfectoNuevoDeberiaReemplazarAlAnterior() {
        j2.recibirEfecto(Efecto.ELECTROCUTADO, 3);
        j2.recibirEfecto(Efecto.CONFUSION, 0.5);
        assertEquals(Efecto.CONFUSION, j2.getEfecto());
        AyudantePruebas.avanzar(partido, 0.6);
        assertEquals(Efecto.NINGUNO, j2.getEfecto());
    }

    /**
     * Un tiro de poder que entra al arco sin tocar al rival cuenta como gol, sin daño.
     */
    @Test
    public void unTiroQueEntraAlArcoDeberiaSerGolSinDanio() {
        Balon balon = partido.getBalon();
        Arco arco = partido.getCancha().getArcoDerecho();
        balon.reiniciar(arco.getX() + arco.getAncho() - 20, arco.getY() + arco.getAlto() - 20);
        balon.lanzarTiro(j1, Camerun.NOMBRE_TIRO, 100, 0, false, false);
        partido.actualizar(AyudantePruebas.PASO);
        assertEquals(1, j1.getGoles());
        assertEquals(j2.getVidaMax(), j2.getVida(), 0.0001);
        assertEquals(Efecto.NINGUNO, j2.getEfecto());
        assertFalse(balon.tieneTiro());
    }

    /**
     * Aturdido no puede activar el poder.
     */
    @Test
    public void aturdidoNoDeberiaActivarElPoder() {
        j1.aumentarBarra(1);
        j1.recibirDanio(1000);
        assertFalse(j1.activarPoder());
    }

    /**
     * La máquina activa su poder sola cuando su barra está llena.
     */
    @Test
    public void laMaquinaDeberiaActivarSuPoderSola() throws DOPOSoccerException {
        partido.ponerMaquina(new MaquinaDefensiva());
        Maquina maquina = partido.getMaquina();
        maquina.aumentarBarra(1);
        maquina.jugar(partido);
        assertTrue(maquina.tienePoder());
    }

    /**
     * Por la fachada: la barra se llena jugando, se activa el poder y el estado lo muestra.
     */
    @Test
    public void deberiaActivarElPoderPorLaFachada() throws DOPOSoccerException {
        DOPOSoccer juego = DOPOSoccer.getInstance();
        juego.reiniciar();
        assertThrows(DOPOSoccerException.class, () -> juego.activarPoder(1));
        juego.iniciarPartido(Pais.CAMERUN, Pais.NIGERIA, Partido.ModoJuego.POR_TIEMPO, 3);
        juego.activarPoder(1);
        assertFalse(juego.getEstadoJugador(1).conPoder);
        AyudantePruebas.avanzar(juego, 10.1);
        assertEquals(1.0, juego.getEstadoJugador(1).barraPoder, 0.0001);
        juego.activarPoder(1);
        assertTrue(juego.getEstadoJugador(1).conPoder);
        assertTrue(AyudantePruebas.buscar(juego, ComandoDibujo.Forma.JUGADOR, 1).aura);
        assertThrows(DOPOSoccerException.class, () -> juego.activarPoder(3));
    }

    /**
     * Guardar y abrir conserva la barra de poder.
     */
    @Test
    public void deberiaGuardarLaBarraDePoder() throws Exception {
        DOPOSoccer juego = DOPOSoccer.getInstance();
        juego.reiniciar();
        juego.iniciarPartido(Pais.CAMERUN, Pais.NIGERIA, Partido.ModoJuego.POR_TIEMPO, 3);
        AyudantePruebas.avanzar(juego, 4);
        double barra = juego.getEstadoJugador(2).barraPoder;
        File archivo = new File(carpeta.getRoot(), "barra.txt");
        juego.guardar(archivo);
        juego.reiniciar();
        juego.abrir(archivo);
        assertEquals(barra, juego.getEstadoJugador(2).barraPoder, 0.0001);
    }
}
