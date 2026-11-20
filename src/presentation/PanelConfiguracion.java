package presentation;

import domain.DOPOSoccer;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.border.*;

/**
 * JPanel: pantalla de configuración del partido (modalidad, perfil de la máquina,
 * resolución, tipo de partido y minutos o goles). Los países se eligen después, en PanelJugadores.
 * Componentes Swing que usa:
 * JLabel (título y etiquetas), JComboBox (listas de opciones),
 * JTextField (minutos o goles), JButton (siguiente) y JPanel con TitledBorder (grupos).
 */
public class PanelConfiguracion extends JPanel {

    /** Opciones de modalidad de juego. */
    public static final String[] MODALIDADES = {"Jugador vs Jugador", "Jugador vs Máquina (versión dos)"};
    /** Perfiles de la máquina. */
    public static final String[] PERFILES = {"Torpe", "Ofensiva", "Defensiva", "Experta"};
    /** Resoluciones que se pueden elegir. */
    public static final String[] RESOLUCIONES = {"1280 x 720", "1600 x 900", "1920 x 1080"};
    /** Tipos de partido que se pueden elegir. */
    public static final String[] TIPOS = {"Por tiempo (minutos)", "Por goles"};

    /** JComboBox: modalidad de juego. */
    private JComboBox<String> comboModalidad;
    /** JComboBox: perfil de la máquina (solo activo en jugador vs máquina). */
    private JComboBox<String> comboPerfil;
    /** JComboBox: resolución de la cancha. */
    private JComboBox<String> comboResolucion;
    /** JComboBox: tipo de partido (por tiempo o por goles). */
    private JComboBox<String> comboTipo;
    /** JTextField: caja de texto para escribir los minutos o los goles. */
    private JTextField textoValor;
    /** JButton: botón para pasar a la pantalla de jugadores. */
    private JButton botonSiguiente;

    /**
     * Crea la pantalla de configuración.
     */
    public PanelConfiguracion() {
        prepareView();
        prepareController();
    }

    /**
     * Organiza los componentes de la pantalla.
     */
    private void prepareView() {
        setLayout(new BorderLayout(10, 10));
        setBorder(new EmptyBorder(20, 40, 20, 40));
        // JLabel: título de la pantalla
        JLabel titulo = new JLabel("DOPOSoccer", SwingConstants.CENTER);
        titulo.setFont(new Font("SansSerif", Font.BOLD, 36));
        add(titulo, BorderLayout.NORTH);

        // JComboBox y JTextField: opciones que elige el usuario
        comboModalidad = new JComboBox<>(MODALIDADES);
        comboPerfil = new JComboBox<>(PERFILES);
        comboPerfil.setEnabled(false);
        comboResolucion = new JComboBox<>(RESOLUCIONES);
        comboResolucion.setSelectedIndex(0);
        comboTipo = new JComboBox<>(TIPOS);
        textoValor = new JTextField("2");

        // JPanel centro: contiene los dos grupos lado a lado
        JPanel centro = new JPanel(new GridLayout(1, 2, 20, 0));
        // JPanel con TitledBorder: grupo "Juego"
        JPanel juego = new JPanel(new GridLayout(4, 2, 10, 10));
        juego.setBorder(new TitledBorder("Juego"));
        juego.add(new JLabel("Modalidad:"));
        juego.add(comboModalidad);
        juego.add(new JLabel("Perfil de la máquina:"));
        juego.add(comboPerfil);
        juego.add(new JLabel("Resolución:"));
        juego.add(comboResolucion);
        juego.add(new JLabel("Tipo de partido:"));
        juego.add(comboTipo);

        // JPanel con TitledBorder: grupo "Partido"
        JPanel partido = new JPanel(new GridLayout(4, 2, 10, 10));
        partido.setBorder(new TitledBorder("Partido"));
        partido.add(new JLabel("Minutos o goles:"));
        partido.add(textoValor);
        partido.add(new JLabel("P: pausa"));
        partido.add(new JLabel("Esc: terminar"));

        centro.add(juego);
        centro.add(partido);
        add(centro, BorderLayout.CENTER);

        // JButton: la ventana principal (JFrame) le asigna la acción
        botonSiguiente = new JButton("Siguiente: elegir jugadores");
        botonSiguiente.setFont(new Font("SansSerif", Font.BOLD, 18));
        add(botonSiguiente, BorderLayout.SOUTH);
    }

    /**
     * Oyente (ActionListener) del JComboBox de modalidad: habilita el perfil
     * de la máquina solo cuando se elige jugador vs máquina.
     */
    private void prepareController() {
        comboModalidad.addActionListener(new ActionListener() {
            /** Atiende el cambio de modalidad. */
            public void actionPerformed(ActionEvent e) {
                comboModalidadAction();
            }
        });
    }

    /**
     * Activa o desactiva el JComboBox de perfil según la modalidad.
     */
    private void comboModalidadAction() {
        comboPerfil.setEnabled(esContraMaquina());
    }

    /**
     * @return JButton para pasar a la pantalla de jugadores (la ventana principal le asigna la acción)
     */
    public JButton getBotonSiguiente() { return botonSiguiente; }

    /**
     * @return true si se eligió jugador vs máquina
     */
    public boolean esContraMaquina() { return comboModalidad.getSelectedIndex() == 1; }

    /**
     * @return "tiempo" o "goles"
     */
    public String getTipo() {
        return comboTipo.getSelectedIndex() == 0 ? DOPOSoccer.POR_TIEMPO : DOPOSoccer.POR_GOLES;
    }

    /**
     * @return texto escrito en el JTextField de minutos o goles
     */
    public String getValor() { return textoValor.getText().trim(); }

    /**
     * Retorna el tamaño de cancha elegido.
     * @return dimensión en píxeles de la resolución seleccionada
     */
    public Dimension getResolucion() {
        String[] partes = ((String) comboResolucion.getSelectedItem()).split(" x ");
        return new Dimension(Integer.parseInt(partes[0]), Integer.parseInt(partes[1]));
    }
}
