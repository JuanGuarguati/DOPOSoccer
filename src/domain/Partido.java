package domain;

/**
 * Partido de DOPOSoccer entre dos jugadores. El jugador 1 defiende el arco izquierdo
 * y el jugador 2 el derecho. Cada tipo de partido define cuándo hay ganador.
 */
public abstract class Partido {

    /** Ancho lógico de la cancha. */
    public static final int ANCHO_CANCHA = 1280;
    /** Alto lógico de la cancha. */
    public static final int ALTO_CANCHA = 720;
    /** Puntos que recibe el ganador del partido. */
    public static final int PUNTOS_GANADOR = 100;
    /** Puntos que recibe el perdedor del partido. */
    public static final int PUNTOS_PERDEDOR = 25;
    /** Distancia desde cada pared a la que empiezan los jugadores. */
    public static final double DISTANCIA_INICIAL = 200;

    private Cancha cancha;
    private Balon balon;
    private Jugador[] jugadores;
    private double tiempoJugado;
    private boolean pausado;
    private boolean terminado;

    /**
     * Crea un partido 0 - 0 con la cancha, el balón en el centro y los dos jugadores.
     * @param pais1 país del jugador 1
     * @param pais2 país del jugador 2
     * @throws DOPOSoccerException si algún país no existe
     */
    public Partido(String pais1, String pais2) throws DOPOSoccerException {
        cancha = new Cancha(ANCHO_CANCHA, ALTO_CANCHA);
        balon = new Balon(ANCHO_CANCHA / 2.0, 100);
        Personaje personaje1 = crearPersonaje(pais1);
        Personaje personaje2 = crearPersonaje(pais2);
        double piso = cancha.getNivelPiso() - Jugador.RADIO * 2;
        jugadores = new Jugador[] {
            new Jugador(1, personaje1, DISTANCIA_INICIAL, piso),
            new Jugador(2, personaje2, ANCHO_CANCHA - DISTANCIA_INICIAL, piso)
        };
    }

    /**
     * Avanza el partido: tiempo, balón, jugadores, choques, goles y ganador.
     * No hace nada si está pausado o terminado.
     * @param segundos tiempo que pasó desde la última actualización
     */
    public void actualizar(double segundos) {
        if (pausado || terminado) return;
        tiempoJugado += segundos;
        balon.mover(segundos, cancha);
        for (Jugador j : jugadores) {
            j.actualizar(segundos, cancha);
            cabecear(j);
        }
        verificarGol();
        if (hayGanador()) {
            finalizar();
        }
    }

    /**
     * Mueve un jugador a la izquierda o a la derecha. No hace nada si el partido está en pausa.
     * @param numero número del jugador (1 o 2)
     * @param direccion "izquierda" o "derecha"
     * @throws DOPOSoccerException si el número o la dirección no son válidos
     */
    public void moverJugador(int numero, String direccion) throws DOPOSoccerException {
        Jugador j = getJugador(numero);
        if (pausado) return;
        if ("izquierda".equals(direccion)) {
            j.moverIzquierda();
        } else if ("derecha".equals(direccion)) {
            j.moverDerecha();
        } else {
            throw new DOPOSoccerException(DOPOSoccerException.DIRECCION_INVALIDA);
        }
    }

    /**
     * Hace saltar a un jugador. No hace nada si el partido está en pausa.
     * @param numero número del jugador (1 o 2)
     * @throws DOPOSoccerException si el número no es válido
     */
    public void saltarJugador(int numero) throws DOPOSoccerException {
        Jugador j = getJugador(numero);
        if (pausado) return;
        j.saltar();
    }

    /**
     * Hace patear a un jugador contra el balón o el rival. No hace nada si el partido está en pausa.
     * @param numero número del jugador (1 o 2)
     * @throws DOPOSoccerException si el número no es válido
     */
    public void patearJugador(int numero) throws DOPOSoccerException {
        Jugador j = getJugador(numero);
        Jugador rival = getJugador(3 - numero);
        if (pausado) return;
        j.patear(balon, rival);
    }

