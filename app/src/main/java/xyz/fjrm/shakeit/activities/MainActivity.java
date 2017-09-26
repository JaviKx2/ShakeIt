package xyz.fjrm.shakeit.activities;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.widget.Toast;

import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;

import java.util.List;

import butterknife.Bind;
import butterknife.OnClick;
import xyz.fjrm.shakeit.R;
import xyz.fjrm.shakeit.utils.AccelerometerChecker;
import xyz.fjrm.shakeit.utils.BluetoothChecker;
import xyz.fjrm.shakeit.utils.GameDataHolder;


/**
 * Activity principal de la aplicación.
 * Está asociada a la vista que sale en pantalla al abrir la aplicación.
 */
public class MainActivity extends BaseActivity {
    @Bind(R.id.toolbar)
    Toolbar mToolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (null != mToolbar) {
            setSupportActionBar(mToolbar);
        }
        Dexter.initialize(this);
        promptPermissions();
    }

    /**
     * Método para preguntat al usuario por permisos si
     * la aplicación se está ejecutando a partir de
     * Android 6.0 Marshmallow
     */
    private void promptPermissions() {
        Dexter.checkPermissions(new MultiplePermissionsListener() {
            @Override public void onPermissionsChecked(MultiplePermissionsReport report) {/* ... */}
            @Override public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {/* ... */}
        }, Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION);
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_main;
    }

    private void invokeGameActivity(Context context) {
        GameDataHolder.clear();
        startActivity(new Intent(context, GameConfigActivity.class));
    }


    /**
     * Método para capturar el click del botón jugar.
     * Si se cumplen todas los requerimientos se iniciará
     * la configuración de la partida.
     */
    @OnClick(R.id.button_play)
    public void onClickPlayButton() {
        BluetoothChecker btChecker = new BluetoothChecker();
        AccelerometerChecker accelerometerChecker = new AccelerometerChecker(this);

        if (accelerometerChecker.hasAccelerometer()) {
            if (btChecker.hasBluetooth())
                if (btChecker.isEnabled())
                    if(BluetoothChecker.supportsAdvertising(this))
                        invokeGameActivity(this);
                    else{
                        Toast.makeText(this,
                                "El dispositivo no puede emitir balizas, el juego no funcionará.",
                                Toast.LENGTH_LONG).show();
                    }
                else {
                    startActivityForResult(
                            new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE),
                            BluetoothChecker.REQUEST_ENABLE_BT
                    );
                }
            else {
                Toast.makeText(this, "El dispositivo no tiene bluetooth.", Toast.LENGTH_LONG).show();
            }
        } else {
            Toast.makeText(this, "El dispositivo no tiene acelerómetro.", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == BluetoothChecker.REQUEST_ENABLE_BT) {
            if (resultCode == RESULT_OK) {
                if(BluetoothChecker.supportsAdvertising(this))
                    invokeGameActivity(this);
                else{
                    Toast.makeText(this,
                            "El dispositivo no puede emitir balizas, el juego no funcionará.",
                            Toast.LENGTH_LONG).show();
                }
            }
            if (resultCode == RESULT_CANCELED) {
                finish();
            }
        }
    }
}
