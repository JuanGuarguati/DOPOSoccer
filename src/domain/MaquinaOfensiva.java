package domain;

/**
 * Máquina con perfil ofensiva: su prioridad es anotar.
 * Se implementa en la Versión dos.
 */
public class MaquinaOfensiva extends Maquina {

    /**
     * Crea una máquina con perfil ofensiva.
     * @param numero número del jugador (1 o 2)
     * @param personaje personaje que usa
     * @param x posición horizontal inicial
     * @param y posición vertical inicial
     */
    public MaquinaOfensiva(int numero, Personaje personaje, double x, double y) {
        super(numero, personaje, x, y);
    }

    /**
     * Decide la acción: su prioridad es anotar.
     * Se implementa en la Versión dos.
     * @param partido partido en curso
     */
    @Override
    public void decidirAccion(Partido partido) {
        // Versión dos
    }
}
