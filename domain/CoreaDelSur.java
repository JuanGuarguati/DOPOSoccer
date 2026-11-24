package domain;

import java.awt.Color;

/**
 * Personaje de Corea del Sur. Tiro de poder: Aura Azul. Efecto: empuje hacia su propio arco.
 */
public final class CoreaDelSur extends Personaje {

    /** Nombre del tiro de poder. */
    public static final String NOMBRE_TIRO = "Aura Azul";
    /** Velocidad horizontal del tiro de poder. */
    public static final double VELOCIDAD_TIRO = 1300;
    /** Colores del uniforme {cabeza, banda, cuerpo}, tomados de la bandera. */
    private static final Color[] COLORES = {Color.WHITE, new Color(205, 46, 58), new Color(0, 71, 160)};

    /**
     * Crea el personaje de Corea del Sur (daño del tiro 40, efecto de 1.5 segundos).
     */
    public CoreaDelSur() {
        super(Pais.COREA_DEL_SUR, 40, 1.5);
    }

    /**
     * Ejecuta el tiro de poder Aura Azul: recto y horizontal, a alta velocidad (sin gravedad).
     * @param balon balón del partido
     * @param tirador jugador que lanza el tiro
     */
    @Override
    public void tiroDePoder(Balon balon, Jugador tirador) {
        double direccion = tirador.estaMirandoDerecha() ? 1 : -1;
        balon.lanzarTiro(tirador, NOMBRE_TIRO, direccion * VELOCIDAD_TIRO, 0, false, false);
    }

    /**
     * Aplica el efecto empuje: el rival es lanzado hacia su propio arco.
     * @param rival jugador que recibe el efecto
     */
    @Override
    public void aplicarEfecto(Jugador rival) {
        rival.recibirEfecto(Efecto.EMPUJE, getDuracionEfecto());
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
