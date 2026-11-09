package presentation;

import domain.DOPOSoccer;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.border.*;

/**
 * Pantalla de configuración del partido: modalidad, perfil de la máquina, resolución,
 * personaje de cada jugador y tipo de partido.
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

    private JComboBox<String> comboModalidad;
    private JComboBox<String> comboPerfil;
    private JComboBox<String> comboResolucion;
    private JComboBox<String> comboPais1;
    private JComboBox<String> comboPais2;
    private JComboBox<String> comboTipo;
    private JTextField textoValor;
    private JButton botonIniciar;

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
        JLabel titulo = new JLabel("DOPOSoccer", SwingConstants.CENTER);
        titulo.setFont(new Font("SansSerif", Font.BOLD, 36));
        add(titulo, BorderLayout.NORTH);

        comboModalidad = new JComboBox<>(MODALIDADES);
        comboPerfil = new JComboBox<>(PERFILES);
        comboPerfil.setEnabled(false);
        comboResolucion = new JComboBox<>(RESOLUCIONES);
        comboResolucion.setSelectedIndex(0);
        comboPais1 = new JComboBox<>(DOPOSoccer.PAISES);
        comboPais2 = new JComboBox<>(DOPOSoccer.PAISES);
        comboPais2.setSelectedIndex(1);
        comboTipo = new JComboBox<>(TIPOS);
        textoValor = new JTextField("2");

        JPanel centro = new JPanel(new GridLayout(1, 2, 20, 0));
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

        JPanel jugadores = new JPanel(new GridLayout(4, 2, 10, 10));
        jugadores.setBorder(new TitledBorder("Jugadores"));
        jugadores.add(new JLabel("Personaje jugador 1:"));
        jugadores.add(comboPais1);
        jugadores.add(new JLabel("Personaje jugador 2:"));
        jugadores.add(comboPais2);
        jugadores.add(new JLabel("Minutos o goles:"));
        jugadores.add(textoValor);
        jugadores.add(new JLabel("J1: A D W S   J2: flechas"));
        jugadores.add(new JLabel("P: pausa   Esc: terminar"));

        centro.add(juego);
        centro.add(jugadores);
        add(centro, BorderLayout.CENTER);

        botonIniciar = new JButton("Iniciar partido");
        botonIniciar.setFont(new Font("SansSerif", Font.BOLD, 18));
        add(botonIniciar, BorderLayout.SOUTH);
    }

    /**
     * Habilita el perfil de la máquina solo cuando se elige jugador vs máquina.
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
     * Activa o desactiva el combo de perfil según la modalidad.
     */
    private void comboModalidadAction() {
        comboPerfil.setEnabled(esContraMaquina());
    }

    /**
     * @return botón para iniciar el partido (la ventana principal le asigna la acción)
     */
    public JButton getBotonIniciar() { return botonIniciar; }

    /**
     * @return true si se eligió jugador vs máquina
     */
    public boolean esContraMaquina() { return comboModalidad.getSelectedIndex() == 1; }

    /**
     * @return país del jugador 1
     */
    public String getPais1() { return (String) comboPais1.getSelectedItem(); }

    /**
     * @return país del jugador 2
     */
    public String getPais2() { return (String) comboPais2.getSelectedItem(); }

    /**
     * @return "tiempo" o "goles"
     */
    public String getTipo() {
        return comboTipo.getSelectedIndex() == 0 ? DOPOSoccer.POR_TIEMPO : DOPOSoccer.POR_GOLES;
    }

    /**
     * @return texto escrito en minutos o goles
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
