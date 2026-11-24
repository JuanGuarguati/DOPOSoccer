package presentation;

import domain.Log;
import javax.sound.midi.*;

/**
 * Música de fondo del juego. La canción es propia: el programa la arma nota por nota
 * (melodía, bajo y batería) con javax.sound.midi, así que no usa archivos externos.
 * Se repite sin parar y se puede activar o silenciar. Si el computador no tiene
 * sintetizador MIDI, el juego sigue funcionando sin música.
 */
public class Musica {

    /** Pulsos por negra de la secuencia. */
    public static final int PULSOS_NEGRA = 4;
    /** Velocidad de la canción en negras por minuto. */
    public static final float TEMPO = 132;
    /** Canal de la melodía. */
    public static final int CANAL_MELODIA = 0;
    /** Canal del bajo. */
    public static final int CANAL_BAJO = 1;
    /** Canal de la batería (en MIDI la batería siempre va en el canal 10, índice 9). */
    public static final int CANAL_BATERIA = 9;
    /** Instrumento de la melodía (81 = lead de onda cuadrada). */
    public static final int INSTRUMENTO_MELODIA = 80;
    /** Instrumento del bajo (39 = bajo sintetizado). */
    public static final int INSTRUMENTO_BAJO = 38;
    /** Volumen de las notas (0 a 127). */
    public static final int VOLUMEN = 85;
    /** Bombo, caja y platillo de la batería MIDI. */
    private static final int BOMBO = 36;
    private static final int CAJA = 38;
    private static final int PLATILLO = 42;
    /** Notas fundamentales del bajo, una por compás (Do, Sol, La, Fa, dos veces). */
    private static final int[] BAJO = {36, 43, 45, 41, 36, 43, 45, 41};
    /** Melodía propia: 8 compases de 8 corcheas (0 = silencio). */
    private static final int[] MELODIA = {
        72, 0, 76, 79, 76, 0, 74, 72,
        71, 0, 74, 79, 74, 0, 71, 67,
        69, 72, 76, 0, 77, 76, 74, 72,
        65, 69, 72, 0, 74, 72, 69, 0,
        72, 74, 76, 79, 81, 79, 76, 74,
        71, 74, 79, 0, 83, 81, 79, 74,
        76, 0, 72, 76, 81, 0, 79, 76,
        77, 76, 74, 72, 74, 0, 72, 0
    };

    /** Secuenciador que reproduce la canción; null si no hay MIDI. */
    private Sequencer secuenciador;
    /** true si la música está sonando. */
    private boolean activa;

    /**
     * Prepara la canción. Si no hay MIDI disponible, la música queda desactivada.
     */
    public Musica() {
        try {
            secuenciador = MidiSystem.getSequencer();
            secuenciador.open();
            secuenciador.setSequence(crearCancion());
            secuenciador.setTempoInBPM(TEMPO);
            secuenciador.setLoopCount(Sequencer.LOOP_CONTINUOUSLY);
        } catch (MidiUnavailableException | InvalidMidiDataException | RuntimeException e) {
            Log.record(e);
            secuenciador = null;
        }
    }

    /**
     * Arma la canción: una pista con la melodía, el bajo y la batería.
     * @return secuencia MIDI de 8 compases
     * @throws InvalidMidiDataException si alguna nota no es válida
     */
    private Sequence crearCancion() throws InvalidMidiDataException {
        Sequence cancion = new Sequence(Sequence.PPQ, PULSOS_NEGRA);
        Track pista = cancion.createTrack();
        pista.add(evento(ShortMessage.PROGRAM_CHANGE, CANAL_MELODIA, INSTRUMENTO_MELODIA, 0, 0));
        pista.add(evento(ShortMessage.PROGRAM_CHANGE, CANAL_BAJO, INSTRUMENTO_BAJO, 0, 0));
        int corchea = PULSOS_NEGRA / 2;
        for (int i = 0; i < MELODIA.length; i++) {
            if (MELODIA[i] > 0) agregarNota(pista, CANAL_MELODIA, MELODIA[i], i * corchea, corchea);
        }
        int compas = PULSOS_NEGRA * 4;
        for (int c = 0; c < BAJO.length; c++) {
            for (int tiempo = 0; tiempo < 4; tiempo++) {
                int inicio = c * compas + tiempo * PULSOS_NEGRA;
                int nota = tiempo % 2 == 0 ? BAJO[c] : BAJO[c] + 12;
                agregarNota(pista, CANAL_BAJO, nota, inicio, PULSOS_NEGRA);
                agregarNota(pista, CANAL_BATERIA, tiempo % 2 == 0 ? BOMBO : CAJA, inicio, corchea);
                agregarNota(pista, CANAL_BATERIA, PLATILLO, inicio + corchea, corchea);
            }
        }
        return cancion;
    }

    /**
     * Agrega una nota a la pista (se prende y se apaga).
     * @param pista pista de la canción
     * @param canal canal MIDI
     * @param nota número de la nota
     * @param inicio pulso en que empieza
     * @param duracion pulsos que dura
     * @throws InvalidMidiDataException si la nota no es válida
     */
    private void agregarNota(Track pista, int canal, int nota, int inicio, int duracion)
            throws InvalidMidiDataException {
        pista.add(evento(ShortMessage.NOTE_ON, canal, nota, VOLUMEN, inicio));
        pista.add(evento(ShortMessage.NOTE_OFF, canal, nota, 0, inicio + duracion));
    }

    /**
     * Crea un evento MIDI.
     * @param comando tipo de mensaje (nota, instrumento)
     * @param canal canal MIDI
     * @param dato1 primer dato (nota o instrumento)
     * @param dato2 segundo dato (volumen)
     * @param pulso momento del evento
     * @return el evento
     * @throws InvalidMidiDataException si los datos no son válidos
     */
    private MidiEvent evento(int comando, int canal, int dato1, int dato2, long pulso)
            throws InvalidMidiDataException {
        return new MidiEvent(new ShortMessage(comando, canal, dato1, dato2), pulso);
    }

    /**
     * Empieza a sonar (si hay MIDI).
     */
    public void iniciar() {
        if (secuenciador == null) return;
        secuenciador.start();
        activa = true;
    }

    /**
     * Deja de sonar.
     */
    public void detener() {
        if (secuenciador == null) return;
        secuenciador.stop();
        activa = false;
    }

    /**
     * Activa la música si estaba en silencio, o la silencia si estaba sonando.
     * @return true si quedó sonando
     */
    public boolean alternar() {
        if (activa) detener(); else iniciar();
        return activa;
    }

    /**
     * Libera el secuenciador (al cerrar el juego).
     */
    public void cerrar() {
        if (secuenciador == null) return;
        secuenciador.close();
        activa = false;
    }

    /**
     * @return true si la música está sonando
     */
    public boolean estaActiva() { return activa; }

    /**
     * @return true si el computador tiene MIDI y la música se puede reproducir
     */
    public boolean estaDisponible() { return secuenciador != null; }
}
