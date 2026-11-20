package domain;

import java.io.IOException;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

/**
 * Registro de errores para los programadores (requisito de diseño "log de errores").
 * Los errores se guardan en el archivo DOPOSoccer.log en la carpeta del proyecto.
 */
public class Log {

    /** Nombre del archivo de log (sin extensión). */
    public static final String NOMBRE = "DOPOSoccer";

    /**
     * Registra una excepción en el archivo de log.
     * Si no se puede escribir el archivo, el error se muestra en la consola.
     * @param e excepción que se quiere registrar
     */
    public static void record(Exception e) {
        Logger logger = Logger.getLogger(NOMBRE);
        logger.setUseParentHandlers(false);
        FileHandler file = null;
        try {
            file = new FileHandler(NOMBRE + ".log", true);
            file.setFormatter(new SimpleFormatter());
            logger.addHandler(file);
            logger.log(Level.SEVERE, e.toString(), e);
        } catch (IOException | SecurityException ex) {
            System.err.println("No se pudo escribir el log: " + ex.getMessage());
            e.printStackTrace();
        } finally {
            if (file != null) {
                logger.removeHandler(file);
                file.close();
            }
        }
    }
}
