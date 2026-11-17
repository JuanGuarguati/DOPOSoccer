package domain;

/**
 * Personaje que representa a un país. Cada país tiene un tiro de poder propio.
 * En la Versión uno solo se usa para identificar al jugador; los poderes se implementan en la Versión dos.
 */
public abstract class Personaje {

    private String pais;
    private int danioTiro;
    private double duracionEfecto;

    /**
     * Crea un personaje.
     * @param pais país que representa
     * @param danioTiro daño base del tiro de poder (ya multiplicado por 5, Anexo 1)
     * @param duracionEfecto duración del efecto del tiro de poder en segundos
     */
    public Personaje(String pais, int danioTiro, double duracionEfecto) {
        this.pais = pais;
        this.danioTiro = danioTiro;
        this.duracionEfecto = duracionEfecto;
    }

    /**
     * Crea el personaje que corresponde a un país.
     * @param pais nombre del país
     * @return el personaje del país
     * @throws DOPOSoccerException si el país no existe
     */
    public static Personaje crear(String pais) throws DOPOSoccerException {
        if (CoreaDelSur.PAIS.equals(pais)) return new CoreaDelSur();
        if (Camerun.PAIS.equals(pais)) return new Camerun();
        if (Nigeria.PAIS.equals(pais)) return new Nigeria();
        if (EstadosUnidos.PAIS.equals(pais)) return new EstadosUnidos();
        throw new DOPOSoccerException(DOPOSoccerException.PAIS_INVALIDO);
    }

    /**
     * Ejecuta el tiro de poder del personaje sobre el balón.
     * @param balon balón del partido
     * @param haciaDerecha true si el tiro sale hacia la derecha
     */
    public abstract void tiroDePoder(Balon balon, boolean haciaDerecha);

    /**
     * Aplica al rival el efecto del tiro de poder.
     * @param rival jugador que recibe el efecto
     */
    public abstract void aplicarEfecto(Jugador rival);

    /**
     * @return país del personaje
     */
    public String getPais() { return pais; }

    /**
     * @return daño base del tiro de poder
     */
    public int getDanioTiro() { return danioTiro; }

    /**
     * @return duración del efecto en segundos
     */
    public double getDuracionEfecto() { return duracionEfecto; }
}
