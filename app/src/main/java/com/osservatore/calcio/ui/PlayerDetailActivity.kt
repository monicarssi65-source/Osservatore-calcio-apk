package com.osservatore.calcio.ui

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.osservatore.calcio.databinding.ActivityPlayerDetailBinding
import com.osservatore.calcio.utils.ExportHelper
import com.osservatore.calcio.viewmodel.MainViewModel
import kotlinx.coroutines.launch

class PlayerDetailActivity : AppCompatActivity() {
    private lateinit var b: ActivityPlayerDetailBinding
    private val vm: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        b = ActivityPlayerDetailBinding.inflate(layoutInflater)
        setContentView(b.root)
        setSupportActionBar(b.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        val id = intent.getStringExtra("ID") ?: return

        vm.players.observe(this) { list ->
            val p = list.find { it.id == id } ?: return@observe
            supportActionBar?.title = "${p.cognome} ${p.nome}"

            b.tvName.text = "${p.cognome} ${p.nome}"
            b.tvRuolo.text = p.ruolo
            b.tvSocieta.text = p.societa
            b.tvNazionalita.text = p.nazionalita
            b.tvDataNascita.text = p.dataNascita
            b.tvLuogoNascita.text = p.luogoNascita
            b.tvAltezzaPeso.text = "${p.altezza} cm / ${p.peso} kg"
            b.tvPiede.text = p.piede
            b.tvStato.text = p.stato
            b.tvStagione.text = p.stagione
            b.tvStats.text = "Presenze: ${p.presenze} • Gol: ${p.gol} • Assist: ${p.assist} • Minuti: ${p.minuti}"

            // Scores
            b.tvScoreTecnica.text = String.format("%.2f", p.scoreTecnica)
            b.tvScoreTattica.text = String.format("%.2f", p.scoreTattica)
            b.tvScoreCoord.text = String.format("%.2f", p.scoreCoordinazione)
            b.tvScoreFisiche.text = String.format("%.2f", p.scoreFisiche)
            b.tvScoreMentali.text = String.format("%.2f", p.scoreMentali)
            b.tvScoreSocial.text = String.format("%.2f", p.scoreSocialita)
            b.tvScoreComplessivo.text = String.format("%.2f", p.scoreComplessivo)

            if (p.note.isNotEmpty()) b.tvNote.text = p.note
            if (p.tags.isNotEmpty()) b.tvTags.text = p.tags

            b.btnPdf.setOnClickListener {
                lifecycleScope.launch {
                    val file = ExportHelper.generatePlayerPdf(this@PlayerDetailActivity, p)
                    ExportHelper.sharePdf(this@PlayerDetailActivity, file)
                }
            }
        }
    }

    override fun onSupportNavigateUp(): Boolean { finish(); return true }
}
