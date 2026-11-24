package domain;

import java.awt.Color;

/**
 * Personaje de Nigeria. Tiro de poder: Tornado. Efecto: inconsciente, no se mueve, no salta ni patea.
 */
public final class Nigeria extends Personaje {

    /** Nombre del tiro de poder. */
    public static final String NOMBRE_TIRO = "Tornado";
    /** Velocidad horizontal del tiro de poder. */
    public static final double VELOCIDAD_TIRO = 800;
    /** Colores del uniforme {cabeza, banda, cuerpo}, tomados de la bandera. */
    private static final Color[] COLORES = {Color.WHITE, new Color(0, 135, 81), new Color(0, 135, 81)};

    /**
     * Crea el personaje de Nigeria (daño del tiro 40, efecto de 3.0 segundos).
     */
    public Nigeria() {
        super(Pais.NIGERIA, 40, 3.0);
    }

    /**
     * Ejecuta el tiro de poder Tornado: remolino a ras de suelo que rebota rápidamente.
     * @param balon balón del partido
     * @param tirador jugador que lanza el tiro
     */
    @Override
    public void tiroDePoder(Balon balon, Jugador tirador) {
        double direccion = tirador.estaMirandoDerecha() ? 1 : -1;
        balon.lanzarTiro(tirador, NOMBRE_TIRO, direccion * VELOCIDAD_TIRO, Balon.REBOTE_TORNADO, true, true);
    }

    /**
     * Aplica el efecto inconsciente: es lanzado al aire y no puede moverse, saltar ni patear.
     * @param rival jugador que recibe el efecto
     */
    @Override
    public void aplicarEfecto(Jugador rival) {
        rival.recibirEfecto(Efecto.INCONSCIENTE, getDuracionEfecto());
    }

    /**
     * Colores del uniforme, tomados de la bandera.
     * @return arreglo {cabeza, banda, cuerpo}
     */
    @Override
    public Color[] getColores() {
        return COLORES.clone();
    }
}
