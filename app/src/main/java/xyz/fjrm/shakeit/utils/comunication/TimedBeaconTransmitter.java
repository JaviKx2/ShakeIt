package xyz.fjrm.shakeit.utils.comunication;

import android.bluetooth.le.AdvertiseCallback;
import android.bluetooth.le.AdvertiseSettings;
import android.content.Context;
import android.os.CountDownTimer;

import org.altbeacon.beacon.Beacon;
import org.altbeacon.beacon.BeaconParser;
import org.altbeacon.beacon.BeaconTransmitter;

/**
 * Emisor de beacons con temporizador añadido.
 * @author Francisco Javier Reyes Mangas
 */
public class TimedBeaconTransmitter extends BeaconTransmitter {
    private CountDownTimer countDownTimer;
    private OnTimedBeaconTransmitterListener listener;

    public TimedBeaconTransmitter(Context context, BeaconParser parser, CountDownTimer timer) {
        super(context, parser);
        this.countDownTimer = timer;

        if (context instanceof OnTimedBeaconTransmitterListener)
            listener = (OnTimedBeaconTransmitterListener) context;
        else
            throw new RuntimeException(context.toString()
                    + " debe implementar OnCountdownListener");
    }

    @Override
    public void startAdvertising(Beacon beacon, AdvertiseCallback callback) {
        super.startAdvertising(beacon, callback);
        countDownTimer.start();
    }

    @Override
    public void startAdvertising() {
        super.startAdvertising();
        countDownTimer.start();
    }

    @Override
    public void startAdvertising(Beacon beacon) {
        super.startAdvertising(beacon, new AdvertiseCallback() {
            @Override
            public void onStartSuccess(AdvertiseSettings settingsInEffect) {
                super.onStartSuccess(settingsInEffect);
            }

            @Override
            public void onStartFailure(int errorCode) {
                super.onStartFailure(errorCode);
                switch (errorCode) {
                    case AdvertiseCallback.ADVERTISE_FAILED_ALREADY_STARTED:
                        break;
                    case AdvertiseCallback.ADVERTISE_FAILED_DATA_TOO_LARGE:
                        break;
                    case AdvertiseCallback.ADVERTISE_FAILED_FEATURE_UNSUPPORTED:
                        break;
                    case AdvertiseCallback.ADVERTISE_FAILED_INTERNAL_ERROR:
                        break;
                    case AdvertiseCallback.ADVERTISE_FAILED_TOO_MANY_ADVERTISERS:
                        break;
                }
                listener.onErrorTransmission();
            }
        });
        countDownTimer.start();
    }

    @Override
    public void stopAdvertising() {
        super.stopAdvertising();
        countDownTimer.cancel();
    }

    public interface OnTimedBeaconTransmitterListener {
        void onSuccessTransmission();

        void onErrorTransmission();
    }
}

