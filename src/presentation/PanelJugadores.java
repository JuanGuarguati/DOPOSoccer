package presentation;

import domain.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.border.*;

/**
 * JPanel: pantalla de jugadores. Cada jugador elige su país y gasta sus puntos
 * mejorando sus estadísticas (speed, kick, jump, survival) antes del partido.
 * Componentes Swing que usa:
 * JComboBox (país), JLabel (puntos, precio y valor de cada estadística),
 * JButton (mejorar, volver e iniciar) y JPanel con TitledBorder (un grupo por jugador).
 */
public class PanelJugadores extends JPanel {

    /** Estadísticas que se pueden mejorar, con el nombre que usa el dominio. */
    public static final String[] ESTADISTICAS = {"speed", "kick", "jump", "survival"};
    /** Nombre que se muestra para cada estadística. */
    public static final String[] NOMBRES = {"Velocidad", "Patada", "Salto", "Resistencia"};

    /** Fachada del dominio de donde se leen puntos y estadísticas. */
    private DOPOSoccer dominio;
    /** JComboBox: país del jugador 1. */
    private JComboBox<String> comboPais1;
    /** JComboBox: país del jugador 2. */
    private JComboBox<String> comboPais2;
    /** JLabel: puntos de cada jugador. */
    private JLabel[] etiquetasPuntos;
    /** JLabel: precio de la siguiente mejora de cada jugador. */
    private JLabel[] etiquetasPrecio;
    /** JLabel: valor de cada estadística, [jugador][estadística]. */
    private JLabel[][] etiquetasValor;
    /** JButton: botón "+" de cada estadística, [jugador][estadística]. */
    private JButton[][] botonesMejorar;
    /** JButton: vuelve a la configuración. */
    private JButton botonVolver;
    /** JButton: inicia el partido. */
    private JButton botonIniciar;

    /**
     * Crea la pantalla de jugadores.
     * @param dominio fachada del juego
     */
    public PanelJugadores(DOPOSoccer dominio) {
        this.dominio = dominio;
        prepareView();
        prepareController();
        actualizar();
    }

    /**
     * Organiza los componentes: título, un grupo por jugador y los botones de abajo.
     */
    private void prepareView() {
        setLayout(new BorderLayout(10, 10));
        setBorder(new EmptyBorder(20, 40, 20, 40));
        JLabel titulo = new JLabel("Jugadores", SwingConstants.CENTER);
        titulo.setFont(new Font("SansSerif", Font.BOLD, 30));
        add(titulo, BorderLayout.NORTH);

        comboPais1 = new JComboBox<>(DOPOSoccer.PAISES);
        comboPais2 = new JComboBox<>(DOPOSoccer.PAISES);
        comboPais2.setSelectedIndex(1);
        etiquetasPuntos = new JLabel[2];
        etiquetasPrecio = new JLabel[2];
        etiquetasValor = new JLabel[2][ESTADISTICAS.length];
        botonesMejorar = new JButton[2][ESTADISTICAS.length];

        JPanel centro = new JPanel(new GridLayout(1, 2, 20, 0));
        centro.add(crearGrupo(1, comboPais1, PanelCancha.COLOR_J1, "A D mover, W saltar, S patear"));
        centro.add(crearGrupo(2, comboPais2, PanelCancha.COLOR_J2, "Flechas mover, arriba saltar, abajo patear"));
        add(centro, BorderLayout.CENTER);

        botonVolver = new JButton("Volver");
        botonIniciar = new JButton("Iniciar partido");
        botonIniciar.setFont(new Font("SansSerif", Font.BOLD, 18));
        JPanel abajo = new JPanel(new BorderLayout());
        abajo.add(botonVolver, BorderLayout.WEST);
        abajo.add(botonIniciar, BorderLayout.CENTER);
        add(abajo, BorderLayout.SOUTH);
    }

