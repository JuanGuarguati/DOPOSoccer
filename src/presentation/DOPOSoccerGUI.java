package presentation;

import domain.*;
import java.awt.*;
import java.awt.event.*;
import java.util.HashSet;
import java.util.Set;
import javax.swing.*;

/**
 * Ventana principal de DOPOSoccer (MVC: modelo = DOPOSoccer, vista = paneles, controlador = oyentes).
 * Muestra la configuración y luego la cancha. Controles:
 * jugador 1 A/D mover, W saltar, S patear; jugador 2 flechas, arriba saltar, abajo patear;
 * P pausa, Esc terminar.
 */
public class DOPOSoccerGUI extends JFrame {

    /** Nombre de la tarjeta de configuración. */
    public static final String CONFIGURACION = "configuracion";
    /** Nombre de la tarjeta de la cancha. */
    public static final String CANCHA = "cancha";
    /** Milisegundos entre cada ciclo del juego. */
    public static final int MILISEGUNDOS = 16;

    private DOPOSoccer dominio;
    private CardLayout tarjetas;
    private JPanel principal;
    private PanelConfiguracion panelConfiguracion;
    private PanelCancha panelCancha;
    private Timer timer;
    private Set<Integer> teclasPresionadas;
    private JMenuItem itemNuevo;
    private JMenuItem itemAbrir;
    private JMenuItem itemGuardar;
    private JMenuItem itemPausa;
    private JMenuItem itemTerminar;
    private JMenuItem itemSalir;

    /**
     * Crea la ventana: modelo, vista y controlador.
     */
    private DOPOSoccerGUI() {
        dominio = new DOPOSoccer();
        teclasPresionadas = new HashSet<>();
        prepareView();
        prepareController();
    }

    /**
     * Arma la vista: menú, pantalla de configuración y pantalla de cancha.
     */
    private void prepareView() {
        setTitle("DOPOSoccer");
        prepareViewMenu();
        tarjetas = new CardLayout();
        principal = new JPanel(tarjetas);
        panelConfiguracion = new PanelConfiguracion();
        panelCancha = new PanelCancha(dominio);
        principal.add(panelConfiguracion, CONFIGURACION);
        principal.add(panelCancha, CANCHA);
        add(principal);
        setSize(900, 500);
        setLocationRelativeTo(null);
    }

    /**
     * Arma el menú Archivo y Juego.
     */
    private void prepareViewMenu() {
        JMenuBar barra = new JMenuBar();
        JMenu archivo = new JMenu("Archivo");
        itemNuevo = new JMenuItem("Nuevo partido");
        itemAbrir = new JMenuItem("Abrir partida");
        itemGuardar = new JMenuItem("Guardar partida");
        itemSalir = new JMenuItem("Salir");
        archivo.add(itemNuevo);
        archivo.add(itemAbrir);
        archivo.add(itemGuardar);
        archivo.addSeparator();
        archivo.add(itemSalir);
        JMenu juego = new JMenu("Juego");
        itemPausa = new JMenuItem("Pausar / continuar (P)");
        itemTerminar = new JMenuItem("Terminar partido (Esc)");
        juego.add(itemPausa);
        juego.add(itemTerminar);
        barra.add(archivo);
        barra.add(juego);
        setJMenuBar(barra);
    }

