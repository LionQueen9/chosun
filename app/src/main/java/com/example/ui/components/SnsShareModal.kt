package com.example.ui.components

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.SillokArticle
import com.example.ui.theme.HanjiPaperDark
import com.example.ui.theme.JoseonGold
import com.example.ui.theme.RoyalCrimson

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SnsShareModal(
    article: SillokArticle,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current

    val formattedShareText = buildString {
        append("📜 [조선왕조실록 기사 공유]\n")
        append("▪ 제목: ${article.title}\n")
        append("▪ 시기: ${article.king}대 (서기 ${article.gregorianYear}년 / ${article.lunarDateStr})\n")
        if (article.location != null) {
            append("▪ 사건 관련 장소: ${article.location.name} (${article.location.region})\n")
        }
        append("\n“${article.excerpt}”\n\n")
        if (article.location != null) {
            append("📍 구글맵 지도 보기: ${article.location.getGoogleMapsUrl()}\n")
        }
        append("🔗 실록 공식 원문: ${article.url}\n")
        append("#조선왕조실록 #역사 #구글맵위치공유")
    }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
        containerColor = MaterialTheme.colorScheme.surface
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 32.dp, start = 20.dp, end = 20.dp, top = 8.dp)
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "SNS 공유 및 내보내기",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = RoyalCrimson
                    )
                    Text(
                        text = "조선왕조실록 기사와 구글맵 위치를 소셜 미디어로 연동합니다",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                IconButton(onClick = onDismiss) {
                    Icon(imageVector = Icons.Default.Close, contentDescription = "닫기")
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Article Summary Card Preview
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                border = androidx.compose.foundation.BorderStroke(1.dp, JoseonGold.copy(alpha = 0.5f))
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text(
                        text = article.title,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "“${article.excerpt}”",
                        style = MaterialTheme.typography.bodySmall,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    if (article.location != null) {
                        Spacer(modifier = Modifier.height(6.dp))
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Map,
                                contentDescription = null,
                                tint = RoyalCrimson,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "구글맵 위치 포함: ${article.location.name}",
                                style = MaterialTheme.typography.labelMedium,
                                color = RoyalCrimson,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))
            HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f))
            Spacer(modifier = Modifier.height(20.dp))

            Text(
                text = "공유할 SNS 플랫폼 선택",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )

            Spacer(modifier = Modifier.height(16.dp))

            // SNS Grid Options
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                // KakaoTalk
                SnsOptionItem(
                    name = "카카오톡",
                    bgColor = Color(0xFFFEE500),
                    textColor = Color(0xFF191919),
                    badgeText = "TALK",
                    onClick = {
                        shareToKakaoTalk(context, formattedShareText)
                        onDismiss()
                    }
                )

                // Facebook
                SnsOptionItem(
                    name = "페이스북",
                    bgColor = Color(0xFF1877F2),
                    textColor = Color.White,
                    badgeText = "f",
                    onClick = {
                        shareToFacebook(context, article, formattedShareText)
                        onDismiss()
                    }
                )

                // Instagram
                SnsOptionItem(
                    name = "인스타그램",
                    bgColor = Color(0xFFE1306C),
                    textColor = Color.White,
                    badgeText = "IG",
                    isSpecialBadge = true,
                    onClick = {
                        InstagramCardGenerator.shareToInstagram(context, article)
                        onDismiss()
                    }
                )

                // General Share
                SnsOptionItem(
                    name = "전체 공유",
                    bgColor = RoyalCrimson,
                    textColor = Color.White,
                    icon = Icons.Default.Share,
                    onClick = {
                        shareGeneralText(context, formattedShareText, article.title)
                        onDismiss()
                    }
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Copy Link Button
            Button(
                onClick = {
                    val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                    val clip = ClipData.newPlainText("Joseon Sillok Share", formattedShareText)
                    clipboard.setPrimaryClip(clip)
                    Toast.makeText(context, "조선왕조실록 내용 및 구글맵 링크가 클립보드에 복사되었습니다!", Toast.LENGTH_LONG).show()
                    onDismiss()
                },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant,
                    contentColor = MaterialTheme.colorScheme.onSurfaceVariant
                ),
                shape = RoundedCornerShape(12.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.ContentCopy,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text("텍스트 및 구글맵 링크 복사하기", fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
private fun SnsOptionItem(
    name: String,
    bgColor: Color,
    textColor: Color,
    badgeText: String? = null,
    icon: androidx.compose.ui.graphics.vector.ImageVector? = null,
    isSpecialBadge: Boolean = false,
    onClick: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .clip(RoundedCornerShape(12.dp))
            .clickable(onClick = onClick)
            .padding(8.dp)
    ) {
        Box(
            modifier = Modifier
                .size(56.dp)
                .background(bgColor, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            if (badgeText != null) {
                Text(
                    text = badgeText,
                    color = textColor,
                    fontWeight = FontWeight.Black,
                    fontSize = if (badgeText.length > 2) 14.sp else 22.sp
                )
            } else if (icon != null) {
                Icon(
                    imageVector = icon,
                    contentDescription = name,
                    tint = textColor,
                    modifier = Modifier.size(26.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(6.dp))

        Text(
            text = name,
            style = MaterialTheme.typography.labelMedium,
            fontWeight = FontWeight.Medium,
            color = MaterialTheme.colorScheme.onSurface
        )

        if (isSpecialBadge) {
            Text(
                text = "카드이미지",
                fontSize = 10.sp,
                color = RoyalCrimson,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

private fun shareToKakaoTalk(context: Context, formattedText: String) {
    val intent = Intent(Intent.ACTION_SEND).apply {
        type = "text/plain"
        putExtra(Intent.EXTRA_TEXT, formattedText)
        setPackage("com.kakao.talk")
    }

    if (intent.resolveActivity(context.packageManager) != null) {
        context.startActivity(intent)
    } else {
        // Fallback to chooser with Kakao target guidance
        val chooser = Intent.createChooser(
            Intent(Intent.ACTION_SEND).apply {
                type = "text/plain"
                putExtra(Intent.EXTRA_TEXT, formattedText)
            },
            "카카오톡으로 조선왕조실록 내보내기"
        )
        context.startActivity(chooser)
    }
}

private fun shareToFacebook(context: Context, article: SillokArticle, formattedText: String) {
    val shareUrl = article.location?.getGoogleMapsUrl() ?: article.url
    val intent = Intent(Intent.ACTION_SEND).apply {
        type = "text/plain"
        putExtra(Intent.EXTRA_TEXT, "$formattedText\n$shareUrl")
        setPackage("com.facebook.katana")
    }

    if (intent.resolveActivity(context.packageManager) != null) {
        context.startActivity(intent)
    } else {
        val webUrl = "https://www.facebook.com/sharer/sharer.php?u=${Uri.encode(shareUrl)}"
        val webIntent = Intent(Intent.ACTION_VIEW, Uri.parse(webUrl))
        context.startActivity(webIntent)
    }
}

private fun shareGeneralText(context: Context, text: String, title: String) {
    val intent = Intent(Intent.ACTION_SEND).apply {
        type = "text/plain"
        putExtra(Intent.EXTRA_SUBJECT, "[조선왕조실록] $title")
        putExtra(Intent.EXTRA_TEXT, text)
    }
    context.startActivity(Intent.createChooser(intent, "조선왕조실록 내보내기"))
}
