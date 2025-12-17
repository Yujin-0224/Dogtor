package kr.co.example.dogtor.ui.map

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Canvas
import android.location.Location
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.DrawableRes
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.DrawableCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import com.google.android.gms.maps.model.BitmapDescriptor
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.*
import kotlinx.coroutines.launch
import kr.co.example.dogtor.R
import kr.co.example.dogtor.ui.theme.KCCGanpan

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HospitalMapScreen(
    onBack: () -> Unit,
    viewModel: HospitalMapViewModel = viewModel()
) {
    val context = LocalContext.current
    val uiState by viewModel.uiState.collectAsState()

    var hasPermission by remember {
        mutableStateOf(ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED)
    }
    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = { isGranted -> hasPermission = isGranted }
    )
    LaunchedEffect(hasPermission) {
        if (hasPermission) {
            viewModel.fetchInitialLocationAndSearch()
        }
    }

    val cameraPositionState = rememberCameraPositionState()
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val scope = rememberCoroutineScope()
    var selectedHospital by remember { mutableStateOf<Hospital?>(null) }

    LaunchedEffect(cameraPositionState.isMoving) {
        if (!cameraPositionState.isMoving) {
            val newLocation = Location("").apply {
                latitude = cameraPositionState.position.target.latitude
                longitude = cameraPositionState.position.target.longitude
            }
            if (uiState.currentLocation != null && newLocation.distanceTo(uiState.currentLocation!!) > 500) {
                viewModel.searchHospitalsAtLocation(newLocation)
            }
        }
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("근처 동물병원", fontFamily = KCCGanpan, color = MaterialTheme.colorScheme.onPrimary) },
                navigationIcon = { IconButton(onClick = onBack) { Icon(Icons.AutoMirrored.Filled.ArrowBack, "뒤로가기", tint = MaterialTheme.colorScheme.onPrimary) } },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = MaterialTheme.colorScheme.primary)
            )
        }
    ) { innerPadding ->
        Box(modifier = Modifier.fillMaxSize().padding(innerPadding)) {
            if (hasPermission) {
                uiState.currentLocation?.let { location ->
                    LaunchedEffect(location) {
                        cameraPositionState.position = CameraPosition.fromLatLngZoom(LatLng(location.latitude, location.longitude), 15f)
                    }

                    GoogleMap(
                        modifier = Modifier.fillMaxSize(),
                        cameraPositionState = cameraPositionState,
                        properties = MapProperties(isMyLocationEnabled = true)
                    ) {
                        val hospitalMarkerIcon = remember(context) {
                            bitmapDescriptorFromDrawableWithTint(
                                context = context,
                                drawableId = R.drawable.dog_paw_marker,
                                tintColor = Color.Red,
                                width = 160,
                                height = 160
                            )
                        }

                        Marker(
                            state = MarkerState(position = LatLng(location.latitude, location.longitude)),
                            title = "현재 위치",
                            icon = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE)
                        )

                        uiState.nearbyHospitals.forEach { hospital ->
                            Marker(
                                state = MarkerState(position = hospital.latLng),
                                title = hospital.name,
                                icon = hospitalMarkerIcon,
                                anchor = Offset(0.5f, 0.5f),
                                // ✅ [수정] onInfoWindowClick을 onClick으로 변경
                                onClick = {
                                    selectedHospital = hospital
                                    scope.launch { sheetState.expand() }
                                    true // true를 반환하여 기본 동작(카메라 이동)을 막습니다.
                                }
                            )
                        }
                    }
                }

                if (uiState.isLoading) {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                }

            } else {
                PermissionRequestUI { permissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION) }
            }
        }
    }

    if (selectedHospital != null) {
        ModalBottomSheet(
            onDismissRequest = { selectedHospital = null },
            sheetState = sheetState
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp, vertical = 16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(selectedHospital!!.name, style = MaterialTheme.typography.headlineSmall, fontFamily = KCCGanpan)
                Spacer(Modifier.height(8.dp))
                Text(selectedHospital!!.address, style = MaterialTheme.typography.bodyLarge, textAlign = TextAlign.Center)
                Spacer(Modifier.height(24.dp))

                Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                    selectedHospital!!.phoneNumber?.let { number ->
                        Button(onClick = {
                            val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:$number"))
                            context.startActivity(intent)
                        }) {
                            Icon(Icons.Default.Call, contentDescription = "전화 걸기")
                            Spacer(Modifier.width(8.dp))
                            Text("전화", fontFamily = KCCGanpan)
                        }
                    }
                    selectedHospital!!.websiteUri?.let { uri ->
                        Button(onClick = {
                            val intent = Intent(Intent.ACTION_VIEW, uri)
                            context.startActivity(intent)
                        }) {
                            Icon(Icons.Default.Language, contentDescription = "웹사이트 열기")
                            Spacer(Modifier.width(8.dp))
                            Text("웹사이트", fontFamily = KCCGanpan)
                        }
                    }
                }
                Spacer(Modifier.height(24.dp))
            }
        }
    }
}

@Composable
fun PermissionRequestUI(onRequestPermission: () -> Unit) {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            imageVector = Icons.Default.LocationOn,
            contentDescription = "위치 권한 필요",
            modifier = Modifier.size(64.dp),
            tint = MaterialTheme.colorScheme.primary
        )
        Spacer(Modifier.height(16.dp))
        Text(
            text = "내 위치 및 주변 병원 검색을 위해 위치 권한이 필요합니다.",
            style = MaterialTheme.typography.titleMedium.copy(fontFamily = KCCGanpan),
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(horizontal = 24.dp)
        )
        Spacer(Modifier.height(24.dp))
        Button(onClick = onRequestPermission) {
            Text("권한 요청하기", fontFamily = KCCGanpan)
        }
    }
}

fun bitmapDescriptorFromDrawableWithTint(
    context: Context,
    @DrawableRes drawableId: Int,
    tintColor: Color,
    width: Int,
    height: Int
): BitmapDescriptor? {
    val drawable = ContextCompat.getDrawable(context, drawableId) ?: return null
    val wrappedDrawable = DrawableCompat.wrap(drawable).mutate()
    DrawableCompat.setTint(wrappedDrawable, tintColor.toArgb())

    val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
    val canvas = Canvas(bitmap)
    wrappedDrawable.setBounds(0, 0, canvas.width, canvas.height)
    wrappedDrawable.draw(canvas)

    return BitmapDescriptorFactory.fromBitmap(bitmap)
}