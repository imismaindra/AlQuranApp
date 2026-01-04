package com.ismaindra.alquranapp.ui.hadist

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.ismaindra.alquranapp.R
import com.ismaindra.alquranapp.data.model.Hadist

class HadistAdapter(
    private val hadistList: List<Hadist>,
    private val onItemClick: (Hadist) -> Unit
) : RecyclerView.Adapter<HadistAdapter.HadistViewHolder>() {

    inner class HadistViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val tvJenisHadist: TextView = itemView.findViewById(R.id.tv_jenis_hadist)
        private val tvNoHadist: TextView = itemView.findViewById(R.id.tv_no_hadist)
        private val tvJudulHadist: TextView = itemView.findViewById(R.id.tv_judul_hadist)
        private val tvArabHadist: TextView = itemView.findViewById(R.id.tv_arab_hadist)
        private val tvIndoHadist: TextView = itemView.findViewById(R.id.tv_indo_hadist)
        private val tvBacaSelengkapnya: TextView = itemView.findViewById(R.id.tv_baca_selengkapnya)

        fun bind(hadist: Hadist) {
            tvJenisHadist.text = hadist.jenis.nama
            tvNoHadist.text = "No. ${hadist.no}"
            tvJudulHadist.text = hadist.judul
            tvArabHadist.text = hadist.arab
            tvIndoHadist.text = hadist.indo

            itemView.setOnClickListener {
                onItemClick(hadist)
            }

            tvBacaSelengkapnya.setOnClickListener {
                onItemClick(hadist)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HadistViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_hadist, parent, false)
        return HadistViewHolder(view)
    }

    override fun onBindViewHolder(holder: HadistViewHolder, position: Int) {
        holder.bind(hadistList[position])
    }

    override fun getItemCount(): Int = hadistList.size
}