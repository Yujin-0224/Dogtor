package kr.co.example.dogtor.ui.skin

import android.graphics.Bitmap
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kr.co.example.dogtor.ui.theme.KCCGanpan

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SkinResultScreen(
    resultText: String,
    imageBitmap: Bitmap?,
    description: String,
    onBack: () -> Unit = {},
    onGoToHome: () -> Unit = {}
) {
    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = "검사 결과",
                        style = MaterialTheme.typography.titleLarge.copy(fontFamily = KCCGanpan),
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary
                )
            )
        },
        // ✅ 전체 배경색을 테마에 맞게 설정
        containerColor = MaterialTheme.colorScheme.background
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Column(
                modifier = Modifier
                    .weight(1f)
                    .verticalScroll(rememberScrollState())
                    .padding(vertical = 16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                if (imageBitmap != null) {
                    Text(
                        "진단 이미지",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontFamily = KCCGanpan,
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 12.dp),
                        textAlign = TextAlign.Start,
                        // ✅ 테마의 기본 글자색(갈색)으로 수정
                        color = MaterialTheme.colorScheme.onBackground
                    )
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(250.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(MaterialTheme.colorScheme.surface)
                            .border(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.3f), RoundedCornerShape(12.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Image(
                            bitmap = imageBitmap.asImageBitmap(),
                            contentDescription = "진단한 이미지",
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )
                    }
                    Spacer(modifier = Modifier.height(32.dp))
                }

                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.Start
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(bottom = 12.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.Search,
                            contentDescription = "진단 결과",
                            // ✅ 아이콘 색상도 기본 글자색(갈색)으로 수정
                            tint = MaterialTheme.colorScheme.onBackground,
                            modifier = Modifier.size(32.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            "진단 결과",
                            style = MaterialTheme.typography.titleLarge.copy(
                                fontFamily = KCCGanpan,
                                fontSize = 24.sp,
                                fontWeight = FontWeight.Bold
                            ),
                            // ✅ 텍스트 색상도 기본 글자색(갈색)으로 수정
                            color = MaterialTheme.colorScheme.onBackground
                        )
                    }
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(MaterialTheme.colorScheme.surface, RoundedCornerShape(8.dp))
                            .border(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.5f), RoundedCornerShape(8.dp))
                            .clip(RoundedCornerShape(8.dp))
                            .padding(16.dp)
                    ) {
                        Text(
                            text = resultText,
                            style = MaterialTheme.typography.bodyLarge.copy(
                                fontFamily = KCCGanpan,
                                fontSize = 20.sp
                            ),
                            modifier = Modifier.fillMaxWidth(),
                            lineHeight = 30.sp,
                            textAlign = TextAlign.Start,
                            color = MaterialTheme.colorScheme.onSurface // ✅ 테마 색상 적용
                        )
                    }
                }

                if (description.isNotBlank()) {
                    Spacer(modifier = Modifier.height(32.dp))
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 12.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.Info,
                            contentDescription = "질병 정보",
                            tint = MaterialTheme.colorScheme.onBackground, // ✅ 수정
                            modifier = Modifier.size(32.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            "질병 정보",
                            style = MaterialTheme.typography.titleLarge.copy(
                                fontFamily = KCCGanpan,
                                fontSize = 24.sp,
                                fontWeight = FontWeight.Bold
                            ),
                            color = MaterialTheme.colorScheme.onBackground // ✅ 수정
                        )
                    }
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(MaterialTheme.colorScheme.surface, RoundedCornerShape(8.dp))
                            .border(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.5f), RoundedCornerShape(8.dp))
                            .clip(RoundedCornerShape(8.dp))
                            .padding(16.dp)
                    ) {
                        Text(
                            text = description,
                            style = MaterialTheme.typography.bodyMedium.copy(
                                fontFamily = KCCGanpan,
                                fontSize = 18.sp,
                                lineHeight = 28.sp
                            ),
                            textAlign = TextAlign.Start,
                            color = MaterialTheme.colorScheme.onSurface // ✅ 테마 색상 적용
                        )
                    }
                }
            }

            //Spacer(modifier = Modifier.height(24.dp))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp), // ✅ 하단 여백 추가
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Button(
                    onClick = onBack,
                    modifier = Modifier
                        .weight(1f)
                        .height(56.dp),
                    // ✅ [추가] 버튼 색상 및 테두리 지정
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.surface
                    ),
                    shape = RoundedCornerShape(12.dp),
                    border = BorderStroke(2.dp, MaterialTheme.colorScheme.primary)
                ) {
                    Text(
                        text = "다시 검사하기",
                        fontSize = 20.sp,
                        fontFamily = KCCGanpan,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
                Button(
                    onClick = onGoToHome,
                    modifier = Modifier
                        .weight(1f)
                        .height(56.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(
                        text = "홈으로 가기",
                        fontSize = 20.sp,
                        fontFamily = KCCGanpan,
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                }
            }
        }
    }
}
