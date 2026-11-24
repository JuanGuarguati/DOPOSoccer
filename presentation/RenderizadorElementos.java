package presentation;

import domain.Camerun;
import domain.CoreaDelSur;
import domain.EstadosUnidos;
import domain.Jugador;
import domain.Nigeria;
import dto.ComandoDibujo;
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Path2D;
import java.awt.geom.Point2D;

/**
 * Pinta los elementos del partido a partir de sus ComandoDibujo (DTO).
 * No conoce el estado del dominio: solo lee los datos del comando (de Jugador solo toma las
 * medidas del cuerpo y del pie, para que el dibujo coincida con los choques).
 * Cada forma tiene su propio método:
 * - la cancha lleva tribuna de tres niveles con público, una barra de anuncios que se mueve
 *   y el campo en perspectiva con sus líneas;
 * - el arco va sombreado y con malla;
 * - el balón brilla según su tiro de poder;
 * - el jugador muestra su aura de flama, su efecto y el pie siempre (al patear solo se mueve).
 * Lo que se anima (público, anuncios, flamas) usa el tiempo de animación que le pasa el panel.
 * Los trazos y colores se crean una sola vez como constantes (no en cada cuadro).
 */
public final class RenderizadorElementos {

    /** Gotas que se dibujan cuando el jugador recibe un golpe. */
    public static final int GOTAS = 5;
    /** Franjas del pasto. */
    public static final int FRANJAS = 12;
    /** Separación entre las rayas del uniforme. */
    public static final int SEPARACION_RAYAS = 8;
    /** Tamaño del ojo del jugador. */
    public static final int OJO = 12;
    /** Margen del brillo alrededor del balón. */
    public static final int BRILLO = 10;
    /** Altura donde empieza la tribuna (debajo del marcador). */
    public static final int TRIBUNA_ARRIBA = 104;
    /** Niveles de la tribuna. */
    public static final int NIVELES_TRIBUNA = 3;
    /** Alto de cada nivel de la tribuna. */
    public static final int ALTO_NIVEL = 118;
    /** Separación horizontal entre los espectadores. */
    public static final int SEPARACION_PUBLICO = 24;
    /** Alto de la barra de anuncios. */
    public static final int ALTO_ANUNCIOS = 34;
    /** Velocidad de la barra de anuncios (píxeles por segundo). */
    public static final int VELOCIDAD_ANUNCIOS = 110;
    /** Texto de la barra de anuncios. */
    public static final String TEXTO_ANUNCIO = "DOPOSoccer";
    /** Ancho de cada anuncio de la barra. */
    public static final int ANCHO_ANUNCIO = 260;
    /** Altura del salto de los cabezones de la tribuna. */
    public static final int SALTO_PUBLICO = 9;
    /** Separación de los hilos de la malla del arco. */
    public static final int MALLA = 10;
    /** Puntos con que se arma el contorno de una flama. */
    public static final int PUNTOS_FLAMA = 56;
    /** Ángulo máximo (grados) que sube el pie al patear. */
    public static final int ANGULO_PATADA = 55;

