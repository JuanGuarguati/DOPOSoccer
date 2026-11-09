package domain;

/**
 * Excepción propia del juego DOPOSoccer.
 * Los mensajes para el usuario están encapsulados como constantes de la clase.
 */
public class DOPOSoccerException extends Exception {

    /** Mensaje cuando el número de jugador no es 1 ni 2. */
    public static final String JUGADOR_INVALIDO = "El número de jugador debe ser 1 o 2.";
    /** Mensaje cuando no hay un partido en curso. */
    public static final String PARTIDO_TERMINADO = "No hay un partido en curso.";
    /** Mensaje cuando el país no existe. */
    public static final String PAIS_INVALIDO = "El país seleccionado no existe. Opciones: Corea del Sur, Camerún, Nigeria, Estados Unidos.";
    /** Mensaje cuando el tipo o el valor del partido no son válidos. */
    public static final String VALOR_INVALIDO = "El tipo de partido debe ser \"tiempo\" o \"goles\" y el valor debe ser mayor que cero.";
    /** Mensaje cuando la dirección no es izquierda ni derecha. */
    public static final String DIRECCION_INVALIDA = "La dirección debe ser \"izquierda\" o \"derecha\".";
    /** Mensaje cuando la estadística no existe o ya está en el máximo. */
    public static final String ESTADISTICA_MAXIMA = "La estadística no existe o ya está en su valor máximo (10).";

    /**
     * Crea una excepción del juego con el mensaje dado.
     * @param message mensaje que describe el error
     */
    public DOPOSoccerException(String message) {
        super(message);
    }
}
