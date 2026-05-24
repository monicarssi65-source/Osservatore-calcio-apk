package com.osservatore.calcio.ui

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.*
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.google.gson.Gson
import com.osservatore.calcio.R
import com.osservatore.calcio.data.Match
import com.osservatore.calcio.databinding.ActivityMatchFormBinding
import com.osservatore.calcio.viewmodel.MainViewModel
import java.text.SimpleDateFormat
import java.util.*

class MatchFormActivity : AppCompatActivity() {
    private lateinit var b: ActivityMatchFormBinding
    private val vm: MainViewModel by viewModels()

    data class Marcatore(var squadra: String, var numero: String, var giocatore: String, var minuto: String)
    data class Sostituzione(var squadra: String, var out: String, var inp: String, var minuto: String)
    data class Giocatore(var numero: String, var nome: String, var titolare: Boolean)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        b = ActivityMatchFormBinding.inflate(layoutInflater)
        setContentView(b.root)
        setSupportActionBar(b.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = "⚽ Scheda Partita"

        b.etData.setText(SimpleDateFormat("yyyy-MM-dd", Locale.ITALY).format(Date()))

        // Aggiungi 11 titolari casa per default
        repeat(11) { addGiocatoreRow(b.containerFormazioneCasa, true, it + 1) }
        repeat(5) { addGiocatoreRow(b.containerFormazioneCasa, false, it + 12) }
        repeat(11) { addGiocatoreRow(b.containerFormazioneOspite, true, it + 1) }
        repeat(5) { addGiocatoreRow(b.containerFormazioneOspite, false, it + 12) }

        b.btnAddGiocatoreCasa.setOnClickListener {
            addGiocatoreRow(b.containerFormazioneCasa, false, 0)
        }
        b.btnAddGiocatoreOspite.setOnClickListener {
            addGiocatoreRow(b.containerFormazioneOspite, false, 0)
        }
        b.btnAddMarcatore.setOnClickListener { addMarcatoreRow() }
        b.btnAddSostituzione.setOnClickListener { addSostituzioneRow() }
        b.btnLavagnaCasa.setOnClickListener {
            startActivity(Intent(this, TacticalBoardActivity::class.java))
        }
        b.btnLavagnaOspite.setOnClickListener {
            startActivity(Intent(this, TacticalBoardActivity::class.java))
        }
        b.btnSave.setOnClickListener { save() }
        b.btnReset.setOnClickListener { resetForm() }
    }

    private fun addGiocatoreRow(container: LinearLayout, titolare: Boolean, numero: Int) {
        val row = LayoutInflater.from(this).inflate(R.layout.row_giocatore, container, false)
        val etNumero = row.findViewById<EditText>(R.id.etNumero)
        val etNome = row.findViewById<EditText>(R.id.etNome)
        val tvTipo = row.findViewById<TextView>(R.id.tvTipo)
        if (numero > 0) etNumero.setText(numero.toString())
        tvTipo.text = if (titolare) "T" else "R"
        tvTipo.setBackgroundColor(if (titolare) getColor(R.color.green_dark) else getColor(R.color.gray))
        row.findViewById<ImageButton>(R.id.btnRemove).setOnClickListener {
            container.removeView(row)
        }
        container.addView(row)
    }

