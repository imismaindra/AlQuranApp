package com.ismaindra.alquranapp.ui.jadwalSholat

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.ismaindra.alquranapp.R
import com.ismaindra.alquranapp.data.model.Jadwal

class JadwalAdapter : RecyclerView.Adapter<JadwalAdapter.ViewHolder>() {

    private val items = mutableListOf<Pair<String, String>>()

    fun submit(jadwal: Jadwal) {
        items.clear()
        items.addAll(
            listOf(
                "Imsak" to jadwal.imsak,
                "Subuh" to jadwal.subuh,
                "Dhuha" to jadwal.dhuha,
                "Dzuhur" to jadwal.dzuhur,
                "Ashar" to jadwal.ashar,
                "Maghrib" to jadwal.maghrib,
                "Isya" to jadwal.isya
            )
        )
        notifyDataSetChanged()
    }

    class ViewHolder(val view: View) : RecyclerView.ViewHolder(view)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_jadwal, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val (nama, waktu) = items[position]
        holder.view.findViewById<TextView>(R.id.tvNama).text = nama
        holder.view.findViewById<TextView>(R.id.tvWaktu).text = waktu
    }

    override fun getItemCount() = items.size
}