    /** Trazo normal (1 px). */
    private static final Stroke TRAZO_NORMAL = new BasicStroke(1);
    /** Trazo de los postes y travesaños. */
    private static final Stroke TRAZO_ARCO = new BasicStroke(6, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND);
    /** Trazo del brillo de los postes. */
    private static final Stroke TRAZO_BRILLO_ARCO = new BasicStroke(2);
    /** Trazo de las líneas de la cancha. */
    private static final Stroke TRAZO_LINEAS = new BasicStroke(3);
    /** Trazo del borde de la cabeza. */
    private static final Stroke TRAZO_CABEZA = new BasicStroke(4);
    /** Trazo del rayo y del remolino. */
    private static final Stroke TRAZO_EFECTO = new BasicStroke(3);
    /** Color del techo de la tribuna. */
    private static final Color TECHO = new Color(40, 45, 70);
    /** Color de los escalones de la tribuna (arriba y abajo de cada nivel). */
    private static final Color GRADA_CLARA = new Color(150, 155, 175);
    private static final Color GRADA_OSCURA = new Color(95, 100, 125);
    /** Colores de la ropa del público. */
    private static final Color[] ROPA_PUBLICO = {
        new Color(230, 60, 60), new Color(40, 110, 230), new Color(250, 210, 40), new Color(30, 170, 90),
        new Color(245, 245, 245), new Color(160, 70, 200), new Color(250, 140, 30), new Color(20, 20, 40)
    };
    /** Colores de piel del público. */
    private static final Color[] PIEL_PUBLICO = {
        new Color(255, 214, 170), new Color(210, 160, 110), new Color(140, 95, 60), new Color(90, 60, 40)
    };
    /** Fondo de la barra de anuncios. */
    private static final Color FONDO_ANUNCIOS = new Color(15, 25, 80);
    /** Colores del texto de los anuncios (se alternan). */
    private static final Color[] COLORES_ANUNCIO = {Estilo.ACENTO, Color.WHITE, Estilo.PRIMARIO};
    /** Fuente de los anuncios. */
    private static final Font FUENTE_ANUNCIO = new Font(Font.SANS_SERIF, Font.BOLD | Font.ITALIC, 22);
    /** Color de las líneas de la cancha. */
    private static final Color LINEAS = new Color(255, 255, 255, 210);
    /** Borde de adelante de la cancha (tierra). */
    private static final Color BORDE_CANCHA = new Color(35, 110, 45);
    /** Sombra del arco y de los jugadores sobre el pasto. */
    private static final Color SOMBRA = new Color(0, 0, 0, 70);
    /** Fondo del arco (adentro, lejos y cerca). */
    private static final Color FONDO_ARCO_LEJOS = new Color(20, 30, 40, 200);
    private static final Color FONDO_ARCO_CERCA = new Color(80, 100, 110, 90);
    /** Color de la malla del arco. */
    private static final Color MALLA_ARCO = new Color(255, 255, 255, 150);
    /** Color de la sombra de los postes. */
    private static final Color SOMBRA_POSTE = new Color(170, 170, 185);
    /** Capas del aura de flama (de afuera hacia adentro). */
    private static final Color[] CAPAS_AURA = {
        new Color(255, 40, 0, 110), new Color(255, 120, 0, 150), new Color(255, 210, 40, 170), new Color(255, 255, 200, 120)
    };
    /** Chispas del aura. */
    private static final Color CHISPA = new Color(255, 230, 120, 220);
    /** Color del Aura Azul. */
    private static final Color TIRO_AZUL = new Color(40, 120, 255, 150);
    /** Color del Rayo. */
    private static final Color TIRO_RAYO = new Color(255, 230, 0, 170);
    /** Color del Tornado. */
    private static final Color TIRO_TORNADO = new Color(30, 160, 80, 150);
    /** Color de la Ilusión. */
    private static final Color TIRO_ILUSION = new Color(170, 80, 255, 120);
    /** Color del zapato y de su suela. */
    private static final Color ZAPATO = new Color(25, 25, 30);
    private static final Color SUELA = new Color(235, 235, 235);

    /**
     * No se crean objetos de esta clase.
     */
    private RenderizadorElementos() { }

    /**
     * Pinta un elemento sin animación (tiempo 0). Lo usa la portada.
     * @param g contexto gráfico
     * @param c comando de dibujo del elemento
     */
    public static void dibujar(Graphics2D g, ComandoDibujo c) {
        dibujar(g, c, 0);
    }

    /**
     * Pinta un elemento según su forma.
     * @param g contexto gráfico
     * @param c comando de dibujo del elemento
     * @param tiempo segundos de animación (mueven el público, los anuncios y las flamas)
     */
    public static void dibujar(Graphics2D g, ComandoDibujo c, double tiempo) {
        switch (c.forma) {
            case CANCHA: dibujarCancha(g, c, tiempo); break;
            case ARCO: dibujarArco(g, c); break;
            case BALON: dibujarBalon(g, c); break;
            default: dibujarJugador(g, c, tiempo);
        }
    }

    /**
     * Pinta el fondo: cielo, tribuna de tres niveles, barra de anuncios, campo en perspectiva y su borde.
     * @param g contexto gráfico
     * @param c comando de la cancha (y es el nivel del piso)
     * @param tiempo segundos de animación
     */
    private static void dibujarCancha(Graphics2D g, ComandoDibujo c, double tiempo) {
        int piso = c.y;
        g.setPaint(new GradientPaint(0, 0, Estilo.CIELO, 0, TRIBUNA_ARRIBA, Estilo.CIELO_ABAJO));
        g.fillRect(c.x, 0, c.ancho, TRIBUNA_ARRIBA);
        int abajoTribuna = dibujarTribuna(g, c.ancho, tiempo);
        dibujarAnuncios(g, c.ancho, abajoTribuna, tiempo);
        dibujarCampo(g, c.ancho, abajoTribuna + ALTO_ANUNCIOS, piso);
        g.setColor(BORDE_CANCHA);
        g.fillRect(c.x, piso, c.ancho, c.alto - piso);
        g.setColor(LINEAS);
        g.fillRect(c.x, piso, c.ancho, 2);
    }

