package xyz.fjrm.shakeit.utils.comunication;

import android.util.Log;

import org.altbeacon.beacon.Beacon;
import org.altbeacon.beacon.RangeNotifier;
import org.altbeacon.beacon.Region;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import xyz.fjrm.shakeit.model.Player;
import xyz.fjrm.shakeit.utils.GameDataHolder;
import xyz.fjrm.shakeit.utils.GameNameParser;

/**
 * Clase para tratar los beacons recibidos.
 *
 * @author Francisco Javier Reyes Mangas
 */
public class BeaconHandler implements RangeNotifier {
    public static final String HOST = "host";
    public static final String PLAYER = "player";
    public static final String SEARCH_FOR_PLAYERS = "0x0000";
    public static final String ITS_YOUR_TURN = "0x0001";
    public static final String WAIT_FOR_YOUR_TURN = "0x0002";
    public static final String PREVIOUS_PLAYER = "0x0003";
    public static final String RESULTS_ARE_READY = "0x0004";
    public static final String ACK = "0x0001";
    public static final String REQ = "0x0000";
    private static final String NO_ID = "000000000000";
    private static final String NO_SCORE = "000";
    private static final String TAG = "BeaconHandler";
    private final String role;
    private BeaconHandlerListener listener;
    private boolean previousPlayerWasReceived = false;
    private Set<String> formerStates = new HashSet<>();
    private Set<String> confirmationIDs = new HashSet<>();
    private String mBluetoothAddress;
    private String mHostBluetoothAddress;
    private String mCurrentPlayerAddress;
    private boolean alreadyPlaying = false;


    public BeaconHandler(BeaconHandlerListener listener, String role, String myBluetoothAddress) {
        this.listener = listener;
        this.role = role;
        this.mBluetoothAddress = myBluetoothAddress;
    }

    public void setAlreadyPlaying(boolean alreadyPlaying) {
        this.alreadyPlaying = alreadyPlaying;
    }

    public void setHostBluetoothAddress(String hostBluetoothAddress) {
        this.mHostBluetoothAddress = hostBluetoothAddress;
    }

    public String getCurrentPlayerAddress() {
        return this.mCurrentPlayerAddress;
    }

    public void addState(String state) {
        formerStates.add(state);
    }

    @Override
    public void didRangeBeaconsInRegion(Collection<Beacon> beacons, Region region) {
        for (Beacon beacon : beacons) {
            if (!formerStates.contains(beacon.getId2().toHexString()))
                handleBeacon(
                        beacon.getId1().toHexString(),
                        beacon.getId2().toHexString(),
                        beacon.getId3().toHexString()
                );
        }
    }

    public void setBeaconHandlerListener(BeaconHandlerListener listener) {
        this.listener = listener;
    }

