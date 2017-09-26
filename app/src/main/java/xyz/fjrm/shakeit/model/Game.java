package xyz.fjrm.shakeit.model;

import android.util.Log;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Clase modelo que representa una partida o juego.
 * @author Francisco Javier Reyes Mangas
 */
public class Game {
    private static final String TAG = "Game";
    private static final int[] seconds = {9, 12, 15, 18};
    private String name;
    private String id;
    private List<Player> playersLeft;
    private List<Player> players;
    private List<Player> hosts;
    private int shakeDuration;
    private Player currentWinner;

    public Game() {
        players = new ArrayList<>();
        playersLeft = new ArrayList<>();
        hosts = new ArrayList<>();
    }

    /**
     * Método para devolver una duración del juego aleatoria.
     */
    public void randomize() {
        Random r = new Random();
        int randomNumber = 0;
        for (int i = 0; i < 10; i++) {
            randomNumber = r.nextInt(seconds.length);
        }
        this.shakeDuration = seconds[randomNumber];
    }

    public String toHexString() {
        StringBuilder builder = new StringBuilder();
        builder.append(shakeDuration);
        return builder.toString();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public List<Player> getPlayersLeft() {
        return playersLeft;
    }

    public List<Player> getPlayers() {
        return players;
    }

    public List<Player> getHosts() {
        return hosts;
    }

    public void addPlayers(List<Player> players) {
        for (Player player : players) {
            if (!this.playersLeft.contains(player))
                this.playersLeft.add(player);
            if (!this.players.contains(player))
                this.players.add(player);
        }
    }

    public void clear(){
        this.players.clear();
        this.playersLeft.clear();
        this.hosts.clear();
    }

    /**
     * Función para saber si una id pertenece a la partida en curso.
     * @param address id de un jugador
     * @return true si el jugador pertenece a la partida en curso, false si no
     */
    public boolean belongs(String address){
        return players.contains(new Player(address));
    }

    public int getShakeDuration() {
        return shakeDuration;
    }

    public void setShakeDuration(int shakeDuration) {
        this.shakeDuration = shakeDuration;
    }

    /**
     * Genera y devuelve aleatoriamente un jugador de los que quedan por jugar.
     * El jugador devuelto es eliminado, pues ya no va a jugar.
     *
     * @return Jugador al que le toca jugar.
     */
    public Player getNextPlayer() {
        Random r = new Random();
        int randomNumber = 0;
        for (int i = 0; i < 10; i++) {
            randomNumber = r.nextInt(playersLeft.size());
        }
        Player nextPlayer = this.playersLeft.get(randomNumber);
        this.playersLeft.remove(nextPlayer);
        return nextPlayer;
    }

    public Player getCurrentWinner() {
        return currentWinner;
    }

    public void setCurrentWinner(Player currentWinner) {
        this.currentWinner = currentWinner;
    }

    /**
     *
     * @param name
     * @param id
     */
    public void addPlayer(String name, String id) {
        Player player = new Player(name, id);
        if (!players.contains(player)) {
            Log.i(TAG, "Añadiendo nuevo jugador.");
            players.add(player);
        }
        if (!playersLeft.contains(player)) {
            Log.i(TAG, "Añadiendo nuevo jugador restante.");
            playersLeft.add(player);
        }
    }

    /**
     *
     * @param name
     * @param id
     */
    public void addHost(String name, String id) {
        Player player = new Player(name, id);
        if (!hosts.contains(player)) {
            Log.i(TAG, "Añadiendo nuevo host.");
            hosts.add(player);
        }
    }

    /**
     * Elimina un jugador de la lista de jugadores que faltan por jugar.
     *
     * @param id
     */
    public void removePlayer(String id) {
        playersLeft.remove(new Player(id));
    }
}


