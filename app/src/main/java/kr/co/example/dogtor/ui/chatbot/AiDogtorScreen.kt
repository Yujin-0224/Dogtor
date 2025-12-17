package kr.co.example.dogtor.ui.chatbot

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import kr.co.example.dogtor.R
import kr.co.example.dogtor.ui.theme.KCCGanpan

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AiDogtorScreen(
    chatHistory: List<ChatMessage>,
    onSendMessage: (String) -> Unit,
    onBack: () -> Unit,
    isLoading: Boolean
) {
    var userInput by remember { mutableStateOf("") }
    val listState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(chatHistory.size) {
        if (chatHistory.isNotEmpty()) {
            coroutineScope.launch {
                listState.animateScrollToItem(chatHistory.size - 1)
            }
        }
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("AI Dogtor", style = MaterialTheme.typography.titleLarge.copy(fontFamily = KCCGanpan)) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "뒤로가기")
                    }
                },
                // ✅ 상단 바 색상을 테마에 맞게 수정
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary,
                    navigationIconContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        },
        // ✅ 전체 배경색을 테마에 맞게 수정
        containerColor = MaterialTheme.colorScheme.background
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            LazyColumn(
                state = listState,
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                contentPadding = PaddingValues(vertical = 16.dp)
            ) {
                items(chatHistory) { message ->
                    if (message.isUser) {
                        UserChatItem(text = message.message)
                    } else {
                        AiChatItem(text = message.message)
                    }
                }
            }
            MessageInput(
                onSendMessage = {
                    if (it.isNotBlank()) {
                        onSendMessage(it)
                        userInput = ""
                    }
                },
                isLoading = isLoading
            )
        }
    }
}

@Composable
fun AiChatItem(text: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.Top,
        horizontalArrangement = Arrangement.Start
    ) {
        Image(
            painter = painterResource(id = R.drawable.chatbot),
            contentDescription = "AI Dogtor Profile",
            modifier = Modifier
                .size(60.dp)
                .clip(CircleShape)
        )
        Spacer(modifier = Modifier.width(8.dp))

        Surface(
            shape = RoundedCornerShape(20.dp, 20.dp, 20.dp, 4.dp),
            // ✅ AI 말풍선 배경색을 테마에 맞게 수정
            color = MaterialTheme.colorScheme.surface,
            modifier = Modifier.weight(1f, fill = false)
        ) {
            val annotatedText = buildAnnotatedString {
                val parts = text.split("(?=\\*\\*)|(?<=\\*\\*)".toRegex())
                var isBold = false
                for (part in parts) {
                    if (part == "**") {
                        isBold = !isBold
                    } else if (isBold) {
                        withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
                            append(part)
                        }
                    } else {
                        append(part)
                    }
                }
            }
            Text(
                text = annotatedText,
                modifier = Modifier.padding(16.dp),
                style = MaterialTheme.typography.bodyMedium.copy(
                    // ✅ AI 말풍선 텍스트 색상을 테마에 맞게 수정
                    color = MaterialTheme.colorScheme.onSurface
                )
            )
        }
    }
}

@Composable
fun UserChatItem(text: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.End
    ) {
        Surface(
            shape = RoundedCornerShape(20.dp, 20.dp, 4.dp, 20.dp),
            // ✅ 사용자 말풍선 배경색은 primary(연노랑)으로 유지
            color = MaterialTheme.colorScheme.primary,
        ) {
            Text(
                text = text,
                modifier = Modifier.padding(16.dp),
                // ✅ 사용자 말풍선 텍스트 색상은 onPrimary(갈색)으로 유지
                color = MaterialTheme.colorScheme.onPrimary,
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}

@Composable
fun MessageInput(
    onSendMessage: (String) -> Unit,
    isLoading: Boolean
) {
    var text by remember { mutableStateOf("") }

    // ✅ 입력창 배경에 그림자와 색상을 적용
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shadowElevation = 8.dp,
        color = MaterialTheme.colorScheme.surface // 테마의 surface 색상 적용
    ) {
        Row(
            modifier = Modifier
                .padding(horizontal = 16.dp, vertical = 8.dp) // 패딩 조절
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // ✅ 텍스트 입력 필드 스타일 개선
            OutlinedTextField(
                value = text,
                onValueChange = { text = it },
                modifier = Modifier.weight(1f),
                placeholder = { Text("메시지를 입력하세요...") },
                enabled = !isLoading,
                textStyle = MaterialTheme.typography.bodyMedium,
                shape = RoundedCornerShape(24.dp), // 둥근 모서리
                colors = OutlinedTextFieldDefaults.colors(
                    unfocusedBorderColor = Color.Transparent,
                    focusedBorderColor = MaterialTheme.colorScheme.primary,
                    unfocusedContainerColor = MaterialTheme.colorScheme.background, // 배경색과 동일하게
                    focusedContainerColor = MaterialTheme.colorScheme.background
                )
            )
            Spacer(modifier = Modifier.width(8.dp))

            if (isLoading) {
                CircularProgressIndicator(modifier = Modifier.size(40.dp))
            } else {
                IconButton(
                    onClick = {
                        onSendMessage(text)
                        text = ""
                    },
                    enabled = text.isNotBlank(),
                    // ✅ 전송 버튼 색상 지정
                    colors = IconButtonDefaults.iconButtonColors(
                        contentColor = MaterialTheme.colorScheme.primary
                    )
                ) {
                    Icon(Icons.AutoMirrored.Filled.Send, contentDescription = "전송", modifier = Modifier.size(28.dp))
                }
            }
        }
    }
}

