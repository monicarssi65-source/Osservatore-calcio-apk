package com.osservatore.calcio.ui

import android.os.Bundle
import android.widget.*
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.osservatore.calcio.R
import com.osservatore.calcio.data.EvalCriteria
import com.osservatore.calcio.data.Player
import com.osservatore.calcio.databinding.ActivityPlayerFormBinding
import com.osservatore.calcio.viewmodel.MainViewModel
import java.util.*

class PlayerFormActivity : AppCompatActivity() {
    private lateinit var b: ActivityPlayerFormBinding
    private val vm: MainViewModel by viewModels()
    private var editPlayer: Player? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        b = ActivityPlayerFormBinding.inflate(layoutInflater)
        setContentView(b.root)
        setSupportActionBar(b.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = "📋 Scheda Giocatore"

        setupSpinners()
        setupEvalSections()

        // Check if editing
        val editId = intent.getStringExtra("EDIT_ID")
        if (editId != null) loadForEdit(editId)

        b.btnSave.setOnClickListener { save() }
        b.btnReset.setOnClickListener { resetForm() }
    }

    private fun setupSpinners() {
        fun spinnerAdapter(items: List<String>) =
            ArrayAdapter(this, android.R.layout.simple_spinner_item, items).also {
                it.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            }

        b.spRuolo.adapter = spinnerAdapter(EvalCriteria.RUOLI)
        b.spNazionalita.adapter = spinnerAdapter(EvalCriteria.NAZIONALITA.values.toList())
        b.spPiede.adapter = spinnerAdapter(listOf("destro", "sinistro", "ambidestro"))
        b.spStato.adapter = spinnerAdapter(listOf("svincolato", "sotto contratto", "in prova", "ritirato"))
        b.spStagione.adapter = spinnerAdapter(listOf("2024/2025", "2023/2024", "2025/2026"))
        b.spGravita.adapter = spinnerAdapter(listOf("nessuno", "lieve", "medio", "grave"))
        b.spMorfologia.adapter = spinnerAdapter(listOf("normotipo", "longilineo", "brevilineo"))
    }

    private fun setupEvalSections() {
        val sections = listOf(
            Triple(b.evalTecnica, "⚙️ TECNICA", EvalCriteria.TECNICA) to "tec",
            Triple(b.evalTattica, "🧠 TATTICA", EvalCriteria.TATTICA) to "tat",
            Triple(b.evalCoord, "🔄 COORDINAZIONE", EvalCriteria.COORDINAZIONE) to "cor",
            Triple(b.evalFisiche, "💪 FISICHE", EvalCriteria.FISICHE) to "fis",
            Triple(b.evalMentali, "🎯 MENTALI", EvalCriteria.MENTALI) to "men",
            Triple(b.evalSocial, "🤝 SOCIALITÀ", EvalCriteria.SOCIAL) to "soc"
        )
        sections.forEach { (triple, prefix) ->
            val (view, title, criteria) = triple
            view.setup(title, criteria, prefix = prefix)
            view.onValueChanged = {
                view.updateTotal(prefix)
                updateOverallScore()
            }
            view.updateTotal(prefix)
        }
        updateOverallScore()
    }

    private fun updateOverallScore() {
        val avg = listOf(
            b.evalTecnica.getAverage(),
            b.evalTattica.getAverage(),
            b.evalCoord.getAverage(),
            b.evalFisiche.getAverage(),
            b.evalMentali.getAverage(),
            b.evalSocial.getAverage()
        ).average().toFloat()

        b.tvScoreTecnica.text = String.format("%.2f", b.evalTecnica.getAverage())
        b.tvScoreTattica.text = String.format("%.2f", b.evalTattica.getAverage())
        b.tvScoreCoord.text = String.format("%.2f", b.evalCoord.getAverage())
        b.tvScoreFisiche.text = String.format("%.2f", b.evalFisiche.getAverage())
        b.tvScoreMentali.text = String.format("%.2f", b.evalMentali.getAverage())
        b.tvScoreSocial.text = String.format("%.2f", b.evalSocial.getAverage())
        b.tvScoreComplessivo.text = String.format("%.2f", avg)
    }

