package com.osservatore.calcio.ui

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.*
import com.osservatore.calcio.R

class EvalSectionView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null
) : LinearLayout(context, attrs) {

    private val rows = mutableListOf<RadioGroup>()
    private var criteria = listOf<String>()
    var onValueChanged: (() -> Unit)? = null

    init {
        orientation = VERTICAL
    }

    fun setup(title: String, items: List<String>, values: Map<String, Int> = emptyMap(), prefix: String) {
        criteria = items
        removeAllViews()
        rows.clear()

        // Title
        val tv = TextView(context).apply {
            text = title
            textSize = 14f
            setTypeface(null, android.graphics.Typeface.BOLD)
            setPadding(16, 16, 16, 8)
            setTextColor(context.getColor(R.color.green_dark))
        }
        addView(tv)

        // Header row
        val header = LinearLayout(context).apply {
            orientation = HORIZONTAL
            setPadding(16, 4, 16, 4)
        }
        header.addView(TextView(context).apply {
            text = "Criterio"
            layoutParams = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 3f)
            textSize = 11f
            setTextColor(context.getColor(R.color.gray))
        })
        listOf("1", "2", "3", "4").forEach { label ->
            header.addView(TextView(context).apply {
                text = label
                layoutParams = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f)
                textSize = 11f
                gravity = android.view.Gravity.CENTER
                setTextColor(context.getColor(R.color.gray))
            })
        }
        addView(header)

        // Each criterion row
        items.forEachIndexed { index, item ->
            val row = LinearLayout(context).apply {
                orientation = HORIZONTAL
                setPadding(16, 6, 16, 6)
                if (index % 2 == 0) setBackgroundColor(context.getColor(R.color.row_even))
            }

            row.addView(TextView(context).apply {
                text = item
                layoutParams = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 3f)
                textSize = 12f
                setTextColor(context.getColor(R.color.text_dark))
            })

            val rg = RadioGroup(context).apply {
                orientation = HORIZONTAL
                layoutParams = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 4f)
            }

            val key = "${prefix}_$index"
            val savedVal = values[key] ?: 3

            (1..4).forEach { value ->
                val rb = RadioButton(context).apply {
                    id = android.view.View.generateViewId()
                    layoutParams = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f)
                    this.text = ""
                    tag = value
                    buttonTintList = context.getColorStateList(R.color.radio_tint)
                    isChecked = (value == savedVal)
                }
                rg.addView(rb)
            }

            rg.setOnCheckedChangeListener { _, _ -> onValueChanged?.invoke() }
            rows.add(rg)
            row.addView(rg)
            addView(row)
        }

        // Total row
        val totalRow = TextView(context).apply {
            id = R.id.sectionTotal
            text = "Totale: - / ${items.size * 4}  —  Media: -/4"
            textSize = 12f
            setTypeface(null, android.graphics.Typeface.BOLD)
            setPadding(16, 8, 16, 12)
            setTextColor(context.getColor(R.color.green_dark))
            setBackgroundColor(context.getColor(R.color.green_light))
        }
        addView(totalRow)
    }

    fun getValues(prefix: String): Map<String, Int> {
        val result = mutableMapOf<String, Int>()
        rows.forEachIndexed { index, rg ->
            val checkedId = rg.checkedRadioButtonId
            val rb = rg.findViewById<RadioButton>(checkedId)
            result["${prefix}_$index"] = (rb?.tag as? Int) ?: 3
        }
        return result
    }

    fun getAverage(): Float {
        if (rows.isEmpty()) return 3f
        var sum = 0
        rows.forEach { rg ->
            val checkedId = rg.checkedRadioButtonId
            val rb = rg.findViewById<RadioButton>(checkedId)
            sum += (rb?.tag as? Int) ?: 3
        }
        return sum.toFloat() / rows.size
    }

    fun updateTotal(prefix: String) {
        val vals = getValues(prefix)
        val total = vals.values.sum()
        val max = rows.size * 4
        val avg = if (rows.isEmpty()) 0f else total.toFloat() / rows.size
        val tv = findViewById<TextView>(R.id.sectionTotal)
        tv?.text = "Totale: $total/$max  —  Media: ${String.format("%.2f", avg)}/4"
    }
}
