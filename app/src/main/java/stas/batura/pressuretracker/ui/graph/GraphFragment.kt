package stas.batura.pressuretracker.ui.graph

import android.graphics.Color
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
import stas.batura.pressuretracker.utils.getCurrentDayBegin


@AndroidEntryPoint
class GraphFragment: Fragment() {

    private val TAG = GraphFragment::class.java.simpleName

    private lateinit var graphViewModel: GraphViewModel

    private lateinit var mainViewModel: MainViewModel

    lateinit var radioGroup: RadioGroup

    private var isStarted = false


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        val time = getCurrentDayBegin()

        mainViewModel = ViewModelProvider(requireActivity()).get(MainViewModel::class.java)

        graphViewModel = ViewModelProvider(this).get(GraphViewModel::class.java)
//        mainViewModel = ViewModelProvider(requireActivity()).get(MainViewModel::class.java)
        val binding: GraphFragmentBinding = DataBindingUtil.inflate(inflater,
                R.layout.graph_fragment,
                container,
                false)

        binding.graphModel = graphViewModel
        binding.mainViewModel = mainViewModel
        binding.setLifecycleOwner(viewLifecycleOwner)
        return binding.getRoot()
    }

    override fun onStart() {
        super.onStart()
        addObservers()

        graphViewModel.updateRainpower()

        checkedradio()

        isStarted = true
    }

    override fun onStop() {
        super.onStop()
        removeObservers()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

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

        radioGroup.setOnCheckedChangeListener(RadioGroup.OnCheckedChangeListener { group, checkedId ->
            when (checkedId) {

                R.id.rain_0 -> {
                    if (isStarted) {
                        graphViewModel.saveRainPower(0)
                        mainViewModel.setServiceRain(0)
                        mainViewModel.savePressureValue()
                    }
                }
                R.id.rain_1 -> {
                    if (isStarted) {
                        graphViewModel.saveRainPower(1)
                        mainViewModel.setServiceRain(1)
                        mainViewModel.savePressureValue()
                    }
                }
                R.id.rain_2 -> {
                    if (isStarted) {
                        graphViewModel.saveRainPower(2)
                        mainViewModel.setServiceRain(2)
                        mainViewModel.savePressureValue()
                    }
                }
                R.id.rain_3 -> {
                    if (isStarted) {
                        graphViewModel.saveRainPower(3)
                        mainViewModel.setServiceRain(3)
                        mainViewModel.savePressureValue()
                    }
                }
                R.id.rain_4 -> {
                    if (isStarted) {
                        graphViewModel.saveRainPower(4)
                        mainViewModel.setServiceRain(4)
                        mainViewModel.savePressureValue()
                    }
                }
                R.id.rain_5 -> {
                    if (isStarted) {
                        graphViewModel.saveRainPower(5)
                        mainViewModel.setServiceRain(5)
                        mainViewModel.savePressureValue()
                    }
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
//                drawLine(prepareData(it))
                graph.removeAllSeries()
                val lines = prepareData(it)
                for(line in lines) {
                    drawLine(line)
                }
                graph.rootView
                graph.viewport.isScalable = true
                graph.viewport.setScalableY(true)
            }

        })
    }

    private fun removeObservers() {
        graphViewModel.pressList.removeObservers(viewLifecycleOwner)
    }

    /**
     * preparing data for plot
     */
    private fun prepareData(list: List<Pressure>):  List<List<Pressure>>{

        val linesList = mutableListOf<List<Pressure>>()

        var count = 0
        var presuresList = mutableListOf<Pressure>()
        if (list.size > 0 ) {
            var lastPower = list[0].rainPower
            for (pressure in list) {
                if (pressure.rainPower == lastPower) {
//                val data = DataPoint(pressure.time.toDouble(), pressure.pressure.toDouble())
                    presuresList.add(pressure)
                } else {
                    linesList.add(presuresList)
                    presuresList = mutableListOf<Pressure>()
                    lastPower = pressure.rainPower
                    presuresList.add(pressure)
                }
                if (presuresList.size > 0) {
                    linesList.add(presuresList)
                }
            }
        }

        return linesList
    }

//    private fun parseAnyData(list: List<Pressure>): List<DataEntry> {
//
//        var outData = mutableListOf<DataEntry>()
//        for(pressure in list) {
////            val entry =
//        }
//    }

    /**
     * checks radio group init value
     */
    private fun checkedradio() {
        radioGroup.check(radioGroup.getChildAt(graphViewModel.lastPress.lastPowr).id)
    }

    /**
     * creating pressue plot using GraphView
     */
    private fun drawLine(array: List<Pressure>) {
        val data = parseData(array)
        val series: LineGraphSeries<DataPoint> = LineGraphSeries(data)
        series.color = getColor(array[0])
        graph.addSeries(series)
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

    private fun getColor(pressure: Pressure): Int {
        when(pressure.rainPower) {
            0 -> return Color.YELLOW
            1 -> return Color.GRAY
            2 -> return Color.CYAN
            3 -> return Color.BLUE
            5 -> return Color.MAGENTA
            6 -> return Color.RED
        }
        return Color.YELLOW
    }

}