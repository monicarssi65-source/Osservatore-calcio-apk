package com.osservatore.calcio.ui

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.*
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.google.gson.Gson
import com.osservatore.calcio.R
import com.osservatore.calcio.data.EvalCriteria
import com.osservatore.calcio.data.Team
import com.osservatore.calcio.databinding.ActivityTeamFormBinding
import com.osservatore.calcio.viewmodel.MainViewModel
import java.util.*

class TeamFormActivity : AppCompatActivity() {
    private lateinit var b: ActivityTeamFormBinding
    private val vm: MainViewModel by viewModels()

    data class Giocatore(var numero: String, var nome: String, var titolare: Boolean)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        b = ActivityTeamFormBinding.inflate(layoutInflater)
        setContentView(b.root)
        setSupportActionBar(b.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = "🏟️ Scheda Squadra"

        fun spinnerAdapter(items: List<String>) =
            ArrayAdapter(this, android.R.layout.simple_spinner_item, items).also {
                it.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            }

        b.spCampionato.adapter = spinnerAdapter(EvalCriteria.CAMPIONATI)
        b.spModulo.adapter = spinnerAdapter(EvalCriteria.MODULI)
        b.spTipoGioco.adapter = spinnerAdapter(listOf("Pressing alto", "Pressing basso", "Possesso palla", "Contropiede", "Misto"))
        b.spPassaggi.adapter = spinnerAdapter(listOf("Corti", "Lunghi", "Misti"))
        b.spDifesa.adapter = spinnerAdapter(listOf("4 in linea", "3 in linea", "Zona", "Uomo a uomo", "Mista"))
        b.spAttacco.adapter = spinnerAdapter(listOf("2 punte", "1 punta", "Falso 9", "3 attaccanti"))

        // Pre-popola 11 titolari + 5 riserve
        repeat(11) { addGiocatoreRow(true, it + 1) }
        repeat(5) { addGiocatoreRow(false, it + 12) }

        b.btnAddGiocatore.setOnClickListener { addGiocatoreRow(false, 0) }
        b.btnLavagna.setOnClickListener {
            startActivity(Intent(this, TacticalBoardActivity::class.java))
        }
        b.btnReset.setOnClickListener { resetForm() }
        b.btnSave.setOnClickListener { save() }
    }

    private fun addGiocatoreRow(titolare: Boolean, numero: Int) {
        val row = LayoutInflater.from(this).inflate(R.layout.row_giocatore, b.containerFormazione, false)
        val etNumero = row.findViewById<EditText>(R.id.etNumero)
        val tvTipo = row.findViewById<TextView>(R.id.tvTipo)
        if (numero > 0) etNumero.setText(numero.toString())
        tvTipo.text = if (titolare) "T" else "R"
        tvTipo.setBackgroundColor(if (titolare) getColor(R.color.green_dark) else getColor(R.color.gray))
        row.findViewById<ImageButton>(R.id.btnRemove).setOnClickListener {
            b.containerFormazione.removeView(row)
        }
        b.containerFormazione.addView(row)
    }

    private fun collectGiocatori(): List<Giocatore> {
        val list = mutableListOf<Giocatore>()
        for (i in 0 until b.containerFormazione.childCount) {
            val row = b.containerFormazione.getChildAt(i)
            val numero = row.findViewById<EditText>(R.id.etNumero)?.text?.toString() ?: ""
            val nome = row.findViewById<EditText>(R.id.etNome)?.text?.toString() ?: ""
            val tipo = row.findViewById<TextView>(R.id.tvTipo)?.text?.toString() ?: "R"
            if (nome.isNotEmpty() || numero.isNotEmpty())
                list.add(Giocatore(numero, nome, tipo == "T"))
        }
        return list
    }

    private fun save() {
        val nome = b.etNome.text.toString().trim()
        if (nome.isEmpty()) {
            Toast.makeText(this, "⚠️ Inserisci il nome della squadra!", Toast.LENGTH_SHORT).show()
            return
        }
        val team = Team(
            id = UUID.randomUUID().toString(),
            nome = nome,
            campionato = b.spCampionato.selectedItem.toString(),
            allenatore = b.etAllenatore.text.toString().trim(),
            modulo = b.spModulo.selectedItem.toString(),
            tipoGioco = b.spTipoGioco.selectedItem.toString(),
            passaggi = b.spPassaggi.selectedItem.toString(),
            difesa = b.spDifesa.selectedItem.toString(),
            attacco = b.spAttacco.selectedItem.toString(),
            forza = b.etForza.text.toString().trim(),
            criticita = b.etCriticita.text.toString().trim(),
            note = b.etNote.text.toString().trim(),
            formazione = Gson().toJson(collectGiocatori())
        )
        vm.saveTeam(team)
        Toast.makeText(this, "✅ Squadra $nome salvata!", Toast.LENGTH_SHORT).show()
        finish()
    }

    private fun resetForm() {
        b.etNome.setText("")
        b.etAllenatore.setText("")
        b.etForza.setText("")
        b.etCriticita.setText("")
        b.etNote.setText("")
        b.containerFormazione.removeAllViews()
        repeat(11) { addGiocatoreRow(true, it + 1) }
        repeat(5) { addGiocatoreRow(false, it + 12) }
    }

    override fun onSupportNavigateUp(): Boolean { finish(); return true }
}
