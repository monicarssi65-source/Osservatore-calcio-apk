package com.osservatore.calcio.ui

import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.osservatore.calcio.databinding.ActivityTacticalBoardBinding

class TacticalBoardActivity : AppCompatActivity() {
    private lateinit var b: ActivityTacticalBoardBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        b = ActivityTacticalBoardBinding.inflate(layoutInflater)
        setContentView(b.root)
        setSupportActionBar(b.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = "🎨 Lavagna Tattica"

        // Color buttons
        b.btnRed.setOnClickListener {
            b.board.currentMode = TacticalBoardView.DrawMode.RED
            updateButtons("red")
        }
        b.btnBlue.setOnClickListener {
            b.board.currentMode = TacticalBoardView.DrawMode.BLUE
            updateButtons("blue")
        }
        b.btnErase.setOnClickListener {
            b.board.currentMode = TacticalBoardView.DrawMode.ERASE
            updateButtons("erase")
        }
        b.btnUndo.setOnClickListener { b.board.undo() }
        b.btnClear.setOnClickListener {
            android.app.AlertDialog.Builder(this)
                .setTitle("Cancella tutto?")
                .setPositiveButton("Sì") { _, _ -> b.board.clearAll() }
                .setNegativeButton("No", null).show()
        }

        updateButtons("red")
    }

    private fun updateButtons(active: String) {
        b.btnRed.alpha = if (active == "red") 1f else 0.5f
        b.btnBlue.alpha = if (active == "blue") 1f else 0.5f
        b.btnErase.alpha = if (active == "erase") 1f else 0.5f
    }

    override fun onSupportNavigateUp(): Boolean { finish(); return true }
}
