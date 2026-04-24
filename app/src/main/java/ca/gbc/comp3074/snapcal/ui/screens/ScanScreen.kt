package ca.gbc.comp3074.snapcal.ui.screens

import android.Manifest
import android.content.pm.PackageManager
import android.util.Size
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.*
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import ca.gbc.comp3074.snapcal.ui.theme.*
import com.google.mlkit.vision.barcode.BarcodeScannerOptions
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.barcode.common.Barcode
import com.google.mlkit.vision.common.InputImage
import java.util.concurrent.Executors

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScanScreen(
    onBack: () -> Unit,
    onBarcodeScanned: (barcode: String) -> Unit,
    scanViewModel: ScanViewModel = viewModel()
) {
    val context       = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val cameraProviderFuture = remember { ProcessCameraProvider.getInstance(context) }

    var hasPermission by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED
        )
    }
    val permLauncher = rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) { hasPermission = it }
    var scannedBarcode by remember { mutableStateOf<String?>(null) }
    var scanFrozen by remember { mutableStateOf(false) }   // stop re-scanning after first hit

    LaunchedEffect(Unit) { if (!hasPermission) permLauncher.launch(Manifest.permission.CAMERA) }
    LaunchedEffect(scannedBarcode) { scannedBarcode?.let { scanViewModel.fetchProduct(it) } }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("📷 Scan Barcode") },
                navigationIcon = {
                    IconButton(onClick = onBack) { Icon(Icons.Default.ArrowBack, "Back", tint = Color.White) }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = PinkPrimary, titleContentColor = Color.White
                )
            )
        }
    ) { padding ->
        Column(
            Modifier.padding(padding).fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(0.dp)
        ) {
            if (!hasPermission) {
                // Permission request UI
                Column(
                    Modifier.fillMaxSize().padding(32.dp),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text("📷", fontSize = 64.sp)
                    Spacer(Modifier.height(16.dp))
                    Text("Camera Permission Required", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                    Spacer(Modifier.height(8.dp))
                    Text("Allow camera access to scan barcodes on food products.", style = MaterialTheme.typography.bodyMedium, color = SubtleGray)
                    Spacer(Modifier.height(24.dp))
                    Button(
                        onClick = { permLauncher.launch(Manifest.permission.CAMERA) },
                        colors = ButtonDefaults.buttonColors(containerColor = PinkPrimary)
                    ) { Text("Grant Camera Permission") }
                }
            } else {
                // Camera viewfinder
                Box(
                    Modifier.fillMaxWidth().height(300.dp).background(Color.Black)
                ) {
                    AndroidView(
                        factory = { ctx ->
                            val previewView = PreviewView(ctx)
                            val preview = Preview.Builder().build().also {
                                it.setSurfaceProvider(previewView.surfaceProvider)
                            }
                            val selector = CameraSelector.Builder()
                                .requireLensFacing(CameraSelector.LENS_FACING_BACK).build()
                            val imageAnalysis = ImageAnalysis.Builder()
                                .setTargetResolution(Size(1280, 720))
                                .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                                .build()
                            val executor = Executors.newSingleThreadExecutor()
                            imageAnalysis.setAnalyzer(executor) { proxy ->
                                if (!scanFrozen) {
                                    processImageProxy(proxy) { barcode ->
                                        scannedBarcode = barcode
                                        scanFrozen = true
                                    }
                                } else {
                                    proxy.close()
                                }
                            }
                            try {
                                cameraProviderFuture.get().bindToLifecycle(lifecycleOwner, selector, preview, imageAnalysis)
                            } catch (e: Exception) { e.printStackTrace() }
                            previewView
                        },
                        modifier = Modifier.fillMaxSize()
                    )
                    // Scan frame overlay
                    Box(
                        Modifier.align(Alignment.Center).size(220.dp, 140.dp)
                            .border(3.dp, if (scannedBarcode != null) GreenAccent else Color.White, RoundedCornerShape(12.dp))
                    )
                    // Hint text
                    Text(
                        if (scannedBarcode == null) "📦 Align barcode within the frame" else "✅ Barcode detected!",
                        modifier = Modifier.align(Alignment.BottomCenter).padding(bottom = 12.dp)
                            .background(Color.Black.copy(0.55f), RoundedCornerShape(8.dp)).padding(horizontal = 12.dp, vertical = 6.dp),
                        color = Color.White, style = MaterialTheme.typography.bodySmall
                    )
                }

                // Result card
                Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    when {
                        scanViewModel.isLoading -> {
                            Card(Modifier.fillMaxWidth(), shape = RoundedCornerShape(16.dp)) {
                                Row(Modifier.padding(20.dp), horizontalArrangement = Arrangement.spacedBy(16.dp), verticalAlignment = Alignment.CenterVertically) {
                                    CircularProgressIndicator(color = PinkPrimary, modifier = Modifier.size(28.dp))
                                    Text("Looking up product...", style = MaterialTheme.typography.bodyMedium)
                                }
                            }
                        }
                        scannedBarcode != null -> {
                            val product = scanViewModel.product
                            Card(
                                Modifier.fillMaxWidth(), shape = RoundedCornerShape(16.dp),
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
                            ) {
                                Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
                                        Icon(Icons.Default.CheckCircle, null, tint = GreenAccent)
                                        Text(
                                            product?.product_name?.takeIf { it.isNotBlank() } ?: "Barcode: $scannedBarcode",
                                            fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium
                                        )
                                    }
                                    if (product?.nutriments != null) {
                                        Row(horizontalArrangement = Arrangement.SpaceAround, modifier = Modifier.fillMaxWidth()) {
                                            MacroChip("Calories", "${product.nutriments.energy_kcal_100g?.toInt() ?: 0} kcal")
                                            MacroChip("Protein",  "${product.nutriments.proteins_100g?.toInt() ?: 0}g")
                                            MacroChip("Carbs",    "${product.nutriments.carbohydrates_100g?.toInt() ?: 0}g")
                                            MacroChip("Fat",      "${product.nutriments.fat_100g?.toInt() ?: 0}g")
                                        }
                                        Text("Per 100g", style = MaterialTheme.typography.labelSmall, color = SubtleGray)
                                    } else {
                                        Text("Product info not found — will save barcode as meal name.", style = MaterialTheme.typography.bodySmall, color = SubtleGray)
                                    }
                                }
                            }
                            Row(horizontalArrangement = Arrangement.spacedBy(10.dp), modifier = Modifier.fillMaxWidth()) {
                                OutlinedButton(
                                    onClick = { scannedBarcode = null; scanFrozen = false; scanViewModel.clearProduct() },
                                    modifier = Modifier.weight(1f), shape = RoundedCornerShape(12.dp)
                                ) { Text("🔄 Scan Again") }
                                Button(
                                    onClick = { scannedBarcode?.let { onBarcodeScanned(it) } },
                                    modifier = Modifier.weight(1f), shape = RoundedCornerShape(12.dp),
                                    colors = ButtonDefaults.buttonColors(containerColor = PinkPrimary)
                                ) { Text("✅ Save to Log") }
                            }
                        }
                        else -> {
                            Card(Modifier.fillMaxWidth(), shape = RoundedCornerShape(16.dp)) {
                                Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                                    Text("💡 How to scan", fontWeight = FontWeight.SemiBold, color = PinkDark)
                                    Text("1. Point the camera at any food product barcode", style = MaterialTheme.typography.bodySmall)
                                    Text("2. Keep the barcode within the white frame", style = MaterialTheme.typography.bodySmall)
                                    Text("3. Hold steady — it detects automatically", style = MaterialTheme.typography.bodySmall)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun MacroChip(label: String, value: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(value, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.bodyMedium, color = PinkPrimary)
        Text(label, style = MaterialTheme.typography.labelSmall, color = SubtleGray)
    }
}

private fun processImageProxy(imageProxy: ImageProxy, onBarcodeScanned: (String) -> Unit) {
    val mediaImage = imageProxy.image ?: run { imageProxy.close(); return }
    val image = InputImage.fromMediaImage(mediaImage, imageProxy.imageInfo.rotationDegrees)
    val options = BarcodeScannerOptions.Builder().setBarcodeFormats(Barcode.FORMAT_ALL_FORMATS).build()
    BarcodeScanning.getClient(options).process(image)
        .addOnSuccessListener { barcodes -> barcodes.firstOrNull()?.rawValue?.let { onBarcodeScanned(it) } }
        .addOnCompleteListener { imageProxy.close() }
}