    private fun addMarcatoreRow() {
        val row = LayoutInflater.from(this).inflate(R.layout.row_marcatore, b.containerMarcatori, false)
        val sp = row.findViewById<Spinner>(R.id.spSquadra)
        sp.adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item,
            listOf("Casa", "Ospite")).also { it.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item) }
        b.containerMarcatori.addView(row)
        row.findViewById<ImageButton>(R.id.btnRemove).setOnClickListener {
            b.containerMarcatori.removeView(row)
        }
    }

    private fun addSostituzioneRow() {
        val row = LayoutInflater.from(this).inflate(R.layout.row_sostituzione, b.containerSostituzioni, false)
        val sp = row.findViewById<Spinner>(R.id.spSquadra)
        sp.adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item,
            listOf("Casa", "Ospite")).also { it.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item) }
        b.containerSostituzioni.addView(row)
        row.findViewById<ImageButton>(R.id.btnRemove).setOnClickListener {
            b.containerSostituzioni.removeView(row)
        }
    }

    private fun collectGiocatori(container: LinearLayout): List<Giocatore> {
        val list = mutableListOf<Giocatore>()
        for (i in 0 until container.childCount) {
            val row = container.getChildAt(i)
            val numero = row.findViewById<EditText>(R.id.etNumero)?.text?.toString() ?: ""
            val nome = row.findViewById<EditText>(R.id.etNome)?.text?.toString() ?: ""
            val tipo = row.findViewById<TextView>(R.id.tvTipo)?.text?.toString() ?: "R"
            if (nome.isNotEmpty() || numero.isNotEmpty())
                list.add(Giocatore(numero, nome, tipo == "T"))
        }
        return list
    }

    private fun collectMarcatori(): List<Marcatore> {
        val list = mutableListOf<Marcatore>()
        for (i in 0 until b.containerMarcatori.childCount) {
            val row = b.containerMarcatori.getChildAt(i)
            val squadra = row.findViewById<Spinner>(R.id.spSquadra)?.selectedItem?.toString() ?: "Casa"
            val numero = row.findViewById<EditText>(R.id.etNumero)?.text?.toString() ?: ""
            val giocatore = row.findViewById<EditText>(R.id.etGiocatore)?.text?.toString() ?: ""
            val minuto = row.findViewById<EditText>(R.id.etMinuto)?.text?.toString() ?: ""
            if (giocatore.isNotEmpty()) list.add(Marcatore(squadra, numero, giocatore, minuto))
        }
        return list
    }

    private fun collectSostituzioni(): List<Sostituzione> {
        val list = mutableListOf<Sostituzione>()
        for (i in 0 until b.containerSostituzioni.childCount) {
            val row = b.containerSostituzioni.getChildAt(i)
            val squadra = row.findViewById<Spinner>(R.id.spSquadra)?.selectedItem?.toString() ?: "Casa"
            val out = row.findViewById<EditText>(R.id.etOut)?.text?.toString() ?: ""
            val inp = row.findViewById<EditText>(R.id.etIn)?.text?.toString() ?: ""
            val minuto = row.findViewById<EditText>(R.id.etMinuto)?.text?.toString() ?: ""
            if (out.isNotEmpty() || inp.isNotEmpty()) list.add(Sostituzione(squadra, out, inp, minuto))
        }
        return list
    }

    private fun save() {
        val casa = b.etCasa.text.toString().trim()
        val ospite = b.etOspite.text.toString().trim()
        if (casa.isEmpty() || ospite.isEmpty()) {
            Toast.makeText(this, "⚠️ Inserisci le squadre!", Toast.LENGTH_SHORT).show()
            return
        }
        val gson = Gson()
        val match = Match(
            id = UUID.randomUUID().toString(),
            data = b.etData.text.toString().trim(),
            casa = casa, ospite = ospite,
            risultato = b.etRisultato.text.toString().trim(),
            competizione = b.etCompetizione.text.toString().trim(),
            stadio = b.etStadio.text.toString().trim(),
            arbitro = b.etArbitro.text.toString().trim(),
            allCasa = b.etAllCasa.text.toString().trim(),
            allOspite = b.etAllOspite.text.toString().trim(),
            angCasa = b.etAngCasa.text.toString().toIntOrNull() ?: 0,
            angOspite = b.etAngOspite.text.toString().toIntOrNull() ?: 0,
            cronaca = b.etCronaca.text.toString().trim(),
            marcatori = gson.toJson(collectMarcatori()),
            sostituzioni = gson.toJson(collectSostituzioni()),
            formazioneCasa = gson.toJson(collectGiocatori(b.containerFormazioneCasa)),
            formazioneOspite = gson.toJson(collectGiocatori(b.containerFormazioneOspite))
        )
        vm.saveMatch(match)
        Toast.makeText(this, "✅ Partita $casa vs $ospite salvata!", Toast.LENGTH_SHORT).show()
        finish()
    }

    private fun resetForm() {
        b.etCasa.setText(""); b.etOspite.setText(""); b.etRisultato.setText("")
        b.etCompetizione.setText(""); b.etStadio.setText(""); b.etArbitro.setText("")
        b.etAllCasa.setText(""); b.etAllOspite.setText("")
        b.etAngCasa.setText("0"); b.etAngOspite.setText("0")
        b.etCronaca.setText("")
        b.containerMarcatori.removeAllViews()
        b.containerSostituzioni.removeAllViews()
        b.containerFormazioneCasa.removeAllViews()
        b.containerFormazioneOspite.removeAllViews()
        b.etData.setText(SimpleDateFormat("yyyy-MM-dd", Locale.ITALY).format(Date()))
        repeat(11) { addGiocatoreRow(b.containerFormazioneCasa, true, it + 1) }
        repeat(5) { addGiocatoreRow(b.containerFormazioneCasa, false, it + 12) }
        repeat(11) { addGiocatoreRow(b.containerFormazioneOspite, true, it + 1) }
        repeat(5) { addGiocatoreRow(b.containerFormazioneOspite, false, it + 12) }
    }

    override fun onSupportNavigateUp(): Boolean { finish(); return true }
}
