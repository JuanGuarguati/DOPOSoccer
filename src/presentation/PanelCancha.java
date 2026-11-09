package presentation;

import domain.*;
import java.awt.*;
import javax.swing.*;

/**
 * Dibuja la cancha, los arcos, el balón, los jugadores y el estado del juego
 * (tiempo, marcador, vida y barra de poder de cada jugador).
 * Las medidas del dominio se escalan a la resolución elegida.
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

    private DOPOSoccer dominio;

    /**
     * Crea el panel de la cancha.
     * @param dominio fachada del juego de donde se lee el estado
     */
    public PanelCancha(DOPOSoccer dominio) {
        this.dominio = dominio;
        setFocusable(true);
        setBackground(CIELO);
    }

    /**
     * Dibuja todo el estado del partido.
     * @param g contexto gráfico
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
     * Dibuja un jugador: cabeza grande, ojo hacia donde mira, cuerpo y nombre del país.
     * Si está aturdido se dibuja gris con estrellas.
     * @param g contexto gráfico
     * @param j jugador
     * @param color color del jugador
     */
    private void dibujarJugador(Graphics2D g, Jugador j, Color color) {
        int r = j.getRadio();
        int x = (int) j.getX();
        int y = (int) j.getY();
        Color c = j.estaAturdido() ? Color.GRAY : color;
        g.setColor(c.darker());
        g.fillRect(x - r / 2, y + r - 5, r, r + 5);
        g.setColor(c);
        g.fillOval(x - r, y - r, 2 * r, 2 * r);
        g.setColor(Color.WHITE);
        int ojo = j.estaMirandoDerecha() ? x + r / 3 : x - r / 3 - 10;
        g.fillOval(ojo, y - 8, 10, 10);
        g.setColor(Color.BLACK);
        g.setFont(new Font("SansSerif", Font.BOLD, 12));
        String pais = j.getPersonaje().getPais();
        g.drawString(pais, x - g.getFontMetrics().stringWidth(pais) / 2, y - r - 8);
        if (j.estaAturdido()) {
            g.setColor(Color.YELLOW);
            g.drawString("* * *", x - 15, y - r - 22);
        }
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
            g.setColor(new Color(0, 0, 0, 120));
            g.fillRect(0, 0, cancha.getAncho(), cancha.getAlto());
            g.setColor(Color.WHITE);
            g.setFont(new Font("SansSerif", Font.BOLD, 48));
            g.drawString("PAUSA", centro - 80, cancha.getAlto() / 2);
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
