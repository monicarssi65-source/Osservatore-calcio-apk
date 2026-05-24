package com.osservatore.calcio.utils

import android.content.Context
import android.content.Intent
import androidx.core.content.FileProvider
import com.itextpdf.text.*
import com.itextpdf.text.pdf.PdfPCell
import com.itextpdf.text.pdf.PdfPTable
import com.itextpdf.text.pdf.PdfWriter
import com.osservatore.calcio.data.EvalCriteria
import com.osservatore.calcio.data.Match
import com.osservatore.calcio.data.Player
import com.osservatore.calcio.data.Team
import java.io.File
import java.io.FileOutputStream

object ExportHelper {

    private val GREEN = BaseColor(0, 156, 59)
    private val RED = BaseColor(206, 43, 55)
    private val LIGHT = BaseColor(248, 248, 248)

    fun shareJson(context: Context, json: String) {
        val file = File(context.cacheDir, "osservatore_export.json")
        file.writeText(json)
        val uri = FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", file)
        val intent = Intent(Intent.ACTION_SEND).apply {
            type = "application/json"
            putExtra(Intent.EXTRA_STREAM, uri)
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }
        context.startActivity(Intent.createChooser(intent, "Esporta Database"))
    }

    fun sharePdf(context: Context, file: File) {
        val uri = FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", file)
        val intent = Intent(Intent.ACTION_SEND).apply {
            type = "application/pdf"
            putExtra(Intent.EXTRA_STREAM, uri)
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }
        context.startActivity(Intent.createChooser(intent, "Condividi PDF"))
    }

