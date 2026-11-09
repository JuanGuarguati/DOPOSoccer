package domain;

/**
 * Máquina con perfil torpe: escoge al azar sus movimientos.
 * Se implementa en la Versión dos.
 */
public class MaquinaTorpe extends Maquina {

    /**
     * Crea una máquina con perfil torpe.
     * @param numero número del jugador (1 o 2)
     * @param personaje personaje que usa
     * @param x posición horizontal inicial
     * @param y posición vertical inicial
     */
    public MaquinaTorpe(int numero, Personaje personaje, double x, double y) {
        super(numero, personaje, x, y);
    }

    /**
     * Decide la acción: escoge al azar sus movimientos.
     * Se implementa en la Versión dos.
     * @param partido partido en curso
     */
    @Override
    public void decidirAccion(Partido partido) {
        // Versión dos
    }
}
