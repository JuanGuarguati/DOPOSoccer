package domain;

/**
 * Fachada del dominio de DOPOSoccer. Es la única clase que conoce la interfaz gráfica.
 * Valida las peticiones y las delega al partido. Hay una sola instancia (singleton),
 * que guarda los puntos y las estadísticas de los dos jugadores entre partidos.
 */
public class DOPOSoccer {

    /** Tipo de partido que termina cuando se acaba el tiempo. */
    public static final String POR_TIEMPO = "tiempo";
    /** Tipo de partido que termina al llegar a un número de goles. */
    public static final String POR_GOLES = "goles";
    /** Países que se pueden elegir. */
    public static final String[] PAISES = {CoreaDelSur.PAIS, Camerun.PAIS, Nigeria.PAIS, EstadosUnidos.PAIS};
    /** Precio en puntos de la primera mejora; cada mejora comprada lo sube en esta misma cantidad. */
    public static final int PRECIO_INICIAL = 25;

    private static DOPOSoccer instancia;

    private Partido partido;
    private int[] puntos;
    private Estadisticas[] estadisticas;

    /**
     * Crea el juego sin partido, con los dos jugadores en 0 puntos y estadísticas en 1.
     */
    private DOPOSoccer() {
        reiniciar();
    }

    /**
     * Retorna la única instancia del juego; la crea la primera vez.
     * @return el juego
     */
    public static DOPOSoccer getInstance() {
        if (instancia == null) instancia = new DOPOSoccer();
        return instancia;
    }

    /**
     * Deja el juego como nuevo: sin partido, 0 puntos y estadísticas en 1.
     */
    public void reiniciar() {
        partido = null;
        puntos = new int[] {0, 0};
        estadisticas = new Estadisticas[] {new Estadisticas(), new Estadisticas()};
    }

    /**
     * Inicia un partido nuevo. Los jugadores conservan los puntos y las estadísticas
     * de los partidos anteriores.
     * @param pais1 país del jugador 1
     * @param pais2 país del jugador 2
     * @param tipo "tiempo" o "goles"
     * @param valor minutos (por tiempo) o goles para ganar (por goles)
     * @throws DOPOSoccerException si el tipo o el valor no son válidos, o si algún país no existe
     */
    public void iniciarPartido(String pais1, String pais2, String tipo, int valor) throws DOPOSoccerException {
        if (valor <= 0) throw new DOPOSoccerException(DOPOSoccerException.VALOR_INVALIDO);
        Partido nuevo;
        if (POR_TIEMPO.equals(tipo)) {
            nuevo = new PartidoPorTiempo(pais1, pais2, valor);
        } else if (POR_GOLES.equals(tipo)) {
            nuevo = new PartidoPorGoles(pais1, pais2, valor);
        } else {
            throw new DOPOSoccerException(DOPOSoccerException.VALOR_INVALIDO);
        }
        guardarPuntos();
        Jugador j1 = nuevo.getJugador(1);
        Jugador j2 = nuevo.getJugador(2);
        j1.sumarPuntos(puntos[0]);
        j2.sumarPuntos(puntos[1]);
        j1.setEstadisticas(estadisticas[0]);
        j2.setEstadisticas(estadisticas[1]);
        partido = nuevo;
    }

    /**
     * Retorna los puntos acumulados de un jugador en todos los partidos jugados.
     * @param numero número del jugador (1 o 2)
     * @return puntos acumulados
     * @throws DOPOSoccerException si el número no es 1 ni 2
     */
    public int getPuntos(int numero) throws DOPOSoccerException {
        validarNumero(numero);
        if (partido != null) return partido.getJugador(numero).getPuntos();
        return puntos[numero - 1];
    }

    /**
     * Retorna las estadísticas de un jugador (las que usará en el próximo partido).
     * @param numero número del jugador (1 o 2)
     * @return estadísticas del jugador
     * @throws DOPOSoccerException si el número no es 1 ni 2
     */
    public Estadisticas getEstadisticas(int numero) throws DOPOSoccerException {
        validarNumero(numero);
        return estadisticas[numero - 1];
    }

    /**
     * Retorna lo que cuesta la siguiente mejora de un jugador:
     * PRECIO_INICIAL por cada mejora ya comprada más uno.
     * @param numero número del jugador (1 o 2)
     * @return precio en puntos
     * @throws DOPOSoccerException si el número no es 1 ni 2
     */
    public int getPrecioMejora(int numero) throws DOPOSoccerException {
        Estadisticas e = getEstadisticas(numero);
        int compradas = e.getSpeed() + e.getKick() + e.getJump() + e.getSurvival() - 4 * Estadisticas.MINIMO;
        return PRECIO_INICIAL * (compradas + 1);
    }

    /**
     * Compra una mejora de estadística con los puntos del jugador. Solo se puede sin partido en curso.
     * @param numero número del jugador (1 o 2)
     * @param estadistica "speed", "kick", "jump" o "survival"
     * @throws DOPOSoccerException si hay un partido en curso, el número no es válido,
     *         no le alcanzan los puntos, o la estadística no existe o ya está en 10
     */
    public void mejorarEstadistica(int numero, String estadistica) throws DOPOSoccerException {
        validarNumero(numero);
        if (partido != null && !partido.estaTerminado()) {
            throw new DOPOSoccerException(DOPOSoccerException.PARTIDO_EN_CURSO);
        }
        guardarPuntos();
        partido = null;
        int precio = getPrecioMejora(numero);
        if (puntos[numero - 1] < precio) throw new DOPOSoccerException(DOPOSoccerException.PUNTOS_INSUFICIENTES);
        estadisticas[numero - 1].mejorar(estadistica);
        puntos[numero - 1] -= precio;
    }

