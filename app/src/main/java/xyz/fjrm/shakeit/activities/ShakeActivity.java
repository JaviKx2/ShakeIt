package xyz.fjrm.shakeit.activities;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Vibrator;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import butterknife.Bind;
import butterknife.OnClick;
import xyz.fjrm.shakeit.R;
import xyz.fjrm.shakeit.interfaces.OnShakeListener;
import xyz.fjrm.shakeit.utils.ShakeDetector;

/**
 * Activity que representa la partida de un jugador.
 * Se computan las agitaciones de un jugador.
 *
 * @author Francisco Javier Reyes Mangas
 */
public class ShakeActivity extends BaseActivity {
    private static final String TAG = "ShakeActivity";
    @Bind(R.id.text_activity_shake)
    TextView mTextViewShakeInfo;
    @Bind(R.id.accelerometervalues_text)
    TextView mShakingLabel;
    @Bind(R.id.start_button)
    Button mStartButton;
    @Bind(R.id.countdown_text)
    TextView mCountDownLabel;
    @Bind(R.id.shakes_counter_msg)
    TextView mTextCounterMsg;
    @Bind(R.id.text_minutes_fixed)
    TextView mTextMinutesFixed;
    private SensorManager mSensorManager;
    private Sensor mAccelerometer;
    private ShakeDetector mShakeDetector;
    private CountDownTimer mCountDownTimer;
    private int mShakeCounter = 0;
    private int mCountDownSeconds;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mCountDownSeconds = getIntent().getIntExtra("seconds", 30000);
        String text;
        text = getString(R.string.minutes_fixed_text);
        if (mCountDownSeconds < 10)
            text += "0";
        text += String.valueOf(mCountDownSeconds);
        mTextMinutesFixed.setText(text);
        mCountDownSeconds *= 1000;
        vibrate(500);
        hookUpListeners();
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_shake;
    }

    /**
     *
     */
    @Override
    public void onResume() {
        super.onResume();
        mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        mAccelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
    }

    /**
     *
     */
    @Override
    public void onPause() {
        super.onPause();
        unregisterShakeListener();
    }

    /**
     * Start receiving sensor data
     */
    private void registerShakeListener() {
        mSensorManager.registerListener(mShakeDetector, mAccelerometer, SensorManager.SENSOR_DELAY_UI);
    }

    /**
     * Stop receiving sensor data
     */
    private void unregisterShakeListener() {
        mSensorManager.unregisterListener(mShakeDetector);
    }

    /**
     * Setting Up Listeners needed
     */
    private void hookUpListeners() {
        mShakeDetector = new ShakeDetector(new OnShakeListener() {
            @Override
            public void onShake() {
                handleShake();
            }
        });
    }

    /**
     * Método para capturar el click del botón de comienzo de partida
     * Se comienzas a registrar agitaciones.
     * Se inicia la cuenta atrás.
     */
    @OnClick(R.id.start_button)
    public void onClickStartButton() {
        registerShakeListener();
        startCountDownTimer();
        mStartButton.setVisibility(View.INVISIBLE);
        mShakingLabel.setVisibility(View.VISIBLE);
        mTextCounterMsg.setVisibility(View.VISIBLE);
    }


    /**
     * Método para iniciar cuenta atrás y
     * actual en consecuencia cada vez que
     * pase el tiempo.
     */
    private void startCountDownTimer() {
        if (mCountDownTimer == null) {
            mCountDownTimer = new CountDownTimer(mCountDownSeconds, 1000) {
                @Override
                public void onTick(long millisUntilFinished) {
                    long seconds = millisUntilFinished / 1000;
                    String text;
                    text = getString(R.string.minutes_fixed_text);
                    if (seconds < 10)
                        text += "0";
                    text += String.valueOf(seconds);
                    mTextMinutesFixed.setText(text);
                }

                @Override
                public void onFinish() {
                    mCountDownLabel.setText(R.string.time_finish);
                    mTextMinutesFixed.setText(R.string.time_to_zeros);
                    unregisterShakeListener();
                    vibrate(500);
                    showFinishDialog();
                }
            };
        }
        mCountDownTimer.start();
    }

    /**
     * Se aumenta el contador de shakes y se muestra.
     */
    private void handleShake() {
        mShakingLabel.setText(String.valueOf(++mShakeCounter));
    }

    /**
     * Método para hacer que vibre el smartphone.
     *
     * @param milis Tiempo que dura la vibración en milisegundos
     */
    private void vibrate(long milis) {
        ((Vibrator) getApplication().getSystemService(Context.VIBRATOR_SERVICE)).vibrate(milis);
    }

    /**
     * Método para mostrar un diálogo de fin de turno.
     */
    private void showFinishDialog() {
        android.support.v7.app.AlertDialog.Builder adb = new android.support.v7.app.AlertDialog.Builder(this);
        adb.setMessage(R.string.dialog_message)
                .setTitle(R.string.dialog_title);
        adb.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                Intent returnIntent = new Intent();
                returnIntent.putExtra("seconds", mShakeCounter);
                setResult(RESULT_OK, returnIntent);
                finish();
                dialog.dismiss();
            }
        });

        android.support.v7.app.AlertDialog dialog = adb.create();
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();
    }

    @Override
    public void onBackPressed() {
        android.support.v7.app.AlertDialog dialog = new android.support.v7.app.AlertDialog.Builder(this)
                .setTitle("¿Quieres salir del juego?")
                .setMessage("Si lo haces, no se podrá reincorporar a la partida.")
                .setNegativeButton(android.R.string.no, null)
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface arg0, int arg1) {
                        ShakeActivity.super.onBackPressed();
                    }
                }).create();
        dialog.show();
    }
}
