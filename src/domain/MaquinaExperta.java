package domain;

/**
 * Máquina con perfil experta: decide la acción según el estado del partido.
 * Se implementa en la Versión dos.
 */
public class MaquinaExperta extends Maquina {

    /**
     * Crea una máquina con perfil experta.
     * @param numero número del jugador (1 o 2)
     * @param personaje personaje que usa
     * @param x posición horizontal inicial
     * @param y posición vertical inicial
     */
    public MaquinaExperta(int numero, Personaje personaje, double x, double y) {
        super(numero, personaje, x, y);
    }

    /**
     * Decide la acción: decide la acción según el estado del partido.
     * Se implementa en la Versión dos.
     * @param partido partido en curso
     */
    @Override
    public void decidirAccion(Partido partido) {
        // Versión dos
    }
}
