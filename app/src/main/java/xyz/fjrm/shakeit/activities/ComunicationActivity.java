package xyz.fjrm.shakeit.activities;

import android.bluetooth.BluetoothManager;
import android.bluetooth.le.AdvertiseCallback;
import android.bluetooth.le.AdvertiseSettings;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.RemoteException;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import org.altbeacon.beacon.Beacon;
import org.altbeacon.beacon.BeaconConsumer;
import org.altbeacon.beacon.BeaconManager;
import org.altbeacon.beacon.BeaconParser;
import org.altbeacon.beacon.BeaconTransmitter;
import org.altbeacon.beacon.Region;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import butterknife.Bind;
import butterknife.OnClick;
import xyz.fjrm.shakeit.R;
import xyz.fjrm.shakeit.adapters.HostsAdapter;
import xyz.fjrm.shakeit.fragments.ResultDialogFragment;
import xyz.fjrm.shakeit.interfaces.OnClickHostListener;
import xyz.fjrm.shakeit.model.Game;
import xyz.fjrm.shakeit.model.Player;
import xyz.fjrm.shakeit.utils.GameDataHolder;
import xyz.fjrm.shakeit.utils.GameNameParser;
import xyz.fjrm.shakeit.utils.ScoreParser;
import xyz.fjrm.shakeit.utils.comunication.BeaconHandler;
import xyz.fjrm.shakeit.utils.comunication.BeaconTransmitterTimer;
import xyz.fjrm.shakeit.utils.comunication.TimedBeaconTransmitter;


/**
 * Activity encargada de la comunicación de una partida en curso.
 *
 * @author Francisco Javier Reyes Mangas
 */
