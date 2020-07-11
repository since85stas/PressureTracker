package stas.batura.pressuretracker.ui.graph

import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import com.jjoe64.graphview.DefaultLabelFormatter
import com.jjoe64.graphview.series.DataPoint
import com.jjoe64.graphview.series.LineGraphSeries
import com.jjoe64.graphview.series.PointsGraphSeries
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

//        graph.gridLabelRenderer.labelFormatter = object : DefaultLabelFormatter() {
//            override fun formatLabel(value: Double, isValueX: Boolean): String {
//                return if (isValueX) {
//                    // show normal x values
//                    ""
//                } else {
//                    // show currency for y values
//                    super.formatLabel(value, isValueX)
//                }
//            }
//        }

        radioGroup = view.findViewById<RadioGroup>(R.id.radio_group)

        radioGroup.setOnCheckedChangeListener(RadioGroup.OnCheckedChangeListener { group, checkedId ->
            when (checkedId) {

                R.id.rain_0 -> {

                        graphViewModel.saveRainPower(0)
                        mainViewModel.setServiceRain(0)
                    if (isStarted) {
                        mainViewModel.savePressureValue()
                    }
                }
                R.id.rain_1 -> {

                        graphViewModel.saveRainPower(1)
                        mainViewModel.setServiceRain(1)
                    if (isStarted) {
                        mainViewModel.savePressureValue()
                    }
                }
                R.id.rain_2 -> {
                        graphViewModel.saveRainPower(2)
                        mainViewModel.setServiceRain(2)
                    if (isStarted) {
                        mainViewModel.savePressureValue()
                    }
                }
                R.id.rain_3 -> {

                        graphViewModel.saveRainPower(3)
                        mainViewModel.setServiceRain(3)
                    if (isStarted) {
                        mainViewModel.savePressureValue()
                    }
                }
                R.id.rain_4 -> {

                        graphViewModel.saveRainPower(4)
                        mainViewModel.setServiceRain(4)
                    if (isStarted) {
                        mainViewModel.savePressureValue()
                    }
                }
                R.id.rain_5 -> {

                        graphViewModel.saveRainPower(5)
                        mainViewModel.setServiceRain(5)
                    if (isStarted) {
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

                graph.removeAllSeries()
                val newList = shiftTime(it)
                drawOld(newList, getRainList(newList))


//                val lines = prepareData(it)
//                for(line in lines) {
//                    drawLine(line)
//                }
//                graph.rootView
//                graph.viewport.isScalable = true
//                graph.viewport.setScalableY(true)
            }

        })
    }

    private fun shiftTime(list: List<Pressure>): List<Pressure> {
        if (list.size > 0) {
            val initVal = list[0].time
            for (pressure in list) {
                val newPress = pressure.time - initVal
                pressure.time = newPress
            }
        }
        return list
    }


    private fun getRainList(list: List<Pressure>): List<List<Pressure>> {
        val rainList = mutableListOf<List<Pressure>>()
        var powerList = mutableListOf<Pressure>()

        var pow = 1
        for (i in pow..5) {
            for (pressure in list) {
                if (pressure.rainPower == pow) {
                    powerList.add(pressure)
                }
            }
            rainList.add(powerList)
            powerList = mutableListOf<Pressure>()
            pow += 1
        }
        return rainList
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

    private fun drawOld(alllist: List<Pressure>, rainlist: List<List<Pressure>>) {
        val allpoints = parseDataOld(alllist)
        val series: LineGraphSeries<DataPoint> = LineGraphSeries(allpoints)
        try{
            graph.addSeries(series)
        } catch (e: Exception) {
            Log.d(TAG, "drawOld: " + e)
            Toast.makeText(requireContext(), "Error graph values", Toast.LENGTH_LONG)
                .show()
        }

        if (rainlist.size > 0) {
            for (list in rainlist) {
                if (list.size > 0) {
                    val ponts = parseDataOld(list)
                    var pointSeries = PointsGraphSeries<DataPoint>(ponts)
                    pointSeries.size = 5f

                    pointSeries.color = getColor(list[0])
                    try {
                        graph.addSeries(pointSeries)
                    } catch (e: Exception) {
                        Log.d(TAG, "drawOld: " + e)
                        Toast.makeText(requireContext(), "Error graph values", Toast.LENGTH_LONG)
                                .show()
                    }
                }
            }
            var viewpost = graph.viewport
            viewpost.isScrollable = true
            viewpost.setScalableY(true)
            viewpost.isScalable = true
            viewpost.isXAxisBoundsManual = true
            if (allpoints.size > 0) {
                viewpost.setMinX(allpoints[0].x)
                viewpost.setMaxX(allpoints[allpoints.size - 1].x)
            }
            viewpost.scrollToEnd()
        }

    }

    private fun parseData(list: List<Pressure>):  Array<DataPoint>{
        var count = 0
        var listM = mutableListOf<DataPoint>()
        if(list.size > 0) {
            for (pressure in list) {
                val timeMin = ((pressure.time)/(1000*60)).toInt()
                val data = DataPoint(timeMin.toDouble(), pressure.pressure.toDouble())
                listM.add(data)
            }
        }
        return listM.toTypedArray()
    }

    private fun parseDataOld(list: List<Pressure>):  Array<DataPoint>{
        var listM = mutableListOf<DataPoint>()
        if(list.size > 0) {
//            val firstTime = list.get(0).time
            for (pressure in list) {
                val timeMin = ((pressure.time)/(1000*60))
                val data = DataPoint(timeMin.toDouble(), getNullAltPressure(pressure.pressure, pressure.altitude))
                listM.add(data)
            }
        }
        return listM.toTypedArray()
    }

    private fun getNullAltPressure(pressure: Float, altitude: Float): Double {
        val nullPress = pressure/ (Math.pow(10.toDouble(), -0.06*(altitude/1000.0f) ))
        return nullPress;
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