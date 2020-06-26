package stas.batura.pressuretracker.ui.main;

import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelProviders;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.Observable;

import dagger.hilt.android.AndroidEntryPoint;
import stas.batura.pressuretracker.R;

@AndroidEntryPoint
public class MainFragment extends Fragment {

    private static final String TAG = MainFragment.class.getSimpleName();

    private MainViewModel mViewModel;

    public static MainFragment newInstance() {
        return new MainFragment();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        mViewModel = new ViewModelProvider(this).get(MainViewModel.class);
//        mViewModel = ViewModelProviders.of(this).get(MainViewModel.class);

        return inflater.inflate(R.layout.main_fragment, container, false);
    }

    @Override
    public void onStart() {
        super.onStart();
        addObservers();
    }

    @Override
    public void onStop() {
        super.onStop();
        removeObservers();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    private void  addObservers() {

        mViewModel.getPressureLive().observe(getViewLifecycleOwner(), new Observer() {
            @Override
            public void onChanged(Object o) {
                Log.d(TAG, "onChanged: ");
            }
        });
    }

    private void removeObservers() {


    }
}