    /**
     * Método para tratar cada beacon recibido y actuar en consecuencia
     */
    public void handleBeacon(String id1, String id2, String id3) {
        Log.i(TAG, "RECEIVED BEACON - " + id2);
        switch (id2) {
            case SEARCH_FOR_PLAYERS:
                if (ACK.equals(id3)) {
                    String address = HOST.equals(role) ? mBluetoothAddress : mHostBluetoothAddress;
                    if (address != null && !address.equals("")) {
                        //0x00000000XXXXXXXXXXXXYYYYYYYYYYYY
                        Log.i(TAG, "ADDRESS NOT NULL");
                        Log.i(TAG, address);
                        if (id1.startsWith(address.toLowerCase(), 10)) {
                            Log.i(TAG, "GUARDANDO ID " + id1);
                            savePlayerId(id1);
                            if (HOST.equals(role)) {
                                listener.onPlayerJoinedGame();
                            }
                        }
                    }
                } else if (REQ.equals(id3)) {
                    if (HOST.equals(role)) return;
                    // Si estoy aquí, significa que quiero unirme a una partida y estoy recibiendo señales de un creador
                    //0x00000000000000000000XXXXXXXXXXXX
                    String aHostBluetoothAddress = id1.substring(22, 34);
                    String aHostName = id1.substring(2, 22);
                    GameDataHolder.getInstance().addHost(GameNameParser.hexToAscii(aHostName), aHostBluetoothAddress);
                    listener.onHostShowedUp();
                }
                break;
            case ITS_YOUR_TURN:
                if (ACK.equals(id3)) {
                    if (PLAYER.equals(role)) return;
                } else if (REQ.equals(id3)) {

                    Log.i(TAG, "IYT hexID: " + id1);
                    int shakeDuration = Integer.parseInt(id1.substring(2, 7));
                    String currentWinnerID = id1.substring(10, 22);
                    String nextPlayerID = id1.substring(22, 34).toLowerCase();
                    if (GameDataHolder.getInstance().belongs(nextPlayerID)) {

                        GameDataHolder.getInstance().setShakeDuration(shakeDuration);
                        if (!alreadyPlaying && nextPlayerID.toLowerCase().equals(mBluetoothAddress.toLowerCase())) {
                            if (!currentWinnerID.equals(NO_ID)) {
                                Log.i(TAG, "El de antes sí ha jugado, así que lo cojo como actual ganador.");
                                if (previousPlayerWasReceived) {
                                    Log.i(TAG, "Recibido jugador previo.");
                                    Log.i(TAG, "Añadiendo Ganador Actual");
                                    Log.i(TAG, "CW-ID: " + currentWinnerID);
                                    int currentWinnerScore = Integer.parseInt(id1.substring(7, 10));
                                    Log.i(TAG, "CW-Score: " + currentWinnerScore);
                                    GameDataHolder.getInstance().setCurrentWinner(new Player("", currentWinnerID, currentWinnerScore));
                                    //startToPlay();
                                    formerStates.add(ITS_YOUR_TURN);
                                    listener.onPlayTurn();
                                }
                            } else {
                                Log.i(TAG, "El de antes no ha jugado, así que soy el primero");
                                //startToPlay();
                                formerStates.add(ITS_YOUR_TURN);
                                listener.onPlayTurn();
                            }
                        } else {
                            if (!nextPlayerID.toLowerCase().equals(mBluetoothAddress.toLowerCase())) {
                                GameDataHolder.getInstance().removePlayer(nextPlayerID);
                            }
                            listener.onAnotherPlayerIsPlaying();
                        }
                        formerStates.add(SEARCH_FOR_PLAYERS);
                    }
                }
                break;
            case PREVIOUS_PLAYER:
                if (REQ.equals(id3)) {
                    String previousPlayerID = id1.substring(22, 34).toLowerCase();
                    if (GameDataHolder.getInstance().belongs(previousPlayerID)) {
                        GameDataHolder.getInstance().removePlayer(previousPlayerID);
                        previousPlayerWasReceived = true;
                    }
                }
                break;
            case WAIT_FOR_YOUR_TURN:
                if (ACK.equals(id3)) {
                    String playerAddress = id1.substring(10, 22).toLowerCase();
                    String btAddress = id1.substring(22, 34).toLowerCase();
                    if (GameDataHolder.getInstance().belongs(playerAddress))
                        if (mBluetoothAddress.toLowerCase().equals(btAddress)) {
                            confirmationIDs.add(playerAddress);
                            if (GameDataHolder.getInstance().getPlayers().size() - 1 == confirmationIDs.size()) {
                                confirmationIDs.clear();
                                formerStates.add(WAIT_FOR_YOUR_TURN);
                                formerStates.add(ITS_YOUR_TURN);
                                listener.onPlayTurn();
                            }
                        }
                } else if (REQ.equals(id3)) {
                    String btAddress = id1.substring(22, 34).toLowerCase();
                    if (GameDataHolder.getInstance().belongs(btAddress)) {
                        formerStates.add(WAIT_FOR_YOUR_TURN);
                        mCurrentPlayerAddress = id1.substring(22, 34).toLowerCase();
                        listener.onWaitForMyTurn();
                    }
                }
                break;
            case RESULTS_ARE_READY:
                if (ACK.equals(id3)) {
                    // De momento nada
                } else if (REQ.equals(id3)) {
                    String winnerID = id1.substring(22, 34).toLowerCase();
                    if(GameDataHolder.getInstance().belongs(winnerID)) {
                        int myScore = Integer.parseInt(id1.substring(19, 22));
                        if (winnerID.toLowerCase().equals(mBluetoothAddress.toLowerCase())) {
                            listener.onGameWin(myScore);
                        } else {
                            listener.onGameLoss(myScore);
                        }
                        formerStates.add(RESULTS_ARE_READY);
                    }
                }
                break;
        }
    }

    private void savePlayerId(String id) {
        String aBluetoothAddress = id.substring(22, 34);
        String aPlayerName = id.substring(2, 10);
        GameDataHolder.getInstance().addPlayer(GameNameParser.hexToAscii(aPlayerName), aBluetoothAddress);
        Log.i(TAG, "NUMERO DE JUGADORES: " + GameDataHolder.getInstance().getPlayers().size());
    }

    /**
     * Interfaz para interactuar con la activity que la implemente
     * y actuar cada vez que se produzcan cada una de las acciones definidas,
     * que coinciden con estados en los que se encuentra la partida.
     */
    public interface BeaconHandlerListener {
        void onPlayerJoinedGame();

        void onHostShowedUp();

        void onGameWin(int winnerScore);

        void onGameLoss(int winnerScore);

        void onAnotherPlayerIsPlaying();

        void onPlayTurn();

        void onWaitForMyTurn();
    }
}
