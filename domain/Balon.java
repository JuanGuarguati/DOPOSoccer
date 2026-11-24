package domain;

import dto.ComandoDibujo;
import java.util.ArrayList;
import java.util.List;

/**
 * Balón del partido. Le afecta la gravedad y rebota en el piso, los bordes y los arcos.
 * Puede ir como tiro de poder: guarda quién lo lanzó, el nombre del tiro, si le afecta
 * la gravedad y si rebota como tornado. En la Ilusión también lleva sus balones falsos.
 * Es Movible (se mueve), Dibujable (arma su ComandoDibujo) y Pateable (sale disparado con una patada).
 */
public class Balon implements Movible, Dibujable, Pateable {

    /** Radio del balón. */
    public static final int RADIO = 15;
    /** Fracción de velocidad que conserva el balón al rebotar en el piso. */
    public static final double REBOTE_PISO = 0.7;
    /** Fracción de velocidad que conserva el balón al rebotar en una pared. */
    public static final double REBOTE_PARED = 0.8;
    /** Factor de roce que frena el balón en cada actualización. */
    public static final double ROCE = 0.99;
    /** Velocidad vertical por debajo de la cual el balón deja de rebotar en el piso. */
    public static final double VELOCIDAD_MINIMA_REBOTE = 30;
    /** Segundos máximos que dura un tiro de poder si no golpea a nadie ni entra al arco. */
    public static final double DURACION_TIRO = 2.5;
    /** Velocidad hacia arriba con la que rebota el Tornado cada vez que toca el piso. */
    public static final double REBOTE_TORNADO = 260;

    private double x;
    private double y;
    private double velocidadX;
    private double velocidadY;
    private int radio;
    /** Altura del balón antes del último movimiento (para saber de qué lado llegó al travesaño). */
    private double yAnterior;
    /** Jugador que lanzó el tiro de poder; null si el balón va normal. */
    private Jugador tirador;
    /** Nombre del tiro de poder que lleva el balón; null si va normal. */
    private String nombreTiro;
    /** Segundos que le quedan al tiro de poder. */
    private double tiempoTiro;
    /** false si el tiro va recto (no le afecta la gravedad). */
    private boolean conGravedad;
    /** true si el tiro rebota como tornado a ras del suelo. */
    private boolean tornado;
    /** Balones falsos de la Ilusión (se ven igual que este). */
    private List<Balon> senuelos;
    /** Segundos que les quedan a los balones falsos. */
    private double tiempoSenuelos;
    /** Cantidad de tiros de poder lanzados con este balón (la interfaz lo usa para la cámara lenta). */
    private int tirosLanzados;

    /**
     * Crea un balón quieto en la posición dada.
     * @param x posición horizontal del centro
     * @param y posición vertical del centro
     */
    public Balon(double x, double y) {
        this.x = x;
        this.y = y;
        this.yAnterior = y;
        this.radio = RADIO;
        this.conGravedad = true;
        this.senuelos = new ArrayList<>();
    }

    /**
     * Mueve el balón durante un tiempo (interfaz Movible): aplica la gravedad (si el tiro no
     * va recto), avanza en sub-pasos de a lo sumo un radio (así no atraviesa el travesaño ni los bordes
     * aunque vaya muy rápido), rebota, descuenta el tiempo del tiro y mueve los balones falsos.
     * @param segundos tiempo que pasó desde la última actualización
     * @param cancha cancha donde se mueve
     */
    @Override
    public void mover(double segundos, Cancha cancha) {
        if (conGravedad) aplicarGravedad(cancha.getGravedad() * segundos);
        double recorrido = Math.hypot(velocidadX, velocidadY) * segundos;
        int pasos = Math.max(1, (int) Math.ceil(recorrido / radio));
        double paso = segundos / pasos;
        for (int i = 0; i < pasos; i++) {
            yAnterior = y;
            x += velocidadX * paso;
            y += velocidadY * paso;
            rebotar(cancha);
        }
        if (tieneTiro()) {
            tiempoTiro -= segundos;
            if (tiempoTiro <= 0) terminarTiro();
        }
        moverSenuelos(segundos, cancha);
    }

