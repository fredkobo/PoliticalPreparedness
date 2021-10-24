package com.example.android.politicalpreparedness.election

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.observe
import com.example.android.politicalpreparedness.database.ElectionDatabase
import com.example.android.politicalpreparedness.databinding.FragmentVoterInfoBinding
import com.example.android.politicalpreparedness.repository.ElectionsRepository

class VoterInfoFragment : Fragment() {

    lateinit var voterInfoViewModel: VoterInfoViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val election = VoterInfoFragmentArgs.fromBundle(requireArguments()).argElection

        val application = requireNotNull(this.activity).application
        val electionDao = ElectionDatabase.getInstance(application).electionDao
        val repository = ElectionsRepository(electionDao)
        val viewModelFactory = VoterInfoViewModelFactory(repository, election)

        val voterInfoViewModel =
            ViewModelProvider(this, viewModelFactory).get(VoterInfoViewModel::class.java)

        val binding = FragmentVoterInfoBinding.inflate(inflater)
        binding.lifecycleOwner = this
        binding.viewModel = voterInfoViewModel

        voterInfoViewModel.voterInfo.observe(viewLifecycleOwner) { voterInfoResponse ->
            voterInfoResponse?.state?.get(0)?.electionAdministrationBody?.let { administrationBody ->
                if (administrationBody.ballotInfoUrl != null || administrationBody.votingLocationFinderUrl != null) {
                    binding.stateHeader.visibility = View.VISIBLE
                    if (administrationBody.ballotInfoUrl != null)
                        binding.stateBallot.visibility = View.VISIBLE
                    else
                        binding.stateBallot.visibility = View.GONE
                    if (administrationBody.votingLocationFinderUrl != null)
                        binding.stateLocations.visibility = View.VISIBLE
                    else
                        binding.stateLocations.visibility = View.GONE
                }
                if (administrationBody.correspondenceAddress != null) {
                    binding.addressGroup.visibility = View.VISIBLE
                    binding.address.text =
                        administrationBody.correspondenceAddress.toFormattedString()
                } else
                    binding.addressGroup.visibility = View.GONE
            }
        }

        voterInfoViewModel.urlBrowser.observe(viewLifecycleOwner) { url ->
            if (url != null) {
                longUrlIntent(url)
            }
        }

        return binding.root
    }

    private fun longUrlIntent(url: String) {
        val i = Intent(Intent.ACTION_VIEW)
        i.data = Uri.parse(url)
        startActivity(i)
        voterInfoViewModel.loadUrlCompleted()
    }

}