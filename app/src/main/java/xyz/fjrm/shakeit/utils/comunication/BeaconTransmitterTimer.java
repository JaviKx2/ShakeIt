package xyz.fjrm.shakeit.utils.comunication;

import android.os.CountDownTimer;

/**
 * Timer específico para limitar la transmisión de beacons.
 * @author Francisco Javier Reyes Mangas
 */
public class BeaconTransmitterTimer extends CountDownTimer {
    public static final long THIRTY_SECONDS = 30000;
    public static final long FIFTEEN_SECONDS = 15000;
    private OnBeaconTransmitterTimerListener listener;

    /**
     * @param millisInFuture    The number of millis in the future from the call
     *                          to {@link #start()} until the countdown is done and {@link #onFinish()}
     *                          is called.
     * @param countDownInterval The interval along the way to receive
     *                          {@link #onTick(long)} callbacks.
     */
    public BeaconTransmitterTimer(long millisInFuture, long countDownInterval) {
        super(millisInFuture, countDownInterval);
    }

    public void setOnBeaconTransmitterTimerListener(OnBeaconTransmitterTimerListener listener){
        this.listener = listener;
    }

    @Override
    public void onTick(long millisUntilFinished) {
        listener.onTransmitterCountdownTick(millisUntilFinished/1000);
    }

    @Override
    public void onFinish() {
        listener.onTransmitterCountdownFinish();
    }

    public interface OnBeaconTransmitterTimerListener {
        void onTransmitterCountdownTick(long secondsUntilFinished);
        void onTransmitterCountdownFinish();
    }
}