    /**
     * Pinta la tribuna: techo y tres niveles escalonados, cada uno con dos filas de público.
     * Algunos espectadores son cabezones que saltan.
     * @param g contexto gráfico
     * @param ancho ancho de la cancha
     * @param tiempo segundos de animación
     * @return altura donde termina la tribuna
     */
    private static int dibujarTribuna(Graphics2D g, int ancho, double tiempo) {
        g.setColor(TECHO);
        g.fillRect(0, TRIBUNA_ARRIBA - 8, ancho, 12);
        for (int nivel = 0; nivel < NIVELES_TRIBUNA; nivel++) {
            int arriba = TRIBUNA_ARRIBA + nivel * ALTO_NIVEL;
            g.setPaint(new GradientPaint(0, arriba, GRADA_OSCURA, 0, arriba + ALTO_NIVEL, GRADA_CLARA));
            g.fillRect(0, arriba, ancho, ALTO_NIVEL);
            g.setColor(TECHO);
            g.fillRect(0, arriba + ALTO_NIVEL - 6, ancho, 6);
            for (int fila = 0; fila < 2; fila++) {
                int base = arriba + ALTO_NIVEL / 2 + fila * (ALTO_NIVEL / 2 - 6) - 4;
                int escala = 6 + nivel + fila;
                int corrimiento = (nivel + fila) % 2 * SEPARACION_PUBLICO / 2;
                for (int x = corrimiento; x < ancho + SEPARACION_PUBLICO; x += SEPARACION_PUBLICO) {
                    dibujarEspectador(g, x, base, escala, mezclarSemilla(x / SEPARACION_PUBLICO, nivel * 2 + fila), tiempo);
                }
            }
        }
        return TRIBUNA_ARRIBA + NIVELES_TRIBUNA * ALTO_NIVEL;
    }

    /**
     * Revuelve la posición de un espectador para que sus colores parezcan al azar,
     * pero sean siempre los mismos en el mismo puesto.
     * @param columna columna del espectador
     * @param fila fila de la tribuna
     * @return número positivo que decide sus colores
     */
    private static int mezclarSemilla(int columna, int fila) {
        int h = columna * 73856093 ^ fila * 19349663;
        h ^= h >>> 13;
        h *= 0x5bd1e995;
        h ^= h >>> 15;
        return h & Integer.MAX_VALUE;
    }

    /**
     * Pinta un espectador: cuerpo y cabeza. Uno de cada tres es un cabezón grande que salta.
     * @param g contexto gráfico
     * @param x centro horizontal
     * @param base línea donde se apoya
     * @param tam tamaño de la cabeza
     * @param semilla número que decide sus colores y si salta (siempre igual para el mismo puesto)
     * @param tiempo segundos de animación
     */
    private static void dibujarEspectador(Graphics2D g, int x, int base, int tam, int semilla, double tiempo) {
        boolean cabezon = semilla % 3 == 0;
        int cabeza = cabezon ? tam + 4 : tam;
        int salto = cabezon ? (int) (Math.abs(Math.sin(tiempo * 6 + semilla % 100)) * SALTO_PUBLICO) : 0;
        int y = base - salto;
        g.setColor(ROPA_PUBLICO[semilla % ROPA_PUBLICO.length]);
        g.fillRoundRect(x - tam, y - tam * 2, tam * 2, tam * 2, tam, tam);
        if (cabezon && salto > SALTO_PUBLICO / 2) {
            g.fillRect(x - tam - 3, y - tam * 3, 3, tam);
            g.fillRect(x + tam, y - tam * 3, 3, tam);
        }
        g.setColor(PIEL_PUBLICO[(semilla / 3) % PIEL_PUBLICO.length]);
        g.fillOval(x - cabeza, y - tam * 2 - cabeza * 2 + 4, cabeza * 2, cabeza * 2);
        if (cabezon) {
            g.setColor(ROPA_PUBLICO[(semilla / 5) % ROPA_PUBLICO.length]);
            g.fillRect(x - cabeza, y - tam * 2 - cabeza - 2, cabeza * 2, Math.max(2, cabeza / 3));
        }
    }

