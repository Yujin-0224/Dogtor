package kr.co.example.dogtor.ui

import androidx.compose.animation.core.tween
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Lightbulb
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import kr.co.example.dogtor.R
import kr.co.example.dogtor.ui.theme.*

private val healthTips = listOf(
    "매일 꾸준한 양치질은 강아지의 치주 질환을 예방하는 가장 좋은 방법입니다.",
    "초콜릿, 포도, 양파, 마카다미아는 강아지에게 매우 위험하니 절대 주지 마세요.",
    "여름철 산책 후에는 발바닥과 털 사이에 진드기가 붙지 않았는지 꼭 확인해주세요.",
    "강아지가 귀를 자주 긁거나 턴다면 외이염의 신호일 수 있습니다. 병원에서 진찰을 받아보세요.",
    "사료를 바꿀 때는 기존 사료에 새 사료를 조금씩 섞어주며 7~10일에 걸쳐 점진적으로 교체해야 합니다.",
    "강아지의 코가 마르는 것은 일시적인 현상일 수 있으나, 다른 증상이 동반된다면 건강 이상 신호일 수 있습니다.",
    "정기적인 건강 검진은 질병을 조기에 발견하고 반려견의 삶의 질을 높이는 데 큰 도움이 됩니다."
)

@Composable
fun MainMenuScreen(onMenuSelected: (String) -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Image(
                painter = painterResource(id = R.drawable.welcome_dogtor),
                contentDescription = "환영 메시지",
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1.5f)
            )

            Spacer(modifier = Modifier.height(24.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                MenuButton(
                    modifier = Modifier.weight(1f),
                    iconId = R.drawable.dog_eye,
                    title = "눈병 진단",
                    subtitle = "사진으로 빠른 진단",
                    onClick = { onMenuSelected("눈병 진단") }
                )
                MenuButton(
                    modifier = Modifier.weight(1f),
                    iconId = R.drawable.dog_skin,
                    title = "피부병 진단",
                    subtitle = "피부질환 판별",
                    onClick = { onMenuSelected("피부병 진단") }
                )
            }
            Spacer(modifier = Modifier.height(16.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                MenuButton(
                    modifier = Modifier.weight(1f),
                    iconId = R.drawable.chatbot,
                    title = "AI Dogtor",
                    subtitle = "궁금한 점을 물어보세요",
                    onClick = { onMenuSelected("AI Dogtor") }
                )
                MenuButton(
                    modifier = Modifier.weight(1f),
                    iconId = R.drawable.hospital,
                    title = "근처 동물병원",
                    subtitle = "가까운 병원을 안내합니다",
                    onClick = { onMenuSelected("근처 동물병원") }
                )
            }

            Spacer(modifier = Modifier.weight(0.5f))

            HealthTipCarousel()

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MenuButton(
    modifier: Modifier = Modifier,
    iconId: Int,
    title: String,
    subtitle: String,
    onClick: () -> Unit
) {
    Card(
        onClick = onClick,
        modifier = modifier
            .aspectRatio(1f)
            .border(width = 2.dp, color = ButtonBorder, shape = RoundedCornerShape(24.dp)),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(
            containerColor = ButtonBackground
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                painter = painterResource(id = iconId),
                contentDescription = title,
                modifier = Modifier.size(90.dp),
                tint = Color.Unspecified
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = title,
                fontFamily = KCCGanpan,
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodyMedium.copy(
                    fontFamily = KCCGanpan,
                    fontSize = 18.sp
                ),
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f)
            )
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun HealthTipCarousel() {
    val pageCount = healthTips.size
    val pagerState = rememberPagerState(pageCount = { pageCount })

    LaunchedEffect(pagerState) {
        while (true) {
            delay(8000L)
            val nextPage = (pagerState.currentPage + 1) % pageCount
            pagerState.animateScrollToPage(
                page = nextPage,
                animationSpec = tween(durationMillis = 700)
            )
        }
    }

    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        HorizontalPager(
            state = pagerState,
            modifier = Modifier.fillMaxWidth()
        ) { page ->
            HealthTipCard(tip = healthTips[page])
        }

        Spacer(modifier = Modifier.height(12.dp))

        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            repeat(pageCount) { iteration ->
                val color = if (pagerState.currentPage == iteration) MaterialTheme.colorScheme.primary else Color.LightGray.copy(alpha = 0.5f)
                Box(
                    modifier = Modifier
                        .clip(CircleShape)
                        .background(color)
                        .size(8.dp)
                )
            }
        }
    }
}

@Composable
private fun HealthTipCard(tip: String) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(120.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = HealthTipBackground
        )
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Outlined.Lightbulb,
                contentDescription = "건강 상식",
                tint = HealthTipText,
                modifier = Modifier.size(32.dp)
            )
            Spacer(modifier = Modifier.width(12.dp))
            Column {
                Text(
                    text = "오늘의 건강 상식",
                    fontFamily = KCCGanpan,
                    style = MaterialTheme.typography.labelLarge.copy(
                        fontSize = 22.sp
                    ),
                    color = HealthTipText
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = tip,
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontSize = 20.sp
                    ),
                    color = MaterialTheme.colorScheme.onSurface,
                    lineHeight = 22.sp
                )
            }
        }
    }
}
