package domain;

/**
 * Estadísticas de un jugador: speed, kick, jump y survival.
 * Todas empiezan en 1 y pueden subir hasta 10. Aplican a todos los personajes del jugador.
 */
public class Estadisticas {

    /** Valor mínimo de una estadística. */
    public static final int MINIMO = 1;
    /** Valor máximo de una estadística. */
    public static final int MAXIMO = 10;
    /** Desplazamiento base del jugador, en píxeles por paso. */
    public static final double VELOCIDAD_BASE = 4;
    /** Velocidad vertical base del salto, en píxeles por segundo. */
    public static final double SALTO_BASE = 480;

    private int speed;
    private int kick;
    private int jump;
    private int survival;

    /**
     * Crea las estadísticas con todos los valores en 1.
     */
    public Estadisticas() {
        speed = MINIMO;
        kick = MINIMO;
        jump = MINIMO;
        survival = MINIMO;
    }

    /**
     * Calcula el multiplicador de daño: 1 + 0.10 * (kick - 1).
     * @return multiplicador del daño que hace el jugador
     */
    public double getMultiplicadorKick() {
        return 1 + 0.10 * (kick - 1);
    }

    /**
     * Calcula la vida máxima: 100 * (1 + 0.10 * (survival - 1)).
     * @return vida máxima del jugador
     */
    public double getVidaMax() {
        return 100 * (1 + 0.10 * (survival - 1));
    }

    /**
     * Calcula cuántos píxeles avanza el jugador en cada paso.
     * Cada punto de speed por encima de 1 suma un 10 %.
     * @return desplazamiento por paso
     */
    public double getVelocidad() {
        return VELOCIDAD_BASE * (1 + 0.10 * (speed - 1));
    }

    /**
     * Calcula la velocidad inicial del salto.
     * Cada punto de jump por encima de 1 suma un 10 %.
     * @return velocidad vertical del salto
     */
    public double getFuerzaSalto() {
        return SALTO_BASE * (1 + 0.10 * (jump - 1));
    }

    /**
     * Sube en 1 una estadística.
     * @param estadistica nombre de la estadística: "speed", "kick", "jump" o "survival"
     * @throws DOPOSoccerException si la estadística no existe o ya está en 10
     */
    public void mejorar(String estadistica) throws DOPOSoccerException {
        if (estadistica == null) throw new DOPOSoccerException(DOPOSoccerException.ESTADISTICA_MAXIMA);
        switch (estadistica) {
            case "speed":
                speed = subir(speed);
                break;
            case "kick":
                kick = subir(kick);
                break;
            case "jump":
                jump = subir(jump);
                break;
            case "survival":
                survival = subir(survival);
                break;
            default:
                throw new DOPOSoccerException(DOPOSoccerException.ESTADISTICA_MAXIMA);
        }
    }

    /**
     * Suma 1 a un valor si no ha llegado al máximo.
     * @param valor valor actual
     * @return valor + 1
     * @throws DOPOSoccerException si el valor ya está en el máximo
     */
    private int subir(int valor) throws DOPOSoccerException {
        if (valor >= MAXIMO) throw new DOPOSoccerException(DOPOSoccerException.ESTADISTICA_MAXIMA);
        return valor + 1;
    }

    /**
     * @return valor de speed
     */
    public int getSpeed() { return speed; }

    /**
     * @return valor de kick
     */
    public int getKick() { return kick; }

    /**
     * @return valor de jump
     */
    public int getJump() { return jump; }

    /**
     * @return valor de survival
     */
    public int getSurvival() { return survival; }
}
