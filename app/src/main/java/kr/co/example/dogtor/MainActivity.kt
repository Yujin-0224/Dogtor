package kr.co.example.dogtor

import android.graphics.Bitmap
import android.os.Bundle
import android.util.Base64
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.*
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kr.co.example.dogtor.ui.MainMenuScreen
import kr.co.example.dogtor.ui.chatbot.AiDogtorScreen
import kr.co.example.dogtor.ui.chatbot.ChatMessage
import kr.co.example.dogtor.ui.eye.EyeDiagnosisScreen
import kr.co.example.dogtor.ui.eye.EyeResultScreen
import kr.co.example.dogtor.ui.map.HospitalMapScreen
import kr.co.example.dogtor.ui.skin.SkinDiagnosisScreen
import kr.co.example.dogtor.ui.skin.SkinResultScreen
import kr.co.example.dogtor.ui.theme.DogtorTheme
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.logging.HttpLoggingInterceptor
import org.json.JSONObject
import java.io.ByteArrayOutputStream

class MainActivity : ComponentActivity() {

    // --- 눈병 모델 정보 ---
    private val EYE_API_KEY = "y8pOVn512GTprmKjljSQ"
    private val EYE_MODEL_ID = "dog-eye-problems-detection"
    private val EYE_MODEL_VERSION = "4"

    // --- 피부병 모델 정보 ---
    private val SKIN_API_KEY = "y8pOVn512GTprmKjljSQ" // 동일한 키를 사용
    private val SKIN_MODEL_ID = "dog-skin-disease-dataset"
    private val SKIN_MODEL_VERSION = "2"

    // --- 챗봇 모델 정보 ---
    private val OPENAI_API_KEY = BuildConfig.OPENAI_API_KEY

    // ✅ [추가] 네트워크 요청/응답을 자세히 보기 위한 OkHttpClient 인스턴스 (재사용)
    private val httpClient: OkHttpClient by lazy {
        val logging = HttpLoggingInterceptor()
        logging.setLevel(HttpLoggingInterceptor.Level.BODY)
        OkHttpClient.Builder()
            .addInterceptor(logging)
            .build()
    }

    // --- 눈병 데이터 맵 ---
    private val eyeDiseaseKoreanMap = mapOf(
        "conjunctivitis" to "결막염",
        "entropion" to "안검내반 (눈꺼풀속말림)",
        "eyelid_lump" to "눈꺼풀 종괴 (혹)",
        "healthy" to "정상",
        "null" to "알 수 없음"
    )

    private val eyeDiseaseDescriptionMap = mapOf(
        "conjunctivitis" to """
            결막염은 눈을 감싸고 있는 투명한 막인 결막에 염증이 생긴 상태를 말합니다.

            ⦿ 주요 특징:
            - 눈의 충혈 및 부어오름
            - 눈물, 끈적한 분비물 (눈곱) 증가
            - 가려움으로 인해 눈을 비비거나 찡그리는 행동

            ⦿ 치료 및 관리:
            세균, 바이러스, 알레르기 등 원인이 다양하므로 정확한 진단이 중요합니다. 보통 항생제 안약이나 안연고를 처방받아 치료하며, 눈 주변을 깨끗하게 유지해주는 것이 좋습니다.
            """.trimIndent(),
        "entropion" to """
            안검내반은 눈꺼풀이 안쪽으로 말려 들어가 속눈썹이 각막을 지속적으로 자극하는 상태입니다.

            ⦿ 주요 특징:
            - 눈물을 자주 흘리거나 눈 주변이 젖어 있음
            - 눈을 제대로 뜨지 못하고 찡그림
            - 각막 손상으로 인한 통증 및 충혈

            ⦿ 치료 및 관리:
            주로 유전적 요인으로 발생하며, 물리적인 문제이므로 수술적 교정이 필요한 경우가 대부분입니다. 방치할 경우 각막 궤양 등 심각한 합병증으로 이어질 수 있어 조기 치료가 중요합니다.
            """.trimIndent(),
        "eyelid_lump" to """
            눈꺼풀 종괴는 눈꺼풀에 생긴 모든 종류의 덩어리나 혹을 통칭하는 말입니다. 특정 질병이 아닌, 증상을 설명하는 용어입니다.

            ⦿ 주요 특징:
            - 눈꺼풀에 작은 뾰루지나 큰 덩어리가 만져짐
            - 크기나 색상은 매우 다양함

            ⦿ 종류 및 관리:
            단순한 다래끼, 콩다래끼부터 양성 종양, 악성 종양까지 가능성이 매우 다양합니다.
            덩어리의 원인을 파악하는 것이 매우 중요하므로, 반드시 동물병원에 방문하여 정확한 진단을 받아야 합니다. 필요한 경우 조직 검사를 진행할 수 있습니다.
            """.trimIndent()
    )

