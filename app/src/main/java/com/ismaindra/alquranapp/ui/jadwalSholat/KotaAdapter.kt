package com.ismaindra.alquranapp.ui.jadwalSholat

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.ismaindra.alquranapp.data.model.Kota

class KotaAdapter(
    private val onClick: (Kota) -> Unit
) : RecyclerView.Adapter<KotaAdapter.VH>() {

    private val data = mutableListOf<Kota>()

    fun submit(list: List<Kota>) {
        data.clear()
        data.addAll(list)
        notifyDataSetChanged()
    }

    inner class VH(view: View) : RecyclerView.ViewHolder(view) {
        val text: TextView = view.findViewById(android.R.id.text1)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val view = LayoutInflater.from(parent.context)
            .inflate(android.R.layout.simple_list_item_1, parent, false)
        return VH(view)
    }

    override fun onBindViewHolder(holder: VH, position: Int) {
        val kota = data[position]
        holder.text.text = kota.lokasi
        holder.itemView.setOnClickListener { onClick(kota) }
    }

    override fun getItemCount() = data.size
}