    /**
     * Pinta la barra larga de anuncios con el texto "DOPOSoccer" moviéndose hacia la izquierda.
     * @param g contexto gráfico
     * @param ancho ancho de la cancha
     * @param y altura de la barra
     * @param tiempo segundos de animación
     */
    private static void dibujarAnuncios(Graphics2D g, int ancho, int y, double tiempo) {
        g.setColor(FONDO_ANUNCIOS);
        g.fillRect(0, y, ancho, ALTO_ANUNCIOS);
        Shape antes = g.getClip();
        g.clipRect(0, y, ancho, ALTO_ANUNCIOS);
        g.setFont(FUENTE_ANUNCIO);
        int corrimiento = (int) (tiempo * VELOCIDAD_ANUNCIOS) % ANCHO_ANUNCIO;
        int texto = g.getFontMetrics().stringWidth(TEXTO_ANUNCIO);
        for (int i = -1; i * ANCHO_ANUNCIO - corrimiento < ancho; i++) {
            int x = i * ANCHO_ANUNCIO - corrimiento;
            int indice = Math.floorMod(i + (int) (tiempo * VELOCIDAD_ANUNCIOS) / ANCHO_ANUNCIO, COLORES_ANUNCIO.length);
            g.setColor(COLORES_ANUNCIO[indice]);
            g.drawString(TEXTO_ANUNCIO, x + (ANCHO_ANUNCIO - texto) / 2, y + ALTO_ANUNCIOS - 9);
            g.fillOval(x + ANCHO_ANUNCIO - 6, y + ALTO_ANUNCIOS / 2 - 3, 6, 6);
        }
        g.setClip(antes);
        g.setColor(Estilo.ACENTO);
        g.fillRect(0, y, ancho, 2);
        g.fillRect(0, y + ALTO_ANUNCIOS - 2, ancho, 2);
    }

    /**
     * Pinta el campo visto en perspectiva (más angosto al fondo): franjas de pasto y las divisiones
     * de la cancha (bandas, línea central, círculo central, áreas y puntos).
     * @param g contexto gráfico
     * @param ancho ancho de la cancha
     * @param fondo altura del borde de atrás del campo
     * @param piso altura del borde de adelante (donde pisan los jugadores)
     */
    private static void dibujarCampo(Graphics2D g, int ancho, int fondo, int piso) {
        for (int i = 0; i < FRANJAS; i++) {
            g.setColor(i % 2 == 0 ? Estilo.PASTO : Estilo.PASTO_OSCURO);
            g.fill(cuadro(ancho, fondo, piso, (double) i / FRANJAS, 0, (i + 1.0) / FRANJAS, 1));
        }
        g.setColor(LINEAS);
        g.setStroke(TRAZO_LINEAS);
        g.draw(cuadro(ancho, fondo, piso, 0.02, 0.08, 0.98, 0.97));
        g.draw(linea(ancho, fondo, piso, 0.5, 0.08, 0.5, 0.97));
        g.draw(ovalo(ancho, fondo, piso, 0.5, 0.52, 0.09, 0.3));
        for (double lado : new double[] {0.02, 0.98}) {
            double dentro = lado < 0.5 ? 1 : -1;
            g.draw(cuadro(ancho, fondo, piso, lado, 0.25, lado + dentro * 0.13, 0.82));
            g.draw(cuadro(ancho, fondo, piso, lado, 0.38, lado + dentro * 0.05, 0.68));
            Point2D punto = proyectar(ancho, fondo, piso, lado + dentro * 0.09, 0.53);
            g.fillOval((int) punto.getX() - 3, (int) punto.getY() - 2, 6, 4);
        }
        Point2D centro = proyectar(ancho, fondo, piso, 0.5, 0.52);
        g.fillOval((int) centro.getX() - 4, (int) centro.getY() - 3, 8, 6);
        g.setStroke(TRAZO_NORMAL);
    }

