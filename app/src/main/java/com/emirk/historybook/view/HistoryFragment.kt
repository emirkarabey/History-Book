package com.emirk.historybook.view

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.emirk.historybook.R
import com.emirk.historybook.adapter.HistoryAdapter
import com.emirk.historybook.databinding.FragmentHistoryBinding
import com.emirk.historybook.model.Histories
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.fragment_add.*
import kotlinx.android.synthetic.main.fragment_history.*
import kotlinx.android.synthetic.main.fragment_history.view.*
import kotlinx.android.synthetic.main.recycler_satir.*

class HistoryFragment : Fragment() {

    private lateinit var firestore : FirebaseFirestore
    private lateinit var historyList:ArrayList<Histories>
    private var _binding: FragmentHistoryBinding? = null
    private lateinit var artAdapter : HistoryAdapter
    private val binding get() = _binding!!
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
        }
        firestore = Firebase.firestore
        historyList = ArrayList<Histories>()
        getData()

    }

    private fun getData(){

        firestore.collection("Histories").orderBy("savedate", Query.Direction.DESCENDING).addSnapshotListener { value, error ->
            if (error!=null){
                Toast.makeText(requireContext(),error.localizedMessage,Toast.LENGTH_LONG).show()
            }else{
                if (value!=null){
                    if (!value.isEmpty){
                        val documents = value.documents

                        for (document in documents){

                            val place = document.get("Place") as String
                            val date = document.get("date")as String
                            val downloadUrl = document.get("downdloadurl") as String
                            if (place!=null&&date!=null&&downloadUrl!=null){

                                val histories = Histories(place,date,downloadUrl)
                                historyList.add(histories)

                                artAdapter.notifyDataSetChanged()

                            }
                        }

                    }
                }
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentHistoryBinding.inflate(layoutInflater,container,false)
        val view = binding.root

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        addhistorybutton.setOnClickListener {
            val action = HistoryFragmentDirections.actionHistoryFragmentToAddFragment("history","History")
            Navigation.findNavController(it).navigate(action)

        }

        handleResponse()

    }



    private fun handleResponse() {
        binding.recyclerView.layoutManager = LinearLayoutManager(requireContext())
        artAdapter = HistoryAdapter(historyList)
        binding.recyclerView.adapter = artAdapter
    }
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}