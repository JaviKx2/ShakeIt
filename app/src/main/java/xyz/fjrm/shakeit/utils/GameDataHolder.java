package xyz.fjrm.shakeit.utils;

import xyz.fjrm.shakeit.model.Game;

/**
 * Clase que permite mantener los datos de una partida.
 * @author Francisco Javier Reyes Mangas
 */
public class GameDataHolder {
    private static Game game;

    /**
     * Función estática que obtiene siempre la misma instancia de una partida.
     * @return Instancia de una partida
     */
    public static Game getInstance() {
        if(game == null) game = new Game();
        return game;
    }


    /**
     * Limpia la instancia
     */
    public static void clear(){
        game = null;
    }
}