    /**
     * Pasa un punto del campo (u de 0 a 1 a lo ancho, v de 0 atrás a 1 adelante) a la pantalla,
     * con perspectiva: al fondo el campo se ve más angosto.
     * @param ancho ancho de la cancha
     * @param fondo altura del borde de atrás
     * @param piso altura del borde de adelante
     * @param u posición a lo ancho (0 a 1)
     * @param v profundidad (0 atrás, 1 adelante)
     * @return punto en la pantalla
     */
    private static Point2D proyectar(int ancho, int fondo, int piso, double u, double v) {
        double anchoFila = ancho * (0.82 + 0.3 * v);
        return new Point2D.Double(ancho / 2.0 + (u - 0.5) * anchoFila, fondo + (piso - fondo) * v);
    }

    /**
     * Arma un cuadrilátero del campo en perspectiva.
     * @param ancho ancho de la cancha
     * @param fondo altura del borde de atrás
     * @param piso altura del borde de adelante
     * @param u1 lado izquierdo (0 a 1)
     * @param v1 lado de atrás (0 a 1)
     * @param u2 lado derecho (0 a 1)
     * @param v2 lado de adelante (0 a 1)
     * @return figura proyectada
     */
    private static Shape cuadro(int ancho, int fondo, int piso, double u1, double v1, double u2, double v2) {
        Path2D figura = new Path2D.Double();
        double[][] esquinas = {{u1, v1}, {u2, v1}, {u2, v2}, {u1, v2}};
        for (int i = 0; i < esquinas.length; i++) {
            Point2D p = proyectar(ancho, fondo, piso, esquinas[i][0], esquinas[i][1]);
            if (i == 0) figura.moveTo(p.getX(), p.getY()); else figura.lineTo(p.getX(), p.getY());
        }
        figura.closePath();
        return figura;
    }

    /**
     * Arma una línea del campo en perspectiva.
     * @param ancho ancho de la cancha
     * @param fondo altura del borde de atrás
     * @param piso altura del borde de adelante
     * @param u1 inicio a lo ancho
     * @param v1 inicio en profundidad
     * @param u2 fin a lo ancho
     * @param v2 fin en profundidad
     * @return línea proyectada
     */
    private static Shape linea(int ancho, int fondo, int piso, double u1, double v1, double u2, double v2) {
        Path2D figura = new Path2D.Double();
        Point2D a = proyectar(ancho, fondo, piso, u1, v1);
        Point2D b = proyectar(ancho, fondo, piso, u2, v2);
        figura.moveTo(a.getX(), a.getY());
        figura.lineTo(b.getX(), b.getY());
        return figura;
    }

    /**
     * Arma un círculo del campo (se ve como óvalo por la perspectiva).
     * @param ancho ancho de la cancha
     * @param fondo altura del borde de atrás
     * @param piso altura del borde de adelante
     * @param u centro a lo ancho
     * @param v centro en profundidad
     * @param radioU radio a lo ancho
     * @param radioV radio en profundidad
     * @return óvalo proyectado
     */
    private static Shape ovalo(int ancho, int fondo, int piso, double u, double v, double radioU, double radioV) {
        Path2D figura = new Path2D.Double();
        for (int i = 0; i <= PUNTOS_FLAMA; i++) {
            double a = 2 * Math.PI * i / PUNTOS_FLAMA;
            Point2D p = proyectar(ancho, fondo, piso, u + Math.cos(a) * radioU, v + Math.sin(a) * radioV);
            if (i == 0) figura.moveTo(p.getX(), p.getY()); else figura.lineTo(p.getX(), p.getY());
        }
        return figura;
    }

