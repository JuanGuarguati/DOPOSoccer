package domain;

/**
 * Fachada del dominio de DOPOSoccer. Es la única clase que conoce la interfaz gráfica.
 * Valida las peticiones y las delega al partido.
 */
public class DOPOSoccer {

    /** Tipo de partido que termina cuando se acaba el tiempo. */
    public static final String POR_TIEMPO = "tiempo";
    /** Tipo de partido que termina al llegar a un número de goles. */
    public static final String POR_GOLES = "goles";
    /** Países que se pueden elegir. */
    public static final String[] PAISES = {CoreaDelSur.PAIS, Camerun.PAIS, Nigeria.PAIS, EstadosUnidos.PAIS};

    private Partido partido;

    /**
     * Crea el juego sin ningún partido en curso.
     */
    public DOPOSoccer() {
        partido = null;
    }

    /**
     * Inicia un partido nuevo.
     * @param pais1 país del jugador 1
     * @param pais2 país del jugador 2
     * @param tipo "tiempo" o "goles"
     * @param valor minutos (por tiempo) o goles para ganar (por goles)
     * @throws DOPOSoccerException si el tipo o el valor no son válidos, o si algún país no existe
     */
    public void iniciarPartido(String pais1, String pais2, String tipo, int valor) throws DOPOSoccerException {
        if (valor <= 0) throw new DOPOSoccerException(DOPOSoccerException.VALOR_INVALIDO);
        if (POR_TIEMPO.equals(tipo)) {
            partido = new PartidoPorTiempo(pais1, pais2, valor);
        } else if (POR_GOLES.equals(tipo)) {
            partido = new PartidoPorGoles(pais1, pais2, valor);
        } else {
            throw new DOPOSoccerException(DOPOSoccerException.VALOR_INVALIDO);
        }
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
     * @return el partido actual (puede ser null)
     */
    public Partido getPartido() {
        return partido;
    }

    /**
     * Verifica que haya un partido en curso.
     * @throws DOPOSoccerException si no hay partido o ya terminó
     */
    private void validarPartido() throws DOPOSoccerException {
        if (partido == null || partido.estaTerminado()) {
            throw new DOPOSoccerException(DOPOSoccerException.PARTIDO_TERMINADO);
        }
    }
}
