package com.example.android.politicalpreparedness.election

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.android.politicalpreparedness.databinding.FragmentElectionBinding
import com.example.android.politicalpreparedness.election.adapter.ElectionListAdapter
import com.example.android.politicalpreparedness.election.adapter.ElectionListener

class ElectionsFragment : Fragment() {

    private val viewModel: ElectionsViewModel by lazy {
        ViewModelProvider(this).get(ElectionsViewModel::class.java)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        //TODO: Add ViewModel values and create ViewModel

        //TODO: Add binding values
        val binding = FragmentElectionBinding.inflate(inflater)

        binding.lifecycleOwner = this

        binding.viewModel = viewModel

        val adapter = ElectionListAdapter(ElectionListener { electionId ->
            viewModel.onElectionClicked(electionId)
        })

        binding.upcomingElectionsRecycler.adapter = adapter
        viewModel.upcomingElections.observe(viewLifecycleOwner, Observer { upcomingElections ->
            upcomingElections?.let {
                adapter.submitList(upcomingElections)
            }
        })

        //TODO: Link elections to voter info

        //TODO: Initiate recycler adapters

        //TODO: Populate recycler adapters
        return binding.root
    }

    //TODO: Refresh adapters when fragment loads

}