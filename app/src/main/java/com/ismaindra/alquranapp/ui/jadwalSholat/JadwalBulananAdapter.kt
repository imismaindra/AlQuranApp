package com.ismaindra.alquranapp.ui.jadwalSholat

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.ismaindra.alquranapp.R
import com.ismaindra.alquranapp.data.model.JadwalBulananItem

class JadwalBulananAdapter :
    RecyclerView.Adapter<JadwalBulananAdapter.ViewHolder>() {

    private val items = mutableListOf<JadwalBulananItem>()

    fun submit(list: List<JadwalBulananItem>) {
        Log.d("BulananAdapter", "Submitting ${list.size} items")
        items.clear()
        items.addAll(list)
        notifyDataSetChanged()
        Log.d("BulananAdapter", "After submit, items size: ${items.size}")
    }

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tanggal: TextView = view.findViewById(R.id.tvTanggal)
        val subuh: TextView = view.findViewById(R.id.tvSubuh)
        val dzuhur: TextView = view.findViewById(R.id.tvDzuhur)
        val ashar: TextView = view.findViewById(R.id.tvAshar)
        val maghrib: TextView = view.findViewById(R.id.tvMaghrib)
        val isya: TextView = view.findViewById(R.id.tvIsya)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        Log.d("BulananAdapter", "onCreateViewHolder called")
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_jadwal_bulanan, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        Log.d("BulananAdapter", "getItemCount: ${items.size}")
        return items.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = items[position]
        Log.d("BulananAdapter", "Binding position $position: ${item.tanggal}")

        holder.tanggal.text = item.tanggal
        holder.subuh.text = "Subuh: ${item.subuh}"
        holder.dzuhur.text = "Dzuhur: ${item.dzuhur}"
        holder.ashar.text = "Ashar: ${item.ashar}"
        holder.maghrib.text = "Maghrib: ${item.maghrib}"
        holder.isya.text = "Isya: ${item.isya}"
    }
}