    /**
     * Asigna los oyentes: ventana, menú, botón iniciar, teclado y timer del juego.
     */
    private void prepareController() {
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            /** Pide confirmación antes de cerrar la ventana. */
            public void windowClosing(WindowEvent e) {
                salirAction();
            }
        });
        itemSalir.addActionListener(new ActionListener() {
            /** Atiende la opción Salir. */
            public void actionPerformed(ActionEvent e) {
                salirAction();
            }
        });
        itemNuevo.addActionListener(new ActionListener() {
            /** Atiende la opción Nuevo. */
            public void actionPerformed(ActionEvent e) {
                nuevoAction();
            }
        });
        ActionListener proximamente = new ActionListener() {
            /** Atiende Abrir y Guardar, que llegan en la Versión dos. */
            public void actionPerformed(ActionEvent e) {
                proximamenteAction();
            }
        };
        itemAbrir.addActionListener(proximamente);
        itemGuardar.addActionListener(proximamente);
        itemPausa.addActionListener(new ActionListener() {
            /** Atiende la opción Pausa. */
            public void actionPerformed(ActionEvent e) {
                pausaAction();
            }
        });
        itemTerminar.addActionListener(new ActionListener() {
            /** Atiende la opción Terminar. */
            public void actionPerformed(ActionEvent e) {
                terminarAction();
            }
        });
        panelConfiguracion.getBotonIniciar().addActionListener(new ActionListener() {
            /** Atiende el botón Iniciar. */
            public void actionPerformed(ActionEvent e) {
                iniciarAction();
            }
        });
        panelCancha.addKeyListener(new KeyAdapter() {
            /** Registra la tecla presionada. */
            public void keyPressed(KeyEvent e) {
                teclaPresionadaAction(e.getKeyCode());
            }
            /** Quita la tecla que se soltó. */
            public void keyReleased(KeyEvent e) {
                teclasPresionadas.remove(e.getKeyCode());
            }
        });
        timer = new Timer(MILISEGUNDOS, new ActionListener() {
            /** Ejecuta un ciclo del juego en cada tic del timer. */
            public void actionPerformed(ActionEvent e) {
                cicloAction();
            }
        });
    }

    /**
     * Lee la configuración e inicia el partido. Muestra un mensaje si algo no es válido.
     */
    private void iniciarAction() {
        if (panelConfiguracion.esContraMaquina()) {
            JOptionPane.showMessageDialog(this, "El modo jugador vs máquina estará disponible en la versión dos.",
                "DOPOSoccer", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        try {
            int valor = Integer.parseInt(panelConfiguracion.getValor());
            dominio.iniciarPartido(panelConfiguracion.getPais1(), panelConfiguracion.getPais2(),
                panelConfiguracion.getTipo(), valor);
            Dimension resolucion = panelConfiguracion.getResolucion();
            panelCancha.setPreferredSize(resolucion);
            tarjetas.show(principal, CANCHA);
            pack();
            setLocationRelativeTo(null);
            teclasPresionadas.clear();
            panelCancha.requestFocusInWindow();
            timer.start();
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Escriba un número entero en minutos o goles.",
                "Error", JOptionPane.ERROR_MESSAGE);
        } catch (DOPOSoccerException e) {
            JOptionPane.showMessageDialog(this, e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Atiende una tecla: saltar, patear, pausar y terminar se hacen una vez;
     * las de movimiento se guardan para moverse mientras estén presionadas.
     * @param codigo código de la tecla
     */
    private void teclaPresionadaAction(int codigo) {
        try {
            switch (codigo) {
                case KeyEvent.VK_W: dominio.saltarJugador(1); break;
                case KeyEvent.VK_S: dominio.patearJugador(1); break;
                case KeyEvent.VK_UP: dominio.saltarJugador(2); break;
                case KeyEvent.VK_DOWN: dominio.patearJugador(2); break;
                case KeyEvent.VK_P: pausaAction(); break;
                case KeyEvent.VK_ESCAPE: terminarAction(); break;
                default: teclasPresionadas.add(codigo);
            }
        } catch (DOPOSoccerException e) {
            Log.record(e);
        }
    }

    /**
     * Un ciclo del juego: mueve según las teclas presionadas, actualiza el dominio,
     * repinta y revisa si el partido terminó.
     */
    private void cicloAction() {
        try {
            Partido partido = dominio.getPartido();
            if (partido != null && !partido.estaPausado() && !partido.estaTerminado()) {
                if (teclasPresionadas.contains(KeyEvent.VK_A)) dominio.moverJugador(1, "izquierda");
                if (teclasPresionadas.contains(KeyEvent.VK_D)) dominio.moverJugador(1, "derecha");
                if (teclasPresionadas.contains(KeyEvent.VK_LEFT)) dominio.moverJugador(2, "izquierda");
                if (teclasPresionadas.contains(KeyEvent.VK_RIGHT)) dominio.moverJugador(2, "derecha");
            }
            dominio.actualizar(MILISEGUNDOS / 1000.0);
            panelCancha.repaint();
            if (partido != null && partido.estaTerminado()) {
                finAction();
            }
        } catch (DOPOSoccerException e) {
            Log.record(e);
        }
    }

    /**
     * Muestra el ganador y los puntos obtenidos, y vuelve a la configuración.
     */
    private void finAction() {
        timer.stop();
        try {
            Partido p = dominio.getPartido();
            Jugador ganador = p.getGanador();
            String mensaje;
            if (ganador == null) {
                mensaje = "Partido terminado en empate " + p.getJugador(1).getGoles() + " - " + p.getJugador(2).getGoles() + ".";
            } else {
                mensaje = "¡Ganó el jugador " + ganador.getNumero() + " (" + ganador.getPersonaje().getPais() + ")!\n"
                    + "Marcador: " + p.getJugador(1).getGoles() + " - " + p.getJugador(2).getGoles() + "\n"
                    + "Puntos: jugador 1 = " + p.getJugador(1).getPuntos() + ", jugador 2 = " + p.getJugador(2).getPuntos();
            }
            JOptionPane.showMessageDialog(this, mensaje, "Fin del partido", JOptionPane.INFORMATION_MESSAGE);
        } catch (DOPOSoccerException e) {
            Log.record(e);
        }
        nuevoAction();
    }

    /**
     * Pausa o reanuda el partido.
     */
    private void pausaAction() {
        try {
            dominio.pausar();
            panelCancha.repaint();
        } catch (DOPOSoccerException e) {
            JOptionPane.showMessageDialog(this, e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Termina el partido en cualquier momento, después de confirmar.
     */
    private void terminarAction() {
        Partido p = dominio.getPartido();
        if (p == null || p.estaTerminado()) return;
        int opcion = JOptionPane.showConfirmDialog(this, "¿Desea terminar el partido?", "Terminar",
            JOptionPane.YES_NO_OPTION);
        if (opcion == JOptionPane.YES_OPTION) {
            try {
                dominio.terminar();
            } catch (DOPOSoccerException e) {
                Log.record(e);
            }
        }
        panelCancha.requestFocusInWindow();
    }

    /**
     * Vuelve a la pantalla de configuración para un partido nuevo.
     */
    private void nuevoAction() {
        timer.stop();
        tarjetas.show(principal, CONFIGURACION);
        setSize(900, 500);
        setLocationRelativeTo(null);
    }

    /**
     * Informa que abrir y guardar partidas llega en la versión dos.
     */
    private void proximamenteAction() {
        JOptionPane.showMessageDialog(this, "Guardar y abrir partidas estará disponible en la versión dos.",
            "DOPOSoccer", JOptionPane.INFORMATION_MESSAGE);
    }

    /**
     * Pregunta si desea salir y cierra la aplicación.
     */
    private void salirAction() {
        int opcion = JOptionPane.showConfirmDialog(this, "¿Desea salir de DOPOSoccer?", "Salir",
            JOptionPane.YES_NO_OPTION);
        if (opcion == JOptionPane.YES_OPTION) {
            timer.stop();
            dispose();
            System.exit(0);
        }
    }

    /**
     * Inicia la aplicación.
     * @param args no se usan
     */
    public static void main(String[] args) {
        DOPOSoccerGUI gui = new DOPOSoccerGUI();
        gui.setVisible(true);
    }
}
