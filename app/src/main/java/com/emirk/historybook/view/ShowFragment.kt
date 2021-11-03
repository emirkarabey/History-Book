package com.emirk.historybook.view

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.emirk.historybook.R
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.fragment_add.*
import kotlinx.android.synthetic.main.fragment_show.*
import kotlinx.android.synthetic.main.fragment_show.view.*


class ShowFragment : Fragment() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_show, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        arguments?.let {
            val place = ShowFragmentArgs.fromBundle(it).place
            val date = ShowFragmentArgs.fromBundle(it).date
            val downloadUrl = ShowFragmentArgs.fromBundle(it).downloadUrl

            view.showPlaceText.text = place.toString()
            view.showDateText.text = date.toString()
            Picasso.get().load(downloadUrl).into(view.showImageView)
        }

    }
}