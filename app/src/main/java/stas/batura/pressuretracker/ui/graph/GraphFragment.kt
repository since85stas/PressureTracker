package stas.batura.pressuretracker.ui.graph

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.android.HiltAndroidApp
import stas.batura.pressuretracker.MainViewModel
import stas.batura.pressuretracker.R
import stas.batura.pressuretracker.databinding.GraphFragmentBinding
import stas.batura.pressuretracker.ui.main.MainFragmentViewModel

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

}