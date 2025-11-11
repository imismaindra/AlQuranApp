package com.ismaindra.alquranapp.ui.detail

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.ismaindra.alquranapp.data.api.RetrofitClient
import com.ismaindra.alquranapp.data.model.Ayat
import com.ismaindra.alquranapp.databinding.ActivitySurahDetailBinding
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.launch

class SurahDetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySurahDetailBinding
    private lateinit var ayahAdapter: AyahAdapter

    private var surahNumber: Int = 0
    private var surahName: String = ""

    companion object {
        const val EXTRA_SURAH_NUMBER = "extra_surah_number"
        const val EXTRA_SURAH_NAME = "extra_surah_name"
        const val EXTRA_SURAH_REVELATION = "extra_surah_revelation"
        const val EXTRA_AYAH_COUNT = "extra_ayah_count"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySurahDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Ambil data dari Intent
        getIntentData()
        setupToolbar()
        setupRecyclerView()
        loadSurahDetail()
    }

    private fun getIntentData() {
        surahNumber = intent.getIntExtra(EXTRA_SURAH_NUMBER, 1)
        surahName = intent.getStringExtra(EXTRA_SURAH_NAME) ?: ""
        val revelation = intent.getStringExtra(EXTRA_SURAH_REVELATION) ?: ""
        val ayahCount = intent.getIntExtra(EXTRA_AYAH_COUNT, 0)

        // Set data ke views
        binding.apply {
            tvSurahName.text = surahName
            tvSurahNameArabic.text = getArabicName(surahNumber)
            tvRevelation.text = revelation
            tvAyahCount.text = "$ayahCount Ayat"

            // Set collapsing toolbar title
            collapsingToolbar.title = surahName

            // Show bismillah card except for surah 1 and 9
            cardBismillah.visibility = if (surahNumber != 1 && surahNumber != 9) {
                View.VISIBLE
            } else {
                View.GONE
            }
        }
    }

    private fun setupToolbar() {
        setSupportActionBar(binding.toolbar)
        supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(true)
            setDisplayShowTitleEnabled(false)
        }

        binding.toolbar.setNavigationOnClickListener {
            onBackPressed()
        }
    }

    private fun setupRecyclerView() {
        ayahAdapter = AyahAdapter()

        binding.rvAyah.apply {
            layoutManager = LinearLayoutManager(this@SurahDetailActivity)
            adapter = ayahAdapter
            setHasFixedSize(false)
        }
    }

    private fun loadSurahDetail() {
        showLoading(true)

        lifecycleScope.launch {
            try {
                val response = RetrofitClient.apiService.getSurahDetail(surahNumber)

                // ✅ PERBAIKAN: Gunakan body() dengan ()
                if (response.isSuccessful && response.body() != null) {
                    val surahDetail = response.body()!!

                    // Update UI dengan info dari response (opsional)
                    binding.apply {
                        tvSurahName.text = surahDetail.namaLatin
                        tvSurahNameArabic.text = surahDetail.nama
                        tvRevelation.text = surahDetail.tempatTurun
                        tvAyahCount.text = "${surahDetail.jumlahAyat} Ayat"
                        collapsingToolbar.title = surahDetail.namaLatin
                    }

                    // Submit list ayat ke adapter
                    ayahAdapter.submitList(surahDetail.ayat)
                    showLoading(false)
                } else {
                    showLoading(false)
                    showError("Gagal memuat ayat: ${response.message()}")
                }

            } catch (e: Exception) {
                showLoading(false)
                showError("Periksa koneksi internet Anda: ${e.localizedMessage}")
                e.printStackTrace()
            }
        }
    }

    private fun showLoading(isLoading: Boolean) {
        binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
    }

    private fun showError(message: String) {
        Snackbar.make(binding.root, message, Snackbar.LENGTH_LONG)
            .setAction("Coba Lagi") {
                loadSurahDetail()
            }
            .show()
    }

    private fun getArabicName(number: Int): String {
        return when (number) {
            1 -> "الفاتحة"
            2 -> "البقرة"
            3 -> "آل عمران"
            4 -> "النساء"
            5 -> "المائدة"
            6 -> "الأنعام"
            7 -> "الأعراف"
            8 -> "الأنفال"
            9 -> "التوبة"
            10 -> "يونس"
            11 -> "هود"
            12 -> "يوسف"
            13 -> "الرعد"
            14 -> "ابراهيم"
            15 -> "الحجر"
            16 -> "النحل"
            17 -> "الإسراء"
            18 -> "الكهف"
            19 -> "مريم"
            20 -> "طه"
            21 -> "الأنبياء"
            22 -> "الحج"
            23 -> "المؤمنون"
            24 -> "النور"
            25 -> "الفرقان"
            26 -> "الشعراء"
            27 -> "النمل"
            28 -> "القصص"
            29 -> "العنكبوت"
            30 -> "الروم"
            31 -> "لقمان"
            32 -> "السجدة"
            33 -> "الأحزاب"
            34 -> "سبإ"
            35 -> "فاطر"
            36 -> "يس"
            37 -> "الصافات"
            38 -> "ص"
            39 -> "الزمر"
            40 -> "غافر"
            41 -> "فصلت"
            42 -> "الشورى"
            43 -> "الزخرف"
            44 -> "الدخان"
            45 -> "الجاثية"
            46 -> "الأحقاف"
            47 -> "محمد"
            48 -> "الفتح"
            49 -> "الحجرات"
            50 -> "ق"
            51 -> "الذاريات"
            52 -> "الطور"
            53 -> "النجم"
            54 -> "القمر"
            55 -> "الرحمن"
            56 -> "الواقعة"
            57 -> "الحديد"
            58 -> "المجادلة"
            59 -> "الحشر"
            60 -> "الممتحنة"
            61 -> "الصف"
            62 -> "الجمعة"
            63 -> "المنافقون"
            64 -> "التغابن"
            65 -> "الطلاق"
            66 -> "التحريم"
            67 -> "الملك"
            68 -> "القلم"
            69 -> "الحاقة"
            70 -> "المعارج"
            71 -> "نوح"
            72 -> "الجن"
            73 -> "المزمل"
            74 -> "المدثر"
            75 -> "القيامة"
            76 -> "الانسان"
            77 -> "المرسلات"
            78 -> "النبإ"
            79 -> "النازعات"
            80 -> "عبس"
            81 -> "التكوير"
            82 -> "الانفطار"
            83 -> "المطففين"
            84 -> "الانشقاق"
            85 -> "البروج"
            86 -> "الطارق"
            87 -> "الأعلى"
            88 -> "الغاشية"
            89 -> "الفجر"
            90 -> "البلد"
            91 -> "الشمس"
            92 -> "الليل"
            93 -> "الضحى"
            94 -> "الشرح"
            95 -> "التين"
            96 -> "العلق"
            97 -> "القدر"
            98 -> "البينة"
            99 -> "الزلزلة"
            100 -> "العاديات"
            101 -> "القارعة"
            102 -> "التكاثر"
            103 -> "العصر"
            104 -> "الهمزة"
            105 -> "الفيل"
            106 -> "قريش"
            107 -> "الماعون"
            108 -> "الكوثر"
            109 -> "الكافرون"
            110 -> "النصر"
            111 -> "المسد"
            112 -> "الإخلاص"
            113 -> "الفلق"
            114 -> "الناس"
            else -> "سورة $number"
        }
    }
}