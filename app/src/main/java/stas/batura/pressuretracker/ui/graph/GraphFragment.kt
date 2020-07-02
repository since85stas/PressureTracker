package stas.batura.pressuretracker.ui.graph

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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
import stas.batura.pressuretracker.R
import stas.batura.pressuretracker.data.room.Pressure
import stas.batura.pressuretracker.databinding.GraphFragmentBinding


@AndroidEntryPoint
class GraphFragment: Fragment() {

    private val TAG = GraphFragment::class.java.simpleName

    private lateinit var graphViewModel: GraphViewModel;

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

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

    private fun createPlot(array: Array<DataPoint>) {
        val series: LineGraphSeries<DataPoint> = LineGraphSeries(array
        )

        graph.removeAllSeries()
        graph.addSeries(series)

    }



}