    /**
     * Pinta un arco sombreado y con malla: sombra en el pasto, fondo oscuro hacia adentro,
     * la red cuadriculada, el techo de red y los postes con brillo.
     * @param g contexto gráfico
     * @param c comando del arco (haciaDerecha es true en el arco izquierdo, que abre hacia la derecha)
     */
    private static void dibujarArco(Graphics2D g, ComandoDibujo c) {
        int frente = c.haciaDerecha ? c.x + c.ancho : c.x;
        int atras = c.haciaDerecha ? c.x : c.x + c.ancho;
        int abajo = c.y + c.alto;
        g.setColor(SOMBRA);
        g.fillOval(c.x - 10, abajo - 8, c.ancho + 20, 16);
        g.setPaint(new GradientPaint(atras, 0, FONDO_ARCO_LEJOS, frente, 0, FONDO_ARCO_CERCA));
        g.fillRect(c.x, c.y, c.ancho, c.alto);
        Shape antes = g.getClip();
        g.clipRect(c.x, c.y, c.ancho, c.alto);
        g.setColor(MALLA_ARCO);
        for (int x = c.x; x <= c.x + c.ancho; x += MALLA) g.drawLine(x, c.y, x, abajo);
        for (int y = c.y; y <= abajo; y += MALLA) g.drawLine(c.x, y, c.x + c.ancho, y);
        for (int d = -c.alto; d < c.ancho + c.alto; d += MALLA * 2) {
            g.drawLine(c.x + d, c.y, c.x + d + c.alto / 3, abajo);
        }
        g.setClip(antes);
        g.setStroke(TRAZO_ARCO);
        g.setColor(SOMBRA_POSTE);
        g.drawLine(atras, c.y, atras, abajo);
        g.drawLine(c.x, c.y + 3, c.x + c.ancho, c.y + 3);
        g.drawLine(frente + 2, c.y, frente + 2, abajo);
        g.setColor(Color.WHITE);
        g.drawLine(c.x, c.y, c.x + c.ancho, c.y);
        g.drawLine(frente, c.y, frente, abajo);
        g.setStroke(TRAZO_BRILLO_ARCO);
        g.setColor(Estilo.CIELO_ABAJO);
        g.drawLine(frente - 1, c.y + 4, frente - 1, abajo - 4);
        g.setStroke(TRAZO_NORMAL);
    }

    /**
     * Pinta el balón: círculo blanco con borde y un punto en el centro.
     * @param g contexto gráfico
     * @param c comando del balón (cuadro que lo encierra)
     */
    private static void dibujarBalon(Graphics2D g, ComandoDibujo c) {
        int r = c.ancho / 2;
        if (c.etiqueta != null) dibujarTiro(g, c);
        g.setColor(Color.WHITE);
        g.fillOval(c.x, c.y, c.ancho, c.alto);
        g.setColor(Color.BLACK);
        g.drawOval(c.x, c.y, c.ancho, c.alto);
        g.fillOval(c.x + r - 4, c.y + r - 4, 8, 8);
    }

    /**
     * Pinta el brillo del tiro de poder alrededor del balón, según el tiro (Aura Azul, Rayo,
     * Tornado o Ilusión).
     * @param g contexto gráfico
     * @param c comando del balón (etiqueta = nombre del tiro)
     */
    private static void dibujarTiro(Graphics2D g, ComandoDibujo c) {
        int cx = c.x + c.ancho / 2;
        int cy = c.y + c.alto / 2;
        int d = c.ancho + 2 * BRILLO;
        Stroke anterior = g.getStroke();
        g.setStroke(TRAZO_EFECTO);
        switch (c.etiqueta) {
            case CoreaDelSur.NOMBRE_TIRO:
                g.setColor(TIRO_AZUL);
                g.fillOval(cx - d / 2, cy - d / 2, d, d);
                break;
            case Camerun.NOMBRE_TIRO:
                g.setColor(TIRO_RAYO);
                g.fillOval(cx - d / 2, cy - d / 2, d, d);
                g.setColor(Color.YELLOW);
                g.drawPolyline(new int[] {cx - d, cx - d / 2, cx - d / 2 - 6, cx},
                               new int[] {cy - 8, cy - 2, cy + 4, cy + 8}, 4);
                break;
            case Nigeria.NOMBRE_TIRO:
                g.setColor(TIRO_TORNADO);
                for (int k = 0; k < 3; k++) {
                    int a = d - k * 8;
                    g.drawArc(cx - a / 2, cy - a / 2, a, a, k * 60, 240);
                }
                break;
            case EstadosUnidos.NOMBRE_TIRO:
                g.setColor(TIRO_ILUSION);
                g.fillOval(cx - d / 2, cy - d / 2, d, d);
                break;
            default:
                break;
        }
        g.setStroke(anterior);
    }

