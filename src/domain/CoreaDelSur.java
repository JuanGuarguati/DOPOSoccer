package domain;

/**
 * Personaje de Corea del Sur. Tiro de poder: Aura Azul. Efecto: empuje hacia su propio arco.
 */
public final class CoreaDelSur extends Personaje {

    /** Nombre del país de este personaje. */
    public static final String PAIS = "Corea del Sur";

    /**
     * Crea el personaje de Corea del Sur (daño del tiro 40, efecto de 1.5 segundos).
     */
    public CoreaDelSur() {
        super(PAIS, 40, 1.5);
    }

    /**
     * Ejecuta el tiro de poder Aura Azul.
     * Se implementa en la Versión dos.
     * @param balon balón del partido
     * @param haciaDerecha true si el tiro sale hacia la derecha
     */
    @Override
    public void tiroDePoder(Balon balon, boolean haciaDerecha) {
        // Versión dos
    }

    /**
     * Aplica el efecto: empuje hacia su propio arco.
     * Se implementa en la Versión dos.
     * @param rival jugador que recibe el efecto
     */
    @Override
    public void aplicarEfecto(Jugador rival) {
        // Versión dos
    }
}
