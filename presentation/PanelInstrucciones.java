package presentation;

import domain.PerfilMaquina;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.border.EmptyBorder;

/**
 * JPanel: pantalla de instrucciones ("Cómo jugar"). Explica el juego de forma visual: seis tarjetas
 * con un dibujo (IconoInstruccion) y un texto corto: objetivo, controles, vida, poder,
 * tiros de poder y opciones de la partida.
 * Hereda el fondo de PanelDegradado y vuelve al inicio con la VistaPrincipal.
 */
public class PanelInstrucciones extends PanelDegradado {

    /** Botón para volver a la pantalla de inicio. */
    private BotonRedondeado botonVolver;
    /** Ventana principal (navegación). */
    private VistaPrincipal vista;

    /**
     * Crea la pantalla de instrucciones.
     * @param vista ventana principal (navegación)
     */
    public PanelInstrucciones(VistaPrincipal vista) {
        super(new BorderLayout(10, 12));
        this.vista = vista;
        prepareView();
        prepareController();
    }

    /**
     * Organiza el título, seis tarjetas (cada una con un dibujo y un texto corto) y el botón Volver.
     */
    private void prepareView() {
        setBorder(new EmptyBorder(8, 30, 10, 30));
        add(Estilo.crearTitulo("CÓMO JUGAR"), BorderLayout.NORTH);
        JPanel centro = new JPanel(new GridLayout(2, 3, 12, 10));
        centro.setOpaque(false);
        centro.add(crearTarjeta("Objetivo", Estilo.PRIMARIO, IconoInstruccion.Tipo.OBJETIVO,
            "Mete el balón en el arco del rival. Gana quien haga más goles."));
        centro.add(crearTarjeta("Controles", Estilo.SECUNDARIO, IconoInstruccion.Tipo.CONTROLES,
            "Contra la máquina, el J2 juega solo."));
        centro.add(crearTarjeta("Vida", Estilo.PELIGRO, IconoInstruccion.Tipo.VIDA,
            "Patear al rival le quita vida. En 0 queda aturdido."));
        centro.add(crearTarjeta("Poder", Estilo.ACENTO, IconoInstruccion.Tipo.PODER,
            "Se llena en 10 s (+1/4 si te hacen gol). Llena: activa el aura y toca el balón."));
        centro.add(crearTarjeta("Tiros de poder", Estilo.SECUNDARIO_HOVER, IconoInstruccion.Tipo.TIROS,
            "Si golpea al rival: daño + efecto. Si entra: gol."));
        centro.add(crearTarjeta("Partida", Estilo.PRIMARIO_HOVER, IconoInstruccion.Tipo.PARTIDA,
            "Máquina: " + nombresPerfiles() + ". Ganar: " + domain.ConstantesJuego.PUNTOS_GANADOR + " pts."));
        add(centro, BorderLayout.CENTER);

        botonVolver = Estilo.crearBotonSecundario("<  Volver");
        JPanel abajo = new JPanel(new FlowLayout(FlowLayout.CENTER));
        abajo.setOpaque(false);
        abajo.add(botonVolver);
        add(abajo, BorderLayout.SOUTH);
    }

    /**
     * Arma una tarjeta con su título, el dibujo que la explica y un texto corto debajo.
     * @param titulo título de la tarjeta
     * @param color color del borde y del título
     * @param tipo qué dibujo lleva
     * @param texto explicación corta
     * @return la tarjeta
     */
    private PanelTarjeta crearTarjeta(String titulo, Color color, IconoInstruccion.Tipo tipo, String texto) {
        PanelTarjeta tarjeta = new PanelTarjeta(titulo, color, new BorderLayout(0, 4));
        tarjeta.setBorder(new EmptyBorder(38, 10, 8, 10));
        tarjeta.add(new IconoInstruccion(tipo), BorderLayout.CENTER);
        JLabel etiqueta = Estilo.crearEtiqueta("<html><center>" + texto + "</center></html>");
        etiqueta.setFont(Estilo.TEXTO_PEQUENO);
        etiqueta.setHorizontalAlignment(SwingConstants.CENTER);
        tarjeta.add(etiqueta, BorderLayout.SOUTH);
        return tarjeta;
    }

    /**
     * @return nombres de los perfiles de la máquina separados por comas
     */
    private String nombresPerfiles() {
        StringBuilder texto = new StringBuilder();
        for (PerfilMaquina perfil : PerfilMaquina.values()) {
            if (texto.length() > 0) texto.append(", ");
            texto.append(perfil.getNombre());
        }
        return texto.toString();
    }

    /**
     * Oyente del botón Volver: regresa a la pantalla de inicio.
     */
    private void prepareController() {
        botonVolver.addActionListener(new ActionListener() {
            /** Atiende el botón Volver. */
            public void actionPerformed(ActionEvent e) {
                vista.mostrarInicio();
            }
        });
    }
}