    /**
     * Pinta un jugador: sombra, aura de flama si la tiene, cuerpo con los colores de su país
     * (con rayas si las tiene), pie (siempre; al patear solo se mueve), cabeza, sangre si recibió
     * un golpe y la etiqueta encima. Si está aturdido se pinta gris con estrellas.
     * @param g contexto gráfico
     * @param c comando del jugador (x, y es el centro de la cabeza y ancho el radio)
     * @param tiempo segundos de animación (mueven la flama del aura)
     */
    private static void dibujarJugador(Graphics2D g, ComandoDibujo c, double tiempo) {
        int r = c.ancho;
        double escala = r / (double) Jugador.RADIO;
        Color color = c.numero == 1 ? Estilo.COLOR_J1 : Estilo.COLOR_J2;
        Color[] colores = c.aturdido ? Estilo.COLORES_ATURDIDO
                                     : new Color[] {c.colorCabeza, c.colorBanda, c.colorCuerpo};
        g.setColor(SOMBRA);
        g.fillOval(c.x - r, c.y + 2 * r - 5, 2 * r, 10);
        if (c.aura) dibujarAura(g, c.x, c.y + r * 0.5, r, tiempo);
        int cuello = (int) (Jugador.CUELLO * escala);
        g.setColor(colores[2]);
        g.fillRect(c.x - r / 2, c.y + r - cuello, r, r + cuello);
        if (!c.aturdido && c.rayas) {
            g.setColor(Color.WHITE);
            for (int franja = c.y + r; franja < c.y + 2 * r; franja += SEPARACION_RAYAS) {
                g.fillRect(c.x - r / 2, franja, r, SEPARACION_RAYAS / 2);
            }
        }
        g.setColor(Color.BLACK);
        g.drawRect(c.x - r / 2, c.y + r - cuello, r, r + cuello);
        dibujarPie(g, c, escala);
        dibujarCabeza(g, c.x, c.y, r, colores, c.haciaDerecha, c.aturdido ? Color.GRAY : color);
        if (c.avanceGolpe >= 0) dibujarSangre(g, c);
        g.setFont(Estilo.TEXTO_PEQUENO);
        g.setColor(color);
        g.drawString(c.etiqueta, c.x - g.getFontMetrics().stringWidth(c.etiqueta) / 2, c.y - r - 8);
        if (c.aturdido) {
            g.setColor(Color.YELLOW);
            g.drawString("* * *", c.x - 15, c.y - r - 22);
        } else if (c.efecto != null) {
            g.setColor(Estilo.ACENTO);
            g.drawString(c.efecto, c.x - g.getFontMetrics().stringWidth(c.efecto) / 2, c.y - r - 22);
        }
    }

    /**
     * Pinta el aura del poder como una flama animada de varias capas (roja por fuera, amarilla
     * por dentro) con chispas que suben.
     * @param g contexto gráfico
     * @param cx centro horizontal
     * @param cy centro vertical
     * @param r radio de la cabeza del jugador
     * @param tiempo segundos de animación
     */
    public static void dibujarAura(Graphics2D g, double cx, double cy, int r, double tiempo) {
        for (int capa = 0; capa < CAPAS_AURA.length; capa++) {
            double tam = 1 - capa * 0.18;
            g.setColor(CAPAS_AURA[capa]);
            g.fill(crearFlama(cx, cy + capa * r * 0.08, (r + 22) * tam, (r * 1.6 + 22) * tam, tiempo, capa * 1.7));
        }
        g.setColor(CHISPA);
        for (int i = 0; i < 6; i++) {
            double avance = (tiempo * 1.3 + i / 6.0) % 1;
            double x = cx + Math.sin(i * 2.1 + tiempo * 3) * r;
            double y = cy - r * 1.2 - avance * r * 1.8;
            int tam = (int) (6 * (1 - avance)) + 1;
            g.fillOval((int) x, (int) y, tam, tam);
        }
    }

    /**
     * Arma el contorno de una flama: un óvalo con bordes que ondulan con el tiempo y lenguas
     * más largas hacia arriba.
     * @param cx centro horizontal
     * @param cy centro vertical
     * @param rx radio horizontal
     * @param ry radio vertical
     * @param tiempo segundos de animación
     * @param fase corrimiento para que cada capa se mueva distinto
     * @return figura de la flama
     */
    public static Shape crearFlama(double cx, double cy, double rx, double ry, double tiempo, double fase) {
        Path2D flama = new Path2D.Double();
        for (int i = 0; i <= PUNTOS_FLAMA; i++) {
            double a = 2 * Math.PI * i / PUNTOS_FLAMA;
            double onda = 1 + 0.08 * Math.sin(6 * a + tiempo * 9 + fase) + 0.05 * Math.sin(11 * a - tiempo * 13);
            double arriba = Math.max(0, -Math.sin(a));
            double lengua = arriba * arriba * (0.45 + 0.3 * Math.sin(7 * a + tiempo * 11 + fase));
            double x = cx + Math.cos(a) * rx * onda * (1 - 0.35 * arriba * arriba);
            double y = cy + Math.sin(a) * ry * onda - lengua * ry;
            if (i == 0) flama.moveTo(x, y); else flama.lineTo(x, y);
        }
        flama.closePath();
        return flama;
    }

