package presentation;

import domain.*;
import java.awt.*;
import javax.swing.*;
import javax.swing.border.*;

/**
 * JPanel: dibuja la cancha, los arcos, el balón, los jugadores y el estado del juego
 * (tiempo, marcador, vida y barra de poder de cada jugador).
 * Las medidas del dominio se escalan a la resolución elegida.
 * Componentes Swing que contiene:
 * JButton de pausa (arriba a la derecha) y PanelPausa (JPanel con el menú de pausa).
 * Recibe el teclado (KeyListener) que le asigna la ventana principal.
 */
public class PanelCancha extends JPanel {

    /** Color del cielo. */
    public static final Color CIELO = new Color(135, 200, 250);
    /** Color del pasto. */
    public static final Color PASTO = new Color(60, 160, 70);
    /** Color del jugador 1. */
    public static final Color COLOR_J1 = new Color(30, 90, 220);
    /** Color del jugador 2. */
    public static final Color COLOR_J2 = new Color(220, 50, 50);

    /** Fachada del dominio de donde se lee el estado del partido. */
    private DOPOSoccer dominio;
    /** JButton: botón "II" para pausar el partido. */
    private JButton botonPausa;
    /** JPanel: menú de pausa que aparece encima de la cancha. */
    private PanelPausa panelPausa;

    /**
     * Crea el panel de la cancha.
     * @param dominio fachada del juego de donde se lee el estado
     */
    public PanelCancha(DOPOSoccer dominio) {
        this.dominio = dominio;
        setFocusable(true);
        setBackground(CIELO);
        prepareView();
    }

    /**
     * Ubica el botón de pausa arriba a la derecha (debajo de la información del jugador 2)
     * y el menú de pausa en el centro, oculto hasta que se pause el partido.
     */
    private void prepareView() {
        setLayout(new BorderLayout());
        // JButton de pausa: no toma el foco para que el teclado siga en la cancha
        botonPausa = new JButton("II");
        botonPausa.setFont(new Font("SansSerif", Font.BOLD, 16));
        botonPausa.setFocusable(false);
        botonPausa.setToolTipText("Pausar (P)");
        // JPanel transparente arriba (NORTH) que ubica el botón a la derecha
        JPanel arriba = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        arriba.setOpaque(false);
        arriba.setBorder(new EmptyBorder(70, 0, 0, 15));
        arriba.add(botonPausa);
        add(arriba, BorderLayout.NORTH);
        // PanelPausa (JPanel): oculto hasta que se pause
        panelPausa = new PanelPausa();
        panelPausa.setVisible(false);
        // JPanel transparente en el centro (GridBagLayout) para centrar el menú
        JPanel centro = new JPanel(new GridBagLayout());
        centro.setOpaque(false);
        centro.add(panelPausa);
        add(centro, BorderLayout.CENTER);
    }

    /**
     * Muestra u oculta el menú de pausa (y oculta el JButton de pausa mientras se ve el menú).
     * @param visible true para mostrarlo
     */
    public void mostrarPausa(boolean visible) {
        panelPausa.setVisible(visible);
        botonPausa.setVisible(!visible);
        revalidate();
        repaint();
    }

    /**
     * @return JButton de pausa de la pantalla
     */
    public JButton getBotonPausa() { return botonPausa; }

    /**
     * @return menú de pausa (JPanel)
     */
    public PanelPausa getPanelPausa() { return panelPausa; }

