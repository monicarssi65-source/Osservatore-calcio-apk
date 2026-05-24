package com.osservatore.calcio.ui

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.osservatore.calcio.data.Match
import com.osservatore.calcio.data.Player
import com.osservatore.calcio.data.Team
import com.osservatore.calcio.databinding.ItemPlayerBinding
import com.osservatore.calcio.databinding.ItemTeamBinding
import com.osservatore.calcio.databinding.ItemMatchDbBinding

class DbPlayerAdapter(
    private val onClick: (Player) -> Unit,
    private val onEdit: (Player) -> Unit,
    private val onDelete: (Player) -> Unit
) : ListAdapter<Player, DbPlayerAdapter.VH>(Diff()) {
    override fun onCreateViewHolder(p: ViewGroup, v: Int) = VH(ItemPlayerBinding.inflate(LayoutInflater.from(p.context), p, false))
    override fun onBindViewHolder(h: VH, pos: Int) = h.bind(getItem(pos))

    inner class VH(private val b: ItemPlayerBinding) : RecyclerView.ViewHolder(b.root) {
        fun bind(p: Player) {
            b.tvName.text = "${p.cognome} ${p.nome}"
            b.tvInfo.text = "${p.ruolo} • ${p.societa} • ${p.stagione}"
            b.tvScore.text = String.format("⭐ %.2f/4", p.scoreComplessivo)
            b.tvScoreDetail.text = buildString {
                append("T:${String.format("%.1f",p.scoreTecnica)} ")
                append("At:${String.format("%.1f",p.scoreTattica)} ")
                append("C:${String.format("%.1f",p.scoreCoordinazione)} ")
                append("F:${String.format("%.1f",p.scoreFisiche)} ")
                append("M:${String.format("%.1f",p.scoreMentali)} ")
                append("S:${String.format("%.1f",p.scoreSocialita)}")
            }
            b.root.setOnClickListener { onClick(p) }
            b.btnEdit.setOnClickListener { onEdit(p) }
            b.btnDelete.setOnClickListener { onDelete(p) }
        }
    }
    class Diff : DiffUtil.ItemCallback<Player>() {
        override fun areItemsTheSame(o: Player, n: Player) = o.id == n.id
        override fun areContentsTheSame(o: Player, n: Player) = o == n
    }
}

class DbTeamAdapter(
    private val onDelete: (Team) -> Unit
) : ListAdapter<Team, DbTeamAdapter.VH>(Diff()) {
    override fun onCreateViewHolder(p: ViewGroup, v: Int) = VH(ItemTeamBinding.inflate(LayoutInflater.from(p.context), p, false))
    override fun onBindViewHolder(h: VH, pos: Int) = h.bind(getItem(pos))

    inner class VH(private val b: ItemTeamBinding) : RecyclerView.ViewHolder(b.root) {
        fun bind(t: Team) {
            b.tvName.text = t.nome
            b.tvInfo.text = "${t.campionato} • ${t.modulo} • All: ${t.allenatore}"
            b.tvDetail.text = "Gioco: ${t.tipoGioco} • Difesa: ${t.difesa}"
            b.btnDelete.setOnClickListener { onDelete(t) }
        }
    }
    class Diff : DiffUtil.ItemCallback<Team>() {
        override fun areItemsTheSame(o: Team, n: Team) = o.id == n.id
        override fun areContentsTheSame(o: Team, n: Team) = o == n
    }
}

class DbMatchAdapter(
    private val onDelete: (Match) -> Unit
) : ListAdapter<Match, DbMatchAdapter.VH>(Diff()) {
    override fun onCreateViewHolder(p: ViewGroup, v: Int) = VH(ItemMatchDbBinding.inflate(LayoutInflater.from(p.context), p, false))
    override fun onBindViewHolder(h: VH, pos: Int) = h.bind(getItem(pos))

    inner class VH(private val b: ItemMatchDbBinding) : RecyclerView.ViewHolder(b.root) {
        fun bind(m: Match) {
            b.tvMatch.text = "${m.casa} vs ${m.ospite}"
            b.tvResult.text = if (m.risultato.isNotEmpty()) m.risultato else "- vs -"
            b.tvInfo.text = "${m.data} • ${m.competizione} • ${m.stadio}"
            b.btnDelete.setOnClickListener { onDelete(m) }
        }
    }
    class Diff : DiffUtil.ItemCallback<Match>() {
        override fun areItemsTheSame(o: Match, n: Match) = o.id == n.id
        override fun areContentsTheSame(o: Match, n: Match) = o == n
    }
}
