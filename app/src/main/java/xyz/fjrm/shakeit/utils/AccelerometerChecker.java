package xyz.fjrm.shakeit.utils;

import android.app.Activity;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorManager;

/**
 * Clase encargada de las comprobaciones necesarias
 * para comprobar si el dipositivo tiene acelerómetro.
 * @author Francisco Javier Reyes Mangas
 */
public class AccelerometerChecker {
    private final Sensor accelerometer;

    public AccelerometerChecker(Context context){
        accelerometer =
                ((SensorManager) context.getSystemService(Activity.SENSOR_SERVICE))
                        .getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
    }

    /**
     * Función encargada de decir si el dispositivo está dotado
     * de un acelerómetro.
     * @return true si el dispositivo tiene acelerómetro, false si no
     */
    public boolean hasAccelerometer() {
        return accelerometer != null;
    }
}
