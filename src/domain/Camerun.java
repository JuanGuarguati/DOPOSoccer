package domain;

/**
 * Personaje de Camerún. Tiro de poder: Rayo. Efecto: electrocutado, no salta y se mueve al 50 %.
 */
public final class Camerun extends Personaje {

    /** Nombre del país de este personaje. */
    public static final String PAIS = "Camerún";

    /**
     * Crea el personaje de Camerún (daño del tiro 60, efecto de 3.0 segundos).
     */
    public Camerun() {
        super(PAIS, 60, 3.0);
    }

    /**
     * Ejecuta el tiro de poder Rayo.
     * Se implementa en la Versión dos.
     * @param balon balón del partido
     * @param haciaDerecha true si el tiro sale hacia la derecha
     */
    @Override
    public void tiroDePoder(Balon balon, boolean haciaDerecha) {
        // Versión dos
    }

    /**
     * Aplica el efecto: electrocutado, no salta y se mueve al 50 %.
     * Se implementa en la Versión dos.
     * @param rival jugador que recibe el efecto
     */
    @Override
    public void aplicarEfecto(Jugador rival) {
        // Versión dos
    }
}
