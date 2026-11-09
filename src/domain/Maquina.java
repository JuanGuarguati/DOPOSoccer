package domain;

/**
 * Jugador controlado por la máquina. Cada perfil decide sus acciones de forma distinta.
 * Se implementa en la Versión dos (jugador vs máquina).
 */
public abstract class Maquina extends Jugador {

    /**
     * Crea un jugador máquina.
     * @param numero número del jugador (1 o 2)
     * @param personaje personaje que usa
     * @param x posición horizontal inicial
     * @param y posición vertical inicial
     */
    public Maquina(int numero, Personaje personaje, double x, double y) {
        super(numero, personaje, x, y);
    }

    /**
     * Decide y ejecuta la acción de la máquina según el estado del partido.
     * @param partido partido en curso
     */
    public abstract void decidirAccion(Partido partido);
}