public class ComunicationActivity
        extends BaseActivity
        implements
        BeaconConsumer,
        OnClickHostListener,
        ResultDialogFragment.ResultDialogFragmentInterface,
        TimedBeaconTransmitter.OnTimedBeaconTransmitterListener,
        BeaconTransmitterTimer.OnBeaconTransmitterTimerListener,
        BeaconHandler.BeaconHandlerListener {

    public static final String TAG = "ComunicationActivity";
    public static final String HOST = "host";
    public static final String PLAYER = "player";
    public static final String SEARCH_FOR_PLAYERS = "0x0000";
    public static final String ITS_YOUR_TURN = "0x0001";
    public static final String WAIT_FOR_YOUR_TURN = "0x0002";
    public static final String PREVIOUS_PLAYER = "0x0003";
    public static final String RESULTS_ARE_READY = "0x0004";
    public static final String ACK = "0x0001";
    public static final String REQ = "0x0000";
    public static final int REQUEST_CODE = 1;
    private static final String RANGING_UNIQUE_ID = "ShakeItRanging";
    private static final String NO_ID = "000000000000";
    private static final String NO_SCORE = "000";
    private final BeaconParser mBeaconParser = new BeaconParser()
            .setBeaconLayout(BeaconParser.ALTBEACON_LAYOUT);
    @Bind(R.id.hosts_recycler_view)
    RecyclerView mHostsRecyclerView;
    @Bind(R.id.countdown_comunication_text)
    TextView mCountdownText;
    @Bind(R.id.status_comunication_text)
    TextView mStatusText;
    @Bind(R.id.winner_comunication_text)
    TextView mWinnerText;
    @Bind(R.id.start_game_button)
    Button mStartGameButton;
    @Bind(R.id.toolbar)
    Toolbar mToolbar;
    /**
     * Dirección bluetooth del dispositivo
     **/
    private String mBluetoothAddress;
    /**
     * Dirección bluetooth del jugador jugando actualmente
     */
    private String mCurrentPlayerAddress;
    /**
     * Dirección bluetooth del ganador actual
     */
    private String mCurrentWinnerAddress;
    /**
     * Resultado del ganador actual
     **/
    private int mCurrentWinnerScore;
    /**
     * Dirección bluetooth del host de la partida
     */
    private String mHostBluetoothAddress;
    /**
     * Dirección bluetooth del siguiente jugador
     */
    private String mNextPlayerBluetoothAddress;
    /**
     * Rol del jugador
     */
    private String role;
    /**
     * Bandera para controlar si ya se está jugando
     */
    private boolean alreadyPlaying = false;
    /**
     * Beacon a emitir
     */
    private Beacon mBeacon;
    /**
     * Transmisores de beacons
     */
    private TimedBeaconTransmitter mTimedBeaconTransmitter;
    private TimedBeaconTransmitter mTimedBeaconTransmitter2;
    private BeaconTransmitter mBeaconTransmitter;
    /**
     * Escaner de beacons
     */
    private BeaconManager mBeaconManager;
    /**
     * Gestor de beacons recibidos
     */
    private BeaconHandler mBeaconHandler;
    /**
     * Lista de jugadores temporales
     */
    private List<Player> players = new ArrayList<>();
    private HostsAdapter mHostsAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setSupportActionBar(mToolbar);

        mHostsRecyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager mHostsLayoutManager = new LinearLayoutManager(this);
        mHostsRecyclerView.setLayoutManager(mHostsLayoutManager);
        mHostsAdapter = new HostsAdapter();

        role = getIntent().getAction();

        mHostsAdapter.setOnClickHostListener(this);

        mHostsRecyclerView.setAdapter(mHostsAdapter);

        BluetoothManager bluetoothManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
        mBluetoothAddress = bluetoothManager.getAdapter().getAddress().replace(":", "").toLowerCase();

        mBeaconHandler = new BeaconHandler(this, role, mBluetoothAddress);

        String mGameName = getIntent().getStringExtra(GameConfigActivity.GAME_NAME);
        GameDataHolder.getInstance().addPlayer(mGameName, mBluetoothAddress);

        if (HOST.equals(role)) {
            waitForPlayers();
        }
        waitForGameHosts();
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_comunication;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (null == mBeaconManager) {
            mBeaconManager = BeaconManager.getInstanceForApplication(this);
            mBeaconManager.bind(this);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        stopAdvertising();
    }

    @Override
    public void onBeaconServiceConnect() {
        try {
            mBeaconManager.startRangingBeaconsInRegion(new Region(RANGING_UNIQUE_ID, null, null, null));
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        mBeaconManager.setRangeNotifier(mBeaconHandler);
    }

    @Override
    public void onClickHost(View v, Player host) {
        mHostBluetoothAddress = host.getId();
        mBeaconHandler.setHostBluetoothAddress(mHostBluetoothAddress);
        GameDataHolder.getInstance().addPlayer("", mHostBluetoothAddress);
        joinGame();
    }

    /**
     * Captura del click crear partida.
     * <p/>
     * Se realizan las acciones pertinentes para la creación de partida.
     * Entre ellas, aleatorizar los parámetros de la partida y elegir
     * al primer jugador de la partida.
     */
    @OnClick(R.id.start_game_button)
    public void onClickStartGameButton() {
        stopAdvertising();
        if (mHostsRecyclerView.getVisibility() == View.VISIBLE)
            mHostsRecyclerView.setVisibility(View.INVISIBLE);
        if (mStartGameButton.getVisibility() == View.VISIBLE)
            mStartGameButton.setVisibility(View.INVISIBLE);
        mBeaconHandler.addState(SEARCH_FOR_PLAYERS);

        GameDataHolder.getInstance().addPlayers(players);
        GameDataHolder.getInstance().randomize();
        String playerBluetoothAddress = GameDataHolder.getInstance().getNextPlayer().getId();

        Log.i(TAG, "NUMERO DE JUGADORES: " + GameDataHolder.getInstance().getPlayers().size());
        if (playerBluetoothAddress.toLowerCase().equals(mBluetoothAddress.toLowerCase())) {
            Log.i(TAG, "Esperad por vuestro turno, que me toca a mi, al creador...");
            waitForYourTurn();
        } else {
            mNextPlayerBluetoothAddress = playerBluetoothAddress;
            waitForMyTurn();
            itsYourTurn();
        }
    }

    /**
     * Método para ejecutar las acciones cuando es mi turno y tengo que
     * indicar a los demás que esperen.
     */
    private void waitForYourTurn() {
        initReqBeacon(WAIT_FOR_YOUR_TURN);
        startAdvertising(BeaconTransmitterTimer.THIRTY_SECONDS);
    }

    /**
     * Método para ejecutar las acciones y cambiar la interfaz
     * cuando yo tengo que esperar por mi turno.
     */
    private void waitForMyTurn() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (mHostsRecyclerView.getVisibility() == View.VISIBLE)
                    mHostsRecyclerView.setVisibility(View.INVISIBLE);
                if (mStartGameButton.getVisibility() == View.VISIBLE)
                    mStartGameButton.setVisibility(View.INVISIBLE);
                mStatusText.setText(R.string.status_msg_wait_for_your_turn);
            }
        });
    }

    /**
     * Método para indicar a un jugador que es su turno.
     */
    private void itsYourTurn() {
        initReqBeacon(ITS_YOUR_TURN);
        startAdvertising(BeaconTransmitterTimer.THIRTY_SECONDS);
    }

    /**
     * Método para indicar que he creado partida y estoy
     * esperando a que otros jugadores se unan.
     */
    private void waitForPlayers() {
        mStatusText.setText(R.string.status_msg_wait_for_players);
        initReqBeacon(SEARCH_FOR_PLAYERS);
        startAdvertisingNoTimer();
    }

    /**
     * Método para indicar que se está esperando
     * para unirse a un creador de partida.
     */
    private void waitForGameHosts() {
        if (PLAYER.equals(role)) {
            mStatusText.setText(R.string.status_msg_wait_for_hosts);
        }
    }

    /**
     * Método para inicializar beacons de respuesta.
     *
     * @param type tipo del beacon creado
     */
    private void initAckBeacon(String type) {
        Log.i(TAG, "Initializing ACK Beacon...");
        Beacon.Builder beaconBuilder = new Beacon.Builder();
        String id1 = "";
        switch (type) {
            case SEARCH_FOR_PLAYERS:
                String hexName = getIntent().getStringExtra(GameConfigActivity.GAME_NAME);
                hexName = GameNameParser.asciiToHex(hexName);
                id1 = hexName + mHostBluetoothAddress + mBluetoothAddress;
                Log.i(TAG, id1);
                break;
            case ITS_YOUR_TURN:
            case PREVIOUS_PLAYER:
                id1 = mBluetoothAddress;
                break;
            case WAIT_FOR_YOUR_TURN:
                id1 = mBluetoothAddress + mCurrentPlayerAddress;
                break;
            case RESULTS_ARE_READY:
                id1 = mCurrentWinnerAddress; // Dirección del ganador
                break;
        }
        beaconBuilder.setId1(id1);
        beaconBuilder.setId2(type);
        beaconBuilder.setId3(ACK);
        beaconBuilder.setManufacturer(0x0118);
        beaconBuilder.setTxPower(-59);
        beaconBuilder.setDataFields(Arrays.asList(0l));
        mBeacon = beaconBuilder.build();
        Log.i(TAG, "Se ha creado un beacon - " + mBeacon.toString());
    }

    /**
     * Método para inicializar beacons de petición.
     *
     * @param type tipo del beacon creado
     */
    private void initReqBeacon(String type) {
        Log.i(TAG, "Initializing Req Beacon...");
        Beacon.Builder beaconBuilder = new Beacon.Builder();
        String id1 = "";
        switch (type) {
            case SEARCH_FOR_PLAYERS:
                String hexName = getIntent().getStringExtra(GameConfigActivity.GAME_NAME);
                hexName = GameNameParser.asciiToHex(hexName);
                id1 = hexName + mBluetoothAddress;
                break;
            case ITS_YOUR_TURN:
                Game game = GameDataHolder.getInstance();
                id1 = game.toHexString();
                id1 += (game.getCurrentWinner() != null)
                        ? ScoreParser.parse(game.getCurrentWinner().getScore()) + game.getCurrentWinner().getId()
                        : NO_SCORE + NO_ID;
                id1 += mNextPlayerBluetoothAddress;
                Log.i(TAG, "IYT - ID1: " + id1);
                break;
            case PREVIOUS_PLAYER:
            case WAIT_FOR_YOUR_TURN:
                id1 = mBluetoothAddress;
                break;
            case RESULTS_ARE_READY:
                id1 = ScoreParser.parse(mCurrentWinnerScore) + mCurrentWinnerAddress;
                break;
        }
        beaconBuilder.setId1(id1);
        beaconBuilder.setId2(type);
        beaconBuilder.setId3(REQ);
        beaconBuilder.setManufacturer(0x0118);
        beaconBuilder.setTxPower(-59);
        beaconBuilder.setDataFields(Arrays.asList(0l));
        mBeacon = beaconBuilder.build();
        Log.i(TAG, "BEACON REQ CREADO - " + mBeacon.toString());
    }

    /**
     * Método para comunicar la intención de unirse a una partida.
     */
    private void joinGame() {
        initAckBeacon(SEARCH_FOR_PLAYERS);
        startAdvertising(BeaconTransmitterTimer.THIRTY_SECONDS);
    }

    /**
     * Método para comenzar a jugar
     */
    private void startToPlay() {
        if (!alreadyPlaying) {
            Intent startToPlayIntent = new Intent(this, ShakeActivity.class);
            Log.i(TAG, "IYT SHAKE DURATION: " + GameDataHolder.getInstance().getShakeDuration());
            startToPlayIntent.putExtra("seconds", GameDataHolder.getInstance().getShakeDuration());
            startActivityForResult(startToPlayIntent, REQUEST_CODE);
        }
        alreadyPlaying = true;
        mBeaconHandler.setAlreadyPlaying(true);
    }

    /**
     * Método para empezar a emitir sin temporizador
     */
    private void startAdvertisingNoTimer() {
        if (null == mBeaconTransmitter) {
            mBeaconTransmitter = new BeaconTransmitter(this, mBeaconParser);
        }
        mBeaconTransmitter.setAdvertiseMode(AdvertiseSettings.ADVERTISE_MODE_BALANCED);
        mBeaconTransmitter.startAdvertising(mBeacon);
    }

    /**
     * Método para empezar a emitir con un timer añadido
     *
     * @param time tiempo que se desea estar emitiendo
     */
    private void startAdvertising(long time) {
        if (null == mTimedBeaconTransmitter) {
            BeaconTransmitterTimer timer = new BeaconTransmitterTimer(time, 1000);
            timer.setOnBeaconTransmitterTimerListener(this);
            mTimedBeaconTransmitter = new TimedBeaconTransmitter(this,
                    mBeaconParser, timer);
        }
        mTimedBeaconTransmitter.setAdvertiseMode(AdvertiseSettings.ADVERTISE_MODE_BALANCED);
        mTimedBeaconTransmitter.startAdvertising(mBeacon, new AdvertiseCallback() {
            @Override
            public void onStartSuccess(AdvertiseSettings settingsInEffect) {
                super.onStartSuccess(settingsInEffect);
                Log.i(TAG, "TRANSMITIENDO");
            }
        });

        Log.i(TAG, "TRANSMITIENDO");
    }

    /**
     * Método para empezar a emitir simultáneamente otro beacon
     * con un timer añadido.
     *
     * @param time tiempo que se desea estar emitiendo
     */
    private void startAdvertising2(long time) {
        if (null == mTimedBeaconTransmitter2) {
            BeaconTransmitterTimer timer = new BeaconTransmitterTimer(time, 1000);
            timer.setOnBeaconTransmitterTimerListener(this);
            mTimedBeaconTransmitter2 = new TimedBeaconTransmitter(this, mBeaconParser, timer);
        }
        mTimedBeaconTransmitter2.setAdvertiseMode(AdvertiseSettings.ADVERTISE_MODE_BALANCED);
        mTimedBeaconTransmitter2.startAdvertising(mBeacon);
        Log.i(TAG, "TRANSMITIENDO");
    }

    /**
     * Método para parar las emisiones de beacons
     */
    private void stopAdvertising() {
        if (mTimedBeaconTransmitter != null && mTimedBeaconTransmitter.isStarted()) {
            mTimedBeaconTransmitter.stopAdvertising();
            Log.i(TAG, "Parando de emitir...");
        }
        if (mTimedBeaconTransmitter2 != null && mTimedBeaconTransmitter2.isStarted())
            mTimedBeaconTransmitter2.stopAdvertising();

        if (mBeaconTransmitter != null && mBeaconTransmitter.isStarted())
            mBeaconTransmitter.stopAdvertising();
    }

    /**
     * Método para mostar por pantalla a los jugadores
     * que se han unido a la partida.
     */
    private void showPlayers() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mHostsAdapter.changeHosts(GameDataHolder.getInstance().getPlayers());
            }
        });
    }

    /**
     * Método para mostrar los creadores de una partida
     */
    private void showHosts() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mHostsAdapter.changeHosts(GameDataHolder.getInstance().getHosts());
            }
        });
    }

    @Override
    public void onBackPressed() {
        new AlertDialog.Builder(this)
                .setTitle("¿Quieres salir del juego?")
                .setMessage("Si lo haces, no se podrá reincorporar a la partida.")
                .setNegativeButton(android.R.string.no, null)
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface arg0, int arg1) {
                        GameDataHolder.getInstance().clear();
                        ComunicationActivity.super.onBackPressed();
                    }
                }).create().show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.i(TAG, "onActivityResult" + String.valueOf(requestCode));
        if (requestCode == 1) {
            mStatusText.setText(R.string.waiting_for_results_status);
            mHostsRecyclerView.setVisibility(View.INVISIBLE);
            String myResult = String.valueOf(data.getIntExtra("seconds", 0));
            String resultText = getString(R.string.your_result_title) + "\n" + myResult + " shakes";
            mWinnerText.setText(resultText);
            mWinnerText.setVisibility(View.VISIBLE);
            GameDataHolder.getInstance().removePlayer(mBluetoothAddress);
            Log.i(TAG, "Acabo de jugar, me quito de la lista de jugadores restantes.");
            if (resultCode == RESULT_OK) {
                int playersLeftCount = GameDataHolder.getInstance().getPlayersLeft().size();
                Log.i(TAG, "¿Cuántos quedan por jugar? " + playersLeftCount);
                Player currentWinner = GameDataHolder.getInstance().getCurrentWinner();
                int myScore = data.getIntExtra("seconds", 0);
                if (playersLeftCount == 0) {
                    //He sido el último en jugar,
                    //por lo que tengo que decir el ganador a todos.
                    Log.i(TAG, "Ya no hay más jugadores, enviar resultados");
                    mCurrentWinnerAddress = currentWinner.getId();
                    int currentWinnerScore = currentWinner.getScore();
                    if (myScore < currentWinnerScore) { //Si el resultado que yo acabo de obtener es mejor que el anterior, yo soy el ganador actual.
                        myScore = currentWinnerScore;
                        ResultDialogFragment
                                .newInstance(ResultDialogFragment.LOSER, myScore)
                                .show(getSupportFragmentManager(), "WinnerMessage");
                    } else {
                        mCurrentWinnerAddress = mBluetoothAddress;
                        ResultDialogFragment
                                .newInstance(ResultDialogFragment.WINNER, myScore)
                                .show(getSupportFragmentManager(), "WinnerMessage");
                    }
                    mCurrentWinnerScore = myScore;
                    initReqBeacon(RESULTS_ARE_READY);
                } else {
                    Log.i(TAG, "Obteniendo siguiente jugador...");
                    Log.i(TAG, "Obteniendo id del siguiente jugador...");
                    mNextPlayerBluetoothAddress = GameDataHolder.getInstance().getNextPlayer().getId();
                    Log.i(TAG, "Obteniendo actual ganador...");
                    if (currentWinner != null) {
                        mCurrentWinnerAddress = currentWinner.getId();
                        int currentWinnerScore = currentWinner.getScore();
                        Log.i(TAG, "Hay un ganador, vamos a compararlo con mis resultados...");
                        if (myScore < currentWinnerScore) {
                            //Si el resultado que yo acabo de obtener es mejor que el anterior,
                            // yo soy el ganador actual.
                            myScore = currentWinnerScore;
                        } else {
                            mCurrentWinnerAddress = mBluetoothAddress;
                        }
                    } else {
                        Log.i(TAG, "No hay ganador de momento, por lo que soy el primero en jugar.");
                        mCurrentWinnerAddress = mBluetoothAddress;
                    }

                    Log.i(TAG, "Guardando actual ganador...");
                    Log.i(TAG, "Actual ganador: " + mCurrentWinnerAddress + " " + String.valueOf(myScore));
                    GameDataHolder.getInstance().setCurrentWinner(new Player("", mCurrentWinnerAddress, myScore));

                    Log.i(TAG, "Notificando turno siguiente...");
                    initReqBeacon(ITS_YOUR_TURN);
                    startAdvertising(BeaconTransmitterTimer.THIRTY_SECONDS);
                    initReqBeacon(PREVIOUS_PLAYER);
                }
                startAdvertising2(BeaconTransmitterTimer.THIRTY_SECONDS);
            }

            if (resultCode == RESULT_CANCELED) {
                //TODO: Write your code if there's no result
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.i(TAG, "onDestroy");
        if (null != mBeaconManager)
            if (mBeaconManager.isBound(this))
                mBeaconManager.unbind(this);

        GameDataHolder.getInstance().clear();
    }

    @Override
    public void onClickButtonPositiveResultDialog() {
        finish();
    }

    @Override
    public void onTransmitterCountdownTick(long secondsUntilFinished) {
        final long seconds = secondsUntilFinished;
        if (null != mCountdownText)
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    mCountdownText.setText(String.valueOf(seconds));
                }
            });

    }

    @Override
    public void onTransmitterCountdownFinish() {
    }

    @Override
    public void onSuccessTransmission() {

    }

    @Override
    public void onErrorTransmission() {

    }

    @Override
    public void onPlayerJoinedGame() {
        showPlayers();
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
            if (mStartGameButton.getVisibility() == View.INVISIBLE)
                mStartGameButton.setVisibility(View.VISIBLE);
            }
        });
    }

    @Override
    public void onHostShowedUp() {
        showHosts();
    }

    @Override
    public void onGameWin(int winnerScore) {
        ResultDialogFragment
                .newInstance(ResultDialogFragment.WINNER, winnerScore)
                .show(getSupportFragmentManager(), "WinnerResultDialog");
    }

    @Override
    public void onGameLoss(int winnerScore) {
        ResultDialogFragment
                .newInstance(ResultDialogFragment.LOSER, winnerScore)
                .show(getSupportFragmentManager(), "LoserResultDialog");
    }

    @Override
    public void onAnotherPlayerIsPlaying() {
        waitForMyTurn();
    }

    @Override
    public void onPlayTurn() {
        stopAdvertising();
        startToPlay();
    }

    @Override
    public void onWaitForMyTurn() {
        waitForMyTurn();
        mCurrentPlayerAddress = mBeaconHandler.getCurrentPlayerAddress();
        initAckBeacon(WAIT_FOR_YOUR_TURN);
        stopAdvertising();
        startAdvertising(BeaconTransmitterTimer.THIRTY_SECONDS);
    }
}

