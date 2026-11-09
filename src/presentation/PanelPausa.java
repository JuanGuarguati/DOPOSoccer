package presentation;

import java.awt.*;
import javax.swing.*;
import javax.swing.border.*;

/**
 * JPanel: menú que aparece sobre la cancha cuando el partido está en pausa.
 * Componentes Swing que contiene:
 * JLabel con el título "PAUSA" y seis JButton (continuar, terminar partido,
 * guardar partida, volver al menú, sonido y música).
 * La ventana principal (JFrame) le asigna la acción a cada JButton.
 */
public class PanelPausa extends JPanel {

    /** Color de fondo del menú (semitransparente). */
    public static final Color FONDO = new Color(30, 30, 30, 210);

    /** JButton: reanuda el partido. */
    private JButton botonContinuar;
    /** JButton: termina el partido. */
    private JButton botonTerminar;
    /** JButton: guardar partida (versión dos). */
    private JButton botonGuardar;
    /** JButton: vuelve a la configuración. */
    private JButton botonMenu;
    /** JButton: sonido (versión dos). */
    private JButton botonSonido;
    /** JButton: música (versión dos). */
    private JButton botonMusica;

    /**
     * Crea el menú de pausa.
     */
    public PanelPausa() {
        prepareView();
    }

    /**
     * Organiza el JLabel del título (arriba) y los seis JButton en dos columnas (centro).
     */
    private void prepareView() {
        setOpaque(false);
        setLayout(new BorderLayout(10, 15));
        setBorder(new EmptyBorder(20, 30, 25, 30));
        // JLabel: título del menú
        JLabel titulo = new JLabel("PAUSA", SwingConstants.CENTER);
        titulo.setFont(new Font("SansSerif", Font.BOLD, 40));
        titulo.setForeground(Color.WHITE);
        add(titulo, BorderLayout.NORTH);
        botonContinuar = crearBoton("Continuar");
        botonTerminar = crearBoton("Terminar partido");
        botonGuardar = crearBoton("Guardar partida");
        botonMenu = crearBoton("Volver al menú");
        botonSonido = crearBoton("Sonido");
        botonMusica = crearBoton("Música");
        // JPanel transparente con GridLayout 3x2 para los JButton
        JPanel botones = new JPanel(new GridLayout(3, 2, 15, 12));
        botones.setOpaque(false);
        botones.add(botonContinuar);
        botones.add(botonTerminar);
        botones.add(botonGuardar);
        botones.add(botonSonido);
        botones.add(botonMenu);
        botones.add(botonMusica);
        add(botones, BorderLayout.CENTER);
    }

    /**
     * Crea un JButton del menú. No toma el foco para que el teclado siga en la cancha.
     * @param texto texto del botón
     * @return el botón
     */
    private JButton crearBoton(String texto) {
        JButton boton = new JButton(texto);
        boton.setFont(new Font("SansSerif", Font.BOLD, 16));
        boton.setFocusable(false);
        boton.setPreferredSize(new Dimension(200, 45));
        return boton;
    }

    /**
     * Dibuja el fondo redondeado y semitransparente del menú.
     * @param g contexto gráfico
     */
    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setColor(FONDO);
        g2.fillRoundRect(0, 0, getWidth(), getHeight(), 30, 30);
        g2.dispose();
        super.paintComponent(g);
    }

    /**
     * @return JButton Continuar
     */
    public JButton getBotonContinuar() { return botonContinuar; }

    /**
     * @return JButton Terminar partido
     */
    public JButton getBotonTerminar() { return botonTerminar; }

    /**
     * @return JButton Guardar partida
     */
    public JButton getBotonGuardar() { return botonGuardar; }

    /**
     * @return JButton Volver al menú
     */
    public JButton getBotonMenu() { return botonMenu; }

    /**
     * @return JButton Sonido
     */
    public JButton getBotonSonido() { return botonSonido; }

    /**
     * @return JButton Música
     */
    public JButton getBotonMusica() { return botonMusica; }
}
