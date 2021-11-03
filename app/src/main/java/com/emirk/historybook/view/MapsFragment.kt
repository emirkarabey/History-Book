package com.emirk.historybook.view

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.*
import androidx.fragment.app.Fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.navigation.Navigation
import com.emirk.historybook.R

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.material.snackbar.Snackbar
import com.google.protobuf.DescriptorProtos
import kotlinx.android.synthetic.main.fragment_maps.*
import java.security.Provider
import java.util.*

class MapsFragment : Fragment() {

    private lateinit var permissionLauncher: ActivityResultLauncher<String>
    private lateinit var activityResultLauncher: ActivityResultLauncher<Intent>
    private lateinit var locationManager: LocationManager
    private lateinit var locationListener: LocationListener
    private lateinit var mMap: GoogleMap
    var Alocation:String =""
    private lateinit var address:List<Address>
    private val callback = OnMapReadyCallback { googleMap ->
        /**
         * Manipulates the map once available.
         * This callback is triggered when the map is ready to be used.
         * This is where we can add markers or lines, add listeners or move the camera.
         * In this case, we just add a marker near Sydney, Australia.
         * If Google Play services is not installed on the device, the user will be prompted to
         * install it inside the SupportMapFragment. This method will only be triggered once the
         * user has installed Google Play services and returned to the app.
         */

        mMap=googleMap
        locationManager = requireActivity().getSystemService(Context.LOCATION_SERVICE) as LocationManager
        locationListener = object:LocationListener{
            override fun onLocationChanged(p0: Location) {
                var latLng = LatLng(p0.latitude,p0.longitude)
                googleMap.addMarker(MarkerOptions().position(latLng).title("Your Location"))
                googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng,15f))

                var geocoder:Geocoder= Geocoder(requireContext(), Locale.getDefault())
                address = geocoder.getFromLocation(p0.latitude,p0.longitude, 1); // Here 1 represent max location result to returned, by documents it recommended 1 to 5
                var country = address.get(0).countryName
                var province = address.get(0).adminArea
                var district = address.get(0).subAdminArea
                Alocation=district.toString()+"/"+province.toString()+","+country.toString()
                println(p0)
            }// location listeneri başka yerde yazman lazım
        }

        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            //request permission
            if (ActivityCompat.shouldShowRequestPermissionRationale(requireActivity(), Manifest.permission.READ_EXTERNAL_STORAGE)) {
                Snackbar.make(requireView(), "Permission needed for location", Snackbar.LENGTH_INDEFINITE).setAction("Give Permission") {
                    permissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE)
                }.show()
            } else {
                permissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
            }
        } else {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0f, locationListener)
        }

        mapsToAddButton.setOnClickListener {

            val action = MapsFragmentDirections.actionMapsFragmentToAddFragment(Alocation,"Maps")
            Navigation.findNavController(it).navigate(action)
        }

    }


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        registerLauncher()



        return inflater.inflate(R.layout.fragment_maps, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        super.onViewCreated(view, savedInstanceState)
        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?
        mapFragment?.getMapAsync(callback)

    }





    private fun registerLauncher(){

        permissionLauncher = registerForActivityResult(ActivityResultContracts.RequestPermission()){result->

            if (result){
                if (ContextCompat.checkSelfPermission(requireContext(),Manifest.permission.ACCESS_FINE_LOCATION)==PackageManager.PERMISSION_GRANTED){
                    locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,0,0f,locationListener)

                }
                //permission granted
            }else{
                //permission denied
                Toast.makeText(requireContext(),"Permission Needed",Toast.LENGTH_LONG).show()
            }
        }
    }
}