    /**
     * Avanza el partido en curso. Si no hay partido, no hace nada.
     * @param segundos tiempo que pasó desde la última actualización
     */
    public void actualizar(double segundos) {
        if (partido != null) partido.actualizar(segundos);
    }

    /**
     * Mueve un jugador.
     * @param numero número del jugador (1 o 2)
     * @param direccion "izquierda" o "derecha"
     * @throws DOPOSoccerException si no hay partido en curso, o el número o la dirección no son válidos
     */
    public void moverJugador(int numero, String direccion) throws DOPOSoccerException {
        validarPartido();
        partido.moverJugador(numero, direccion);
    }

    /**
     * Hace saltar a un jugador.
     * @param numero número del jugador (1 o 2)
     * @throws DOPOSoccerException si no hay partido en curso o el número no es válido
     */
    public void saltarJugador(int numero) throws DOPOSoccerException {
        validarPartido();
        partido.saltarJugador(numero);
    }

    /**
     * Hace patear a un jugador.
     * @param numero número del jugador (1 o 2)
     * @throws DOPOSoccerException si no hay partido en curso o el número no es válido
     */
    public void patearJugador(int numero) throws DOPOSoccerException {
        validarPartido();
        partido.patearJugador(numero);
    }

    /**
     * Pausa o reanuda el partido.
     * @throws DOPOSoccerException si no hay partido en curso
     */
    public void pausar() throws DOPOSoccerException {
        validarPartido();
        partido.pausar();
    }

    /**
     * Termina el partido en curso.
     * @throws DOPOSoccerException si no hay partido en curso
     */
    public void terminar() throws DOPOSoccerException {
        validarPartido();
        partido.terminar();
    }

    /**
     * @return true si hay un partido (en curso o recién terminado)
     */
    public boolean hayPartido() {
        return partido != null;
    }

    /**
     * @return true si el partido está en pausa
     * @throws DOPOSoccerException si no hay partido
     */
    public boolean estaPausado() throws DOPOSoccerException {
        validarExistePartido();
        return partido.estaPausado();
    }

    /**
     * @return true si el partido terminó
     * @throws DOPOSoccerException si no hay partido
     */
    public boolean estaTerminado() throws DOPOSoccerException {
        validarExistePartido();
        return partido.estaTerminado();
    }

    /**
     * Retorna un jugador del partido.
     * @param numero número del jugador (1 o 2)
     * @return el jugador
     * @throws DOPOSoccerException si no hay partido o el número no es válido
     */
    public Jugador getJugador(int numero) throws DOPOSoccerException {
        validarExistePartido();
        return partido.getJugador(numero);
    }

    /**
     * @return el ganador del partido, o null si van empatados
     * @throws DOPOSoccerException si no hay partido
     */
    public Jugador getGanador() throws DOPOSoccerException {
        validarExistePartido();
        return partido.getGanador();
    }

    /**
     * @return balón del partido
     * @throws DOPOSoccerException si no hay partido
     */
    public Balon getBalon() throws DOPOSoccerException {
        validarExistePartido();
        return partido.getBalon();
    }

    /**
     * @return cancha del partido
     * @throws DOPOSoccerException si no hay partido
     */
    public Cancha getCancha() throws DOPOSoccerException {
        validarExistePartido();
        return partido.getCancha();
    }

    /**
     * @return texto del tiempo según el tipo de partido
     * @throws DOPOSoccerException si no hay partido
     */
    public String getTextoTiempo() throws DOPOSoccerException {
        validarExistePartido();
        return partido.getTextoTiempo();
    }

    /**
     * Guarda los puntos que tienen los jugadores del partido actual para pasarlos al siguiente.
     * @throws DOPOSoccerException si no se puede leer un jugador
     */
    private void guardarPuntos() throws DOPOSoccerException {
        if (partido != null) {
            puntos[0] = partido.getJugador(1).getPuntos();
            puntos[1] = partido.getJugador(2).getPuntos();
        }
    }

    /**
     * Verifica que haya un partido en curso (que exista y no haya terminado).
     * @throws DOPOSoccerException si no hay partido o ya terminó
     */
    private void validarPartido() throws DOPOSoccerException {
        if (partido == null || partido.estaTerminado()) {
            throw new DOPOSoccerException(DOPOSoccerException.PARTIDO_TERMINADO);
        }
    }

    /**
     * Verifica que exista un partido, aunque ya haya terminado.
     * @throws DOPOSoccerException si no hay partido
     */
    private void validarExistePartido() throws DOPOSoccerException {
        if (partido == null) throw new DOPOSoccerException(DOPOSoccerException.PARTIDO_TERMINADO);
    }

    /**
     * Verifica que el número de jugador sea 1 o 2.
     * @param numero número del jugador
     * @throws DOPOSoccerException si no es 1 ni 2
     */
    private void validarNumero(int numero) throws DOPOSoccerException {
        if (numero != 1 && numero != 2) throw new DOPOSoccerException(DOPOSoccerException.JUGADOR_INVALIDO);
    }
}
