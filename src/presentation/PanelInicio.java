package presentation;

import domain.Camerun;
import domain.CoreaDelSur;
import java.awt.*;
import javax.swing.*;
import javax.swing.border.EmptyBorder;

/**
 * JPanel: pantalla de inicio de DOPOSoccer. Dibuja el título, la cancha y dos jugadores
 * (Corea del Sur y Camerún) con sus banderas, y tiene el botón para empezar.
 */
public class PanelInicio extends JPanel {

    /** Colores {cabeza, banda, cuerpo} del jugador de la izquierda (Corea del Sur). */
    public static final Color[] COLORES_J1 = {Color.WHITE, new Color(205, 46, 58), new Color(0, 71, 160)};
    /** Colores {cabeza, banda, cuerpo} del jugador de la derecha (Camerún). */
    public static final Color[] COLORES_J2 = {new Color(0, 122, 94), new Color(252, 209, 22), new Color(206, 17, 38)};

    /** JButton: botón para empezar (la ventana principal le asigna la acción). */
    private JButton botonIniciar;

    /**
     * Crea la pantalla de inicio.
     */
    public PanelInicio() {
        prepareView();
    }

    /**
     * Ubica el botón de inicio abajo, en el centro.
     */
    private void prepareView() {
        setLayout(new BorderLayout());
        setBackground(PanelCancha.CIELO);
        botonIniciar = new JButton("Jugar");
        botonIniciar.setFont(new Font("SansSerif", Font.BOLD, 22));
        JPanel abajo = new JPanel(new FlowLayout(FlowLayout.CENTER));
        abajo.setOpaque(false);
        abajo.setBorder(new EmptyBorder(0, 0, 30, 0));
        abajo.add(botonIniciar);
        add(abajo, BorderLayout.SOUTH);
    }

    /**
     * Dibuja el fondo, el título, los dos jugadores y sus banderas.
     * @param g contexto gráfico (Graphics)
     */
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        int ancho = getWidth();
        int alto = getHeight();
        int piso = alto * 3 / 4;
        g2.setColor(PanelCancha.PASTO);
        g2.fillRect(0, piso, ancho, alto - piso);
        g2.setColor(Color.WHITE);
        g2.setFont(new Font("SansSerif", Font.BOLD, 56));
        String titulo = "DOPOSoccer";
        g2.drawString(titulo, (ancho - g2.getFontMetrics().stringWidth(titulo)) / 2, alto / 5);
        int r = 40;
        int y = piso - 2 * r - r / 2;
        dibujarPersonaje(g2, ancho / 4, y, r, COLORES_J1, PanelCancha.COLOR_J1, true, CoreaDelSur.PAIS);
        dibujarPersonaje(g2, ancho * 3 / 4, y, r, COLORES_J2, PanelCancha.COLOR_J2, false, Camerun.PAIS);
        dibujarBanderaCorea(g2, ancho / 4 - 45, alto / 5 + 30, 90, 60);
        dibujarBanderaCamerun(g2, ancho * 3 / 4 - 45, alto / 5 + 30, 90, 60);
    }

    /**
     * Dibuja un personaje: cuerpo, cabeza con banda, ojo y nombre debajo.
     * @param g contexto gráfico
     * @param x centro horizontal de la cabeza
     * @param y centro vertical de la cabeza
     * @param r radio de la cabeza
     * @param colores colores {cabeza, banda, cuerpo}
     * @param color color del borde
     * @param derecha true si mira a la derecha
     * @param etiqueta texto que se escribe debajo
     */
    private void dibujarPersonaje(Graphics2D g, int x, int y, int r, Color[] colores, Color color,
                                  boolean derecha, String etiqueta) {
        g.setColor(colores[2]);
        g.fillRect(x - r / 2, y + r - 5, r, r + 5);
        g.setColor(Color.BLACK);
        g.drawRect(x - r / 2, y + r - 5, r, r + 5);
        g.setColor(colores[0]);
        g.fillOval(x - r, y - r, 2 * r, 2 * r);
        Shape antes = g.getClip();
        g.clip(new java.awt.geom.Ellipse2D.Double(x - r, y - r, 2 * r, 2 * r));
        g.setColor(colores[1]);
        g.fillRect(x - r, y - r / 2, 2 * r, r / 3);
        g.setClip(antes);
        g.setStroke(new BasicStroke(5));
        g.setColor(color);
        g.drawOval(x - r, y - r, 2 * r, 2 * r);
        g.setStroke(new BasicStroke(1));
        int ojo = derecha ? x + r / 3 : x - r / 3 - 16;
        g.setColor(Color.WHITE);
        g.fillOval(ojo, y - 6, 18, 18);
        g.setColor(Color.BLACK);
        g.fillOval(ojo + (derecha ? 9 : 2), y, 7, 7);
        g.setFont(new Font("SansSerif", Font.BOLD, 18));
        g.setColor(Color.WHITE);
        g.drawString(etiqueta, x - g.getFontMetrics().stringWidth(etiqueta) / 2, y + 2 * r + 30);
    }

    /**
     * Dibuja una bandera simplificada de Corea del Sur.
     * @param g contexto gráfico
     * @param x esquina izquierda
     * @param y esquina superior
     * @param w ancho
     * @param h alto
     */
    private void dibujarBanderaCorea(Graphics2D g, int x, int y, int w, int h) {
        g.setColor(Color.WHITE);
        g.fillRect(x, y, w, h);
        int d = h / 2;
        int cx = x + w / 2 - d / 2;
        int cy = y + h / 2 - d / 2;
        g.setColor(new Color(205, 46, 58));
        g.fillArc(cx, cy, d, d, 0, 180);
        g.setColor(new Color(0, 71, 160));
        g.fillArc(cx, cy, d, d, 180, 180);
        g.setColor(Color.BLACK);
        g.drawRect(x, y, w, h);
    }

    /**
     * Dibuja una bandera simplificada de Camerún.
     * @param g contexto gráfico
     * @param x esquina izquierda
     * @param y esquina superior
     * @param w ancho
     * @param h alto
     */
    private void dibujarBanderaCamerun(Graphics2D g, int x, int y, int w, int h) {
        int tercio = w / 3;
        g.setColor(new Color(0, 122, 94));
        g.fillRect(x, y, tercio, h);
        g.setColor(new Color(206, 17, 38));
        g.fillRect(x + tercio, y, tercio, h);
        g.setColor(new Color(252, 209, 22));
        g.fillRect(x + 2 * tercio, y, w - 2 * tercio, h);
        g.fillOval(x + w / 2 - 6, y + h / 2 - 6, 12, 12);
        g.setColor(Color.BLACK);
        g.drawRect(x, y, w, h);
    }

    /**
     * @return JButton para empezar (la ventana principal le asigna la acción)
     */
    public JButton getBotonIniciar() { return botonIniciar; }
}
