package com.osservatore.calcio.ui

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.osservatore.calcio.data.Match
import com.osservatore.calcio.data.Player
import com.osservatore.calcio.data.Team
import com.osservatore.calcio.databinding.ActivityDatabaseBinding
import com.osservatore.calcio.utils.ExportHelper
import com.osservatore.calcio.viewmodel.MainViewModel
import kotlinx.coroutines.launch

class DatabaseActivity : AppCompatActivity() {
    private lateinit var b: ActivityDatabaseBinding
    private val vm: MainViewModel by viewModels()
    private var currentTab = "players"
    private var allPlayers = listOf<Player>()
    private var allTeams = listOf<Team>()
    private var allMatches = listOf<Match>()

    private val playerAdapter = DbPlayerAdapter(
        onClick = { p -> startActivity(Intent(this, PlayerDetailActivity::class.java).putExtra("ID", p.id)) },
        onEdit = { p -> startActivity(Intent(this, PlayerFormActivity::class.java).putExtra("EDIT_ID", p.id)) },
        onDelete = { p -> confirmDelete("${p.nome} ${p.cognome}") { vm.deletePlayer(p) } }
    )

    private val teamAdapter = DbTeamAdapter(
        onDelete = { t -> confirmDelete(t.nome) { vm.deleteTeam(t) } }
    )

    private val matchAdapter = DbMatchAdapter(
        onDelete = { m -> confirmDelete("${m.casa} vs ${m.ospite}") { vm.deleteMatch(m) } }
    )

    private val importLauncher = registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        uri?.let { importFromJson(it) }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        b = ActivityDatabaseBinding.inflate(layoutInflater)
        setContentView(b.root)
        setSupportActionBar(b.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = "🗄️ Database"

        b.rv.layoutManager = LinearLayoutManager(this)

        // Tab buttons
        b.btnTabPlayers.setOnClickListener { showTab("players") }
        b.btnTabTeams.setOnClickListener { showTab("teams") }
        b.btnTabMatches.setOnClickListener { showTab("matches") }

        // Search
        b.etSearch.addTextChangedListener(object : android.text.TextWatcher {
            override fun afterTextChanged(s: android.text.Editable?) { filterCurrent(s.toString()) }
            override fun beforeTextChanged(s: CharSequence?, st: Int, c: Int, a: Int) {}
            override fun onTextChanged(s: CharSequence?, st: Int, c: Int, a: Int) {}
        })

        // Actions
        b.btnExport.setOnClickListener { exportJson() }
        b.btnImport.setOnClickListener { importLauncher.launch("application/json") }
        b.btnExportPdf.setOnClickListener { exportPdf() }
        b.btnDeleteAll.setOnClickListener { confirmDeleteAll() }

        // Observe data
        vm.players.observe(this) { list -> allPlayers = list; if (currentTab == "players") filterCurrent("") }
        vm.teams.observe(this) { list -> allTeams = list; if (currentTab == "teams") filterCurrent("") }
        vm.matches.observe(this) { list -> allMatches = list; if (currentTab == "matches") filterCurrent("") }

        showTab("players")
    }

    private fun showTab(tab: String) {
        currentTab = tab
        b.etSearch.setText("")
        val activeColor = getColor(com.osservatore.calcio.R.color.green_dark)
        val inactiveColor = getColor(com.osservatore.calcio.R.color.gray)
        b.btnTabPlayers.setTextColor(if (tab == "players") activeColor else inactiveColor)
        b.btnTabTeams.setTextColor(if (tab == "teams") activeColor else inactiveColor)
        b.btnTabMatches.setTextColor(if (tab == "matches") activeColor else inactiveColor)
        filterCurrent("")
    }

