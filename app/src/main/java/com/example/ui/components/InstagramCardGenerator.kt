package com.example.ui.components

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.RectF
import android.graphics.Typeface
import android.net.Uri
import androidx.core.content.FileProvider
import com.example.data.model.SillokArticle
import java.io.File
import java.io.FileOutputStream

object InstagramCardGenerator {

    /**
     * Generates a Hanji parchment style quote card bitmap for Instagram Posts/Stories
     */
    fun createInstagramStoryCard(context: Context, article: SillokArticle): Bitmap {
        val width = 1080
        val height = 1920 // Standard Instagram Story aspect ratio (9:16)

        val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)

        // 1. Hanji Background Color (#FAF6EE)
        val bgPaint = Paint().apply {
            color = android.graphics.Color.parseColor("#FAF6EE")
            style = Paint.Style.FILL
        }
        canvas.drawRect(0f, 0f, width.toFloat(), height.toFloat(), bgPaint)

        // 2. Outer Border & Frame (#7A121C - Royal Crimson & #D4AF37 - Gold)
        val borderMargin = 60f
        val framePaint = Paint().apply {
            color = android.graphics.Color.parseColor("#7A121C")
            style = Paint.Style.STROKE
            strokeWidth = 14f
        }
        canvas.drawRect(
            borderMargin,
            borderMargin,
            width - borderMargin,
            height - borderMargin,
            framePaint
        )

        val innerGoldFrame = Paint().apply {
            color = android.graphics.Color.parseColor("#D4AF37")
            style = Paint.Style.STROKE
            strokeWidth = 4f
        }
        canvas.drawRect(
            borderMargin + 20f,
            borderMargin + 20f,
            width - borderMargin - 20f,
            height - borderMargin - 20f,
            innerGoldFrame
        )

        // 3. Top Royal Seal Badge Header
        val sealPaint = Paint().apply {
            color = android.graphics.Color.parseColor("#B71C1C")
            style = Paint.Style.FILL
        }
        val sealRect = RectF(width / 2f - 180f, 120f, width / 2f + 180f, 220f)
        canvas.drawRoundRect(sealRect, 16f, 16f, sealPaint)

        val sealTextPaint = Paint().apply {
            color = android.graphics.Color.WHITE
            textSize = 48f
            isFakeBoldText = true
            textAlign = Paint.Align.CENTER
        }
        canvas.drawText("朝鮮王朝實錄", width / 2f, 185f, sealTextPaint)

        // Subhead
        val subheadPaint = Paint().apply {
            color = android.graphics.Color.parseColor("#7A121C")
            textSize = 38f
            isFakeBoldText = true
            textAlign = Paint.Align.CENTER
        }
        canvas.drawText("조선왕조실록 역사 탐방 기록", width / 2f, 290f, subheadPaint)

        // 4. King & Year Banner
        val bannerBg = Paint().apply {
            color = android.graphics.Color.parseColor("#EFE8D8")
            style = Paint.Style.FILL
        }
        val bannerRect = RectF(120f, 340f, width - 120f, 440f)
        canvas.drawRoundRect(bannerRect, 12f, 12f, bannerBg)

        val bannerText = Paint().apply {
            color = android.graphics.Color.parseColor("#1C1917")
            textSize = 42f
            isFakeBoldText = true
            textAlign = Paint.Align.CENTER
        }
        canvas.drawText(
            "${article.king}대 (서기 ${article.gregorianYear}년 / ${article.lunarDateStr})",
            width / 2f,
            405f,
            bannerText
        )

        // 5. Article Title
        val titlePaint = Paint().apply {
            color = android.graphics.Color.parseColor("#7A121C")
            textSize = 52f
            isFakeBoldText = true
            textAlign = Paint.Align.CENTER
        }

        // Draw Title wrapped
        val titleText = article.title
        val lines = wrapText(titleText, titlePaint, width - 300)
        var startY = 540f
        for (line in lines.take(3)) {
            canvas.drawText(line, width / 2f, startY, titlePaint)
            startY += 70f
        }

        // 6. Excerpt Decorative Quote Box
        val quoteBox = RectF(120f, startY + 30f, width - 120f, startY + 680f)
        val quoteBg = Paint().apply {
            color = android.graphics.Color.parseColor("#FFFDF9")
            style = Paint.Style.FILL
        }
        canvas.drawRoundRect(quoteBox, 20f, 20f, quoteBg)

        val quoteBorder = Paint().apply {
            color = android.graphics.Color.parseColor("#D4AF37")
            style = Paint.Style.STROKE
            strokeWidth = 3f
        }
        canvas.drawRoundRect(quoteBox, 20f, 20f, quoteBorder)

        // Excerpt Content
        val bodyPaint = Paint().apply {
            color = android.graphics.Color.parseColor("#1C1917")
            textSize = 40f
            textAlign = Paint.Align.LEFT
        }

