package xyz.fjrm.shakeit.utils;


import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Clase encargada de comunicar si un nombre de usuario introducido
 * es válido o no.
 *
 * @author Francisco Javier Reyes Mangas
 */
public class UsernameValidator {

    private static final String USERNAME_PATTERN = "^[a-zA-ZÑñ0-9_-]{1,";
    private static final String USERNAME_END_PATTERN = "}$";
    private Pattern pattern;
    private String username;

    public UsernameValidator(String username, int validLength) {
        String usernamePattern = USERNAME_PATTERN + validLength + USERNAME_END_PATTERN;
        pattern = Pattern.compile(usernamePattern);
        this.username = username;
    }

    /**
     * Función que valida el nombre de usuario haciendo uso
     * de una expresión regular.
     *
     * @return true nombre de usuario válido, false si no
     */
    public boolean matchRegex() {
        Matcher matcher = pattern.matcher(username);
        return matcher.matches();
    }

    /**
     * Función para comprobar todas las condiciones a la vez.
     * @return true cumple todas las condiciones, false falla alguna
     */
    public boolean validate() {
        return matchRegex();
    }
}