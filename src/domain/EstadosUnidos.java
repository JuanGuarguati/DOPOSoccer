package domain;

/**
 * Personaje de Estados Unidos. Tiro de poder: Ilusión. Efecto: confusión, 8 balones iguales.
 */
public final class EstadosUnidos extends Personaje {

    /** Nombre del país de este personaje. */
    public static final String PAIS = "Estados Unidos";

    /**
     * Crea el personaje de Estados Unidos (daño del tiro 30, efecto de 2.5 segundos).
     */
    public EstadosUnidos() {
        super(PAIS, 30, 2.5);
    }

    /**
     * Ejecuta el tiro de poder Ilusión.
     * Se implementa en la Versión dos.
     * @param balon balón del partido
     * @param haciaDerecha true si el tiro sale hacia la derecha
     */
    @Override
    public void tiroDePoder(Balon balon, boolean haciaDerecha) {
        // Versión dos
    }

    /**
     * Aplica el efecto: confusión, 8 balones iguales.
     * Se implementa en la Versión dos.
     * @param rival jugador que recibe el efecto
     */
    @Override
    public void aplicarEfecto(Jugador rival) {
        // Versión dos
    }
}