    /**
     * Revisa si el balón entró a algún arco. Gol en el arco izquierdo es del jugador 2
     * y gol en el derecho es del jugador 1. Después de un gol se reinician las posiciones.
     */
    public void verificarGol() {
        if (cancha.getArcoIzquierdo().esGol(balon)) {
            jugadores[1].anotarGol();
            reiniciarPosiciones();
        } else if (cancha.getArcoDerecho().esGol(balon)) {
            jugadores[0].anotarGol();
            reiniciarPosiciones();
        }
    }

    /**
     * Pone a los jugadores en su posición inicial y deja caer el balón desde el centro.
     */
    public void reiniciarPosiciones() {
        double piso = cancha.getNivelPiso() - Jugador.RADIO * 2;
        jugadores[0].reiniciar(DISTANCIA_INICIAL, piso);
        jugadores[1].reiniciar(ANCHO_CANCHA - DISTANCIA_INICIAL, piso);
        balon.reiniciar(ANCHO_CANCHA / 2.0, 100);
    }

    /**
     * Pausa el partido o lo reanuda si ya estaba pausado.
     */
    public void pausar() {
        pausado = !pausado;
    }

    /**
     * Termina el partido en cualquier momento y entrega los puntos si hay ganador.
     */
    public void terminar() {
        if (!terminado) finalizar();
    }

    /**
     * Indica si se cumplió la condición para ganar según el tipo de partido.
     * @return true si ya hay ganador
     */
    public abstract boolean hayGanador();

    /**
     * Retorna el jugador con más goles.
     * @return el ganador, o null si están empatados
     */
    public Jugador getGanador() {
        if (jugadores[0].getGoles() > jugadores[1].getGoles()) return jugadores[0];
        if (jugadores[1].getGoles() > jugadores[0].getGoles()) return jugadores[1];
        return null;
    }

    /**
     * Retorna un jugador por su número.
     * @param numero número del jugador (1 o 2)
     * @return el jugador
     * @throws DOPOSoccerException si el número no es 1 ni 2
     */
    public Jugador getJugador(int numero) throws DOPOSoccerException {
        if (numero != 1 && numero != 2) throw new DOPOSoccerException(DOPOSoccerException.JUGADOR_INVALIDO);
        return jugadores[numero - 1];
    }

    /**
     * @return balón del partido
     */
    public Balon getBalon() { return balon; }

    /**
     * @return cancha del partido
     */
    public Cancha getCancha() { return cancha; }

    /**
     * @return true si el partido terminó
     */
    public boolean estaTerminado() { return terminado; }

    /**
     * @return true si el partido está en pausa
     */
    public boolean estaPausado() { return pausado; }

    /**
     * @return segundos jugados
     */
    public double getTiempoJugado() { return tiempoJugado; }

    /**
     * Crea el personaje que corresponde a un país.
     * @param pais nombre del país
     * @return el personaje
     * @throws DOPOSoccerException si el país no existe
     */
    private Personaje crearPersonaje(String pais) throws DOPOSoccerException {
        if (CoreaDelSur.PAIS.equals(pais)) return new CoreaDelSur();
        if (Camerun.PAIS.equals(pais)) return new Camerun();
        if (Nigeria.PAIS.equals(pais)) return new Nigeria();
        if (EstadosUnidos.PAIS.equals(pais)) return new EstadosUnidos();
        throw new DOPOSoccerException(DOPOSoccerException.PAIS_INVALIDO);
    }

    /**
     * Si la cabeza del jugador choca con el balón, el balón sale rebotado.
     * @param j jugador que puede cabecear
     */
    private void cabecear(Jugador j) {
        double dx = balon.getX() - j.getX();
        double dy = balon.getY() - j.getY();
        double distancia = Math.sqrt(dx * dx + dy * dy);
        double minimo = j.getRadio() + balon.getRadio();
        if (distancia < minimo && distancia > 0) {
            double velocidad = Math.max(300, Math.hypot(balon.getVelocidadX(), balon.getVelocidadY()));
            balon.recibirPatada(dx / distancia * velocidad, dy / distancia * velocidad);
        }
    }

    /**
     * Marca el partido como terminado y entrega 100 puntos al ganador y 25 al perdedor.
     */
    private void finalizar() {
        terminado = true;
        Jugador ganador = getGanador();
        if (ganador != null) {
            ganador.sumarPuntos(PUNTOS_GANADOR);
            jugadores[2 - ganador.getNumero()].sumarPuntos(PUNTOS_PERDEDOR);
        }
    }
}
