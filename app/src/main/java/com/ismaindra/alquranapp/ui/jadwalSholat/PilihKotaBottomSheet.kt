package com.ismaindra.alquranapp.ui.jadwalSholat

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.ismaindra.alquranapp.data.model.Kota
import com.ismaindra.alquranapp.R

class PilihKotaBottomSheet(
    private val kotaList: List<Kota>,
    private val onSelected: (Kota) -> Unit
) : BottomSheetDialogFragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(
            R.layout.bottom_sheet_kota,
            container,
            false
        )

        val rv = view.findViewById<RecyclerView>(R.id.rvKota)
        val adapter = KotaAdapter {
            onSelected(it)
            dismiss()
        }

        rv.layoutManager = LinearLayoutManager(requireContext())
        rv.adapter = adapter
        adapter.submit(kotaList)

        return view
    }
}