    /**
     * Dibuja todo el estado del partido. Swing lo llama cada vez que se hace repaint().
     * @param g contexto gráfico (Graphics)
     */
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Partido partido = dominio.getPartido();
        if (partido == null) return;
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        Cancha cancha = partido.getCancha();
        double escala = Math.min(getWidth() / (double) cancha.getAncho(), getHeight() / (double) cancha.getAlto());
        g2.scale(escala, escala);
        dibujarCancha(g2, cancha);
        dibujarBalon(g2, partido.getBalon());
        try {
            dibujarJugador(g2, partido.getJugador(1), COLOR_J1);
            dibujarJugador(g2, partido.getJugador(2), COLOR_J2);
            dibujarEstado(g2, partido, cancha);
        } catch (DOPOSoccerException e) {
            Log.record(e);
        }
    }

    /**
     * Dibuja el pasto, la línea central y los dos arcos.
     * @param g contexto gráfico
     * @param cancha cancha del partido
     */
    private void dibujarCancha(Graphics2D g, Cancha cancha) {
        g.setColor(PASTO);
        g.fillRect(0, cancha.getNivelPiso(), cancha.getAncho(), cancha.getAlto() - cancha.getNivelPiso());
        g.setColor(Color.WHITE);
        g.drawLine(cancha.getAncho() / 2, cancha.getNivelPiso(), cancha.getAncho() / 2, cancha.getAlto());
        for (Arco arco : new Arco[] {cancha.getArcoIzquierdo(), cancha.getArcoDerecho()}) {
            g.setColor(new Color(255, 255, 255, 120));
            g.fillRect(arco.getX(), arco.getY(), arco.getAncho(), arco.getAlto());
            g.setColor(Color.WHITE);
            g.setStroke(new BasicStroke(5));
            g.drawLine(arco.getX(), arco.getY(), arco.getX() + arco.getAncho(), arco.getY());
            int poste = arco.esIzquierdo() ? arco.getX() + arco.getAncho() : arco.getX();
            g.drawLine(poste, arco.getY(), poste, arco.getY() + arco.getAlto());
            g.setStroke(new BasicStroke(1));
        }
    }

    /**
     * Dibuja el balón.
     * @param g contexto gráfico
     * @param balon balón del partido
     */
    private void dibujarBalon(Graphics2D g, Balon balon) {
        int r = balon.getRadio();
        int x = (int) balon.getX() - r;
        int y = (int) balon.getY() - r;
        g.setColor(Color.WHITE);
        g.fillOval(x, y, 2 * r, 2 * r);
        g.setColor(Color.BLACK);
        g.drawOval(x, y, 2 * r, 2 * r);
        g.fillOval(x + r - 4, y + r - 4, 8, 8);
    }

    /**
     * Dibuja un jugador. Cada país se ve distinto (colores de su bandera):
     * cabeza, banda en la frente y cuerpo. Estados Unidos lleva el cuerpo a rayas.
     * Para saber quién es quién, la cabeza tiene un borde del color del jugador
     * (azul J1, rojo J2) y encima se escribe "J1" o "J2" con el país.
     * Si está aturdido se dibuja gris con estrellas.
     * @param g contexto gráfico
     * @param j jugador
     * @param color color del jugador (borde y etiqueta)
     */
    private void dibujarJugador(Graphics2D g, Jugador j, Color color) {
        int r = j.getRadio();
        int x = (int) j.getX();
        int y = (int) j.getY();
        String pais = j.getPersonaje().getPais();
        Color[] colores = coloresPais(pais);
        boolean aturdido = j.estaAturdido();
        Color cabeza = aturdido ? Color.LIGHT_GRAY : colores[0];
        Color banda = aturdido ? Color.GRAY : colores[1];
        Color cuerpo = aturdido ? Color.GRAY : colores[2];
        // cuerpo
        g.setColor(cuerpo);
        g.fillRect(x - r / 2, y + r - 5, r, r + 5);
        if (!aturdido && pais.equals(EstadosUnidos.PAIS)) {
            g.setColor(Color.WHITE);
            for (int franja = y + r; franja < y + 2 * r; franja += 8) {
                g.fillRect(x - r / 2, franja, r, 4);
            }
        }
        g.setColor(Color.BLACK);
        g.drawRect(x - r / 2, y + r - 5, r, r + 5);
        // cabeza con la banda en la frente
        g.setColor(cabeza);
        g.fillOval(x - r, y - r, 2 * r, 2 * r);
        Shape antes = g.getClip();
        g.clipRect(x - r, y - r, 2 * r, 2 * r);
        g.clip(new java.awt.geom.Ellipse2D.Double(x - r, y - r, 2 * r, 2 * r));
        g.setColor(banda);
        g.fillRect(x - r, y - r / 2, 2 * r, r / 3);
        g.setClip(antes);
        // borde del color del jugador
        g.setStroke(new BasicStroke(4));
        g.setColor(aturdido ? Color.GRAY : color);
        g.drawOval(x - r, y - r, 2 * r, 2 * r);
        g.setStroke(new BasicStroke(1));
        // ojo hacia donde mira
        int ojo = j.estaMirandoDerecha() ? x + r / 3 : x - r / 3 - 10;
        g.setColor(Color.WHITE);
        g.fillOval(ojo, y - 4, 12, 12);
        g.setColor(Color.BLACK);
        g.drawOval(ojo, y - 4, 12, 12);
        g.fillOval(ojo + (j.estaMirandoDerecha() ? 6 : 1), y, 5, 5);
        // etiqueta: J1/J2 y país
        g.setFont(new Font("SansSerif", Font.BOLD, 13));
        String etiqueta = "J" + j.getNumero() + " · " + pais;
        g.setColor(color);
        g.drawString(etiqueta, x - g.getFontMetrics().stringWidth(etiqueta) / 2, y - r - 8);
        if (aturdido) {
            g.setColor(Color.YELLOW);
            g.drawString("* * *", x - 15, y - r - 22);
        }
    }

    /**
     * Colores de cada país, tomados de su bandera.
     * @param pais nombre del país
     * @return arreglo {cabeza, banda, cuerpo}
     */
    private Color[] coloresPais(String pais) {
        if (pais.equals(CoreaDelSur.PAIS)) return new Color[] {Color.WHITE, new Color(205, 46, 58), new Color(0, 71, 160)};
        if (pais.equals(Camerun.PAIS)) return new Color[] {new Color(0, 122, 94), new Color(252, 209, 22), new Color(206, 17, 38)};
        if (pais.equals(Nigeria.PAIS)) return new Color[] {Color.WHITE, new Color(0, 135, 81), new Color(0, 135, 81)};
        if (pais.equals(EstadosUnidos.PAIS)) return new Color[] {new Color(60, 59, 110), Color.WHITE, new Color(178, 34, 52)};
        return new Color[] {Color.LIGHT_GRAY, Color.GRAY, Color.DARK_GRAY};
    }

    /**
     * Dibuja el marcador, el tiempo y la información de cada jugador.
     * @param g contexto gráfico
     * @param partido partido en curso
     * @param cancha cancha del partido
     * @throws DOPOSoccerException si no se puede leer un jugador
     */
    private void dibujarEstado(Graphics2D g, Partido partido, Cancha cancha) throws DOPOSoccerException {
        Jugador j1 = partido.getJugador(1);
        Jugador j2 = partido.getJugador(2);
        int centro = cancha.getAncho() / 2;
        g.setColor(new Color(0, 0, 0, 150));
        g.fillRoundRect(centro - 90, 8, 180, 60, 15, 15);
        g.setColor(Color.WHITE);
        g.setFont(new Font("SansSerif", Font.BOLD, 28));
        String marcador = j1.getGoles() + " - " + j2.getGoles();
        g.drawString(marcador, centro - g.getFontMetrics().stringWidth(marcador) / 2, 40);
        g.setFont(new Font("SansSerif", Font.PLAIN, 14));
        String tiempo = textoTiempo(partido);
        g.drawString(tiempo, centro - g.getFontMetrics().stringWidth(tiempo) / 2, 60);
        dibujarInfoJugador(g, j1, 20, COLOR_J1);
        dibujarInfoJugador(g, j2, cancha.getAncho() - 240, COLOR_J2);
        if (partido.estaPausado()) {
            // Se oscurece la cancha pero no el marcador, para que el estado siga a la vista.
            g.setColor(new Color(0, 0, 0, 120));
            g.fillRect(0, 70, cancha.getAncho(), cancha.getAlto() - 70);
        }
    }

    /**
     * Arma el texto del tiempo según el tipo de partido.
     * @param partido partido en curso
     * @return texto a mostrar
     */
    private String textoTiempo(Partido partido) {
        if (partido instanceof PartidoPorTiempo) {
            PartidoPorTiempo p = (PartidoPorTiempo) partido;
            if (p.estaEnMuerteSubita()) return "MUERTE SÚBITA";
            return formato(p.getTiempoRestante());
        }
        return "A " + ((PartidoPorGoles) partido).getGolesParaGanar() + " goles - " + formato(partido.getTiempoJugado());
    }

    /**
     * Convierte segundos a mm:ss.
     * @param segundos segundos
     * @return texto mm:ss
     */
    private String formato(double segundos) {
        int s = (int) Math.ceil(segundos);
        return String.format("%02d:%02d", s / 60, s % 60);
    }

    /**
     * Dibuja la vida y la barra de poder de un jugador.
     * @param g contexto gráfico
     * @param j jugador
     * @param x posición horizontal del recuadro
     * @param color color del jugador
     */
    private void dibujarInfoJugador(Graphics2D g, Jugador j, int x, Color color) {
        double vidaMax = j.getEstadisticas().getVidaMax();
        g.setColor(color);
        g.setFont(new Font("SansSerif", Font.BOLD, 14));
        g.drawString("J" + j.getNumero() + " - " + j.getPersonaje().getPais(), x, 22);
        g.setColor(Color.DARK_GRAY);
        g.fillRect(x, 28, 220, 14);
        g.setColor(j.estaAturdido() ? Color.GRAY : new Color(40, 200, 60));
        g.fillRect(x, 28, (int) (220 * j.getVida() / vidaMax), 14);
        g.setColor(Color.WHITE);
        g.setFont(new Font("SansSerif", Font.PLAIN, 11));
        g.drawString("Vida " + (int) j.getVida() + "/" + (int) vidaMax, x + 4, 39);
        g.setColor(Color.DARK_GRAY);
        g.fillRect(x, 46, 220, 10);
        g.setColor(Color.WHITE);
        g.drawString("Poder (versión dos)", x + 4, 55);
    }
}