    // --- 피부병 데이터 맵 ---
    private val skinDiseaseKoreanMap = mapOf(
        "bacterial dermatosis" to "세균성 피부염",
        "fungal infection" to "곰팡이성 감염",
        "healthy" to "정상",
        "hypersensitivity dermatitis" to "과민성 피부염 (알레르기성 피부염)"
    )

    private val skinDiseaseDescriptionMap = mapOf(
        "bacterial dermatosis" to """
            세균성 피부염은 피부에 세균이 과도하게 증식하여 발생하는 염증성 질환입니다.

            ⦿ 주요 특징:
            - 피부가 붉어지고 가려움증, 농포 (고름집) 형성
            - 딱지, 비듬, 탈모, 악취 동반
            - 지속적으로 긁거나 핥는 행동

            ⦿ 치료 및 관리:
            항생제 복용 및 약용 샴푸를 이용한 목욕이 주요 치료법입니다. 근본적인 원인(알레르기, 호르몬 문제 등)을 찾아 함께 치료하는 것이 중요합니다.
            """.trimIndent(),
        "fungal infection" to """
            곰팡이성 감염(백선)은 피부사상균이라는 곰팡이에 감염되어 발생하는 피부병입니다. 전염성이 매우 강합니다.

            ⦿ 주요 특징:
            - 원형 또는 불규칙한 탈모 부위
            - 각질, 딱지, 붉은 발진
            - 심한 가려움증을 유발할 수 있으며, 다른 동물이나 사람에게도 전파 가능

            ⦿ 치료 및 관리:
            항진균제 연고, 약용 샴푸, 또는 경구용 약물을 사용합니다. 완치까지 시간이 걸릴 수 있으며, 환경 소독을 철저히 하여 재감염을 막아야 합니다.
            """.trimIndent(),
        "hypersensitivity dermatitis" to """
            과민성 피부염(알레르기성 피부염)은 특정 알레르겐에 대한 과민 반응으로 인해 발생하는 피부 염증입니다.

            ⦿ 주요 특징:
            - 극심한 가려움증 (특히 귀, 발, 배, 겨드랑이 등)
            - 긁거나 핥아서 생긴 붉은 발진, 염증, 탈모
            - 피부가 두꺼워지거나 색소침착이 발생할 수 있음

            ⦿ 치료 및 관리:
            알레르겐을 파악하고 회피하는 것이 가장 중요합니다. 증상 완화를 위해 항히스타민제, 스테로드, 면역억제제 등을 사용할 수 있으며, 피부 보습과 관리에 신경 써야 합니다.
            """.trimIndent()
    )


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            DogtorTheme {
                var currentScreen by remember { mutableStateOf("menu") }
                var resultText by remember { mutableStateOf("") }
                var selectedBitmap by remember { mutableStateOf<Bitmap?>(null) }
                var isLoading by remember { mutableStateOf(false) }
                var diseaseDescription by remember { mutableStateOf("") }

                // ✅ 챗봇 화면을 위한 상태 변수 추가
                val chatHistory = remember { mutableStateListOf<ChatMessage>() }
                var isChatLoading by remember { mutableStateOf(false) }

                when (currentScreen) {
                    "menu" -> MainMenuScreen { selected ->
                        when (selected) {
                            "눈병 진단" -> currentScreen = "eye_diagnosis"
                            "피부병 진단" -> currentScreen = "skin_diagnosis"
                            "근처 동물병원" -> currentScreen = "find_hospitals"
                            // ✅ AI Dogtor 메뉴 선택 시 화면 전환
                            "AI Dogtor" -> {
                                if (chatHistory.isEmpty()) {
                                    chatHistory.add(
                                        ChatMessage(
                                            """
                                        안녕하세요! 저는 🐶 **AI Dogtor**예요.
                                        
                                        강아지의 건강, 피부, 식습관, 행동 등  
                                        일상적인 궁금증을 함께 이야기할 수 있어요 💬  
                                        
                                        예를 들어 이런 질문들을 할 수 있답니다:
                                        - "강아지가 자꾸 눈을 비벼요"
                                        - "피부에 빨간 점이 생겼어요"
                                        - "밥을 잘 안 먹어요"
                                        - "자꾸 발을 핥아요"
                                        
                                        Dogtor 앱에는 이런 기능들도 있어요:
                                        🩺 눈병 진단 — 눈 사진으로 빠른 검사  
                                        🐾 피부병 진단 — 피부 사진으로 분석  
                                        🏥 근처 동물병원 — 가까운 병원 위치 확인  
                                        
                                        AI Dogtor는 반려견의 건강 정보를 도와주는 친구예요.  
                                        정확한 진단이 필요하다면 🏥 수의사에게 꼭 상담받는 걸 추천드려요!
                                                        """.trimIndent(),
                                            isUser = false
                                        )
                                    )
                                }
                                currentScreen = "ai_dogtor"
                            }

                        }
                    }

                    "eye_diagnosis" -> EyeDiagnosisScreen(
                        onBack = { currentScreen = "menu" },
                        onUpload = { bitmap -> selectedBitmap = bitmap },
                        isLoading = isLoading,
                        onDiagnose = {
                            selectedBitmap?.let { bitmap ->
                                lifecycleScope.launch {
                                    withContext(Dispatchers.Main) { isLoading = true }
                                    val (formattedResult, topClassName) = withContext(Dispatchers.IO) {
                                        detectEyeDisease(bitmap)
                                    }
                                    withContext(Dispatchers.Main) {
                                        resultText = formattedResult
                                        diseaseDescription = eyeDiseaseDescriptionMap[topClassName] ?: ""
                                        isLoading = false
                                        currentScreen = "eye_result"
                                    }
                                }
                            } ?: run {
                                resultText = "⚠️ 사진을 먼저 업로드해주세요."
                                diseaseDescription = ""
                                currentScreen = "eye_result"
                            }
                        }
                    )

                    "eye_result" -> EyeResultScreen(
                        resultText = resultText,
                        imageBitmap = selectedBitmap,
                        description = diseaseDescription,
                        onBack = { currentScreen = "eye_diagnosis" },
                        onGoToHome = { currentScreen = "menu" }
                    )

                    "find_hospitals" -> HospitalMapScreen(
                        onBack = { currentScreen = "menu" }
                    )

                    "skin_diagnosis" -> SkinDiagnosisScreen(
                        onBack = { currentScreen = "menu" },
                        onUpload = { bitmap -> selectedBitmap = bitmap },
                        isLoading = isLoading,
                        onDiagnose = {
                            selectedBitmap?.let { bitmap ->
                                lifecycleScope.launch {
                                    withContext(Dispatchers.Main) { isLoading = true }
                                    val (formattedResult, topClassName) = withContext(Dispatchers.IO) {
                                        detectSkinDisease(bitmap)
                                    }
                                    withContext(Dispatchers.Main) {
                                        resultText = formattedResult
                                        diseaseDescription = skinDiseaseDescriptionMap[topClassName] ?: ""
                                        isLoading = false
                                        currentScreen = "skin_result"
                                    }
                                }
                            } ?: run {
                                resultText = "⚠️ 사진을 먼저 업로드해주세요."
                                diseaseDescription = ""
                                currentScreen = "skin_result"
                            }
                        }
                    )

                    "skin_result" -> SkinResultScreen(
                        resultText = resultText,
                        imageBitmap = selectedBitmap,
                        description = diseaseDescription,
                        onBack = { currentScreen = "skin_diagnosis" },
                        onGoToHome = { currentScreen = "menu" }
                    )

                    // ✅ 챗봇 화면 로직 추가
                    "ai_dogtor" -> AiDogtorScreen(
                        chatHistory = chatHistory,
                        isLoading = isChatLoading,
                        onBack = { currentScreen = "menu" },
                        onSendMessage = { userInput ->
                            chatHistory.add(ChatMessage(userInput, isUser = true))
                            isChatLoading = true

                            lifecycleScope.launch {
                                val aiResponse = withContext(Dispatchers.IO) {
                                    getChatbotResponse(userInput)
                                }
                                withContext(Dispatchers.Main) {
                                    chatHistory.add(ChatMessage(aiResponse, isUser = false))
                                    isChatLoading = false
                                }
                            }
                        }
                    )
                }
            }
        }
    }

    private fun bitmapToBase64(bitmap: Bitmap): String {
        val byteArrayOutputStream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream)
        val byteArray = byteArrayOutputStream.toByteArray()
        return Base64.encodeToString(byteArray, Base64.DEFAULT)
    }

    private fun getPostposition(word: String): String {
        if (word.isEmpty()) return "가"
        val lastChar = word.last()
        return if ((lastChar.code - 0xAC00) % 28 > 0) "이" else "가"
    }

    private fun getChatbotResponse(userInput: String): String {
        val client = httpClient
        val apiUrl = "https://api.openai.com/v1/responses"

        val systemPrompt = """
You are **"AI Dogtor"**, a friendly and knowledgeable puppy doctor 🐶
who lives inside the Dogtor app to help dog guardians with everyday questions about their dogs’ health and behavior.

---

### 🐾 Core Behavior Rules

1. **Persona**
   - Speak like a warm, kind, slightly playful puppy doctor.
   - Use soft and caring expressions with gentle empathy.
   - Write naturally, like talking to a friend who loves their dog.
   - Use 반말+존댓말 혼합체 (“~해요”, “~할 수도 있어요”) tone.

2. **Scope**
   - Talk only about dogs: health, habits, food, grooming, emotions, and care.
   - Give gentle, practical explanations or helpful advice.
   - Avoid making direct medical diagnoses or prescriptions.
   - Only when a situation sounds **serious or dangerous** (ex: bleeding, swelling, pain, not eating for days), add:
     > “정확한 진단과 치료를 위해 가까운 동물병원에 내원해 수의사에게 상담받는 게 좋아요 🏥”

3. **Tone**
   - Sound warm, conversational, and helpful.
   - Be encouraging and positive, never cold or overly formal.
   - Use a few emojis like 🐶, 💕, 💡, 🩺, 🏥 when appropriate — but not too many.

4. **Unrelated Questions**
   - If the user asks something not related to dogs (like human food, weather, daily life), kindly decline:
     > “저는 강아지 건강을 도와주는 AI Dogtor예요 🐾  
     > 강아지와 관련된 이야기를 해주시면 기쁘게 도와드릴게요!”

5. **Language & Format**
   - Always reply in Korean.
   - Use short, natural sentences.
   - Break long replies into short paragraphs or bullet points.
   - Emphasize tips or cautions with 💡 or ❗️

---
💬 Example behaviors

**① 눈 관련 질문**
> 강아지 눈에서 눈물이 많이 나요  
> → “눈물이 자주 나면 알레르기나 먼지 자극일 수도 있어요.  
> 눈 주변을 깨끗하게 닦아주고, 며칠 동안 상태를 지켜보세요 👀  
> 그래도 계속 심해지면 병원에 가보는 게 좋아요.”

**② 피부 관련 질문**
> 피부가 붉어요  
> → “붉은 부위가 있다면 가려움이나 알레르기 때문일 수도 있어요.  
> 너무 심하게 긁지 않게 주의해주시고, 보습제를 사용해보세요 💧”

**③ 무관한 질문**
> 오늘 점심 뭐 먹을까?  
> → “저는 강아지 건강을 도와주는 AI Dogtor예요 🐶  
> 강아지와 관련된 이야기를 해주시면 기쁘게 도와드릴게요!”
""".trimIndent()

        // ✅ 사용자 입력도 함께 전달
        val jsonBody = JSONObject().apply {
            put("model", "gpt-4o-mini")
            put("input", "$systemPrompt\n\n사용자의 질문: $userInput")
        }

        val mediaType = "application/json; charset=utf-8".toMediaType()
        val requestBody = jsonBody.toString().toRequestBody(mediaType)

        val request = Request.Builder()
            .url(apiUrl)
            .post(requestBody)
            .addHeader("Authorization", "Bearer $OPENAI_API_KEY")
            .addHeader("Content-Type", "application/json")
            .build()

        return try {
            val response = client.newCall(request).execute()
            val responseBody = response.body?.string()
            if (response.isSuccessful && responseBody != null) {
                parseResponseJson(responseBody)
            } else {
                "오류가 발생했습니다: ${response.code}\n응답: $responseBody"
            }
        } catch (e: Exception) {
            "인터넷 연결을 확인해주세요. (${e.message})"
        }
    }


    // ✅ Responses API의 실제 구조에 맞게 파싱
    private fun parseResponseJson(responseBody: String): String {
        return try {
            val json = JSONObject(responseBody)
            val outputArray = json.optJSONArray("output")
            if (outputArray != null && outputArray.length() > 0) {
                val firstOutput = outputArray.getJSONObject(0)
                val contentArray = firstOutput.optJSONArray("content")
                if (contentArray != null && contentArray.length() > 0) {
                    contentArray.getJSONObject(0).optString("text", "답변을 생성하지 못했습니다.")
                } else "답변을 생성하지 못했습니다."
            } else "답변을 생성하지 못했습니다."
        } catch (e: Exception) {
            "답변 형식을 파싱하는 데 실패했습니다. (${e.message})"
        }
    }



    private fun detectEyeDisease(bitmap: Bitmap): Pair<String, String> {
        val client = httpClient // ✅ 수정: 공용 클라이언트 사용
        val base64Image = bitmapToBase64(bitmap)
        val apiUrl = "https://detect.roboflow.com/$EYE_MODEL_ID/$EYE_MODEL_VERSION?api_key=$EYE_API_KEY"
        val mediaType = "application/x-www-form-urlencoded".toMediaType()
        val requestBody = base64Image.toRequestBody(mediaType)
        val request = Request.Builder()
            .url(apiUrl)
            .post(requestBody)
            .addHeader("Content-Type", "application/x-www-form-urlencoded")
            .build()
        try {
            val response = client.newCall(request).execute()
            val responseBody = response.body?.string()
            return if (response.isSuccessful && responseBody != null) {
                parseEyeResponse(responseBody)
            } else {
                Pair("진단 중 오류가 발생했습니다. (코드: ${response.code})", "")
            }
        } catch (e: Exception) {
            return Pair("인터넷 연결을 확인해주세요.", "")
        }
    }

    private fun parseEyeResponse(responseBody: String): Pair<String, String> {
        val jsonObject = JSONObject(responseBody)
        val predictionsJson = jsonObject.optJSONArray("predictions")
        val healthyMessage = "✅ 분석 결과, 특별한 이상 소견이 발견되지 않았습니다. 눈이 건강해 보입니다."

        if (predictionsJson == null || predictionsJson.length() == 0) {
            return Pair(healthyMessage, "healthy")
        }

        var topPrediction: JSONObject? = null
        var maxConfidence = -1.0
        for (i in 0 until predictionsJson.length()) {
            val pred = predictionsJson.getJSONObject(i)
            if (pred.getDouble("confidence") > maxConfidence) {
                maxConfidence = pred.getDouble("confidence")
                topPrediction = pred
            }
        }

        val className = topPrediction?.getString("class")?.lowercase() ?: "null"
        val koreanName = eyeDiseaseKoreanMap[className] ?: className
        val resultText = if (koreanName == "정상") {
            healthyMessage
        } else {
            val postposition = getPostposition(koreanName)
            "AI 분석 결과, ‘${koreanName}’${postposition} 의심됩니다.\n(신뢰도: ${String.format("%.1f%%", maxConfidence * 100)})\n\n정확한 진단은 반드시 동물병원에서 받아보세요."
        }

        return Pair(resultText, className)
    }

    private fun detectSkinDisease(bitmap: Bitmap): Pair<String, String> {
        val client = httpClient // ✅ 수정: 공용 클라이언트 사용
        val base64Image = bitmapToBase64(bitmap)
        val apiUrl = "https://detect.roboflow.com/$SKIN_MODEL_ID/$SKIN_MODEL_VERSION?api_key=$SKIN_API_KEY"
        val mediaType = "application/x-www-form-urlencoded".toMediaType()
        val requestBody = base64Image.toRequestBody(mediaType)
        val request = Request.Builder()
            .url(apiUrl)
            .post(requestBody)
            .addHeader("Content-Type", "application/x-www-form-urlencoded")
            .build()
        try {
            val response = client.newCall(request).execute()
            val responseBody = response.body?.string()
            return if (response.isSuccessful && responseBody != null) {
                parseSkinResponse(responseBody)
            } else {
                Pair("진단 중 오류가 발생했습니다. (코드: ${response.code})", "")
            }
        } catch (e: Exception) {
            return Pair("인터넷 연결을 확인해주세요.", "")
        }
    }

    private fun parseSkinResponse(responseBody: String): Pair<String, String> {
        val jsonObject = JSONObject(responseBody)
        val predictionsJson = jsonObject.optJSONArray("predictions")
        val healthyMessage = "✅ 분석 결과, 특별한 이상 소견이 발견되지 않았습니다. 피부가 건강해 보입니다."

        if (predictionsJson == null || predictionsJson.length() == 0) {
            return Pair(healthyMessage, "healthy")
        }

        var topPrediction: JSONObject? = null
        var maxConfidence = -1.0
        for (i in 0 until predictionsJson.length()) {
            val pred = predictionsJson.getJSONObject(i)
            if (pred.getDouble("confidence") > maxConfidence) {
                maxConfidence = pred.getDouble("confidence")
                topPrediction = pred
            }
        }

        val className = topPrediction?.getString("class")?.lowercase() ?: "null"
        val koreanName = skinDiseaseKoreanMap[className] ?: className
        val resultText = if (koreanName == "정상") {
            healthyMessage
        } else {
            val postposition = getPostposition(koreanName)
            "AI 분석 결과, ‘${koreanName}’${postposition} 의심됩니다.\n(신뢰도: ${String.format("%.1f%%", maxConfidence * 100)})\n\n정확한 진단은 반드시 동물병원에서 받아보세요."
        }

        return Pair(resultText, className)
    }
}

