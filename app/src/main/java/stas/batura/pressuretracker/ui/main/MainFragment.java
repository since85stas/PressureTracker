package stas.batura.pressuretracker.ui.main;

import androidx.databinding.DataBindingUtil;
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
import android.widget.Button;
import android.widget.RadioGroup;

import java.util.List;

import dagger.hilt.android.AndroidEntryPoint;
import stas.batura.pressuretracker.MainViewModel;
import stas.batura.pressuretracker.R;
import stas.batura.pressuretracker.data.room.Pressure;
import stas.batura.pressuretracker.data.room.Rain;
import stas.batura.pressuretracker.databinding.PressureFragmentBinding;

@AndroidEntryPoint
public class MainFragment extends Fragment {

    private static final String TAG = MainFragment.class.getSimpleName();

    private MainFragmentViewModel fragmentModel;

    private MainViewModel mainViewModel;

    private RecyclerView recyclerView;

    private PressureAdapter adapter;

//    private RecyclerView recyclerRainView;
//
//    private RainAdapter rainAdapter;

    private Button stopButton;

    public static MainFragment newInstance() {
        return new MainFragment();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        fragmentModel = new ViewModelProvider(this).get(MainFragmentViewModel.class);

        mainViewModel = new ViewModelProvider(requireActivity()).get(MainViewModel.class);

        PressureFragmentBinding binding = DataBindingUtil.inflate(inflater,
                R.layout.pressure_fragment,
                container,
                false);
        binding.setViewModel(fragmentModel);
        binding.setLifecycleOwner(getViewLifecycleOwner());

        return binding.getRoot();
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
//        LinearLayoutManager viewManagerR = new LinearLayoutManager(requireContext());

        recyclerView = (RecyclerView) view.findViewById(R.id.pressure_recycle);
        recyclerView.setLayoutManager(viewManager);

        adapter = new PressureAdapter();
        recyclerView.setAdapter(adapter);

        stopButton = view.findViewById(R.id.stop_button);

        stopButton.setOnClickListener(v -> {
            mainViewModel.stopService();
        });

        RadioGroup radioGroup = view.findViewById(R.id.radio_group);

        radioGroup.getChildAt(mainViewModel.getPowerFromServ()).setActivated(true);

        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId) {

                    case R.id.rain_0:
                        mainViewModel.setServiceRain(0);
                        break;

                    case R.id.rain_1:
                        mainViewModel.setServiceRain(1);
                        break;

                    case R.id.rain_2:
                        mainViewModel.setServiceRain(2);
                        break;

                    case R.id.rain_3:
                        mainViewModel.setServiceRain(3);
                        break;
                }

            }
        });
    }

    /**
     * adding observers
     */
    private void  addObservers() {

        fragmentModel.getPressureLive().observe(getViewLifecycleOwner(), new Observer() {
            @Override
            public void onChanged(Object o) {
                Log.d(TAG, "onChanged: " );
                adapter.submitList((List<Pressure>) o);
            }
        });

//        fragmentModel.getRainLive().observe(getViewLifecycleOwner(), new Observer<List<Rain>>() {
//            @Override
//            public void onChanged(List<Rain> rain) {
//                rainAdapter.submitList(rain);
//            }
//        });

    }

    /**
     * removing observers
     */
    private void removeObservers() {


    }
}