package com.emirk.historybook.view

import android.Manifest
import android.app.Activity
import android.content.ContentResolver
import android.content.Context
import android.content.ContextWrapper
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.ImageDecoder
import android.location.LocationListener
import android.location.LocationManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.net.toUri
import androidx.navigation.Navigation
import androidx.navigation.fragment.navArgs
import com.emirk.historybook.R
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.database.ktx.database
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.ktx.storage
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.fragment_add.*
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.OutputStream
import java.sql.Timestamp
import java.util.*

class AddFragment : Fragment() {

    var selectedBitmap: Bitmap? = null
    private lateinit var permissionLauncher: ActivityResultLauncher<String>
    private lateinit var activityResultLauncher: ActivityResultLauncher<Intent>
    private lateinit var storage : FirebaseStorage
    private lateinit var selectedUri : Uri
    private lateinit var firestore : FirebaseFirestore
    private lateinit var me :Fragment
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            firestore = Firebase.firestore
        }

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment

        return inflater.inflate(R.layout.fragment_add, container, false)
    }
    val args: AddFragmentArgs by navArgs()
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        registerLauncher()
        imageView.setOnClickListener {
            if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                if (ActivityCompat.shouldShowRequestPermissionRationale(requireActivity(),Manifest.permission.READ_EXTERNAL_STORAGE)) {
                    Snackbar.make(imageView, "Need Permission", Snackbar.LENGTH_INDEFINITE)
                        .setAction("Give Permission", View.OnClickListener {
                            //izin iste
                            permissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE)
                        }).show()
                } else {
                    //izin iste
                    permissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE)
                }
            } else {
                //izin iste
                val intentToGallery = Intent(Intent.ACTION_PICK,MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                activityResultLauncher.launch(intentToGallery)
            }
        }

        getLocationButton.setOnClickListener {
            val action = AddFragmentDirections.actionAddFragmentToMapsFragment()
            Navigation.findNavController(it).navigate(action)
        }

        button.setOnClickListener {view->

            //firestore kayıt yaptır
            val uuid = UUID.randomUUID()
            storage = Firebase.storage
            val storageRef = storage.reference
            val imageRef = storageRef.child("images/"+uuid+".jpg")
            if(selectedUri!=null){
                imageRef!!.putFile(selectedUri).addOnSuccessListener {
                    Toast.makeText(context,"Basariyla kaydedildi",Toast.LENGTH_LONG).show()
                    imageRef.downloadUrl.addOnSuccessListener {
                        val hasMap = hashMapOf<String,Any>()
                        hasMap.put("Place",placeText.text.toString())
                        hasMap.put("downdloadurl",it.toString())
                        hasMap.put("date",dateText.text.toString())
                        hasMap.put("savedate",com.google.firebase.Timestamp.now())

                        firestore.collection("Histories").add(hasMap).addOnSuccessListener {

                            val action = AddFragmentDirections.actionAddFragmentToHistoryFragment()
                            Navigation.findNavController(view).navigate(action)

                        }.addOnFailureListener {
                            Toast.makeText(requireContext(),it.localizedMessage,Toast.LENGTH_LONG).show()
                        }
                    }.addOnFailureListener {
                        Toast.makeText(context,it.localizedMessage,Toast.LENGTH_LONG).show()
                    }
                }.addOnFailureListener(requireActivity(), OnFailureListener {
                    Toast.makeText(context,it.localizedMessage,Toast.LENGTH_LONG).show()
                })

            }

        }

        val Alocation:String = args.location
        if (Alocation.equals("history")){
            println("historyden geldi birşey yapma")
        }else{
            placeText.setText(Alocation)
        }

    }

    private fun registerLauncher() {
        activityResultLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                val intentFromResult = result.data
                if (intentFromResult != null) {
                    val imageData = intentFromResult.data
                    selectedUri = imageData!!
                    selectedUri?.let {
                        imageView.setImageURI(selectedUri)
                    }
                }
            }
        permissionLauncher = registerForActivityResult(ActivityResultContracts.RequestPermission()){result->
            if (result){
                val intentToGallery = Intent(Intent.ACTION_PICK,MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                activityResultLauncher.launch(intentToGallery)
            }else{
                Toast.makeText(requireContext(),"Permission needed",Toast.LENGTH_LONG).show()

            }

        }
    }
}