        val excerptLines = wrapText("“" + article.excerpt + "”", bodyPaint, width - 360)
        var bodyY = startY + 110f
        for (line in excerptLines.take(8)) {
            canvas.drawText(line, 160f, bodyY, bodyPaint)
            bodyY += 60f
        }

        // Original Text (Hanja) if present
        if (article.originalText.isNotBlank()) {
            val hanjaPaint = Paint().apply {
                color = android.graphics.Color.parseColor("#57534E")
                textSize = 34f
                textAlign = Paint.Align.LEFT
            }
            bodyY += 20f
            canvas.drawText("[原 文] " + article.originalText.take(28) + "...", 160f, bodyY, hanjaPaint)
        }

        // 7. Location Info Box
        if (article.location != null) {
            val locBox = RectF(120f, height - 380f, width - 120f, height - 200f)
            val locBg = Paint().apply {
                color = android.graphics.Color.parseColor("#7A121C")
                style = Paint.Style.FILL
            }
            canvas.drawRoundRect(locBox, 16f, 16f, locBg)

            val locTitlePaint = Paint().apply {
                color = android.graphics.Color.parseColor("#D4AF37")
                textSize = 44f
                isFakeBoldText = true
                textAlign = Paint.Align.CENTER
            }
            canvas.drawText("📍 사건 관련 장소: ${article.location.name}", width / 2f, height - 300f, locTitlePaint)

            val locDetailPaint = Paint().apply {
                color = android.graphics.Color.WHITE
                textSize = 34f
                textAlign = Paint.Align.CENTER
            }
            canvas.drawText(article.location.region, width / 2f, height - 240f, locDetailPaint)
        }

        // 8. Bottom Branding Footer
        val footerPaint = Paint().apply {
            color = android.graphics.Color.parseColor("#7A121C")
            textSize = 36f
            isFakeBoldText = true
            textAlign = Paint.Align.CENTER
        }
        canvas.drawText("조선왕조실록 검색 앱 #조선왕조실록 #역사탐방", width / 2f, height - 100f, footerPaint)

        return bitmap
    }

    private fun wrapText(text: String, paint: Paint, maxWidth: Int): List<String> {
        val words = text.split(" ")
        val lines = mutableListOf<String>()
        var currentLine = ""

        for (word in words) {
            val testLine = if (currentLine.isEmpty()) word else "$currentLine $word"
            if (paint.measureText(testLine) <= maxWidth) {
                currentLine = testLine
            } else {
                if (currentLine.isNotEmpty()) lines.add(currentLine)
                currentLine = word
            }
        }
        if (currentLine.isNotEmpty()) lines.add(currentLine)
        return lines
    }

    /**
     * Saves bitmap to cache and triggers Instagram Story/Feed share intent
     */
    fun shareToInstagram(context: Context, article: SillokArticle) {
        val bitmap = createInstagramStoryCard(context, article)
        val imageUri = saveBitmapToCache(context, bitmap)

        val intent = Intent("com.instagram.share.ADD_TO_STORY").apply {
            setDataAndType(imageUri, "image/jpeg")
            putExtra("interactive_asset_uri", imageUri)
            putExtra("content_url", article.url)
            flags = Intent.FLAG_GRANT_READ_URI_PERMISSION
        }

        if (intent.resolveActivity(context.packageManager) != null) {
            context.startActivity(intent)
        } else {
            // Fallback to standard Image share sheet targeting Instagram or general apps
            val shareIntent = Intent(Intent.ACTION_SEND).apply {
                type = "image/jpeg"
                putExtra(Intent.EXTRA_STREAM, imageUri)
                putExtra(Intent.EXTRA_TEXT, "조선왕조실록: ${article.title}\n\n${article.excerpt}\n\n#조선왕조실록 #인스타그램")
                setPackage("com.instagram.android")
                flags = Intent.FLAG_GRANT_READ_URI_PERMISSION
            }
            if (shareIntent.resolveActivity(context.packageManager) != null) {
                context.startActivity(shareIntent)
            } else {
                // Fallback to chooser
                val chooser = Intent.createChooser(
                    Intent(Intent.ACTION_SEND).apply {
                        type = "image/jpeg"
                        putExtra(Intent.EXTRA_STREAM, imageUri)
                        putExtra(Intent.EXTRA_TEXT, "조선왕조실록: ${article.title}\n${article.url}")
                        flags = Intent.FLAG_GRANT_READ_URI_PERMISSION
                    },
                    "인스타그램 / 이미지 공유"
                )
                context.startActivity(chooser)
            }
        }
    }

    fun saveBitmapToCache(context: Context, bitmap: Bitmap): Uri {
        val imagesFolder = File(context.cacheDir, "shared_images")
        imagesFolder.mkdirs()
        val file = File(imagesFolder, "sillok_instagram_card.jpg")
        val stream = FileOutputStream(file)
        bitmap.compress(Bitmap.CompressFormat.JPEG, 95, stream)
        stream.flush()
        stream.close()
        return FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", file)
    }
}
