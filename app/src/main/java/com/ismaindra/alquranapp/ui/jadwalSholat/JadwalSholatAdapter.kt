package com.ismaindra.alquranapp.ui.jadwalSholat

import android.content.res.ColorStateList
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.graphics.ColorUtils
import androidx.core.graphics.drawable.DrawableCompat
import androidx.recyclerview.widget.RecyclerView
import com.ismaindra.alquranapp.R
import com.ismaindra.alquranapp.data.model.Jadwal

class JadwalAdapter : RecyclerView.Adapter<JadwalAdapter.ViewHolder>() {

    private data class PrayerItem(
        val title: String,
        val time: String,
        val description: String,
        val iconRes: Int,
        val colorRes: Int
    )

    private val items = mutableListOf<PrayerItem>()

    fun submit(jadwal: Jadwal) {
        items.clear()
        items.addAll(
            listOf(
                PrayerItem(
                    title = "Imsak",
                    time = jadwal.imsak,
                    description = "Menahan diri sebelum Subuh",
                    iconRes = R.drawable.ic_imsak,
                    colorRes = R.color.imsak_accent
                ),
                PrayerItem(
                    title = "Subuh",
                    time = jadwal.subuh,
                    description = "Waktu fajar menuju sholat Subuh",
                    iconRes = R.drawable.ic_subuh,
                    colorRes = R.color.subuh_accent
                ),
                PrayerItem(
                    title = "Dhuha",
                    time = jadwal.dhuha,
                    description = "Waktu cahaya pagi untuk berdoa",
                    iconRes = R.drawable.ic_dhuha,
                    colorRes = R.color.dhuha_accent
                ),
                PrayerItem(
                    title = "Dzuhur",
                    time = jadwal.dzuhur,
                    description = "Istirahat sejenak untuk sholat",
                    iconRes = R.drawable.ic_dzuhur,
                    colorRes = R.color.dzuhur_accent
                ),
                PrayerItem(
                    title = "Ashar",
                    time = jadwal.ashar,
                    description = "Pengingat di tengah aktivitas",
                    iconRes = R.drawable.ic_ashar,
                    colorRes = R.color.ashar_accent
                ),
                PrayerItem(
                    title = "Maghrib",
                    time = jadwal.maghrib,
                    description = "Syukuri senja dengan sholat",
                    iconRes = R.drawable.ic_maghrib,
                    colorRes = R.color.maghrib_accent
                ),
                PrayerItem(
                    title = "Isya",
                    time = jadwal.isya,
                    description = "Menutup malam dengan tenang",
                    iconRes = R.drawable.ic_isya,
                    colorRes = R.color.isya_accent
                )
            )
        )
        this.notifyDataSetChanged()
    }

    class ViewHolder(val view: View) : RecyclerView.ViewHolder(view) {
        val tvNama: TextView = view.findViewById(R.id.tvNama)
        val tvWaktu: TextView = view.findViewById(R.id.tvWaktu)
        val tvKeterangan: TextView = view.findViewById(R.id.tvKeterangan)
        val tvStatusWaktu: TextView = view.findViewById(R.id.tvStatusWaktu)
        val ivPrayerIcon: ImageView = view.findViewById(R.id.ivPrayerIcon)
        val containerIcon: View = view.findViewById(R.id.containerIcon)
        val stripAccent: View = view.findViewById(R.id.stripAccent)
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_jadwal, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = items[position]
        val context = holder.view.context
        val accentColor = ContextCompat.getColor(context, item.colorRes)
        val iconTint = ColorUtils.blendARGB(accentColor, 0xFF2E2E2E.toInt(), 0.35f)

        holder.tvNama.text = item.title
        holder.tvWaktu.text = item.time
        holder.tvKeterangan.text = item.description
        holder.tvStatusWaktu.text = "WIB"

        holder.ivPrayerIcon.setImageResource(item.iconRes)
        holder.ivPrayerIcon.imageTintList = ColorStateList.valueOf(iconTint)

        val iconBackground = DrawableCompat.wrap(holder.containerIcon.background.mutate())
        DrawableCompat.setTint(iconBackground, accentColor)
        holder.containerIcon.background = iconBackground

        val stripBackground = DrawableCompat.wrap(holder.stripAccent.background.mutate())
        DrawableCompat.setTint(stripBackground, accentColor)
        holder.stripAccent.background = stripBackground
    }

    override fun getItemCount() = items.size
}