    /**
     * Arma el grupo de un jugador: país, puntos, precio, cada estadística con su botón "+" y los controles.
     * @param numero número del jugador (1 o 2)
     * @param comboPais JComboBox del país de ese jugador
     * @param color color del jugador
     * @param controles texto con las teclas del jugador
     * @return el JPanel del grupo
     */
    private JPanel crearGrupo(int numero, JComboBox<String> comboPais, Color color, String controles) {
        int i = numero - 1;
        JPanel grupo = new JPanel(new GridLayout(ESTADISTICAS.length + 4, 2, 8, 6));
        TitledBorder borde = new TitledBorder("Jugador " + numero);
        borde.setTitleColor(color);
        borde.setTitleFont(new Font("SansSerif", Font.BOLD, 16));
        grupo.setBorder(borde);
        grupo.add(new JLabel("País:"));
        grupo.add(comboPais);
        etiquetasPuntos[i] = new JLabel();
        etiquetasPrecio[i] = new JLabel();
        grupo.add(new JLabel("Puntos:"));
        grupo.add(etiquetasPuntos[i]);
        grupo.add(new JLabel("Precio de la mejora:"));
        grupo.add(etiquetasPrecio[i]);
        for (int k = 0; k < ESTADISTICAS.length; k++) {
            etiquetasValor[i][k] = new JLabel();
            botonesMejorar[i][k] = new JButton("+");
            botonesMejorar[i][k].setToolTipText("Mejorar " + NOMBRES[k]);
            JPanel fila = new JPanel(new BorderLayout(6, 0));
            fila.add(etiquetasValor[i][k], BorderLayout.CENTER);
            fila.add(botonesMejorar[i][k], BorderLayout.EAST);
            grupo.add(new JLabel(NOMBRES[k] + ":"));
            grupo.add(fila);
        }
        grupo.add(new JLabel("Controles:"));
        grupo.add(new JLabel(controles));
        return grupo;
    }

    /**
     * Oyentes (ActionListener) de los botones "+": cada uno mejora su estadística.
     */
    private void prepareController() {
        for (int i = 0; i < 2; i++) {
            for (int k = 0; k < ESTADISTICAS.length; k++) {
                final int numero = i + 1;
                final String estadistica = ESTADISTICAS[k];
                botonesMejorar[i][k].addActionListener(new ActionListener() {
                    /** Atiende el botón "+" de una estadística. */
                    public void actionPerformed(ActionEvent e) {
                        mejorarAction(numero, estadistica);
                    }
                });
            }
        }
    }

    /**
     * Compra una mejora para un jugador; si no se puede, muestra el motivo.
     * @param numero número del jugador (1 o 2)
     * @param estadistica estadística a mejorar
     */
    private void mejorarAction(int numero, String estadistica) {
        try {
            dominio.mejorarEstadistica(numero, estadistica);
        } catch (DOPOSoccerException e) {
            JOptionPane.showMessageDialog(this, e.getMessage(), "No se pudo mejorar", JOptionPane.WARNING_MESSAGE);
        }
        actualizar();
    }

    /**
     * Vuelve a leer del dominio los puntos, el precio y las estadísticas de los dos jugadores.
     */
    public void actualizar() {
        try {
            for (int i = 0; i < 2; i++) {
                int numero = i + 1;
                Estadisticas e = dominio.getEstadisticas(numero);
                int[] valores = {e.getSpeed(), e.getKick(), e.getJump(), e.getSurvival()};
                etiquetasPuntos[i].setText(String.valueOf(dominio.getPuntos(numero)));
                etiquetasPrecio[i].setText(dominio.getPrecioMejora(numero) + " puntos");
                for (int k = 0; k < ESTADISTICAS.length; k++) {
                    etiquetasValor[i][k].setText(valores[k] + " / " + Estadisticas.MAXIMO);
                    botonesMejorar[i][k].setEnabled(valores[k] < Estadisticas.MAXIMO);
                }
            }
        } catch (DOPOSoccerException e) {
            Log.record(e);
        }
    }

    /**
     * @return país del jugador 1
     */
    public String getPais1() { return (String) comboPais1.getSelectedItem(); }

    /**
     * @return país del jugador 2
     */
    public String getPais2() { return (String) comboPais2.getSelectedItem(); }

    /**
     * @return JButton para volver a la configuración
     */
    public JButton getBotonVolver() { return botonVolver; }

    /**
     * @return JButton para iniciar el partido
     */
    public JButton getBotonIniciar() { return botonIniciar; }
}
