package com.ismaindra.alquranapp.ui.home

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.ismaindra.alquranapp.R
import androidx.recyclerview.widget.RecyclerView
import com.ismaindra.alquranapp.data.model.Doa

class DoaAdapter(
    private val items: List<Doa>,
    private val onClick: (Doa) -> Unit
) : RecyclerView.Adapter<DoaAdapter.DoaViewHolder>() {

    inner class DoaViewHolder(val view: View) : RecyclerView.ViewHolder(view) {
        val content: TextView = view.findViewById(R.id.tvDoaContent)
        val source: TextView = view.findViewById(R.id.tvDoaSource)
        val tr: TextView = view.findViewById(R.id.tvDoaIndo)
        val image: ImageView = view.findViewById(R.id.imgBackground)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DoaViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_doa, parent, false)
        return DoaViewHolder(view)
    }

    override fun onBindViewHolder(holder: DoaViewHolder, position: Int) {
        val doa = items[position]
        holder.content.text = doa.arab
        holder.source.text = doa.judul
        holder.tr.text = doa.indo

        holder.view.setOnClickListener { onClick(doa) }
    }

    override fun getItemCount(): Int = items.size
}
