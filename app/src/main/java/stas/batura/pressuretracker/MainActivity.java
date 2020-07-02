package stas.batura.pressuretracker;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.widget.Toast;

import dagger.hilt.android.AndroidEntryPoint;
import stas.batura.pressuretracker.ui.main.MainFragment;
import stas.batura.pressuretracker.service.PressureService;

import static android.widget.Toast.LENGTH_LONG;

@AndroidEntryPoint
public class MainActivity extends AppCompatActivity {

    MainViewModel mainViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mainViewModel = new ViewModelProvider(this).get(MainViewModel.class);

        SensorManager sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);

        setContentView(R.layout.main_activity);
//        setContentView(R.layout.main_activity);
//        if (savedInstanceState == null) {
//            getSupportFragmentManager().beginTransaction()
//                    .replace(R.id.container, MainFragment.newInstance())
//                    .commitNow();
//        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        addObservers();

        startService(
                new Intent(
                        this,
                        PressureService.class
                    )
                );
    }

    @Override
    protected void onStop() {
        super.onStop();
        removeObservers();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mainViewModel.getServiceConnection().getValue() != null) {

            // unbinding service after destroing activity
            unbindService(mainViewModel.getServiceConnection().getValue());
        }
    }

    /**
     * adding observers
     */
    private void addObservers() {

        // fetting service connection
        mainViewModel.getServiceConnection().observe(this, new Observer<ServiceConnection>() {
            @Override
            public void onChanged(ServiceConnection serviceConnection) {
                if (serviceConnection != null) {
                    bindCurrentService(serviceConnection);
                }
            }
        });

        mainViewModel.getStopServiceLive().observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean aBoolean) {
                if (aBoolean && mainViewModel.getServiceConnection().getValue() != null) {
                    unbindService(mainViewModel.getServiceConnection().getValue());

                    mainViewModel.closeService();
                }
            }
        });
    }

    /**
     * removing observers
     */
    private void removeObservers() {
        mainViewModel.getServiceConnection().removeObservers(this);
        mainViewModel.getStopServiceLive().removeObservers(this);
    }

    /**
     * Binding service to activity
     * @param serviceConnection
     */
    private void  bindCurrentService(ServiceConnection serviceConnection) {
        // привязываем сервис к активити
        bindService(new Intent(getApplicationContext(), PressureService.class),
        serviceConnection,
                Context.BIND_AUTO_CREATE);
    }


}