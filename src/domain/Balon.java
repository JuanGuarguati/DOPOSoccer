package domain;

/**
 * Balón del partido. Le afecta la gravedad y rebota en el piso, los bordes y los arcos.
 */
public class Balon {

    /** Radio del balón. */
    public static final int RADIO = 15;
    /** Fracción de velocidad que conserva el balón al rebotar en el piso. */
    public static final double REBOTE_PISO = 0.7;
    /** Fracción de velocidad que conserva el balón al rebotar en una pared. */
    public static final double REBOTE_PARED = 0.8;
    /** Factor de roce que frena el balón en cada actualización. */
    public static final double ROCE = 0.99;

    private double x;
    private double y;
    private double velocidadX;
    private double velocidadY;
    private int radio;

    /**
     * Crea un balón quieto en la posición dada.
     * @param x posición horizontal del centro
     * @param y posición vertical del centro
     */
    public Balon(double x, double y) {
        this.x = x;
        this.y = y;
        this.radio = RADIO;
    }

    /**
     * Mueve el balón durante un tiempo: aplica la gravedad, avanza y rebota.
     * @param segundos tiempo que pasó desde la última actualización
     * @param cancha cancha donde se mueve
     */
    public void mover(double segundos, Cancha cancha) {
        aplicarGravedad(cancha.getGravedad() * segundos);
        x += velocidadX * segundos;
        y += velocidadY * segundos;
        rebotar(cancha);
    }

    /**
     * Aumenta la velocidad vertical (hacia abajo).
     * @param gravedad cantidad que se suma a la velocidad vertical
     */
    public void aplicarGravedad(double gravedad) {
        velocidadY += gravedad;
    }

    /**
     * Hace rebotar el balón en el piso, el techo, los bordes y los travesaños.
     * @param cancha cancha donde está el balón
     */
    public void rebotar(Cancha cancha) {
        int piso = cancha.getNivelPiso();
        if (y + radio > piso) {
            y = piso - radio;
            velocidadY = -velocidadY * REBOTE_PISO;
            velocidadX *= ROCE;
            if (Math.abs(velocidadY) < 30) velocidadY = 0;
        }
        if (y - radio < 0) {
            y = radio;
            velocidadY = -velocidadY * REBOTE_PARED;
        }
        if (x - radio < 0) {
            x = radio;
            velocidadX = -velocidadX * REBOTE_PARED;
        }
        if (x + radio > cancha.getAncho()) {
            x = cancha.getAncho() - radio;
            velocidadX = -velocidadX * REBOTE_PARED;
        }
        for (Arco arco : new Arco[] {cancha.getArcoIzquierdo(), cancha.getArcoDerecho()}) {
            if (arco.chocaConTravesano(this)) {
                y = velocidadY > 0 ? arco.getY() - radio : arco.getY() + radio;
                velocidadY = -velocidadY * REBOTE_PARED;
            }
        }
    }

    /**
     * Cambia la velocidad del balón por una patada o un cabezazo.
     * @param fuerzaX nueva velocidad horizontal
     * @param fuerzaY nueva velocidad vertical (negativa = hacia arriba)
     */
    public void recibirPatada(double fuerzaX, double fuerzaY) {
        velocidadX = fuerzaX;
        velocidadY = fuerzaY;
    }

    /**
     * Pone el balón quieto en una posición.
     * @param x nueva posición horizontal
     * @param y nueva posición vertical
     */
    public void reiniciar(double x, double y) {
        this.x = x;
        this.y = y;
        velocidadX = 0;
        velocidadY = 0;
    }

    /**
     * @return posición horizontal del centro
     */
    public double getX() { return x; }

    /**
     * @return posición vertical del centro
     */
    public double getY() { return y; }

    /**
     * @return radio del balón
     */
    public int getRadio() { return radio; }

    /**
     * @return velocidad horizontal
     */
    public double getVelocidadX() { return velocidadX; }

    /**
     * @return velocidad vertical
     */
    public double getVelocidadY() { return velocidadY; }
}
