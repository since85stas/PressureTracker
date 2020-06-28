package stas.batura.pressuretracker.ui.main;

import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

import dagger.hilt.android.AndroidEntryPoint;
import stas.batura.pressuretracker.R;
import stas.batura.pressuretracker.data.room.Pressure;

@AndroidEntryPoint
public class MainFragment extends Fragment {

    private static final String TAG = MainFragment.class.getSimpleName();

    private MainFragmentViewModel fragmentModel;

    private RecyclerView recyclerView;

    private PressureAdapter adapter;

    public static MainFragment newInstance() {
        return new MainFragment();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        fragmentModel = new ViewModelProvider(this).get(MainFragmentViewModel.class);
//        mViewModel = ViewModelProviders.of(this).get(MainViewModel.class);

        return inflater.inflate(R.layout.pressure_fragment, container, false);
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
        LinearLayoutManager viewManager = new LinearLayoutManager(requireContext());

        recyclerView = (RecyclerView) view.findViewById(R.id.pressure_recycle);
        recyclerView.setLayoutManager(viewManager);

        adapter = new PressureAdapter();
        recyclerView.setAdapter(adapter);

    }

    private void  addObservers() {

        fragmentModel.getPressureLive().observe(getViewLifecycleOwner(), new Observer() {
            @Override
            public void onChanged(Object o) {
                Log.d(TAG, "onChanged: " );
                adapter.submitList((List<Pressure>) o);
            }
        });
    }

    private void removeObservers() {


    }
}