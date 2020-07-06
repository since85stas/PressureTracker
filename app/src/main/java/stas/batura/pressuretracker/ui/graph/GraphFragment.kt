package stas.batura.pressuretracker.ui.graph

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import com.jjoe64.graphview.DefaultLabelFormatter
import com.jjoe64.graphview.series.DataPoint
import com.jjoe64.graphview.series.LineGraphSeries
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.graph_fragment.*
import stas.batura.pressuretracker.MainViewModel
import stas.batura.pressuretracker.R
import stas.batura.pressuretracker.data.room.Pressure
import stas.batura.pressuretracker.databinding.GraphFragmentBinding


@AndroidEntryPoint
class GraphFragment: Fragment() {

    private val TAG = GraphFragment::class.java.simpleName

    private lateinit var graphViewModel: GraphViewModel

    private lateinit var mainViewModel: MainViewModel

    lateinit var radioGroup: RadioGroup

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        mainViewModel = ViewModelProvider(requireActivity()).get(MainViewModel::class.java)

        graphViewModel = ViewModelProvider(this).get(GraphViewModel::class.java)
//        mainViewModel = ViewModelProvider(requireActivity()).get(MainViewModel::class.java)
        val binding: GraphFragmentBinding = DataBindingUtil.inflate(inflater,
                R.layout.graph_fragment,
                container,
                false)

        binding.graphModel = graphViewModel
        binding.setLifecycleOwner(viewLifecycleOwner)
        return binding.getRoot()
    }

    override fun onStart() {
        super.onStart()
        addObservers()

        checkedradio()
    }

    override fun onStop() {
        super.onStop()
        removeObservers()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

// custom label formatter to show currency "EUR"

        // custom label formatter to show currency "EUR"
        graph.gridLabelRenderer.labelFormatter = object : DefaultLabelFormatter() {
            override fun formatLabel(value: Double, isValueX: Boolean): String {
                return if (isValueX) {
                    // show normal x values
                    ""
                } else {
                    // show currency for y values
                    super.formatLabel(value, isValueX)
                }
            }
        }

        radioGroup = view.findViewById<RadioGroup>(R.id.radio_group)

//        radioGroup.getChildAt(fragmentModel.getLastPress().getLastPowr()).setActivated(true);

//        radioGroup.check(radioGroup.getChildAt(fragmentModel.getLastPress().getLastPowr()).getId());


//        radioGroup.getChildAt(fragmentModel.getLastPress().getLastPowr()).setActivated(true);

//        radioGroup.check(radioGroup.getChildAt(fragmentModel.getLastPress().getLastPowr()).getId());
        radioGroup.setOnCheckedChangeListener(RadioGroup.OnCheckedChangeListener { group, checkedId ->
            when (checkedId) {
                R.id.rain_0 -> {
                    graphViewModel.saveRainPower(0)
                    mainViewModel.setServiceRain(0)
                    mainViewModel.savePressureValue()
                }
                R.id.rain_1 -> {
                    graphViewModel.saveRainPower(1)
                    mainViewModel.setServiceRain(1)
                    mainViewModel.savePressureValue()
                }
                R.id.rain_2 -> {
                    graphViewModel.saveRainPower(2)
                    mainViewModel.setServiceRain(2)
                    mainViewModel.savePressureValue()
                }
                R.id.rain_3 -> {
                    graphViewModel.saveRainPower(3)
                    mainViewModel.setServiceRain(3)
                    mainViewModel.savePressureValue()
                }
                R.id.rain_4 -> {
                    graphViewModel.saveRainPower(4)
                    mainViewModel.setServiceRain(4)
                    mainViewModel.savePressureValue()
                }
                R.id.rain_5 -> {
                    graphViewModel.saveRainPower(5)
                    mainViewModel.setServiceRain(5)
                    mainViewModel.savePressureValue()
                }
            }
        })

        graph_to_list.setOnClickListener { v ->
            run {

                v.findNavController().navigate(R.id.action_graphFragment_to_listFragment)

            }
        }
    }

    private fun addObservers() {
        graphViewModel.pressList.observe(viewLifecycleOwner, Observer {

            if (it != null) {
                createPlot(parseData(it))
            }

        })
    }

    private fun removeObservers() {
        graphViewModel.pressList.removeObservers(viewLifecycleOwner)
    }

    private fun parseData(list: List<Pressure>):  Array<DataPoint>{

        var count = 0
        var listM = mutableListOf<DataPoint>()
        for (pressure in list) {

            val data = DataPoint(pressure.time.toDouble(), pressure.pressure.toDouble())
            listM.add(data)
        }
        return listM.toTypedArray()
    }

    private fun checkedradio() {
        radioGroup.check(radioGroup.getChildAt(graphViewModel.lastPress.lastPowr).id)
    }



    private fun createPlot(array: Array<DataPoint>) {
        val series: LineGraphSeries<DataPoint> = LineGraphSeries(array
        )

        graph.removeAllSeries()
        graph.addSeries(series)

    }



}