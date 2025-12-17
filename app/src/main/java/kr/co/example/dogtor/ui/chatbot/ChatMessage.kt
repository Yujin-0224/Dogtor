package kr.co.example.dogtor.ui.chatbot

// 메시지 데이터를 담을 클래스
data class ChatMessage(
    val message: String,
    val isUser: Boolean, // 사용자가 보낸 메시지인지(true), AI가 보낸 메시지인지(false) 구분
    // ✅ [추가] AI가 답변을 생성 중인지 여부를 나타내는 상태
    val isLoading: Boolean = false
)