    private fun save() {
        val cognome = b.etCognome.text.toString().trim()
        val nome = b.etNome.text.toString().trim()
        val ruolo = b.spRuolo.selectedItem.toString()

        if (cognome.isEmpty() || nome.isEmpty()) {
            Toast.makeText(this, "⚠️ Inserisci COGNOME e NOME!", Toast.LENGTH_SHORT).show()
            return
        }
        if (ruolo.isEmpty()) {
            Toast.makeText(this, "⚠️ Seleziona il RUOLO!", Toast.LENGTH_SHORT).show()
            return
        }

        // Collect radio values
        val radioVals = mutableMapOf<String, Int>()
        radioVals.putAll(b.evalTecnica.getValues("tec"))
        radioVals.putAll(b.evalTattica.getValues("tat"))
        radioVals.putAll(b.evalCoord.getValues("cor"))
        radioVals.putAll(b.evalFisiche.getValues("fis"))
        radioVals.putAll(b.evalMentali.getValues("men"))
        radioVals.putAll(b.evalSocial.getValues("soc"))

        val nazKeys = EvalCriteria.NAZIONALITA.keys.toList()
        val nazKey = nazKeys.getOrElse(b.spNazionalita.selectedItemPosition) { "IT" }

        val player = Player(
            id = editPlayer?.id ?: UUID.randomUUID().toString(),
            createdAt = editPlayer?.createdAt ?: System.currentTimeMillis(),
            cognome = cognome, nome = nome,
            dataNascita = b.etDataNascita.text.toString().trim(),
            luogoNascita = b.etLuogoNascita.text.toString().trim(),
            nazionalita = nazKey,
            ruolo = ruolo,
            piede = b.spPiede.selectedItem.toString(),
            stato = b.spStato.selectedItem.toString(),
            societa = b.etSocieta.text.toString().trim(),
            luogoRilevazione = b.etLuogoRil.text.toString().trim(),
            altezza = b.etAltezza.text.toString().trim(),
            peso = b.etPeso.text.toString().trim(),
            morfologia = b.spMorfologia.selectedItem.toString(),
            stagione = b.spStagione.selectedItem.toString(),
            presenze = b.etPresenze.text.toString().toIntOrNull() ?: 0,
            gol = b.etGol.text.toString().toIntOrNull() ?: 0,
            assist = b.etAssist.text.toString().toIntOrNull() ?: 0,
            minuti = b.etMinuti.text.toString().toIntOrNull() ?: 0,
            note = b.etNote.text.toString().trim(),
            tags = b.etTags.text.toString().trim(),
            infortunioTipo = b.etInfortunio.text.toString().trim(),
            infortunioGravita = b.spGravita.selectedItem.toString(),
            infortunioGiorni = b.etGiorniStop.text.toString().toIntOrNull() ?: 0,
            scoreTecnica = b.evalTecnica.getAverage(),
            scoreTattica = b.evalTattica.getAverage(),
            scoreCoordinazione = b.evalCoord.getAverage(),
            scoreFisiche = b.evalFisiche.getAverage(),
            scoreMentali = b.evalMentali.getAverage(),
            scoreSocialita = b.evalSocial.getAverage(),
            scoreComplessivo = b.tvScoreComplessivo.text.toString().toFloatOrNull() ?: 3f,
            radioValues = Gson().toJson(radioVals)
        )

        vm.savePlayer(player)
        val msg = if (editPlayer != null) "✅ $nome $cognome aggiornato!" else "✅ $nome $cognome salvato!"
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
        finish()
    }

    private fun loadForEdit(id: String) {
        vm.players.observe(this) { list ->
            val p = list.find { it.id == id } ?: return@observe
            editPlayer = p
            supportActionBar?.title = "✏️ Modifica Giocatore"

            b.etCognome.setText(p.cognome)
            b.etNome.setText(p.nome)
            b.etDataNascita.setText(p.dataNascita)
            b.etLuogoNascita.setText(p.luogoNascita)
            b.etSocieta.setText(p.societa)
            b.etLuogoRil.setText(p.luogoRilevazione)
            b.etAltezza.setText(p.altezza)
            b.etPeso.setText(p.peso)
            b.etNote.setText(p.note)
            b.etTags.setText(p.tags)
            b.etInfortunio.setText(p.infortunioTipo)
            b.etPresenze.setText(p.presenze.toString())
            b.etGol.setText(p.gol.toString())
            b.etAssist.setText(p.assist.toString())
            b.etMinuti.setText(p.minuti.toString())
            b.etGiorniStop.setText(p.infortunioGiorni.toString())

            // Restore spinners
            val ruoloIdx = EvalCriteria.RUOLI.indexOf(p.ruolo)
            if (ruoloIdx >= 0) b.spRuolo.setSelection(ruoloIdx)

            val nazKeys = EvalCriteria.NAZIONALITA.keys.toList()
            val nazIdx = nazKeys.indexOf(p.nazionalita)
            if (nazIdx >= 0) b.spNazionalita.setSelection(nazIdx)

            // Restore radio values
            if (p.radioValues.isNotEmpty() && p.radioValues != "{}") {
                val type = object : TypeToken<Map<String, Int>>() {}.type
                val savedVals: Map<String, Int> = Gson().fromJson(p.radioValues, type)
                b.evalTecnica.setup("⚙️ TECNICA", EvalCriteria.TECNICA, savedVals, "tec")
                b.evalTattica.setup("🧠 TATTICA", EvalCriteria.TATTICA, savedVals, "tat")
                b.evalCoord.setup("🔄 COORDINAZIONE", EvalCriteria.COORDINAZIONE, savedVals, "cor")
                b.evalFisiche.setup("💪 FISICHE", EvalCriteria.FISICHE, savedVals, "fis")
                b.evalMentali.setup("🎯 MENTALI", EvalCriteria.MENTALI, savedVals, "men")
                b.evalSocial.setup("🤝 SOCIALITÀ", EvalCriteria.SOCIAL, savedVals, "soc")

                listOf(
                    b.evalTecnica to "tec", b.evalTattica to "tat", b.evalCoord to "cor",
                    b.evalFisiche to "fis", b.evalMentali to "men", b.evalSocial to "soc"
                ).forEach { (view, prefix) ->
                    view.onValueChanged = { view.updateTotal(prefix); updateOverallScore() }
                    view.updateTotal(prefix)
                }
                updateOverallScore()
            }
        }
    }

    private fun resetForm() {
        b.etCognome.setText("")
        b.etNome.setText("")
        b.etDataNascita.setText("")
        b.etLuogoNascita.setText("")
        b.etSocieta.setText("")
        b.etLuogoRil.setText("")
        b.etAltezza.setText("")
        b.etPeso.setText("")
        b.etNote.setText("")
        b.etTags.setText("")
        b.etInfortunio.setText("")
        b.etPresenze.setText("0")
        b.etGol.setText("0")
        b.etAssist.setText("0")
        b.etMinuti.setText("0")
        b.etGiorniStop.setText("0")
        b.spRuolo.setSelection(0)
        setupEvalSections()
        editPlayer = null
    }

    override fun onSupportNavigateUp(): Boolean { finish(); return true }
}
