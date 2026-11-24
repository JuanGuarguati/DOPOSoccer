package domain;

/**
 * Efecto que deja un tiro de poder en el rival (Anexo 1). Cada efecto dice qué puede hacer
 * el jugador mientras dura y a qué fracción de su velocidad se mueve.
 * Un efecto nuevo reemplaza al anterior: los efectos no se acumulan.
 */
public enum Efecto {
    /** Sin efecto: juega normal. */
    NINGUNO("Normal", true, true, true, 1.0),
    /** Aura Azul (Corea del Sur): el rival es lanzado hacia su propio arco y no se controla. */
    EMPUJE("Empujado", false, false, false, 1.0),
    /** Rayo (Camerún): no puede saltar y se mueve al 50 % de su velocidad. */
    ELECTROCUTADO("Electrocutado", true, false, true, 0.5),
    /** Tornado (Nigeria): lanzado al aire, no puede moverse, saltar ni patear. */
    INCONSCIENTE("Inconsciente", false, false, false, 0.0),
    /** Ilusión (Estados Unidos): no sabe cuál es el balón real (la máquina persigue uno falso). */
    CONFUSION("Confundido", true, true, true, 1.0);

    private final String nombre;
    private final boolean mueve;
    private final boolean salta;
    private final boolean patea;
    private final double factorVelocidad;

    /**
     * Crea un efecto.
     * @param nombre nombre para mostrar
     * @param mueve true si el jugador se puede mover a los lados
     * @param salta true si el jugador puede saltar
     * @param patea true si el jugador puede patear
     * @param factorVelocidad fracción de la velocidad con que se mueve
     */
    Efecto(String nombre, boolean mueve, boolean salta, boolean patea, double factorVelocidad) {
        this.nombre = nombre;
        this.mueve = mueve;
        this.salta = salta;
        this.patea = patea;
        this.factorVelocidad = factorVelocidad;
    }

    /**
     * @return true si con este efecto el jugador se puede mover
     */
    public boolean permiteMoverse() { return mueve; }

    /**
     * @return true si con este efecto el jugador puede saltar
     */
    public boolean permiteSaltar() { return salta; }

    /**
     * @return true si con este efecto el jugador puede patear
     */
    public boolean permitePatear() { return patea; }

    /**
     * @return fracción de la velocidad con que se mueve (1 = normal)
     */
    public double getFactorVelocidad() { return factorVelocidad; }

    /**
     * @return nombre para mostrar
     */
    public String getNombre() { return nombre; }
}
