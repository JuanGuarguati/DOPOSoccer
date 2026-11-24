package domain;

import java.awt.Color;

/**
 * Personaje de Estados Unidos. Tiro de poder: Ilusión. Efecto: confusión, 8 balones iguales.
 */
public final class EstadosUnidos extends Personaje {

    /** Nombre del tiro de poder. */
    public static final String NOMBRE_TIRO = "Ilusión";
    /** Velocidad horizontal del tiro de poder. */
    public static final double VELOCIDAD_TIRO = 850;
    /** Cantidad de balones falsos de la Ilusión. */
    public static final int BALONES_FALSOS = 7;
    /** Colores del uniforme {cabeza, banda, cuerpo}, tomados de la bandera. */
    private static final Color[] COLORES = {new Color(60, 59, 110), Color.WHITE, new Color(178, 34, 52)};

    /**
     * Crea el personaje de Estados Unidos (daño del tiro 30, efecto de 2.5 segundos).
     */
    public EstadosUnidos() {
        super(Pais.ESTADOS_UNIDOS, 30, 2.5);
    }

    /**
     * Ejecuta el tiro de poder Ilusión: el balón se divide en 8 balones iguales; 7 son falsos y desaparecen al terminar el efecto.
     * @param balon balón del partido
     * @param tirador jugador que lanza el tiro
     */
    @Override
    public void tiroDePoder(Balon balon, Jugador tirador) {
        double direccion = tirador.estaMirandoDerecha() ? 1 : -1;
        balon.lanzarTiro(tirador, NOMBRE_TIRO, direccion * VELOCIDAD_TIRO, -200, true, false);
        balon.crearSenuelos(BALONES_FALSOS, getDuracionEfecto());
    }

    /**
     * Aplica el efecto confusión: no sabe cuál es el balón real.
     * @param rival jugador que recibe el efecto
     */
    @Override
    public void aplicarEfecto(Jugador rival) {
        rival.recibirEfecto(Efecto.CONFUSION, getDuracionEfecto());
    }

    /**
     * Colores del uniforme, tomados de la bandera.
     * @return arreglo {cabeza, banda, cuerpo}
     */
    @Override
    public Color[] getColores() {
        return COLORES.clone();
    }

    /**
     * El uniforme de Estados Unidos lleva rayas blancas en el cuerpo.
     * @return true
     */
    @Override
    public boolean tieneRayas() { return true; }
}