    /**
     * Mueve los balones falsos y los quita cuando se les acaba el tiempo.
     * @param segundos tiempo que pasó
     * @param cancha cancha del partido
     */
    private void moverSenuelos(double segundos, Cancha cancha) {
        if (senuelos.isEmpty()) return;
        tiempoSenuelos -= segundos;
        if (tiempoSenuelos <= 0) {
            senuelos.clear();
            return;
        }
        for (Balon falso : senuelos) falso.mover(segundos, cancha);
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
     * El Tornado rebota siempre a la misma altura; un tiro que choca con un borde lateral termina.
     * @param cancha cancha donde está el balón
     */
    public void rebotar(Cancha cancha) {
        int piso = cancha.getNivelPiso();
        if (y + radio > piso) {
            y = piso - radio;
            if (tornado) {
                velocidadY = -REBOTE_TORNADO;
            } else {
                velocidadY = -velocidadY * REBOTE_PISO;
                velocidadX *= ROCE;
                if (Math.abs(velocidadY) < VELOCIDAD_MINIMA_REBOTE) velocidadY = 0;
            }
        }
        if (y - radio < 0) {
            y = radio;
            velocidadY = -velocidadY * REBOTE_PARED;
        }
        if (x - radio < 0 || x + radio > cancha.getAncho()) {
            x = x - radio < 0 ? radio : cancha.getAncho() - radio;
            velocidadX = -velocidadX * REBOTE_PARED;
            terminarTiro();
        }
        for (Arco arco : new Arco[] {cancha.getArcoIzquierdo(), cancha.getArcoDerecho()}) {
            if (arco.chocaConTravesano(this)) {
                boolean veniaDeArriba = yAnterior <= arco.getY();
                y = veniaDeArriba ? arco.getY() - radio : arco.getY() + radio;
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
     * Lanza el balón como tiro de poder (lo llama el personaje en su tiroDePoder).
     * @param tirador jugador que lanza el tiro
     * @param nombre nombre del tiro (por ejemplo "Rayo")
     * @param fuerzaX velocidad horizontal
     * @param fuerzaY velocidad vertical (negativa = hacia arriba)
     * @param gravedad false si el tiro va recto
     * @param comoTornado true si rebota como tornado
     */
    public void lanzarTiro(Jugador tirador, String nombre, double fuerzaX, double fuerzaY,
                           boolean gravedad, boolean comoTornado) {
        this.tirador = tirador;
        this.nombreTiro = nombre;
        this.tiempoTiro = DURACION_TIRO;
        this.conGravedad = gravedad;
        this.tornado = comoTornado;
        tirosLanzados++;
        recibirPatada(fuerzaX, fuerzaY);
    }

    /**
     * Pone el balón en una posición y lo hace rebotar contra una superficie (cuerpo o pie de un jugador).
     * Si el balón iba hacia la superficie, invierte esa parte de la velocidad y la multiplica por el rebote.
     * @param nuevoX nueva posición horizontal (afuera de la superficie)
     * @param nuevoY nueva posición vertical (afuera de la superficie)
     * @param normalX componente horizontal de la dirección que sale de la superficie (largo 1)
     * @param normalY componente vertical de la dirección que sale de la superficie (largo 1)
     * @param rebote fracción de velocidad que conserva (0 a 1)
     */
    public void rebotarEn(double nuevoX, double nuevoY, double normalX, double normalY, double rebote) {
        x = nuevoX;
        y = nuevoY;
        double haciaSuperficie = velocidadX * normalX + velocidadY * normalY;
        if (haciaSuperficie < 0) {
            velocidadX -= (1 + rebote) * haciaSuperficie * normalX;
            velocidadY -= (1 + rebote) * haciaSuperficie * normalY;
        }
    }

    /**
     * @return cantidad de tiros de poder lanzados con este balón
     */
    public int getTirosLanzados() { return tirosLanzados; }

    /**
     * Crea balones falsos que salen desde este balón en distintas direcciones (Ilusión).
     * Se ven igual que el real y desaparecen cuando pasa la duración.
     * @param cantidad número de balones falsos
     * @param duracion segundos que duran
     */
    public void crearSenuelos(int cantidad, double duracion) {
        senuelos.clear();
        tiempoSenuelos = duracion;
        for (int i = 0; i < cantidad; i++) {
            Balon falso = new BalonFalso(this, x, y);
            double angulo = -Math.PI / 2 + Math.PI * (i + 1) / (cantidad + 1);
            double rapidez = Math.max(500, Math.abs(velocidadX));
            double sentido = velocidadX < 0 ? -1 : 1;
            falso.recibirPatada(sentido * rapidez * Math.cos(angulo), rapidez * Math.sin(angulo) * 0.8);
            senuelos.add(falso);
        }
    }

    /**
     * Termina el tiro de poder: el balón vuelve a ser normal (los balones falsos siguen hasta que se acaben).
     */
    public void terminarTiro() {
        tirador = null;
        nombreTiro = null;
        tiempoTiro = 0;
        conGravedad = true;
        tornado = false;
    }

    /**
     * @return true si el balón va como tiro de poder
     */
    public boolean tieneTiro() { return tirador != null; }

    /**
     * Indica si el tiro de poder lo lanzó este jugador.
     * @param jugador jugador que se revisa
     * @return true si el balón va como tiro de poder de ese jugador
     */
    public boolean esTiroDe(Jugador jugador) { return tirador != null && tirador == jugador; }

    /**
     * @return jugador que lanzó el tiro de poder, o null si el balón va normal
     */
    public Jugador getTirador() { return tirador; }

    /**
     * @return nombre del tiro de poder, o null si el balón va normal
     */
    public String getNombreTiro() { return nombreTiro; }

    /**
     * @return copia de la lista de balones falsos de la Ilusión
     */
    public List<Balon> getSenuelos() { return new ArrayList<>(senuelos); }

    /**
     * Indica si el pie del pateador alcanza el balón (interfaz Pateable).
     * @param pateador jugador que patea
     * @return true si lo alcanza
     */
    @Override
    public boolean estaAlAlcanceDe(Jugador pateador) {
        return pateador.tocaBalon(this);
    }

    /**
     * Recibe una patada (interfaz Pateable). Si el pateador tiene el aura, sale su tiro de poder;
     * si no, sale hacia donde mira el pateador y hacia arriba.
     * @param pateador jugador que patea
     */
    @Override
    public void serPateado(Jugador pateador) {
        if (pateador.usarPoder()) {
            pateador.getPersonaje().tiroDePoder(this, pateador);
            return;
        }
        double direccion = pateador.estaMirandoDerecha() ? 1 : -1;
        recibirPatada(direccion * Jugador.FUERZA_PATADA_X, Jugador.FUERZA_PATADA_Y);
    }

    /**
     * Arma los datos para dibujar el balón (interfaz Dibujable): el cuadro que lo encierra
     * y, si va como tiro de poder, el nombre del tiro.
     * @return comando de dibujo del balón
     */
    @Override
    public ComandoDibujo aComandoDibujo() {
        return new ComandoDibujo(ComandoDibujo.Forma.BALON, (int) x - radio, (int) y - radio,
            2 * radio, 2 * radio, nombreTiro);
    }

    /**
     * Pone el balón quieto en una posición (interfaz Movible). Termina el tiro y quita los balones falsos.
     * @param x nueva posición horizontal
     * @param y nueva posición vertical
     */
    @Override
    public void reiniciar(double x, double y) {
        this.x = x;
        this.y = y;
        yAnterior = y;
        velocidadX = 0;
        velocidadY = 0;
        terminarTiro();
        senuelos.clear();
    }

    /**
     * @return posición horizontal del centro
     */
    @Override
    public double getX() { return x; }

    /**
     * @return posición vertical del centro
     */
    @Override
    public double getY() { return y; }

    /**
     * @return radio del balón
     */
    @Override
    public int getRadio() { return radio; }

    /**
     * @return rapidez del balón: módulo de su velocidad (interfaz Movible)
     */
    @Override
    public double getVelocidad() { return Math.hypot(velocidadX, velocidadY); }

    /**
     * @return velocidad horizontal
     */
    public double getVelocidadX() { return velocidadX; }

    /**
     * @return velocidad vertical
     */
    public double getVelocidadY() { return velocidadY; }
}
