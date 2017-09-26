package xyz.fjrm.shakeit.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.Toolbar;

import butterknife.Bind;
import xyz.fjrm.shakeit.R;
import xyz.fjrm.shakeit.fragments.GameFragment;
import xyz.fjrm.shakeit.fragments.GameJoinFragment;
import xyz.fjrm.shakeit.fragments.GameNameCreationFragment;

/**
 * Activity de configuración de partida.
 *
 * @author Francisco Javier Reyes Mangas
 */
public class GameConfigActivity
        extends BaseActivity
        implements
        GameNameCreationFragment.OnFragmentGameNameCreationInteractionListener,
        GameFragment.OnFragmentGameInteractionListener,
        GameJoinFragment.OnFragmentGameJoinInteractionListener{

    public static final String GAME_NAME = "gamename";
    private static final String TAG = "GameConfigActivity";
    @Bind(R.id.toolbar)
    Toolbar mToolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (null != mToolbar)
            setSupportActionBar(mToolbar);
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_game;
    }

    @Override
    public void OnClickFragmentButtonCreate(Fragment fragment) {
        replaceFragmentBy(fragment);
    }

    @Override
    public void OnClickFragmentButtonJoin(Fragment fragment) {
        replaceFragmentBy(fragment);
    }

    @Override
    public void OnClickGameCreationSubmit(String gameName) {
        invokeComunicationActivity(gameName);
    }

    @Override
    public void OnClickJoinSubmit(String playerName) {
        invokeMonitoringActivity(playerName);
    }

    private void invokeComunicationActivity(String gameName) {
        Intent invokeComunicationActivityIntent = new Intent(this, ComunicationActivity.class);
        invokeComunicationActivityIntent.setAction(ComunicationActivity.HOST);
        invokeComunicationActivityIntent.putExtra(GameConfigActivity.GAME_NAME, gameName);
        startActivity(invokeComunicationActivityIntent);
    }

    private void invokeMonitoringActivity(String gameName) {
        Intent invokeMonitoringActivityIntent = new Intent(this, ComunicationActivity.class);
        invokeMonitoringActivityIntent.setAction(ComunicationActivity.PLAYER);
        invokeMonitoringActivityIntent.putExtra(GameConfigActivity.GAME_NAME, gameName);
        startActivity(invokeMonitoringActivityIntent);
    }

    /** Método para intercambiar el fragment actual del contenedor */
    private void replaceFragmentBy(Fragment fragment, String... arguments) {
        FragmentManager manager = getSupportFragmentManager();
        String backStateName = fragment.getClass().getName();
        boolean fragmentPopped = manager.popBackStackImmediate(backStateName, 0);
        boolean fragmentFound = manager.findFragmentByTag(backStateName) != null;

        if (!fragmentPopped && !fragmentFound) {
            FragmentTransaction ft = manager.beginTransaction();
            ft.replace(R.id.linearlayout_fragment_container, fragment, backStateName);
            ft.addToBackStack(backStateName);
            ft.commit();
        }
    }
}
