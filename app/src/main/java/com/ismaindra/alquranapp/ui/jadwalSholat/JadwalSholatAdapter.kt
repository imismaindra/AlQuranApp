package com.ismaindra.alquranapp.ui.jadwalSholat

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.card.MaterialCardView
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
        this.notifyDataSetChanged()
    }

    class ViewHolder(val view: View) : RecyclerView.ViewHolder(view){
        val tvNama: TextView = view.findViewById(R.id.tvNama)
        val tvWaktu: TextView =  view.findViewById(R.id.tvWaktu)
        val ivPrayerIcon: ImageView = view.findViewById(R.id.ivPrayerIcon)
        val colorStrip: View = view.findViewById(R.id.colorStrip)
        val iconBg: MaterialCardView = view.findViewById<ImageView>(R.id.ivPrayerIcon).parent as MaterialCardView

    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_jadwal, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val (nama, waktu) = items[position]
        holder.tvNama.text = nama
        holder.tvWaktu.text = waktu
        
        // Set icon dan warna berdasarkan nama sholat
        val (iconRes, primaryColor, bgColor) = when(nama.lowercase()) {
            "imsak" -> Triple(R.drawable.ic_fajar, "#FF6B6B", "#FFEBEE")
            "subuh" -> Triple(R.drawable.ic_fajar, "#FF6B6B", "#FFEBEE")
            "dhuha" -> Triple(R.drawable.ic_johor, "#FFA726", "#FFF3E0")
            "dzuhur" -> Triple(R.drawable.ic_johor, "#66BB6A", "#E8F5E9")
            "ashar" -> Triple(R.drawable.ic_asr, "#AB47BC", "#F3E5F5")
            "maghrib" -> Triple(R.drawable.ic_maghrib, "#FF7043", "#FFEBEE")
            "isya" -> Triple(R.drawable.ic_isya, "#29B6F6", "#E1F5FE")
            else -> Triple(R.drawable.ic_fajar, "#FF7A00", "#FFF3E0")
        }
        
        holder.ivPrayerIcon.setImageResource(iconRes)
        holder.ivPrayerIcon.setColorFilter(android.graphics.Color.parseColor(primaryColor))
        holder.colorStrip.setBackgroundColor(android.graphics.Color.parseColor(primaryColor))
        holder.iconBg.setCardBackgroundColor(android.graphics.Color.parseColor(bgColor))
    }

    override fun getItemCount() = items.size
}