    /**
     * Pinta una cabeza: círculo con la banda en la frente, borde del color del jugador
     * y un ojo hacia donde mira. Se usa en la cancha y junto a las barras de vida y poder.
     * @param g contexto gráfico
     * @param x centro horizontal
     * @param y centro vertical
     * @param r radio
     * @param colores colores {cabeza, banda, cuerpo}
     * @param derecha true si mira a la derecha
     * @param borde color del borde
     */
    public static void dibujarCabeza(Graphics2D g, int x, int y, int r, Color[] colores, boolean derecha, Color borde) {
        int ojo = OJO * r / Jugador.RADIO;
        g.setColor(colores[0]);
        g.fillOval(x - r, y - r, 2 * r, 2 * r);
        Shape antes = g.getClip();
        g.clip(new Ellipse2D.Double(x - r, y - r, 2 * r, 2 * r));
        g.setColor(colores[1]);
        g.fillRect(x - r, y - r / 2, 2 * r, r / 3);
        g.setClip(antes);
        g.setStroke(TRAZO_CABEZA);
        g.setColor(borde);
        g.drawOval(x - r, y - r, 2 * r, 2 * r);
        g.setStroke(TRAZO_NORMAL);
        int xOjo = derecha ? x + r / 3 : x - r / 3 - ojo + 2;
        g.setColor(Color.WHITE);
        g.fillOval(xOjo, y - 4, ojo, ojo);
        g.setColor(Color.BLACK);
        g.drawOval(xOjo, y - 4, ojo, ojo);
        g.fillOval(xOjo + (derecha ? ojo / 2 : 1), y, ojo * 2 / 5, ojo * 2 / 5);
    }

    /**
     * Pinta el pie (zapato). Siempre se ve apoyado adelante del cuerpo; al patear gira hacia
     * arriba y adelante según el avance de la patada, y vuelve a su lugar.
     * Sus medidas son las mismas que usa el dominio para los choques con el balón.
     * @param g contexto gráfico
     * @param c comando del jugador
     * @param escala tamaño del jugador dibujado respecto al del dominio
     */
    private static void dibujarPie(Graphics2D g, ComandoDibujo c, double escala) {
        int r = c.ancho;
        int largo = (int) ((Jugador.LARGO_PIE + r / escala / 2) * escala);
        int alto = (int) (Jugador.ALTO_PIE * escala);
        double direccion = c.haciaDerecha ? 1 : -1;
        double tobilloX = c.x;
        double tobilloY = c.y + 2 * r - alto / 2.0;
        AffineTransform antes = g.getTransform();
        g.translate(tobilloX, tobilloY);
        if (c.avancePatada >= 0) {
            double giro = Math.sin(Math.PI * c.avancePatada);
            g.rotate(-direccion * Math.toRadians(ANGULO_PATADA) * giro);
            g.translate(direccion * 4 * giro, 0);
        }
        int inicio = direccion > 0 ? -alto / 2 : -largo;
        int ancho = largo + alto / 2;
        g.setColor(ZAPATO);
        g.fillRoundRect(inicio, -alto / 2, ancho, alto, alto, alto);
        g.setColor(SUELA);
        g.fillRect(inicio + alto / 2, alto / 2 - 2, ancho - alto, 2);
        g.setTransform(antes);
    }

    /**
     * Pinta gotas de sangre que salen de la cabeza según el avance del golpe.
     * @param g contexto gráfico
     * @param c comando del jugador
     */
    private static void dibujarSangre(Graphics2D g, ComandoDibujo c) {
        int r = c.ancho;
        g.setColor(Estilo.SANGRE);
        for (int i = 0; i < GOTAS; i++) {
            double angulo = Math.PI * (0.15 + 0.7 * i / (GOTAS - 1));
            int dx = (int) (Math.cos(angulo) * (r + 6 + 14 * c.avanceGolpe));
            int dy = (int) (Math.sin(angulo) * (r + 6 + 14 * c.avanceGolpe));
            g.fillOval(c.x - dx - 3, c.y - dy - 3, 6, 6);
        }
    }
}
