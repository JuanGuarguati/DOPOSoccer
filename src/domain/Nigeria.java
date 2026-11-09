package domain;

/**
 * Personaje de Nigeria. Tiro de poder: Tornado. Efecto: inconsciente, no se mueve, no salta ni patea.
 */
public final class Nigeria extends Personaje {

    /** Nombre del país de este personaje. */
    public static final String PAIS = "Nigeria";

    /**
     * Crea el personaje de Nigeria (daño del tiro 40, efecto de 3.0 segundos).
     */
    public Nigeria() {
        super(PAIS, 40, 3.0);
    }

    /**
     * Ejecuta el tiro de poder Tornado.
     * Se implementa en la Versión dos.
     * @param balon balón del partido
     * @param haciaDerecha true si el tiro sale hacia la derecha
     */
    @Override
    public void tiroDePoder(Balon balon, boolean haciaDerecha) {
        // Versión dos
    }

    /**
     * Aplica el efecto: inconsciente, no se mueve, no salta ni patea.
     * Se implementa en la Versión dos.
     * @param rival jugador que recibe el efecto
     */
    @Override
    public void aplicarEfecto(Jugador rival) {
        // Versión dos
    }
}
