package com.osservatore.calcio.ui

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View

class TacticalBoardView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null
) : View(context, attrs) {

    enum class DrawMode { RED, BLUE, ERASE }

    var currentMode = DrawMode.RED
    var strokeWidth = 6f
    var eraseWidth = 30f

    private val paths = mutableListOf<Triple<Path, Paint, DrawMode>>()
    private var currentPath = Path()

    private val redPaint = Paint().apply {
        color = Color.RED
        style = Paint.Style.STROKE
        strokeWidth = this@TacticalBoardView.strokeWidth
        strokeCap = Paint.Cap.ROUND
        strokeJoin = Paint.Join.ROUND
        isAntiAlias = true
    }

    private val bluePaint = Paint().apply {
        color = Color.BLUE
        style = Paint.Style.STROKE
        strokeWidth = this@TacticalBoardView.strokeWidth
        strokeCap = Paint.Cap.ROUND
        strokeJoin = Paint.Join.ROUND
        isAntiAlias = true
    }

    private val erasePaint = Paint().apply {
        style = Paint.Style.STROKE
        strokeWidth = eraseWidth
        strokeCap = Paint.Cap.ROUND
        strokeJoin = Paint.Join.ROUND
        isAntiAlias = true
        xfermode = PorterDuffXfermode(PorterDuff.Mode.CLEAR)
    }

    private val fieldPaint = Paint().apply {
        color = Color.parseColor("#2E7D32")
        style = Paint.Style.FILL
    }

    private val linePaint = Paint().apply {
        color = Color.WHITE
        style = Paint.Style.STROKE
        strokeWidth = 3f
        isAntiAlias = true
    }

    private val linePaintThin = Paint().apply {
        color = Color.parseColor("#AAFFFFFF")
        style = Paint.Style.STROKE
        strokeWidth = 2f
        isAntiAlias = true
    }

    private var bitmap: Bitmap? = null
    private var bitmapCanvas: Canvas? = null

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        bitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888)
        bitmapCanvas = Canvas(bitmap!!)
        drawField(bitmapCanvas!!, w.toFloat(), h.toFloat())
    }

    private fun drawField(c: Canvas, w: Float, h: Float) {
        // Background
        c.drawRect(0f, 0f, w, h, fieldPaint)

        val pad = w * 0.05f

        // Border
        c.drawRect(pad, pad, w - pad, h - pad, linePaint)

        // Center line
        c.drawLine(pad, h / 2, w - pad, h / 2, linePaint)

        // Center circle
        val cr = w * 0.12f
        c.drawCircle(w / 2, h / 2, cr, linePaint)
        c.drawCircle(w / 2, h / 2, 6f, linePaint)

        // Penalty areas
        val paW = w * 0.55f
        val paH = h * 0.18f
        val paX = (w - paW) / 2
        // Top
        c.drawRect(paX, pad, paX + paW, pad + paH, linePaint)
        // Bottom
        c.drawRect(paX, h - pad - paH, paX + paW, h - pad, linePaint)

        // Goal areas
        val gaW = w * 0.28f
        val gaH = h * 0.08f
        val gaX = (w - gaW) / 2
        c.drawRect(gaX, pad, gaX + gaW, pad + gaH, linePaintThin)
        c.drawRect(gaX, h - pad - gaH, gaX + gaW, h - pad, linePaintThin)

        // Goals
        val goalW = w * 0.14f
        val goalH = h * 0.025f
        val goalX = (w - goalW) / 2
        c.drawRect(goalX, pad - goalH, goalX + goalW, pad, linePaintThin)
        c.drawRect(goalX, h - pad, goalX + goalW, h - pad + goalH, linePaintThin)

        // Penalty spots
        c.drawCircle(w / 2, pad + paH * 0.7f, 5f, linePaint)
        c.drawCircle(w / 2, h - pad - paH * 0.7f, 5f, linePaint)

        // Penalty arcs
        val path = Path()
        path.addArc(
            w / 2 - cr, pad + paH - cr,
            w / 2 + cr, pad + paH + cr,
            0f, 180f
        )
        c.drawPath(path, linePaintThin)

        val path2 = Path()
        path2.addArc(
            w / 2 - cr, h - pad - paH - cr,
            w / 2 + cr, h - pad - paH + cr,
            180f, 180f
        )
        c.drawPath(path2, linePaintThin)

        // Corner arcs
        val cor = w * 0.04f
        val corners = listOf(
            Triple(pad, pad, 0f),
            Triple(w - pad, pad, 90f),
            Triple(pad, h - pad, 270f),
            Triple(w - pad, h - pad, 180f)
        )
        corners.forEach { (cx, cy, startAngle) ->
            val cp = Path()
            cp.addArc(cx - cor, cy - cor, cx + cor, cy + cor, startAngle, 90f)
            c.drawPath(cp, linePaintThin)
        }
    }

    override fun onDraw(canvas: Canvas) {
        bitmap?.let { canvas.drawBitmap(it, 0f, 0f, null) }
        paths.forEach { (path, paint, _) -> canvas.drawPath(path, paint) }
        val currentPaint = when (currentMode) {
            DrawMode.RED -> redPaint
            DrawMode.BLUE -> bluePaint
            DrawMode.ERASE -> erasePaint
        }
        canvas.drawPath(currentPath, currentPaint)
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        val x = event.x
        val y = event.y
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                currentPath = Path()
                currentPath.moveTo(x, y)
            }
            MotionEvent.ACTION_MOVE -> {
                currentPath.lineTo(x, y)
                invalidate()
            }
            MotionEvent.ACTION_UP -> {
                currentPath.lineTo(x, y)
                val paint = when (currentMode) {
                    DrawMode.RED -> Paint(redPaint)
                    DrawMode.BLUE -> Paint(bluePaint)
                    DrawMode.ERASE -> Paint(erasePaint)
                }
                paths.add(Triple(currentPath, paint, currentMode))
                currentPath = Path()
                invalidate()
            }
        }
        return true
    }

    fun clearAll() {
        paths.clear()
        bitmap?.let {
            bitmapCanvas?.let { c -> drawField(c, it.width.toFloat(), it.height.toFloat()) }
        }
        invalidate()
    }

    fun undo() {
        if (paths.isNotEmpty()) {
            paths.removeAt(paths.size - 1)
            invalidate()
        }
    }

    fun getBitmap(): Bitmap {
        val result = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        val c = Canvas(result)
        draw(c)
        return result
    }

    fun saveToPaths(): String {
        // Returns count of strokes for info
        return paths.size.toString()
    }
}
