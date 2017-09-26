package xyz.fjrm.shakeit.interfaces;

import android.view.View;

import xyz.fjrm.shakeit.model.Player;

/**
 * Interfaz para responder ante el click de un host.
 * @author Francisco Javier Reyes Mangas
 */
public interface OnClickHostListener {
    void onClickHost(View v, Player host);
}
