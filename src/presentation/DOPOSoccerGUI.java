package presentation;

import domain.*;
import java.awt.*;
import java.awt.event.*;
import java.util.HashSet;
import java.util.Set;
import javax.swing.*;

/**
 * JFrame: ventana principal de DOPOSoccer (es el gran componente que contiene todo).
 * MVC: modelo = DOPOSoccer (dominio), vista = los paneles, controlador = los oyentes.
 *
 * Organización de componentes:
 *   JFrame (esta clase)
 *     JMenuBar  con JMenu "Archivo" y JMenu "Juego" (cada opción es un JMenuItem)
 *     JPanel principal con CardLayout, que muestra una tarjeta a la vez:
 *       PanelInicio (JPanel) pantalla de bienvenida
 *       PanelConfiguracion (JPanel) para preparar el partido
 *       PanelJugadores (JPanel) para elegir país y mejorar estadísticas
 *       PanelCancha (JPanel) donde se juega; adentro tiene el PanelPausa (JPanel)
 *
 * Controles: jugador 1 A/D mover, W saltar, S patear; jugador 2 flechas, arriba saltar,
 * abajo patear; P pausa, Esc terminar.
 */
public class DOPOSoccerGUI extends JFrame {

    /** Nombre de la tarjeta de inicio. */
    public static final String INICIO = "inicio";
    /** Nombre de la tarjeta de jugadores. */
    public static final String JUGADORES = "jugadores";
    /** Nombre de la tarjeta de configuración. */
    public static final String CONFIGURACION = "configuracion";
    /** Nombre de la tarjeta de la cancha. */
    public static final String CANCHA = "cancha";
    /** Milisegundos entre cada ciclo del juego. */
    public static final int MILISEGUNDOS = 16;

    /** Modelo: fachada del dominio. */
    private DOPOSoccer dominio;
    /** Estilo (layout) del panel principal: muestra una tarjeta a la vez. */
    private CardLayout tarjetas;
    /** JPanel contenedor principal: guarda las tarjetas (inicio, configuración, jugadores y cancha). */
    private JPanel principal;
    /** JPanel de la pantalla de inicio. */
    private PanelInicio panelInicio;
    /** JPanel de la pantalla de jugadores. */
    private PanelJugadores panelJugadores;
    /** JPanel de la pantalla de configuración. */
    private PanelConfiguracion panelConfiguracion;
    /** JPanel de la pantalla de juego. */
    private PanelCancha panelCancha;
    /** Timer de Swing: cada MILISEGUNDOS ejecuta un ciclo del juego. */
    private Timer timer;
    /** Teclas de movimiento que están presionadas en este momento. */
    private Set<Integer> teclasPresionadas;
    /** JMenuItem del menú Archivo: vuelve a la configuración para un partido nuevo. */
    private JMenuItem itemNuevo;
    /** JMenuItem del menú Archivo: abrir partida (versión dos). */
    private JMenuItem itemAbrir;
    /** JMenuItem del menú Archivo: guardar partida (versión dos). */
    private JMenuItem itemGuardar;
    /** JMenuItem del menú Juego: pausa o reanuda el partido. */
    private JMenuItem itemPausa;
    /** JMenuItem del menú Juego: termina el partido. */
    private JMenuItem itemTerminar;
    /** JMenuItem del menú Archivo: cierra la aplicación. */
    private JMenuItem itemSalir;

    /**
     * Crea la ventana: modelo, vista y controlador.
     */
    private DOPOSoccerGUI() {
        dominio = DOPOSoccer.getInstance();
        teclasPresionadas = new HashSet<>();
        prepareView();
        prepareController();
    }

    /**
     * Arma la vista: menú y las cuatro pantallas (inicio, configuración, jugadores y cancha).
     */
    private void prepareView() {
        setTitle("DOPOSoccer");                 // título del JFrame
        prepareViewMenu();
        tarjetas = new CardLayout();
        principal = new JPanel(tarjetas);       // JPanel contenedor con CardLayout
        panelInicio = new PanelInicio();
        panelConfiguracion = new PanelConfiguracion();
        panelJugadores = new PanelJugadores(dominio);
        panelCancha = new PanelCancha(dominio);
        principal.add(panelInicio, INICIO);                 // tarjeta 1 (se muestra primero)
        principal.add(panelConfiguracion, CONFIGURACION);   // tarjeta 2
        principal.add(panelJugadores, JUGADORES);           // tarjeta 3
        principal.add(panelCancha, CANCHA);                 // tarjeta 4
        add(principal);                                     // el JPanel va dentro del JFrame
        setSize(900, 500);
        setLocationRelativeTo(null);
    }

