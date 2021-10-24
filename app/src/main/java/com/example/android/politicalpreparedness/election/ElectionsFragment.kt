package com.example.android.politicalpreparedness.election

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.android.politicalpreparedness.R
import com.example.android.politicalpreparedness.database.ElectionDatabase
import com.example.android.politicalpreparedness.databinding.FragmentElectionBinding
import com.example.android.politicalpreparedness.election.adapter.ElectionListAdapter
import com.example.android.politicalpreparedness.election.adapter.ElectionListener
import com.example.android.politicalpreparedness.repository.ElectionsRepository

class ElectionsFragment : Fragment() {

    private lateinit var viewModel: ElectionsViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val application = requireNotNull(this.activity).application
        val electionDao = ElectionDatabase.getInstance(application).electionDao
        val repository = ElectionsRepository(electionDao)
        val viewModelFactory = ElectionsViewModelFactory(repository)

        viewModel = ViewModelProvider(this, viewModelFactory).get(ElectionsViewModel::class.java)

        val binding = FragmentElectionBinding.inflate(inflater)

        binding.lifecycleOwner = this

        binding.viewModel = viewModel

        val upcomingElectionsAdapter = ElectionListAdapter(
            getString(R.string.upcoming_elections),
            ElectionListener { electionId ->
                viewModel.onElectionClicked(electionId)
            })

        val savedElectionsAdapter = ElectionListAdapter(
            getString(R.string.saved_elections),
            ElectionListener { electionId ->
                viewModel.onElectionClicked(electionId)
            })

        binding.upcomingElectionsRecycler.apply {
            adapter = upcomingElectionsAdapter
            viewModel.upcomingElections.observe(viewLifecycleOwner, Observer { upcomingElections ->
                upcomingElections?.let {
                    upcomingElectionsAdapter.addHeaderAndSubmitList(upcomingElections)
                }
            })
        }

        binding.savedElectionsRecycler.apply {
            adapter = savedElectionsAdapter
            viewModel.savedElections.observe(viewLifecycleOwner, Observer { savedElections ->
                savedElections?.let {
                    savedElectionsAdapter.addHeaderAndSubmitList(savedElections)
                }
            })
        }

        viewModel.navigateToVoterInfo.observe(viewLifecycleOwner, Observer { election ->
            if (election != null) {
                findNavController().navigate(
                    ElectionsFragmentDirections.actionElectionsFragmentToVoterInfoFragment(election)
                )
                viewModel.navigateToVoterInfoComplete()
            }
        })

        return binding.root
    }

}