package xyz.fjrm.shakeit.utils;

import java.text.DecimalFormat;

/**
 * Parser para devolver siempre un número de tres cifras.
 * Ej: Si un jugador consigue 15 shakes, se devolverá 015.
 *
 * @author Francisco Javier Reyes Mangas
 */
public class ScoreParser {
    private static String pattern = "000";

    /**
     * Formatea el resultado recibido por parámetro.
     * @param score
     * @return cadena de texto de un número de tres cifras
     */
    public static String parse(int score){
        return new DecimalFormat(pattern).format(score);
    }

    /**
     * Función de conversión de un número decimal en hexadecimal
     * @param score cadena de texto de un número en decimal
     * @return cadena de texto de un número en hexadecimal
     */
    public static String toHex(String score){
        return Integer.toHexString(Integer.parseInt(score));
    }

    /**
     * Función de conversión de un número hexadecimal en decimal
     * @param hex cadena de texto de un número en hexadecimal
     * @return cadena de texto de un número en decimal
     */
    public static String fromHex(String hex){
        return String.valueOf(Integer.parseInt(hex, 16));
    }
}