    private fun filterCurrent(query: String) {
        val q = query.lowercase()
        when (currentTab) {
            "players" -> {
                b.rv.adapter = playerAdapter
                val filtered = if (q.isEmpty()) allPlayers
                else allPlayers.filter {
                    it.nome.lowercase().contains(q) || it.cognome.lowercase().contains(q) ||
                    it.ruolo.lowercase().contains(q) || it.societa.lowercase().contains(q)
                }
                playerAdapter.submitList(filtered)
                b.tvEmpty.visibility = if (filtered.isEmpty()) View.VISIBLE else View.GONE
                b.tvEmpty.text = if (allPlayers.isEmpty()) "Nessun giocatore" else "Nessun risultato"
            }
            "teams" -> {
                b.rv.adapter = teamAdapter
                val filtered = if (q.isEmpty()) allTeams
                else allTeams.filter { it.nome.lowercase().contains(q) }
                teamAdapter.submitList(filtered)
                b.tvEmpty.visibility = if (filtered.isEmpty()) View.VISIBLE else View.GONE
            }
            "matches" -> {
                b.rv.adapter = matchAdapter
                val filtered = if (q.isEmpty()) allMatches
                else allMatches.filter {
                    it.casa.lowercase().contains(q) || it.ospite.lowercase().contains(q)
                }
                matchAdapter.submitList(filtered)
                b.tvEmpty.visibility = if (filtered.isEmpty()) View.VISIBLE else View.GONE
            }
        }
    }

    private fun confirmDelete(name: String, action: () -> Unit) {
        AlertDialog.Builder(this)
            .setTitle("Eliminare $name?")
            .setPositiveButton("Elimina") { _, _ -> action() }
            .setNegativeButton("Annulla", null).show()
    }

    private fun confirmDeleteAll() {
        AlertDialog.Builder(this)
            .setTitle("⚠️ Elimina tutto?")
            .setMessage("Verranno eliminati tutti i dati dell'archivio. L'operazione è irreversibile.")
            .setPositiveButton("Elimina tutto") { _, _ ->
                vm.deleteAllPlayers(); vm.deleteAllTeams(); vm.deleteAllMatches()
                Toast.makeText(this, "✅ Database azzerato", Toast.LENGTH_SHORT).show()
            }
            .setNegativeButton("Annulla", null).show()
    }

    private fun exportJson() {
        lifecycleScope.launch {
            val data = mapOf(
                "players" to vm.getAllPlayers(),
                "teams" to vm.getAllTeams(),
                "matches" to vm.getAllMatches()
            )
            val json = Gson().toJson(data)
            ExportHelper.shareJson(this@DatabaseActivity, json)
        }
    }

    private fun importFromJson(uri: android.net.Uri) {
        try {
            val json = contentResolver.openInputStream(uri)?.bufferedReader()?.readText() ?: return
            val type = object : TypeToken<Map<String, Any>>() {}.type
            val data: Map<String, Any> = Gson().fromJson(json, type)

            lifecycleScope.launch {
                val gson = Gson()
                (data["players"] as? List<*>)?.forEach { item ->
                    val p = gson.fromJson(gson.toJson(item), Player::class.java)
                    vm.savePlayer(p)
                }
                (data["teams"] as? List<*>)?.forEach { item ->
                    val t = gson.fromJson(gson.toJson(item), Team::class.java)
                    vm.saveTeam(t)
                }
                (data["matches"] as? List<*>)?.forEach { item ->
                    val m = gson.fromJson(gson.toJson(item), Match::class.java)
                    vm.saveMatch(m)
                }
                Toast.makeText(this@DatabaseActivity, "✅ Import completato!", Toast.LENGTH_SHORT).show()
            }
        } catch (e: Exception) {
            Toast.makeText(this, "❌ Errore import: ${e.message}", Toast.LENGTH_LONG).show()
        }
    }

    private fun exportPdf() {
        lifecycleScope.launch {
            val players = vm.getAllPlayers()
            val teams = vm.getAllTeams()
            val matches = vm.getAllMatches()
            val file = ExportHelper.generatePdf(this@DatabaseActivity, players, teams, matches)
            ExportHelper.sharePdf(this@DatabaseActivity, file)
        }
    }

    override fun onSupportNavigateUp(): Boolean { finish(); return true }
}
