package com.osservatore.calcio.ui

import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.osservatore.calcio.databinding.ActivityMainBinding
import com.osservatore.calcio.viewmodel.MainViewModel

class MainActivity : AppCompatActivity() {
    private lateinit var b: ActivityMainBinding
    private val vm: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        b = ActivityMainBinding.inflate(layoutInflater)
        setContentView(b.root)
        setSupportActionBar(b.toolbar)

        // Observe stats
        vm.players.observe(this) { list ->
            b.statGiocatori.text = list.size.toString()
            val avg = if (list.isEmpty()) 0f else list.map { it.scoreComplessivo }.average().toFloat()
            b.statMedia.text = String.format("%.2f", avg)
        }
        vm.teams.observe(this) { b.statSquadre.text = it.size.toString() }
        vm.matches.observe(this) { b.statPartite.text = it.size.toString() }

        // Navigation cards
        b.cardGiocatore.setOnClickListener {
            startActivity(Intent(this, PlayerFormActivity::class.java))
        }
        b.cardSquadra.setOnClickListener {
            startActivity(Intent(this, TeamFormActivity::class.java))
        }
        b.cardPartita.setOnClickListener {
            startActivity(Intent(this, MatchFormActivity::class.java))
        }
        b.cardDatabase.setOnClickListener {
            startActivity(Intent(this, DatabaseActivity::class.java))
        }
    }
}
