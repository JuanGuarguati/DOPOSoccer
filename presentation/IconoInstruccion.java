package presentation;

import domain.Camerun;
import domain.CoreaDelSur;
import domain.EstadosUnidos;
import domain.Nigeria;
import domain.Pais;
import java.awt.*;
import java.awt.geom.GeneralPath;
import javax.swing.JComponent;

/**
 * JComponent: dibujo que explica una parte del juego en la pantalla "Cómo jugar".
 * Cada Tipo pinta un dibujo distinto (el objetivo, las teclas, la vida, la barra de poder,
 * los cuatro tiros y las opciones de la partida). Se dibuja en una caja de diseño de
 * ANCHO_DISENO x ALTO_DISENO que se escala al tamaño del componente.
 * Reutiliza los dibujos del juego (cabezas, aura, color de la barra de poder).
 */
public class IconoInstruccion extends JComponent {

    /**
     * Qué explica el dibujo.
     */
    public enum Tipo {
        /** Meter el balón en el arco del rival. */
        OBJETIVO,
        /** Teclas de cada jugador. */
        CONTROLES,
        /** Vida y aturdido. */
        VIDA,
        /** Barra de poder y aura. */
        PODER,
        /** Los cuatro tiros de poder. */
        TIROS,
        /** Modos, máquina, pausa, música y archivos. */
        PARTIDA
    }

    /** Ancho de la caja de diseño. */
    public static final int ANCHO_DISENO = 260;
    /** Alto de la caja de diseño. */
    public static final int ALTO_DISENO = 100;
    /** Lado de una tecla dibujada. */
    public static final int TECLA = 22;
    /** Fuente de las teclas y de los textos del dibujo. */
    private static final Font FUENTE = new Font(Font.SANS_SERIF, Font.BOLD, 11);
    /** Fuente pequeña de los nombres de los tiros. */
    private static final Font FUENTE_PEQUENA = new Font(Font.SANS_SERIF, Font.BOLD, 9);
    /** Trazo grueso de las flechas y los arcos. */
    private static final Stroke TRAZO_GRUESO = new BasicStroke(3, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND);
    /** Trazo punteado del recorrido del balón. */
    private static final Stroke TRAZO_PUNTEADO = new BasicStroke(3, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND, 1, new float[] {6, 5}, 0);
    /** Trazo normal. */
    private static final Stroke TRAZO_NORMAL = new BasicStroke(1);
    /** Color de las teclas. */
    private static final Color TECLA_FONDO = new Color(245, 245, 255);
    /** Colores de los tiros (Aura Azul, Rayo, Tornado, Ilusión). */
    private static final Color[] COLORES_TIROS = {
        new Color(40, 120, 255), new Color(255, 220, 0), new Color(30, 170, 80), new Color(170, 80, 255)
    };

    /** Qué dibuja este icono. */
    private final Tipo tipo;

    /**
     * Crea el dibujo de una parte del juego.
     * @param tipo qué explica
     */
    public IconoInstruccion(Tipo tipo) {
        this.tipo = tipo;
        setOpaque(false);
        setPreferredSize(new Dimension(ANCHO_DISENO, ALTO_DISENO));
    }

    /**
     * @return qué explica este dibujo
     */
    public Tipo getTipo() { return tipo; }

