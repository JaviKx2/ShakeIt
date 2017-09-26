package xyz.fjrm.shakeit.utils;

import android.bluetooth.BluetoothAdapter;
import android.content.Context;

import org.altbeacon.beacon.BeaconTransmitter;

/**
 * Clase BluetoothChecker
 *
 * Encargada de realizar las comprobaciones necesarias para saber
 * si el dispositivo tiene bluetooth y es compatible con la característica
 * del Bluetooth LE Peripheral Mode.
 *
 * @author Francisco Javier Reyes Mangas
 */
public class BluetoothChecker {
    public static final int REQUEST_ENABLE_BT = 0; /** Valor para pedir la activación del bluetooth */
    private static final String TAG = "BtChecker";
    private final BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

    /**
     * Función para saber si el dispositivo soporta el Bluetooth LE Peripheral Mode
     * para poder emitir balizas o beacons.
     * @param context contexto de la aplicación
     * @return true si el modo es soportado, false si lo contrario
     */
    public static boolean supportsAdvertising(Context context) {
        return BeaconTransmitter.checkTransmissionSupported(context) == BeaconTransmitter.SUPPORTED;
    }

    /**
     * Función para saber si el bluetooth está activado en el dispositivo.
     * @return true si el dispositivo tiene bluetooth activado, false en caso contrario
     */
    public boolean isEnabled() {
        if (mBluetoothAdapter == null) {
            throw new IllegalStateException("Debe usar la función hasBluetooth previamente.");
        }
        return mBluetoothAdapter.isEnabled();
    }

    /**
     * Función para saber si el dispositivo tiene bluetooth
     * @return true si el dispositivo tiene bluetooth
     */
    public boolean hasBluetooth(){
        return mBluetoothAdapter != null;
    }
}
