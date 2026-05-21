# Dogtor

AI 기반 반려견 건강 체크 Android 앱입니다.
반려견의 눈과 피부 사진을 분석해 건강 이상 가능성을 확인하고, AI 챗봇 상담과 주변 동물병원 안내 기능을 제공합니다.

## 프로젝트 소개

Dogtor는 반려견 보호자가 병원에 방문하기 전, 사진을 통해 간단히 상태를 확인할 수 있도록 만든 모바일 앱입니다.
AI 진단 결과는 참고용으로만 제공하며, 이상 증상이 있을 경우 동물병원 방문이 필요하다는 안내를 함께 제공합니다.

## 주요 기능

- 반려견 눈 사진 기반 건강 이상 가능성 분석
- 반려견 피부 사진 기반 건강 이상 가능성 분석
- 진단 결과와 예측 신뢰도 표시
- OpenAI API 기반 AI 챗봇 상담
- Google Maps / Places API 기반 주변 동물병원 안내
- Firebase 로그인 및 진단 기록 저장
- 마이페이지와 진단 기록 화면

## 사용 기술

- Kotlin
- Android
- Jetpack Compose
- Firebase Authentication
- Firebase Firestore
- Roboflow API
- OpenAI API
- Google Maps API
- Google Places API
- Kakao Login
- Naver Login

## 화면 구성

```text
MainMenuScreen              # 메인 메뉴
HealthAnalysisScreen        # 건강 분석 화면
EyeResultScreen             # 눈 진단 결과 화면
SkinResultScreen            # 피부 진단 결과 화면
AiDogtorScreen              # AI 챗봇 화면
HospitalMapScreen           # 주변 동물병원 지도 화면
DiagnosisHistoryScreen      # 진단 기록 화면
MyPageScreen                # 마이페이지
AuthScreen                  # 로그인 화면
```

## AI 진단 모델

### 눈 진단

- 데이터셋: Dog Eye Problems Detection
- 분류 항목: 정상, 결막염, 안검내반, 눈꺼풀 종괴
- 사용 API: Roboflow API

### 피부 진단

- 데이터셋: dog-skin-disease-dataset
- 분류 항목: 정상, 세균성 피부염, 곰팡이성 감염, 과민성 피부염
- 사용 API: Roboflow API

## 실행 전 필요한 설정

API 키와 민감한 설정값은 코드에 직접 넣지 않고 `gradle.properties` 또는 별도 설정 파일에서 관리합니다.

필요한 값 예시는 다음과 같습니다.

```properties
PAWCARE_API_BASE_URL=...
MAPS_API_KEY=...
KAKAO_NATIVE_APP_KEY=...
NAVER_CLIENT_ID=...
NAVER_CLIENT_SECRET=...
NAVER_CLIENT_NAME=Dogtor
```

Firebase를 사용하는 경우 `google-services.json` 파일도 필요합니다.
단, 이 파일은 공개 저장소에 올리지 않아야 합니다.

## 실행 방법

1. Android Studio에서 프로젝트를 엽니다.
2. 필요한 API 키와 Firebase 설정 파일을 추가합니다.
3. Gradle Sync를 실행합니다.
4. 에뮬레이터 또는 Android 기기에서 실행합니다.

## 폴더 구조

```text
app/src/main/java/kr/co/example/dogtor/
├─ data/          # Firebase 진단 기록 저장소
├─ model/         # 진단 기록, 진단 타입 모델
├─ ui/            # Compose 화면
├─ util/          # 이미지 변환 유틸
├─ MainActivity.kt
└─ Labels.kt

docs/             # 발표자료와 데모 영상
```

## 유의사항

이 앱의 AI 진단 결과는 참고용입니다.
실제 수의사의 진단을 대체할 수 없으며, 반려견에게 이상 증상이 있으면 동물병원에 방문해야 합니다.

## 배운 점

- Jetpack Compose를 사용해 Android 앱 화면을 구성하는 방법을 익혔습니다.
- 사진 업로드 후 외부 AI API로 분석 결과를 받아오는 흐름을 구현했습니다.
- OpenAI API, Roboflow API, Google Maps API 등 여러 외부 API를 앱 안에서 함께 사용하는 경험을 했습니다.
- Firebase 로그인과 Firestore를 활용해 사용자별 진단 기록을 저장하는 구조를 만들어 보았습니다.
- API 키와 인증 파일은 공개 저장소에 올리지 않고 별도로 관리해야 한다는 점을 배웠습니다.

## 개발자

정유진
