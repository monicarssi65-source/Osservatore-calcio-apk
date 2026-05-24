package com.osservatore.calcio.ui

import android.content.Intent
import android.os.Bundle
import android.widget.*
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.osservatore.calcio.data.EvalCriteria
import com.osservatore.calcio.data.Team
import com.osservatore.calcio.databinding.ActivityTeamFormBinding
import com.osservatore.calcio.viewmodel.MainViewModel
import java.util.*

class TeamFormActivity : AppCompatActivity() {
    private lateinit var b: ActivityTeamFormBinding
    private val vm: MainViewModel by viewModels()

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

        b.btnLavagna.setOnClickListener {
            startActivity(Intent(this, TacticalBoardActivity::class.java))
        }
        b.btnReset.setOnClickListener { resetForm() }
        b.btnSave.setOnClickListener { save() }
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
            note = b.etNote.text.toString().trim()
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
    }

    override fun onSupportNavigateUp(): Boolean { finish(); return true }
}
