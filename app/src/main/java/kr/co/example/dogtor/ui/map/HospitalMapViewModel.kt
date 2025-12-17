package kr.co.example.dogtor.ui.map

import android.annotation.SuppressLint
import android.app.Application
import android.location.Location
import android.net.Uri
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.maps.model.LatLng
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.CircularBounds
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.api.net.PlacesClient
import com.google.android.libraries.places.api.net.SearchNearbyRequest
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kr.co.example.dogtor.BuildConfig
import java.util.Locale

data class Hospital(
    val id: String,
    val name: String,
    val address: String,
    val latLng: LatLng,
    val phoneNumber: String?,
    val websiteUri: Uri?
)

data class MapState(
    val currentLocation: Location? = null,
    val nearbyHospitals: List<Hospital> = emptyList(),
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)

@SuppressLint("MissingPermission")
class HospitalMapViewModel(application: Application) : AndroidViewModel(application) {

    private val fusedLocationClient = LocationServices.getFusedLocationProviderClient(application)
    private val placesClient: PlacesClient

    private val _uiState = MutableStateFlow(MapState(isLoading = true))
    val uiState = _uiState.asStateFlow()

    init {
        if (!Places.isInitialized()) {
            // SDK 초기화 시점에 언어를 한국어로 전역 설정
            Places.initialize(application, BuildConfig.MAPS_API_KEY, Locale.KOREAN)
        }
        placesClient = Places.createClient(application)
    }

    // 처음 앱 진입 시 호출되는 함수
    fun fetchInitialLocationAndSearch() {
        _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)
        fusedLocationClient.getCurrentLocation(Priority.PRIORITY_HIGH_ACCURACY, null)
            .addOnSuccessListener { location: Location? ->
                if (location != null) {
                    Log.d("MapViewModel", "Current Location Fetched: Lat=${location.latitude}, Lng=${location.longitude}")
                    _uiState.value = _uiState.value.copy(currentLocation = location)
                    searchHospitalsAtLocation(location) // 위치 기반으로 병원 검색
                } else {
                    _uiState.value = MapState(isLoading = false, errorMessage = "현재 위치를 가져올 수 없습니다.")
                }
            }.addOnFailureListener { exception: Exception ->
                Log.e("MapViewModel", "Location fetch failed", exception)
                _uiState.value = MapState(isLoading = false, errorMessage = "위치 정보를 가져오는 데 실패했습니다.")
            }
    }

    // UI에서 지도 중앙 위치를 받아 검색할 수 있는 공개 함수
    fun searchHospitalsAtLocation(location: Location) {
        _uiState.value = _uiState.value.copy(isLoading = true)

        val placeFields = listOf(
            Place.Field.ID,
            Place.Field.NAME,
            Place.Field.ADDRESS,
            Place.Field.LAT_LNG,
            Place.Field.PHONE_NUMBER,
            Place.Field.WEBSITE_URI
        )

        // 현재 위치를 중심으로 5000미터(5km) 반경의 원형 범위를 생성
        val bounds = CircularBounds.newInstance(LatLng(location.latitude, location.longitude), 5000.0)

        val request = SearchNearbyRequest.builder(bounds, placeFields)
            .setMaxResultCount(20)
            .setIncludedTypes(listOf("veterinary_care"))
            .build()

        placesClient.searchNearby(request).addOnSuccessListener { response ->
            val hospitals = response.places.mapNotNull { place ->
                place.latLng?.let { latLng ->
                    Hospital(
                        id = place.id ?: "unknown",
                        name = place.name ?: "이름 없음",
                        address = place.address ?: "주소 없음",
                        latLng = latLng,
                        phoneNumber = place.phoneNumber,
                        websiteUri = place.websiteUri
                    )
                }
            }
            _uiState.value = _uiState.value.copy(isLoading = false, nearbyHospitals = hospitals)
        }.addOnFailureListener { exception: Exception ->
            Log.e("MapViewModel", "SearchNearby failed", exception)
            _uiState.value = _uiState.value.copy(isLoading = false, errorMessage = "주변 동물병원을 검색하는 중 오류가 발생했습니다.")
        }
    }
}
