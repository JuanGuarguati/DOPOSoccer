package domain;

/**
 * Partido por goles: gana el primero que llegue a un número de goles, sin límite de tiempo.
 */
public class PartidoPorGoles extends Partido {

    private int golesParaGanar;

    /**
     * Crea un partido por goles.
     * @param pais1 país del jugador 1
     * @param pais2 país del jugador 2
     * @param goles goles necesarios para ganar
     * @throws DOPOSoccerException si algún país no existe o los goles no son mayores que cero
     */
    public PartidoPorGoles(String pais1, String pais2, int goles) throws DOPOSoccerException {
        super(pais1, pais2);
        if (goles <= 0) throw new DOPOSoccerException(DOPOSoccerException.VALOR_INVALIDO);
        this.golesParaGanar = goles;
    }

    /**
     * Hay ganador cuando algún jugador llega a los goles definidos.
     * @return true si ya hay ganador
     */
    @Override
    public boolean hayGanador() {
        try {
            return getJugador(1).getGoles() >= golesParaGanar || getJugador(2).getGoles() >= golesParaGanar;
        } catch (DOPOSoccerException e) {
            return false;
        }
    }

    /**
     * @return goles necesarios para ganar
     */
    public int getGolesParaGanar() {
        return golesParaGanar;
    }
}
