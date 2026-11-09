package domain;

/**
 * Partido por tiempo: dura un número de minutos. Gana quien tenga más goles al final.
 * Si hay empate, entra en muerte súbita y gana el primero que anote.
 */
public class PartidoPorTiempo extends Partido {

    private double duracion;
    private boolean muerteSubita;

    /**
     * Crea un partido por tiempo.
     * @param pais1 país del jugador 1
     * @param pais2 país del jugador 2
     * @param minutos duración del partido en minutos
     * @throws DOPOSoccerException si algún país no existe o los minutos no son mayores que cero
     */
    public PartidoPorTiempo(String pais1, String pais2, int minutos) throws DOPOSoccerException {
        super(pais1, pais2);
        if (minutos <= 0) throw new DOPOSoccerException(DOPOSoccerException.VALOR_INVALIDO);
        this.duracion = minutos * 60.0;
    }

    /**
     * Hay ganador cuando se acabó el tiempo y un jugador tiene más goles.
     * Si al acabarse el tiempo hay empate, activa la muerte súbita.
     * @return true si ya hay ganador
     */
    @Override
    public boolean hayGanador() {
        if (getTiempoJugado() < duracion) return false;
        if (getGanador() != null) return true;
        muerteSubita = true;
        return false;
    }

    /**
     * @return segundos que faltan (0 si ya se acabó el tiempo)
     */
    public double getTiempoRestante() {
        return Math.max(0, duracion - getTiempoJugado());
    }

    /**
     * @return true si el partido está en muerte súbita
     */
    public boolean estaEnMuerteSubita() {
        return muerteSubita;
    }
}
