package domain;

import java.awt.Color;

/**
 * Personaje de Camerún. Tiro de poder: Rayo. Efecto: electrocutado, no salta y se mueve al 50 %.
 */
public final class Camerun extends Personaje {

    /** Nombre del tiro de poder. */
    public static final String NOMBRE_TIRO = "Rayo";
    /** Velocidad horizontal del tiro de poder. */
    public static final double VELOCIDAD_TIRO = 1050;
    /** Colores del uniforme {cabeza, banda, cuerpo}, tomados de la bandera. */
    private static final Color[] COLORES = {new Color(0, 122, 94), new Color(252, 209, 22), new Color(206, 17, 38)};

    /**
     * Crea el personaje de Camerún (daño del tiro 60, efecto de 3.0 segundos).
     */
    public Camerun() {
        super(Pais.CAMERUN, 60, 3.0);
    }

    /**
     * Ejecuta el tiro de poder Rayo: recto, cubierto de electricidad (sin gravedad).
     * @param balon balón del partido
     * @param tirador jugador que lanza el tiro
     */
    @Override
    public void tiroDePoder(Balon balon, Jugador tirador) {
        double direccion = tirador.estaMirandoDerecha() ? 1 : -1;
        balon.lanzarTiro(tirador, NOMBRE_TIRO, direccion * VELOCIDAD_TIRO, 0, false, false);
    }

    /**
     * Aplica el efecto electrocutado: no puede saltar y se mueve al 50 %.
     * @param rival jugador que recibe el efecto
     */
    @Override
    public void aplicarEfecto(Jugador rival) {
        rival.recibirEfecto(Efecto.ELECTROCUTADO, getDuracionEfecto());
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
