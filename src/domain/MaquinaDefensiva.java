package domain;

/**
 * Máquina con perfil defensiva: su prioridad es defender el arco.
 * Se implementa en la Versión dos.
 */
public class MaquinaDefensiva extends Maquina {

    /**
     * Crea una máquina con perfil defensiva.
     * @param numero número del jugador (1 o 2)
     * @param personaje personaje que usa
     * @param x posición horizontal inicial
     * @param y posición vertical inicial
     */
    public MaquinaDefensiva(int numero, Personaje personaje, double x, double y) {
        super(numero, personaje, x, y);
    }

    /**
     * Decide la acción: su prioridad es defender el arco.
     * Se implementa en la Versión dos.
     * @param partido partido en curso
     */
    @Override
    public void decidirAccion(Partido partido) {
        // Versión dos
    }
}
