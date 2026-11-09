package domain;

/**
 * Jugador del partido. Se mueve, salta y patea. Tiene goles, vida, estadísticas y puntos.
 * (x, y) es el centro de la cabeza del personaje.
 */
public class Jugador {

    /** Radio del cuerpo del jugador. */
    public static final int RADIO = 25;
    /** Distancia extra a la que llega el pie al patear. */
    public static final double ALCANCE_PIE = 20;
    /** Daño base de una patada al rival. */
    public static final double DANIO_PATADA = 10;
    /** Segundos que el jugador queda aturdido cuando su vida llega a cero. */
    public static final double TIEMPO_ATURDIDO = 3;
    /** Fuerza horizontal que recibe el balón con una patada. */
    public static final double FUERZA_PATADA_X = 550;
    /** Fuerza vertical que recibe el balón con una patada (negativa es hacia arriba). */
    public static final double FUERZA_PATADA_Y = -380;

    private int numero;
    private Personaje personaje;
    private Estadisticas estadisticas;
    private double x;
    private double y;
    private double velocidadY;
    private boolean enSuelo;
    private boolean mirandoDerecha;
    private int goles;
    private double vida;
    private boolean aturdido;
    private double tiempoAturdido;
    private int puntos;

    /**
     * Crea un jugador con 0 goles, vida máxima y estadísticas en 1.
     * El jugador 1 mira a la derecha y el jugador 2 a la izquierda.
     * @param numero número del jugador (1 o 2)
     * @param personaje personaje (país) elegido
     * @param x posición horizontal inicial
     * @param y posición vertical inicial
     */
    public Jugador(int numero, Personaje personaje, double x, double y) {
        this.numero = numero;
        this.personaje = personaje;
        this.estadisticas = new Estadisticas();
        this.x = x;
        this.y = y;
        this.enSuelo = true;
        this.mirandoDerecha = (numero == 1);
        this.vida = estadisticas.getVidaMax();
    }

    /**
     * Mueve el jugador a la izquierda un paso, si no está aturdido.
     */
    public void moverIzquierda() {
        if (aturdido) return;
        x -= estadisticas.getVelocidad();
        mirandoDerecha = false;
    }

    /**
     * Mueve el jugador a la derecha un paso, si no está aturdido.
     */
    public void moverDerecha() {
        if (aturdido) return;
        x += estadisticas.getVelocidad();
        mirandoDerecha = true;
    }

    /**
     * Hace saltar al jugador si está en el suelo y no está aturdido.
     */
    public void saltar() {
        if (aturdido || !enSuelo) return;
        velocidadY = -estadisticas.getFuerzaSalto();
        enSuelo = false;
    }

    /**
     * Patea. Si alcanza el balón, solo lo impulsa. Si no, y alcanza al rival,
     * le hace 10 x multiplicadorKick de daño.
     * @param balon balón del partido
     * @param rival jugador contrario
     */
    public void patear(Balon balon, Jugador rival) {
        if (aturdido) return;
        if (tocaBalon(balon)) {
            double direccion = mirandoDerecha ? 1 : -1;
            balon.recibirPatada(direccion * FUERZA_PATADA_X, FUERZA_PATADA_Y);
        } else if (alcanzaRival(rival)) {
            rival.recibirDanio(DANIO_PATADA * estadisticas.getMultiplicadorKick());
        }
    }

    /**
     * Indica si el balón está al alcance del jugador (cuerpo + pie).
     * @param balon balón del partido
     * @return true si lo alcanza
     */
    public boolean tocaBalon(Balon balon) {
        double dx = balon.getX() - x;
        double dy = balon.getY() - (y + RADIO);
        double distancia = Math.sqrt(dx * dx + dy * dy);
        return distancia <= RADIO * 2 + balon.getRadio() + ALCANCE_PIE;
    }

    /**
     * Indica si el rival está al alcance del pie.
     * @param rival jugador contrario
     * @return true si lo alcanza
     */
    public boolean alcanzaRival(Jugador rival) {
        double dx = Math.abs(rival.getX() - x);
        double dy = Math.abs(rival.getY() - y);
        return dx <= RADIO * 2 + ALCANCE_PIE && dy <= RADIO * 2;
    }

    /**
     * Resta vida al jugador. La vida nunca queda menor que cero.
     * Si llega a cero, queda aturdido 3 segundos.
     * @param danio cantidad de vida que se resta
     */
    public void recibirDanio(double danio) {
        vida = Math.max(0, vida - danio);
        if (vida == 0 && !aturdido) {
            aturdido = true;
            tiempoAturdido = TIEMPO_ATURDIDO;
        }
    }

    /**
     * Suma un gol al jugador.
     */
    public void anotarGol() {
        goles++;
    }

    /**
     * Suma puntos al jugador (100 al ganar, 25 al perder).
     * @param cantidad puntos que se suman
     */
    public void sumarPuntos(int cantidad) {
        puntos += cantidad;
    }

    /**
     * Actualiza el jugador: gravedad, límites de la cancha y tiempo de aturdimiento.
     * Al terminar el aturdimiento, la vida vuelve a su máximo.
     * @param segundos tiempo que pasó desde la última actualización
     * @param cancha cancha del partido
     */
    public void actualizar(double segundos, Cancha cancha) {
        if (aturdido) {
            tiempoAturdido -= segundos;
            if (tiempoAturdido <= 0) {
                aturdido = false;
                tiempoAturdido = 0;
                vida = estadisticas.getVidaMax();
            }
        }
        velocidadY += cancha.getGravedad() * segundos;
        y += velocidadY * segundos;
        double sueloCabeza = cancha.getNivelPiso() - RADIO * 2;
        if (y >= sueloCabeza) {
            y = sueloCabeza;
            velocidadY = 0;
            enSuelo = true;
        }
        x = Math.max(RADIO, Math.min(cancha.getAncho() - RADIO, x));
    }

    /**
     * Pone el jugador en una posición, quieto y en el suelo.
     * @param x nueva posición horizontal
     * @param y nueva posición vertical
     */
    public void reiniciar(double x, double y) {
        this.x = x;
        this.y = y;
        velocidadY = 0;
        enSuelo = true;
        mirandoDerecha = (numero == 1);
    }

    /**
     * @return número del jugador
     */
    public int getNumero() { return numero; }

    /**
     * @return goles anotados
     */
    public int getGoles() { return goles; }

    /**
     * @return vida actual
     */
    public double getVida() { return vida; }

    /**
     * @return personaje del jugador
     */
    public Personaje getPersonaje() { return personaje; }

    /**
     * @return estadísticas del jugador
     */
    public Estadisticas getEstadisticas() { return estadisticas; }

    /**
     * @return puntos acumulados
     */
    public int getPuntos() { return puntos; }

    /**
     * @return true si está aturdido
     */
    public boolean estaAturdido() { return aturdido; }

    /**
     * @return posición horizontal
     */
    public double getX() { return x; }

    /**
     * @return posición vertical
     */
    public double getY() { return y; }

    /**
     * @return radio de la cabeza
     */
    public int getRadio() { return RADIO; }

    /**
     * @return true si mira a la derecha
     */
    public boolean estaMirandoDerecha() { return mirandoDerecha; }
}
