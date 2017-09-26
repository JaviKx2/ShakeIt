package xyz.fjrm.shakeit.utils;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;

import xyz.fjrm.shakeit.interfaces.OnShakeListener;

/**
 * Clase encargada de tratar los valores recibidos por el acelerómetro.
 *
 * @author Francisco Javier Reyes Mangas
 */
public class ShakeDetector implements SensorEventListener {

    /** Aceleración mínima necesaria para contar una agitación */
    private static final int MIN_SHAKE_ACCELERATION = 5;

    /** Número mínimo necesario de momivimientos para considerarlos una agitación */
    private static final int MIN_MOVEMENTS = 5;

    /** Tiempo máximo de tope (en milisegundos) para que una agitación tenga cabida */
    private static final int MAX_SHAKE_DURATION = 600;
    /** Índices para acceder posteriormente a las posiciones de los arrays */
    private static final int X = 0;
    private static final int Y = 1;
    private static final int Z = 2;
    /** Tiempo en el que se comienza a hacer el movimiento */
    long startTime = 0;
    /** Contador de movimientos */
    int moveCount = 0;
    /**
     * Arrays para guardar los valores de la gravedad y aceleración lineal
     */
    private float[] mGravity = {0.0f, 0.0f, 0.0f};
    private float[] mLinearAcceleration = {0.0f, 0.0f, 0.0f};
    /**
     * Listener para notificar en el momento que se detecte un shake o agitación
     */
    private OnShakeListener mShakeListener;

    /**
     * Constructor para inicializar directamente el listener
     * @param shakeListener clase o contexto que implementa el listener
     */
    public ShakeDetector(OnShakeListener shakeListener) {
        mShakeListener = shakeListener;
    }

    /**
     * Setter para inicializar el listener
     * @param onShakeListener clase o contexto que implementa el listener.
     */
    public void setOnShakeListener(OnShakeListener onShakeListener){
        mShakeListener = onShakeListener;
    }

    /**
     * Algoritmo de detección de agitaciones.
     * @param event evento producido
     */
    @Override
    public void onSensorChanged(SensorEvent event) {
        setCurrentAcceleration(event);
        float maxLinearAcceleration = getMaxCurrentLinearAcceleration();
        if (maxLinearAcceleration > MIN_SHAKE_ACCELERATION) {
            long now = System.currentTimeMillis();
            if (startTime == 0) {
                startTime = now;
            }
            long elapsedTime = now - startTime;
            if (elapsedTime > MAX_SHAKE_DURATION) {
                resetShakeDetection();
            } else {
                moveCount++;
                if (moveCount > MIN_MOVEMENTS) {
                    mShakeListener.onShake();
                    resetShakeDetection();
                }
            }
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        //No es necesario en nuestro caso.
    }

    /**
     * Procedimiento que aplica un filtro de paso alto
     * para tener en cuenta la gravedad, eliminando ésta
     * en cierta medida.
     * @param event representa el sensor utilizado
     */
    private void setCurrentAcceleration(SensorEvent event) {
        final float alpha = 0.8f;

        mGravity[X] = alpha * mGravity[X] + (1 - alpha) * event.values[X];
        mGravity[Y] = alpha * mGravity[Y] + (1 - alpha) * event.values[Y];
        mGravity[Z] = alpha * mGravity[Z] + (1 - alpha) * event.values[Z];

        //Valores aceleración linear con los efectos de la gravedad eliminados
        mLinearAcceleration[X] = event.values[X] - mGravity[X];
        mLinearAcceleration[Y] = event.values[Y] - mGravity[Y];
        mLinearAcceleration[Z] = event.values[Z] - mGravity[Z];
    }

    /**
     * Función para averiguar en que eje se ha producido
     * la mayor aceleración.
     * @return Mayor aceleración de alguno de los tres ejes.
     */
    private float getMaxCurrentLinearAcceleration() {
        float maxLinearAcceleration = mLinearAcceleration[X];
        if (mLinearAcceleration[Y] > maxLinearAcceleration) {
            maxLinearAcceleration = mLinearAcceleration[Y];
        }
        if (mLinearAcceleration[Z] > maxLinearAcceleration) {
            maxLinearAcceleration = mLinearAcceleration[Z];
        }
        return maxLinearAcceleration;
    }

    /**
     * Método para reiniciar los valores de las variables.
     */
    private void resetShakeDetection() {
        startTime = 0;
        moveCount = 0;
    }
}
