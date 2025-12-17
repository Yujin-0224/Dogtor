package kr.co.example.dogtor.ui.eye

import android.R.attr.fontFamily
import android.R.attr.fontWeight
import android.graphics.Bitmap
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.outlined.Visibility
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberAsyncImagePainter
import kr.co.example.dogtor.util.ImageUtils
import kr.co.example.dogtor.ui.theme.KCCGanpan

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EyeDiagnosisScreen(
    onBack: () -> Unit = {},
    onUpload: (Bitmap) -> Unit = {},
    onDiagnose: () -> Unit = {},
    isLoading: Boolean
) {
    var selectedImageUri by remember { mutableStateOf<Uri?>(null) }
    var capturedBitmap by remember { mutableStateOf<Bitmap?>(null) }
    var showDialog by remember { mutableStateOf(false) }

    val context = LocalContext.current

    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            val bitmap = ImageUtils.uriToBitmap(context, it)
            if (bitmap != null) {
                selectedImageUri = it
                capturedBitmap = null
                onUpload(bitmap)
            }
        }
    }

    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicturePreview()
    ) { bitmap ->
        if (bitmap != null) {
            capturedBitmap = bitmap
            selectedImageUri = null
            onUpload(bitmap)
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Scaffold(
            topBar = {
                CenterAlignedTopAppBar(
                    title = {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Outlined.Visibility,
                                contentDescription = "눈병 진단",
                                tint = MaterialTheme.colorScheme.onPrimary,
                                modifier = Modifier.size(28.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "눈병 진단",
                                style = MaterialTheme.typography.titleLarge.copy(fontFamily = KCCGanpan),
                                color = MaterialTheme.colorScheme.onPrimary
                            )
                        }
                    },
                    navigationIcon = {
                        IconButton(onClick = onBack) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "뒤로가기",
                                tint = MaterialTheme.colorScheme.onPrimary
                            )
                        }
                    },
                    colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                        containerColor = MaterialTheme.colorScheme.primary,
                        titleContentColor = MaterialTheme.colorScheme.onPrimary,
                        navigationIconContentColor = MaterialTheme.colorScheme.onPrimary
                    )
                )
            }
        ) { innerPadding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .padding(16.dp)
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(250.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f))
                        .border(1.dp, MaterialTheme.colorScheme.surfaceVariant, RoundedCornerShape(12.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    when {
                        selectedImageUri != null -> {
                            Image(
                                painter = rememberAsyncImagePainter(selectedImageUri),
                                contentDescription = "선택한 이미지",
                                modifier = Modifier.fillMaxSize(),
                                contentScale = ContentScale.Crop
                            )
                        }
                        capturedBitmap != null -> {
                            Image(
                                bitmap = capturedBitmap!!.asImageBitmap(),
                                contentDescription = "촬영한 이미지",
                                modifier = Modifier.fillMaxSize(),
                                contentScale = ContentScale.Crop
                            )
                        }
                        else -> {
                            Text(
                                "📷 사진을 업로드 해주세요",
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                style = MaterialTheme.typography.bodyMedium.copy(
                                    fontFamily = KCCGanpan,
                                    fontSize = 20.sp
                                )
                            )
                        }
                    }
                }
                Spacer(modifier = Modifier.height(24.dp))
                Text(
                    text = "반려견 눈 사진을 업로드하여 진단을 시작하세요.",
                    style = MaterialTheme.typography.bodyLarge.copy(
                        fontWeight = FontWeight.Medium,
                        fontFamily = KCCGanpan,
                        fontSize = 20.sp
                    ),
                    modifier = Modifier.padding(bottom = 24.dp),
                    textAlign = TextAlign.Center,
                    color = MaterialTheme.colorScheme.onSurface // ✅ 테마 색상 적용
                )
                Button(
                    onClick = { showDialog = true },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                ) {
                    Text(
                        text = "사진 업로드",
                        fontSize = 22.sp,
                        fontFamily = KCCGanpan,
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                }
                if (selectedImageUri != null || capturedBitmap != null) {
                    Spacer(modifier = Modifier.height(16.dp))
                    Button(
                        onClick = onDiagnose,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary),
                        shape = RoundedCornerShape(12.dp),
                        enabled = !isLoading
                    ) {
                        if (isLoading) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(24.dp),
                                color = MaterialTheme.colorScheme.onSecondary,
                                strokeWidth = 2.dp
                            )
                        } else {
                            Text(
                                text = "검사 시작하기",
                                fontSize = 22.sp,
                                fontFamily = KCCGanpan,
                                color = MaterialTheme.colorScheme.onSecondary
                            )
                        }
                    }
                }
                Spacer(modifier = Modifier.height(32.dp))

                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.Start
                ) {
                    Text(
                        text = "사진 촬영 가이드",
                        // ✅ [수정] 테마의 기본 텍스트 색상을 사용합니다.
                        color = MaterialTheme.colorScheme.onSurface,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = KCCGanpan
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = """
                    - 플래시나 강한 조명을 피하고, 자연광에서 촬영하세요.
                    - 주변 털이나 그림자가 눈을 가리지 않도록 해주세요.
                    - 카메라가 흔들리지 않도록 가까이에서 촬영하세요.
                    """.trimIndent(),
                        // ✅ [수정] 기본 텍스트 색상에 투명도를 주어 살짝 연하게 만듭니다.
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                        fontSize = 18.sp,
                        lineHeight = 24.sp,
                        fontFamily = KCCGanpan
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.Start
                ) {
                    Text(
                        text = "진단 안내",
                        color = MaterialTheme.colorScheme.onSurface,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = KCCGanpan
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = """
                    - 본 앱의 결과는 참고용이며, 실제 수의사의 진단을 대체할 수 없습니다.
                    - 이상 소견이 보이면 반드시 동물병원을 방문하세요.
                    """.trimIndent(),
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                        fontSize = 18.sp,
                        lineHeight = 24.sp,
                        fontFamily = KCCGanpan
                    )
                }
            }
        }

        if (isLoading) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.5f)),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    CircularProgressIndicator(color = Color.White)
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "분석중입니다...",
                        color = Color.White,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = KCCGanpan
                    )
                }
            }
        }

        if (showDialog) {
            AlertDialog(
                onDismissRequest = { showDialog = false },
                title = { Text("사진 선택", fontFamily = KCCGanpan, fontSize = 20.sp, color = MaterialTheme.colorScheme.onSurface) },
                text = { Text("사진을 업로드할 방법을 선택하세요.", fontFamily = KCCGanpan, fontSize = 16.sp, color = MaterialTheme.colorScheme.onSurfaceVariant) },
                confirmButton = {
                    TextButton(onClick = {
                        showDialog = false
                        galleryLauncher.launch("image/*")
                    }) {
                        // ✅ [수정] 버튼 텍스트 색상을 테마의 primary 색상으로 지정합니다.
                        Text("갤러리에서 선택", fontFamily = KCCGanpan, fontSize = 18.sp, color = MaterialTheme.colorScheme.primary)
                    }
                },
                dismissButton = {
                    TextButton(onClick = {
                        showDialog = false
                        cameraLauncher.launch(null)
                    }) {
                        Text("카메라로 촬영", fontFamily = KCCGanpan, fontSize = 18.sp, color = MaterialTheme.colorScheme.primary)
                    }
                }
            )
        }
    }
}
