package com.emirk.historybook.adapter
import android.app.Activity
import android.app.AlertDialog
import android.app.PendingIntent.getActivity
import android.content.DialogInterface
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.navigation.Navigation.findNavController
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.emirk.historybook.R
import com.emirk.historybook.databinding.RecyclerSatirBinding
import com.emirk.historybook.model.Histories
import com.emirk.historybook.view.HistoryFragment
import com.emirk.historybook.view.HistoryFragmentDirections
import com.emirk.historybook.view.MainActivity
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.fragment_add.view.*
import kotlinx.android.synthetic.main.recycler_satir.view.*
import kotlin.coroutines.coroutineContext


class HistoryAdapter(private val historylist : ArrayList<Histories>) : RecyclerView.Adapter<HistoryAdapter.PostHolder>() {

    interface Listener{
        fun onItemClick(histories:Histories)
    }
    class PostHolder(val binding: RecyclerSatirBinding) : RecyclerView.ViewHolder(binding.root) {


    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostHolder {

        val binding = RecyclerSatirBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return PostHolder(binding)
    }

    override fun getItemCount(): Int {
        return historylist.size
    }

    override fun onBindViewHolder(holder: PostHolder, position: Int) {

        holder.itemView.recyclerViewPlaceText.text=historylist[position].place
        holder.itemView.recyclerViewDateText.text=historylist[position].date
        Picasso.get().load(historylist.get(position).downloadUrl).into(holder.binding.recyclerViewImageView)


        holder.itemView.showButton.setOnClickListener {

            val action = HistoryFragmentDirections.actionHistoryFragmentToShowFragment(historylist[position].place,historylist[position].date,historylist[position].downloadUrl)
            Navigation.findNavController(it).navigate(action)

        }
        holder.itemView.deleteButton.setOnClickListener {
            val builder = AlertDialog.Builder(it.context)
            builder.setTitle("Birini Seçin")
            builder.setMessage("Silmek İstiyor Musunuz?")
            builder.setPositiveButton("Yes",DialogInterface.OnClickListener { dialogInterface, i ->
                println("Buraya girdi")
                val firestore:FirebaseFirestore
                firestore=Firebase.firestore
                firestore.collection("Histories").addSnapshotListener { value, error ->
                    val documents = value!!.documents
                    if (value!=null){
                        if (!value.isEmpty){
                            for (document in documents){

                                if (document.get("Place")!!.equals(historylist[position].place)){
                                    firestore.collection("Histories").document(document.id).delete().addOnSuccessListener { it2->
                                        println("Silindi")
                                        notifyDataSetChanged()
                                        val action = HistoryFragmentDirections.actionHistoryFragmentSelf()
                                        Navigation.findNavController(it).navigate(action)


                                    }.addOnFailureListener {
                                        println(it.localizedMessage)
                                    }
                                }
                            }
                        }
                    }



                }

                //firebaseden silinecek

            })
            builder.setNegativeButton("No",DialogInterface.OnClickListener { dialogInterface, i ->
                println("Negative girdi")
            })
            builder.show()
        }
        //long click koyup silmek istiyor musunuz diye sorulacak evet derse silinecek

    }






}

