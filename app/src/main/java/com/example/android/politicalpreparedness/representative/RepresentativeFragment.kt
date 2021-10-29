package com.example.android.politicalpreparedness.representative

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Geocoder
import android.location.Location
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.android.politicalpreparedness.BuildConfig
import com.example.android.politicalpreparedness.R
import com.example.android.politicalpreparedness.database.ElectionDatabase
import com.example.android.politicalpreparedness.databinding.FragmentRepresentativeBinding
import com.example.android.politicalpreparedness.network.models.Address
import com.example.android.politicalpreparedness.repository.ElectionsRepository
import com.example.android.politicalpreparedness.representative.adapter.RepresentativeListAdapter
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.material.snackbar.Snackbar
import java.util.*

class RepresentativeFragment : Fragment() {

    companion object {
        const val REQUEST_LOCATION_PERMISSION = 1000
    }

    private lateinit var viewModel: RepresentativeViewModel
    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val application = requireNotNull(this.activity).application
        val electionDao = ElectionDatabase.getInstance(application).electionDao
        val repository = ElectionsRepository(electionDao)
        val viewModelFactory = RepresentativeViewModelFactory(repository)
        viewModel =
            ViewModelProvider(this, viewModelFactory).get(RepresentativeViewModel::class.java)

        val binding = FragmentRepresentativeBinding.inflate(inflater)
        binding.lifecycleOwner = this
        binding.viewModel = viewModel
        fusedLocationProviderClient =
            LocationServices.getFusedLocationProviderClient(requireActivity())

        viewModel.showSnackBarErrorMessage.observe(viewLifecycleOwner, Observer { string ->
            Snackbar.make(this.requireView(), getString(string), Snackbar.LENGTH_LONG).show()
        })

        val representativeAdapter = RepresentativeListAdapter()

        with(binding) {
            representativeRecycler.apply {
                adapter = representativeAdapter
                viewModel?.representatives?.observe(viewLifecycleOwner, Observer { representatives ->
                    representativeAdapter.submitList(representatives)
                })
            }

            useMyLocationButton.setOnClickListener {
                if (isPermissionGranted()) {
                    getLocation()
                } else {
                    requestPermissions(
                        arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                        REQUEST_LOCATION_PERMISSION
                    )
                }
            }

            findMyRepsButton.setOnClickListener {
                val address = Address(
                    binding.addressLine1Edit.text.toString(),
                    binding.addressLine2Edit.text.toString(),
                    binding.cityEdit.text.toString(),
                    binding.stateSpinner.selectedItem.toString(),
                    binding.zipEdit.text.toString()
                )

                viewModel?.getRepresentatives(address)
                hideKeyboard()
            }
        }

        return binding.root
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        if (
            grantResults.isEmpty() ||
            grantResults[0] == PackageManager.PERMISSION_DENIED
        ) {
            Snackbar.make(
                this.requireView(),
                R.string.permission_denied_explanation,
                Snackbar.LENGTH_LONG
            )
                .setAction(R.string.settings) {
                    startActivity(Intent().apply {
                        action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
                        data = Uri.fromParts("package", BuildConfig.APPLICATION_ID, null)
                        flags = Intent.FLAG_ACTIVITY_NEW_TASK
                    })
                }.show()
        } else {
            getLocation()
        }
    }

    @Suppress("DEPRECATED_IDENTITY_EQUALS")
    private fun isPermissionGranted(): Boolean {
        return ContextCompat.checkSelfPermission(
            requireContext(),
            Manifest.permission.ACCESS_FINE_LOCATION
        ) === PackageManager.PERMISSION_GRANTED
    }


    @SuppressLint("MissingPermission")
    private fun getLocation() {
        val locationResult = fusedLocationProviderClient.lastLocation
        locationResult.addOnCompleteListener(requireActivity()) { task ->
            if (task.isSuccessful) {
                task.result?.let { location ->
                    val address = geoCodeLocation(location)
                    viewModel.address.postValue(address)
                    viewModel.getRepresentatives(address)
                }
            }
        }
    }

    private fun geoCodeLocation(location: Location): Address {
        val geocoder = Geocoder(context, Locale.getDefault())
        return geocoder.getFromLocation(location.latitude, location.longitude, 1)
            .map { address ->
                Address(
                    address.thoroughfare,
                    address.subThoroughfare,
                    address.locality,
                    address.adminArea,
                    address.postalCode
                )
            }
            .first()
    }

    private fun hideKeyboard() {
        val imm = activity?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(view!!.windowToken, 0)
    }
}