    /**
     * Arma la barra de menú: JMenuBar con dos JMenu (Archivo y Juego) y sus JMenuItem.
     */
    private void prepareViewMenu() {
        JMenuBar barra = new JMenuBar();            // JMenuBar: barra de menú del JFrame
        JMenu archivo = new JMenu("Archivo");       // JMenu: contenedor de opciones
        itemNuevo = new JMenuItem("Nuevo partido");
        itemAbrir = new JMenuItem("Abrir partida");
        itemGuardar = new JMenuItem("Guardar partida");
        itemSalir = new JMenuItem("Salir");
        archivo.add(itemNuevo);
        archivo.add(itemAbrir);
        archivo.add(itemGuardar);
        archivo.addSeparator();
        archivo.add(itemSalir);
        JMenu juego = new JMenu("Juego");           // JMenu: contenedor de opciones
        itemPausa = new JMenuItem("Pausar / continuar (P)");
        itemTerminar = new JMenuItem("Terminar partido (Esc)");
        juego.add(itemPausa);
        juego.add(itemTerminar);
        barra.add(archivo);
        barra.add(juego);
        setJMenuBar(barra);                         // se pone la barra en el JFrame
    }

    /**
     * Controlador: asigna los oyentes (listeners) a la ventana, al menú, a los botones,
     * al teclado y al timer del juego.
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
        prepareControllerPausa();
        panelInicio.getBotonIniciar().addActionListener(new ActionListener() {
            /** Atiende el botón Jugar de la pantalla de inicio. */
            public void actionPerformed(ActionEvent e) {
                nuevoAction();
            }
        });
        panelConfiguracion.getBotonSiguiente().addActionListener(new ActionListener() {
            /** Atiende el botón Siguiente de la configuración. */
            public void actionPerformed(ActionEvent e) {
                siguienteAction();
            }
        });
        panelJugadores.getBotonVolver().addActionListener(new ActionListener() {
            /** Atiende el botón Volver de la pantalla de jugadores. */
            public void actionPerformed(ActionEvent e) {
                nuevoAction();
            }
        });
        panelJugadores.getBotonIniciar().addActionListener(new ActionListener() {
            /** Atiende el botón Iniciar partido. */
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
        // Timer de Swing: genera un evento cada MILISEGUNDOS para mover el juego
        timer = new Timer(MILISEGUNDOS, new ActionListener() {
            /** Ejecuta un ciclo del juego en cada tic del timer. */
            public void actionPerformed(ActionEvent e) {
                cicloAction();
            }
        });
    }

    /**
     * Asigna los oyentes del JButton de pausa y de los JButton del menú de pausa.
     */
    private void prepareControllerPausa() {
        PanelPausa pausa = panelCancha.getPanelPausa();
        ActionListener pausar = new ActionListener() {
            /** Atiende el botón de pausa y el botón Continuar. */
            public void actionPerformed(ActionEvent e) {
                pausaAction();
            }
        };
        panelCancha.getBotonPausa().addActionListener(pausar);
        pausa.getBotonContinuar().addActionListener(pausar);
        pausa.getBotonTerminar().addActionListener(new ActionListener() {
            /** Atiende el botón Terminar partido. */
            public void actionPerformed(ActionEvent e) {
                terminarAction();
            }
        });
        pausa.getBotonGuardar().addActionListener(new ActionListener() {
            /** Atiende el botón Guardar partida, que llega en la versión dos. */
            public void actionPerformed(ActionEvent e) {
                proximamenteAction();
            }
        });
        pausa.getBotonMenu().addActionListener(new ActionListener() {
            /** Atiende el botón Volver al menú. */
            public void actionPerformed(ActionEvent e) {
                volverMenuAction();
            }
        });
        ActionListener sonido = new ActionListener() {
            /** Atiende los botones Sonido y Música, que llegan en la versión dos. */
            public void actionPerformed(ActionEvent e) {
                sonidoAction();
            }
        };
        pausa.getBotonSonido().addActionListener(sonido);
        pausa.getBotonMusica().addActionListener(sonido);
    }

    /**
     * Pasa de la configuración a la pantalla de jugadores, después de revisar la configuración.
     */
    private void siguienteAction() {
        if (panelConfiguracion.esContraMaquina()) {
            JOptionPane.showMessageDialog(this, "El modo jugador vs máquina estará disponible en la versión dos.",
                "DOPOSoccer", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        try {
            if (Integer.parseInt(panelConfiguracion.getValor()) <= 0) throw new NumberFormatException();
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Escriba un número entero mayor que cero en minutos o goles.",
                "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        mostrarJugadores();
    }

    /**
     * Muestra la pantalla de jugadores con los puntos y estadísticas al día.
     */
    private void mostrarJugadores() {
        timer.stop();
        panelCancha.mostrarPausa(false);
        panelJugadores.actualizar();
        tarjetas.show(principal, JUGADORES);
        setSize(900, 500);
        setLocationRelativeTo(null);
    }

    /**
     * Lee la configuración y los países e inicia el partido. Muestra un mensaje si algo no es válido.
     */
    private void iniciarAction() {
        try {
            int valor = Integer.parseInt(panelConfiguracion.getValor());
            dominio.iniciarPartido(panelJugadores.getPais1(), panelJugadores.getPais2(),
                panelConfiguracion.getTipo(), valor);
            Dimension resolucion = panelConfiguracion.getResolucion();
            panelCancha.setPreferredSize(resolucion);
            panelCancha.mostrarPausa(false);
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
            if (!dominio.hayPartido()) return;
            if (!dominio.estaPausado() && !dominio.estaTerminado()) {
                if (teclasPresionadas.contains(KeyEvent.VK_A)) dominio.moverJugador(1, "izquierda");
                if (teclasPresionadas.contains(KeyEvent.VK_D)) dominio.moverJugador(1, "derecha");
                if (teclasPresionadas.contains(KeyEvent.VK_LEFT)) dominio.moverJugador(2, "izquierda");
                if (teclasPresionadas.contains(KeyEvent.VK_RIGHT)) dominio.moverJugador(2, "derecha");
            }
            dominio.actualizar(MILISEGUNDOS / 1000.0);
            panelCancha.repaint();
            if (dominio.estaTerminado()) {
                finAction();
            }
        } catch (DOPOSoccerException e) {
            Log.record(e);
        }
    }

    /**
     * Muestra el ganador, el marcador, los puntos obtenidos en este partido y los puntos
     * acumulados de cada jugador. Luego pasa a la pantalla de jugadores para gastar los puntos.
     */
    private void finAction() {
        timer.stop();
        panelCancha.mostrarPausa(false);
        try {
            Jugador j1 = dominio.getJugador(1);
            Jugador j2 = dominio.getJugador(2);
            Jugador ganador = dominio.getGanador();
            String marcador = "Marcador: " + j1.getGoles() + " - " + j2.getGoles() + "\n";
            String mensaje;
            if (ganador == null) {
                mensaje = "EMPATE\n" + marcador + "No se otorgan puntos.\n";
            } else {
                Jugador perdedor = ganador == j1 ? j2 : j1;
                mensaje = "¡Ganó el jugador " + ganador.getNumero() + " (" + ganador.getPais() + ")!\n"
                    + marcador
                    + "Puntos obtenidos: jugador " + ganador.getNumero() + " +" + Partido.PUNTOS_GANADOR
                    + ", jugador " + perdedor.getNumero() + " +" + Partido.PUNTOS_PERDEDOR + "\n";
            }
            mensaje += "Puntos acumulados: jugador 1 = " + dominio.getPuntos(1)
                + ", jugador 2 = " + dominio.getPuntos(2);
            JOptionPane.showMessageDialog(this, mensaje, "Fin del partido", JOptionPane.INFORMATION_MESSAGE);
        } catch (DOPOSoccerException e) {
            Log.record(e);
        }
        mostrarJugadores();
    }

    /**
     * Pausa o reanuda el partido.
     */
    private void pausaAction() {
        try {
            dominio.pausar();
            panelCancha.mostrarPausa(dominio.estaPausado());
        } catch (DOPOSoccerException e) {
            JOptionPane.showMessageDialog(this, e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Termina el partido en cualquier momento, después de confirmar.
     */
    private void terminarAction() {
        try {
            if (!dominio.hayPartido() || dominio.estaTerminado()) return;
        } catch (DOPOSoccerException e) {
            Log.record(e);
            return;
        }
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
        panelCancha.mostrarPausa(false);
        tarjetas.show(principal, CONFIGURACION);
        setSize(900, 500);
        setLocationRelativeTo(null);
    }

    /**
     * Termina el partido actual con el marcador que lleva y vuelve a la configuración, después de confirmar.
     */
    private void volverMenuAction() {
        int opcion = JOptionPane.showConfirmDialog(this,
            "¿Desea volver al menú? El partido se terminará con el marcador actual.", "Volver al menú",
            JOptionPane.YES_NO_OPTION);
        if (opcion == JOptionPane.YES_OPTION) {
            try {
                dominio.terminar();
            } catch (DOPOSoccerException e) {
                Log.record(e);
            }
            nuevoAction();
        }
    }

    /**
     * Informa que el sonido y la música llegan en la versión dos.
     */
    private void sonidoAction() {
        JOptionPane.showMessageDialog(this, "El sonido y la música estarán disponibles en la versión dos.",
            "DOPOSoccer", JOptionPane.INFORMATION_MESSAGE);
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
