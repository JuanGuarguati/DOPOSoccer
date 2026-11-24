package domain;

import dto.ComandoDibujo;

/**
 * Balón falso de la Ilusión (Estados Unidos). Rebota como un balón normal y se ve
 * exactamente igual que el balón real, pero no hace goles ni se puede usar para un tiro de poder.
 */
public class BalonFalso extends Balon {

    /** Balón real que imita (para verse igual). */
    private Balon original;

    /**
     * Crea un balón falso.
     * @param original balón real que imita
     * @param x posición horizontal inicial
     * @param y posición vertical inicial
     */
    public BalonFalso(Balon original, double x, double y) {
        super(x, y);
        this.original = original;
    }

    /**
     * Se dibuja igual que el balón real, pero en su propia posición.
     * @return comando de dibujo
     */
    @Override
    public ComandoDibujo aComandoDibujo() {
        ComandoDibujo real = original.aComandoDibujo();
        return new ComandoDibujo(ComandoDibujo.Forma.BALON, (int) getX() - getRadio(), (int) getY() - getRadio(),
            real.ancho, real.alto, real.etiqueta);
    }

    /**
     * Un balón falso no sirve para tiros de poder: si lo patean sale como una patada normal.
     * @param pateador jugador que patea
     */
    @Override
    public void serPateado(Jugador pateador) {
        double direccion = pateador.estaMirandoDerecha() ? 1 : -1;
        recibirPatada(direccion * Jugador.FUERZA_PATADA_X, Jugador.FUERZA_PATADA_Y);
    }
}
