package xyz.fjrm.shakeit.utils;

/**
 * Clase que siurve de ayuda para transformar las
 * cadenas de texto pasadas entre comunicaciones bluetooth.
 *
 * @author Francisco Javier Reyes Mangas
 */
public class GameNameParser {

    /**
     * Función que convierte un texto en formato hexadecimal
     * a su homógeno en ASCII.
     * @param hex Cadena de texto en hexadecimal
     * @return cadena de texto en ASCII
     */
    public static String hexToAscii(String hex) {
        StringBuilder ascii = new StringBuilder();
        String str;
        for (int i = 0; i < hex.length(); i += 2) {
            str = hex.substring(i, i + 2);
            ascii.append((char) Integer.parseInt(str, 16));
        }
        return ascii.toString();
    }

    /**
     * Función que convierte una cadena de texto en formato hexadecimal.
     * @param asciiString Cadena de texto en ASCII
     * @return cadena de texto en hexadecimal
     */
    public static String asciiToHex(String asciiString) {
        char[] chars = asciiString.toCharArray();
        StringBuilder hex = new StringBuilder();
        for (char aChar : chars) {
            hex.append(Integer.toHexString((int) aChar));
        }
        return hex.toString();
    }
}
