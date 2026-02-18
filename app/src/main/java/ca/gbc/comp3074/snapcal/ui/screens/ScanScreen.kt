@file:OptIn(ExperimentalMaterial3Api::class)

package ca.gbc.comp3074.snapcal.ui.screens

import android.Manifest
import android.content.pm.PackageManager
import android.util.Size
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.concurrent.futures.await
import androidx.core.content.ContextCompat
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.latin.TextRecognizerOptions
import java.util.concurrent.Executors

@Composable
fun ScanScreen(
    onBack: () -> Unit,
    onUseText: (recognizedText: String) -> Unit
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current

    var hasPermission by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED
        )
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { granted ->
        hasPermission = granted
    }

    var recognizedText by remember { mutableStateOf("") }
    var isProcessing by remember { mutableStateOf(false) }

    // CameraX
    val cameraExecutor = remember { Executors.newSingleThreadExecutor() }
    val previewView = remember { PreviewView(context) }
    var imageCapture: ImageCapture? by remember { mutableStateOf(null) }

    LaunchedEffect(Unit) {
        if (!hasPermission) permissionLauncher.launch(Manifest.permission.CAMERA)
    }

    LaunchedEffect(hasPermission) {
        if (hasPermission) {
            val cameraProvider = ProcessCameraProvider.getInstance(context).await()

            val preview = Preview.Builder()
                .build()
                .also { it.setSurfaceProvider(previewView.surfaceProvider) }

            imageCapture = ImageCapture.Builder()
                .setTargetResolution(Size(1280, 720))
                .setCaptureMode(ImageCapture.CAPTURE_MODE_MINIMIZE_LATENCY)
                .build()

            val selector = CameraSelector.DEFAULT_BACK_CAMERA

            cameraProvider.unbindAll()
            cameraProvider.bindToLifecycle(
                lifecycleOwner,
                selector,
                preview,
                imageCapture
            )
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Scan (OCR)") },
                navigationIcon = { TextButton(onClick = onBack) { Text("Back") } }
            )
        }
    ) { padding ->
        Column(
            Modifier.padding(padding).padding(16.dp).fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            if (!hasPermission) {
                Text("Camera permission is required.")
                Button(onClick = { permissionLauncher.launch(Manifest.permission.CAMERA) }) {
                    Text("Grant Permission")
                }
                return@Column
            }

            // Camera Preview
            AndroidView(
                factory = { previewView },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(280.dp)
            )

            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                Button(
                    onClick = {
                        val capture = imageCapture ?: return@Button
                        isProcessing = true
                        recognizedText = ""

                        capture.takePicture(
                            cameraExecutor,
                            object : ImageCapture.OnImageCapturedCallback() {
                                @OptIn(ExperimentalGetImage::class)
                                override fun onCaptureSuccess(imageProxy: ImageProxy) {
                                    try {
                                        val mediaImage = imageProxy.image
                                        if (mediaImage != null) {
                                            val inputImage = InputImage.fromMediaImage(
                                                mediaImage,
                                                imageProxy.imageInfo.rotationDegrees
                                            )
                                            val recognizer = TextRecognition.getClient(
                                                TextRecognizerOptions.DEFAULT_OPTIONS
                                            )
                                            recognizer.process(inputImage)
                                                .addOnSuccessListener { result ->
                                                    recognizedText = result.text.orEmpty()
                                                    isProcessing = false
                                                }
                                                .addOnFailureListener {
                                                    recognizedText = "OCR failed: ${'$'}{it.message}"
                                                    isProcessing = false
                                                }
                                                .addOnCompleteListener {
                                                    imageProxy.close()
                                                }
                                        } else {
                                            recognizedText = "No image captured."
                                            isProcessing = false
                                            imageProxy.close()
                                        }
                                    } catch (e: Exception) {
                                        recognizedText = "Error: ${'$'}{e.message}"
                                        isProcessing = false
                                        imageProxy.close()
                                    }
                                }

                                override fun onError(exception: ImageCaptureException) {
                                    recognizedText = "Capture error: ${'$'}{exception.message}"
                                    isProcessing = false
                                }
                            }
                        )
                    },
                    enabled = !isProcessing
                ) { Text(if (isProcessing) "Processing..." else "Capture") }

                OutlinedButton(
                    onClick = { if (recognizedText.isNotBlank()) onUseText(recognizedText) },
                    enabled = recognizedText.isNotBlank() && !isProcessing
                ) { Text("Use Text") }
            }

            Text("Recognized Text:", style = MaterialTheme.typography.titleMedium)
            OutlinedTextField(
                value = recognizedText,
                onValueChange = { recognizedText = it },
                modifier = Modifier.fillMaxWidth().weight(1f),
                label = { Text("OCR Output") }
            )
        }
    }
}