    fun generatePlayerPdf(context: Context, p: Player): File {
        val file = File(context.cacheDir, "${p.cognome}_${p.nome}.pdf")
        val doc = Document(PageSize.A4, 36f, 36f, 54f, 36f)
        PdfWriter.getInstance(doc, FileOutputStream(file))
        doc.open()

        val titleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 18f, GREEN)
        val headerFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 11f, BaseColor.WHITE)
        val bodyFont = FontFactory.getFont(FontFactory.HELVETICA, 10f, BaseColor.BLACK)
        val boldFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 10f, BaseColor.BLACK)

        doc.add(Paragraph("⚽ OSSERVATORE CALCIO ITALIA", FontFactory.getFont(FontFactory.HELVETICA_BOLD, 14f, GREEN)).apply { alignment = Element.ALIGN_CENTER })
        doc.add(Paragraph("SCHEDA GIOCATORE", FontFactory.getFont(FontFactory.HELVETICA_BOLD, 16f, RED)).apply { alignment = Element.ALIGN_CENTER })
        doc.add(Chunk.NEWLINE)

        // Player info
        doc.add(Paragraph("${p.cognome} ${p.nome}", titleFont))
        doc.add(Paragraph("${p.ruolo} • ${p.piede} • ${p.nazionalita}", bodyFont))
        doc.add(Paragraph("Società: ${p.societa} • Stagione: ${p.stagione}", bodyFont))
        doc.add(Paragraph("Nato: ${p.dataNascita} a ${p.luogoNascita}", bodyFont))
        doc.add(Paragraph("Altezza: ${p.altezza}cm • Peso: ${p.peso}kg • Morfologia: ${p.morfologia}", bodyFont))
        doc.add(Chunk.NEWLINE)

        // Stats
        doc.add(Paragraph("STATISTICHE", FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12f, RED)))
        doc.add(Paragraph("Presenze: ${p.presenze} | Gol: ${p.gol} | Assist: ${p.assist} | Minuti: ${p.minuti}", bodyFont))
        doc.add(Chunk.NEWLINE)

        // Scores
        doc.add(Paragraph("VALUTAZIONE", FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12f, RED)))
        val scoreTable = PdfPTable(7).apply { widthPercentage = 100f }
        listOf("Tecnica", "Tattica", "Coord.", "Fisiche", "Mentali", "Social", "TOTALE").forEach { h ->
            scoreTable.addCell(PdfPCell(Phrase(h, headerFont)).apply {
                backgroundColor = if (h == "TOTALE") RED else GREEN
                horizontalAlignment = Element.ALIGN_CENTER; paddingBottom = 6f
            })
        }
        listOf(p.scoreTecnica, p.scoreTattica, p.scoreCoordinazione, p.scoreFisiche,
            p.scoreMentali, p.scoreSocialita, p.scoreComplessivo).forEach { score ->
            scoreTable.addCell(PdfPCell(Phrase(String.format("%.2f", score), boldFont)).apply {
                horizontalAlignment = Element.ALIGN_CENTER; paddingBottom = 4f
            })
        }
        doc.add(scoreTable)
        doc.add(Chunk.NEWLINE)

        if (p.note.isNotEmpty()) {
            doc.add(Paragraph("NOTE", FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12f, RED)))
            doc.add(Paragraph(p.note, bodyFont))
        }

        doc.close()
        return file
    }

    fun generatePdf(context: Context, players: List<Player>, teams: List<Team>, matches: List<Match>): File {
        val file = File(context.cacheDir, "osservatore_report.pdf")
        val doc = Document(PageSize.A4, 36f, 36f, 54f, 36f)
        PdfWriter.getInstance(doc, FileOutputStream(file))
        doc.open()

        val titleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 18f, GREEN)
        val sectionFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 14f, RED)
        val headerFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 10f, BaseColor.WHITE)
        val bodyFont = FontFactory.getFont(FontFactory.HELVETICA, 9f, BaseColor.BLACK)

        doc.add(Paragraph("⚽ OSSERVATORE CALCIO ITALIA", titleFont).apply { alignment = Element.ALIGN_CENTER })
        doc.add(Paragraph("Report Completo — ${players.size} Giocatori • ${teams.size} Squadre • ${matches.size} Partite",
            FontFactory.getFont(FontFactory.HELVETICA, 11f, BaseColor.DARK_GRAY)).apply { alignment = Element.ALIGN_CENTER })
        doc.add(Chunk.NEWLINE)

        if (players.isNotEmpty()) {
            doc.add(Paragraph("GIOCATORI", sectionFont))
            doc.add(Chunk.NEWLINE)
            val t = PdfPTable(6).apply { widthPercentage = 100f; setWidths(floatArrayOf(3f, 2f, 2f, 1f, 1f, 1f)) }
            listOf("Nome", "Ruolo", "Società", "Score", "Stagione", "Gol").forEach { h ->
                t.addCell(PdfPCell(Phrase(h, headerFont)).apply { backgroundColor = GREEN; paddingBottom = 5f })
            }
            players.forEach { p ->
                listOf("${p.cognome} ${p.nome}", p.ruolo, p.societa,
                    String.format("%.2f", p.scoreComplessivo), p.stagione, p.gol.toString()).forEachIndexed { i, v ->
                    t.addCell(PdfPCell(Phrase(v, bodyFont)).apply {
                        backgroundColor = if (players.indexOf(p) % 2 == 0) BaseColor.WHITE else LIGHT
                        paddingBottom = 3f
                    })
                }
            }
            doc.add(t)
            doc.add(Chunk.NEWLINE)
        }

        if (teams.isNotEmpty()) {
            doc.add(Paragraph("SQUADRE", sectionFont))
            doc.add(Chunk.NEWLINE)
            val t = PdfPTable(4).apply { widthPercentage = 100f }
            listOf("Nome", "Campionato", "Modulo", "Allenatore").forEach { h ->
                t.addCell(PdfPCell(Phrase(h, headerFont)).apply { backgroundColor = GREEN; paddingBottom = 5f })
            }
            teams.forEach { team ->
                listOf(team.nome, team.campionato, team.modulo, team.allenatore).forEachIndexed { _, v ->
                    t.addCell(PdfPCell(Phrase(v, bodyFont)).apply { paddingBottom = 3f })
                }
            }
            doc.add(t)
            doc.add(Chunk.NEWLINE)
        }

        if (matches.isNotEmpty()) {
            doc.add(Paragraph("PARTITE", sectionFont))
            doc.add(Chunk.NEWLINE)
            val t = PdfPTable(4).apply { widthPercentage = 100f }
            listOf("Data", "Casa", "Ospite", "Risultato").forEach { h ->
                t.addCell(PdfPCell(Phrase(h, headerFont)).apply { backgroundColor = GREEN; paddingBottom = 5f })
            }
            matches.forEach { m ->
                listOf(m.data, m.casa, m.ospite, m.risultato).forEach { v ->
                    t.addCell(PdfPCell(Phrase(v, bodyFont)).apply { paddingBottom = 3f })
                }
            }
            doc.add(t)
        }

        doc.close()
        return file
    }
}