    /**
     * Escala la caja de diseño al tamaño del componente y pinta el dibujo de su tipo.
     * @param g contexto gráfico
     */
    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        double escala = Math.min(getWidth() / (double) ANCHO_DISENO, getHeight() / (double) ALTO_DISENO);
        g2.translate((getWidth() - ANCHO_DISENO * escala) / 2, (getHeight() - ALTO_DISENO * escala) / 2);
        g2.scale(escala, escala);
        g2.setFont(FUENTE);
        switch (tipo) {
            case OBJETIVO: dibujarObjetivo(g2); break;
            case CONTROLES: dibujarControles(g2); break;
            case VIDA: dibujarVida(g2); break;
            case PODER: dibujarPoder(g2); break;
            case TIROS: dibujarTiros(g2); break;
            default: dibujarPartida(g2);
        }
        g2.dispose();
    }

    /**
     * Dos arcos, un jugador de cada lado y el balón entrando al arco rival.
     * @param g contexto gráfico
     */
    private void dibujarObjetivo(Graphics2D g) {
        g.setColor(Estilo.PASTO);
        g.fillRect(0, 84, ANCHO_DISENO, 16);
        dibujarArquito(g, 4, true);
        dibujarArquito(g, ANCHO_DISENO - 34, false);
        cabeza(g, Pais.COREA_DEL_SUR, 70, 66, 14, true, Estilo.COLOR_J1);
        cabeza(g, Pais.CAMERUN, 190, 66, 14, false, Estilo.COLOR_J2);
        g.setColor(Color.WHITE);
        g.setStroke(TRAZO_PUNTEADO);
        g.drawArc(95, 22, 140, 90, 20, 140);
        g.setStroke(TRAZO_GRUESO);
        flecha(g, 232, 48, 240, 62);
        balon(g, 238, 70, 8);
        g.setColor(Estilo.ACENTO);
        g.drawString("¡GOL!", 112, 20);
        g.setColor(Estilo.COLOR_J1);
        g.drawString("J1", 20, 98);
        g.setColor(Estilo.COLOR_J2);
        g.drawString("J2", ANCHO_DISENO - 32, 98);
        g.setStroke(TRAZO_NORMAL);
    }

    /**
     * Las teclas de cada jugador: W A S D y Q para el jugador 1; flechas y Ctrl derecho para el 2.
     * @param g contexto gráfico
     */
    private void dibujarControles(Graphics2D g) {
        g.setColor(Estilo.COLOR_J1);
        g.drawString("J1", 4, 14);
        tecla(g, 34, 4, "W", Estilo.SECUNDARIO);
        tecla(g, 10, 30, "A", Estilo.PRIMARIO);
        tecla(g, 34, 30, "S", Estilo.PELIGRO);
        tecla(g, 58, 30, "D", Estilo.PRIMARIO);
        tecla(g, 92, 30, "Q", Estilo.ACENTO);
        g.setColor(Estilo.COLOR_J2);
        g.drawString("J2", 134, 14);
        tecla(g, 166, 4, "↑", Estilo.SECUNDARIO);
        tecla(g, 142, 30, "←", Estilo.PRIMARIO);
        tecla(g, 166, 30, "↓", Estilo.PELIGRO);
        tecla(g, 190, 30, "→", Estilo.PRIMARIO);
        g.setColor(TECLA_FONDO);
        g.fillRoundRect(220, 30, 38, TECLA, 6, 6);
        g.setColor(Estilo.ACENTO);
        g.setStroke(TRAZO_GRUESO);
        g.drawRoundRect(220, 30, 38, TECLA, 6, 6);
        g.setStroke(TRAZO_NORMAL);
        g.setColor(Estilo.TEXTO_OSCURO);
        g.drawString("Ctrl", 228, 45);
        int y = 76;
        leyenda(g, 6, y, Estilo.PRIMARIO, "mover");
        leyenda(g, 66, y, Estilo.SECUNDARIO, "saltar");
        leyenda(g, 130, y, Estilo.PELIGRO, "patear");
        leyenda(g, 196, y, Estilo.ACENTO, "poder");
        g.setColor(Color.WHITE);
        g.drawString("Ctrl = Ctrl derecho", 70, 96);
    }

    /**
     * Una patada que le quita vida al rival y, con la vida en cero, el rival aturdido con estrellas.
     * @param g contexto gráfico
     */
    private void dibujarVida(Graphics2D g) {
        cabeza(g, Pais.NIGERIA, 24, 40, 16, true, Estilo.COLOR_J1);
        g.setColor(Color.BLACK);
        g.fillRoundRect(36, 56, 30, 8, 8, 8);
        g.setColor(Estilo.SANGRE);
        g.setStroke(TRAZO_GRUESO);
        g.drawLine(70, 50, 78, 44);
        g.drawLine(70, 60, 80, 60);
        g.setStroke(TRAZO_NORMAL);
        cabeza(g, Pais.ESTADOS_UNIDOS, 102, 40, 16, false, Estilo.COLOR_J2);
        barra(g, 79, 70, 46, 0.4, Estilo.VIDA);
        g.setColor(Color.WHITE);
        g.drawString("−10", 92, 14);
        g.setStroke(TRAZO_GRUESO);
        flecha(g, 138, 42, 168, 42);
        g.setStroke(TRAZO_NORMAL);
        RenderizadorElementos.dibujarCabeza(g, 204, 40, 16, Estilo.COLORES_ATURDIDO, false, Color.GRAY);
        barra(g, 182, 70, 46, 0, Estilo.VIDA);
        g.setColor(Color.YELLOW);
        g.drawString("* * *", 190, 18);
        g.setColor(Color.WHITE);
        g.drawString("3 s", 236, 44);
    }

    /**
     * La barra de poder con forma de flama cambiando de color de claro a rojo, y el jugador con el aura.
     * @param g contexto gráfico
     */
    private void dibujarPoder(Graphics2D g) {
        double[] cargas = {0.15, 0.5, 1};
        String[] textos = {"15 %", "50 %", "100 %"};
        for (int i = 0; i < cargas.length; i++) {
            int y = 6 + i * 30;
            barra(g, 6, y, 120, cargas[i], PanelCancha.colorPoder(cargas[i]));
            g.setColor(Color.WHITE);
            g.drawString(textos[i], 132, y + 11);
        }
        g.setStroke(TRAZO_GRUESO);
        g.setColor(Color.WHITE);
        flecha(g, 168, 66, 186, 66);
        g.setStroke(TRAZO_NORMAL);
        RenderizadorElementos.dibujarAura(g, 222, 52, 15, 0.4);
        cabeza(g, Pais.CAMERUN, 222, 44, 15, true, Estilo.COLOR_J1);
        g.setColor(Estilo.ACENTO);
        g.drawString("Q / Ctrl", 170, 96);
    }

    /**
     * Los cuatro tiros de poder: el balón con su brillo, el país y el efecto.
     * @param g contexto gráfico
     */
    private void dibujarTiros(Graphics2D g) {
        String[] tiros = {CoreaDelSur.NOMBRE_TIRO, Camerun.NOMBRE_TIRO, Nigeria.NOMBRE_TIRO, EstadosUnidos.NOMBRE_TIRO};
        String[] efectos = {"empujón", "sin salto", "inconsciente", "7 falsos"};
        Pais[] paises = {Pais.COREA_DEL_SUR, Pais.CAMERUN, Pais.NIGERIA, Pais.ESTADOS_UNIDOS};
        for (int i = 0; i < tiros.length; i++) {
            int cx = 33 + i * 65;
            cabeza(g, paises[i], cx, 14, 10, true, Color.WHITE);
            Color c = COLORES_TIROS[i];
            g.setColor(new Color(c.getRed(), c.getGreen(), c.getBlue(), 140));
            g.fillOval(cx - 16, 32, 32, 32);
            if (i == 3) {
                g.fillOval(cx - 30, 40, 14, 14);
                g.fillOval(cx + 16, 40, 14, 14);
            }
            balon(g, cx, 48, 9);
            g.setFont(FUENTE);
            g.setColor(c);
            centrado(g, tiros[i], cx, 80);
            g.setFont(FUENTE_PEQUENA);
            g.setColor(Color.WHITE);
            centrado(g, efectos[i], cx, 94);
        }
        g.setFont(FUENTE);
    }

    /**
     * Opciones de la partida: por tiempo o por goles, contra la máquina, pausa, música y archivos.
     * @param g contexto gráfico
     */
    private void dibujarPartida(Graphics2D g) {
        g.setColor(Color.WHITE);
        g.setStroke(TRAZO_GRUESO);
        g.drawOval(10, 8, 34, 34);
        g.drawLine(27, 25, 27, 14);
        g.drawLine(27, 25, 36, 25);
        g.setStroke(TRAZO_NORMAL);
        g.drawString("tiempo", 8, 56);
        g.setColor(Estilo.FONDO_MARCADOR);
        g.fillRoundRect(60, 10, 50, 30, 8, 8);
        g.setColor(Color.WHITE);
        centrado(g, "2 - 1", 85, 30);
        g.drawString("goles", 70, 56);
        cabeza(g, Pais.NIGERIA, 140, 24, 15, false, Estilo.COLOR_J2);
        g.setColor(Estilo.COLOR_J2);
        g.drawString("CPU", 129, 56);
        tecla(g, 6, 68, "P", Estilo.PRIMARIO);
        g.setColor(Color.WHITE);
        g.drawString("pausa", 32, 84);
        tecla(g, 76, 68, "M", Estilo.SECUNDARIO);
        g.setColor(Color.WHITE);
        g.drawString("♪ música", 102, 84);
        dibujarDisco(g, 182, 6);
        g.setColor(Color.WHITE);
        g.drawString("guardar", 176, 56);
        g.drawString("y abrir", 178, 70);
        tecla(g, 166, 76, "Esc", Estilo.PELIGRO);
        g.setColor(Color.WHITE);
        g.drawString("salir", 202, 92);
    }

    /**
     * Dibuja un arco pequeño con malla.
     * @param g contexto gráfico
     * @param x borde izquierdo
     * @param izquierdo true si abre hacia la derecha
     */
    private void dibujarArquito(Graphics2D g, int x, boolean izquierdo) {
        g.setColor(new Color(255, 255, 255, 90));
        for (int i = 0; i <= 30; i += 6) g.drawLine(x + i, 40, x + i, 84);
        for (int j = 40; j <= 84; j += 6) g.drawLine(x, j, x + 30, j);
        g.setColor(Color.WHITE);
        g.setStroke(TRAZO_GRUESO);
        int poste = izquierdo ? x + 30 : x;
        g.drawLine(x, 40, x + 30, 40);
        g.drawLine(poste, 40, poste, 84);
        g.setStroke(TRAZO_NORMAL);
    }

    /**
     * Dibuja un disco (guardar).
     * @param g contexto gráfico
     * @param x borde izquierdo
     * @param y borde de arriba
     */
    private void dibujarDisco(Graphics2D g, int x, int y) {
        g.setColor(Estilo.COLOR_J1);
        g.fillRoundRect(x, y, 34, 34, 6, 6);
        g.setColor(Color.WHITE);
        g.fillRect(x + 7, y + 3, 20, 11);
        g.fillRect(x + 6, y + 20, 22, 12);
        g.setColor(Estilo.COLOR_J1);
        g.fillRect(x + 20, y + 5, 4, 7);
    }

    /**
     * Dibuja la cabeza de un país.
     * @param g contexto gráfico
     * @param pais país
     * @param x centro horizontal
     * @param y centro vertical
     * @param r radio
     * @param derecha true si mira a la derecha
     * @param borde color del borde
     */
    private void cabeza(Graphics2D g, Pais pais, int x, int y, int r, boolean derecha, Color borde) {
        RenderizadorElementos.dibujarCabeza(g, x, y, r, pais.crearPersonaje().getColores(), derecha, borde);
    }

    /**
     * Dibuja un balón.
     * @param g contexto gráfico
     * @param x centro horizontal
     * @param y centro vertical
     * @param r radio
     */
    private void balon(Graphics2D g, int x, int y, int r) {
        g.setColor(Color.WHITE);
        g.fillOval(x - r, y - r, 2 * r, 2 * r);
        g.setColor(Color.BLACK);
        g.drawOval(x - r, y - r, 2 * r, 2 * r);
        g.fillOval(x - r / 3, y - r / 3, 2 * r / 3 + 1, 2 * r / 3 + 1);
    }

    /**
     * Dibuja una tecla con su letra y un borde del color de su acción.
     * @param g contexto gráfico
     * @param x borde izquierdo
     * @param y borde de arriba
     * @param letra texto de la tecla
     * @param color color de la acción
     */
    private void tecla(Graphics2D g, int x, int y, String letra, Color color) {
        int ancho = Math.max(TECLA, g.getFontMetrics().stringWidth(letra) + 10);
        g.setColor(TECLA_FONDO);
        g.fillRoundRect(x, y, ancho, TECLA, 6, 6);
        g.setColor(color);
        g.setStroke(TRAZO_GRUESO);
        g.drawRoundRect(x, y, ancho, TECLA, 6, 6);
        g.setStroke(TRAZO_NORMAL);
        g.setColor(Estilo.TEXTO_OSCURO);
        centrado(g, letra, x + ancho / 2, y + 15);
    }

    /**
     * Dibuja un cuadrito de color con el nombre de la acción.
     * @param g contexto gráfico
     * @param x borde izquierdo
     * @param y línea del texto
     * @param color color de la acción
     * @param texto nombre de la acción
     */
    private void leyenda(Graphics2D g, int x, int y, Color color, String texto) {
        g.setColor(color);
        g.fillRoundRect(x, y - 9, 10, 10, 3, 3);
        g.setColor(Color.WHITE);
        g.drawString(texto, x + 14, y);
    }

    /**
     * Dibuja una barra redondeada llena en una fracción.
     * @param g contexto gráfico
     * @param x borde izquierdo
     * @param y borde de arriba
     * @param ancho ancho total
     * @param fraccion parte llena (0 a 1)
     * @param color color de la parte llena
     */
    private void barra(Graphics2D g, int x, int y, int ancho, double fraccion, Color color) {
        g.setColor(Color.DARK_GRAY);
        g.fillRoundRect(x, y, ancho, 14, 14, 14);
        g.setColor(color);
        g.fillRoundRect(x, y, (int) (ancho * fraccion), 14, 14, 14);
        g.setColor(Color.BLACK);
        g.drawRoundRect(x, y, ancho, 14, 14, 14);
    }

    /**
     * Dibuja la punta de una flecha que va de (x1, y1) a (x2, y2), con su línea.
     * @param g contexto gráfico
     * @param x1 inicio horizontal
     * @param y1 inicio vertical
     * @param x2 punta horizontal
     * @param y2 punta vertical
     */
    private void flecha(Graphics2D g, int x1, int y1, int x2, int y2) {
        g.drawLine(x1, y1, x2, y2);
        double angulo = Math.atan2(y2 - y1, x2 - x1);
        GeneralPath punta = new GeneralPath();
        punta.moveTo(x2, y2);
        punta.lineTo(x2 - 9 * Math.cos(angulo - 0.5), y2 - 9 * Math.sin(angulo - 0.5));
        punta.moveTo(x2, y2);
        punta.lineTo(x2 - 9 * Math.cos(angulo + 0.5), y2 - 9 * Math.sin(angulo + 0.5));
        g.draw(punta);
    }

    /**
     * Escribe un texto centrado en una posición.
     * @param g contexto gráfico
     * @param texto texto
     * @param cx centro horizontal
     * @param y línea del texto
     */
    private void centrado(Graphics2D g, String texto, int cx, int y) {
        g.drawString(texto, cx - g.getFontMetrics().stringWidth(texto) / 2, y);
    }
}
