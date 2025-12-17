package kr.co.example.dogtor.ui.skin

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
import androidx.compose.material.icons.outlined.Pets // 🐾 [변경] 피부병 아이콘으로 변경
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
import kr.co.example.dogtor.ui.theme.KCCGanpan // 폰트 임포트

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SkinDiagnosisScreen(
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
                            // 🐾 [변경] 피부병 아이콘으로 변경 (Eyes 대신 Pets)
                            Icon(
                                imageVector = Icons.Outlined.Pets,
                                contentDescription = "피부병 진단",
                                tint = MaterialTheme.colorScheme.onPrimary,
                                modifier = Modifier.size(28.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "피부병 진단", // 🐾 [변경] 텍스트 변경
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
                        .border(1.dp, Color.LightGray, RoundedCornerShape(12.dp)),
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
                    text = "반려견 피부 사진을 업로드하여 진단을 시작하세요.", // 🐾 [변경] 텍스트 변경
                    style = MaterialTheme.typography.bodyLarge.copy(
                        fontWeight = FontWeight.Medium,
                        fontFamily = KCCGanpan,
                        fontSize = 20.sp
                    ),
                    modifier = Modifier.padding(bottom = 24.dp),
                    textAlign = TextAlign.Center
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
                        color = MaterialTheme.colorScheme.onSurface,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = KCCGanpan
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = """
                    - 플래시나 강한 조명을 피하고, 자연광에서 촬영하세요.
                    - 피부 병변 부위가 명확히 보이도록 가까이에서 촬영하세요.
                    - 카메라가 흔들리지 않도록 고정하고, 선명하게 찍어주세요.
                    """.trimIndent(), // 🐾 [변경] 피부병 관련 가이드로 텍스트 변경
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
                title = { Text("사진 선택", fontFamily = KCCGanpan, fontSize = 20.sp) },
                text = { Text("사진을 업로드할 방법을 선택하세요.", fontFamily = KCCGanpan, fontSize = 16.sp) },
                confirmButton = {
                    TextButton(onClick = {
                        showDialog = false
                        galleryLauncher.launch("image/*")
                    }) {
                        Text("갤러리에서 선택", fontFamily = KCCGanpan, fontSize = 18.sp)
                    }
                },
                dismissButton = {
                    TextButton(onClick = {
                        showDialog = false
                        cameraLauncher.launch(null)
                    }) {
                        Text("카메라로 촬영", fontFamily = KCCGanpan, fontSize = 18.sp)
                    }
                }
            )
        }
    }
}