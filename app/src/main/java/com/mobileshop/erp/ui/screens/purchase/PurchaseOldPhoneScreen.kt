package com.mobileshop.erp.ui.screens.purchase

import android.Manifest
import android.graphics.Bitmap
import android.util.Base64
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.animation.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.hilt.navigation.compose.hiltViewModel
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.common.InputImage
import java.io.ByteArrayOutputStream
import java.util.concurrent.Executors

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PurchaseOldPhoneScreen(
    onNavigateBack: () -> Unit,
    viewModel: PurchaseOldPhoneViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val uiState by viewModel.uiState.collectAsState()
    
    var showCamera by remember { mutableStateOf(false) }
    var hasCameraPermission by remember { mutableStateOf(false) }
    
    // Camera permission launcher
    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        hasCameraPermission = isGranted
        if (isGranted) {
            showCamera = true
        } else {
            Toast.makeText(context, "Camera permission required for scanning", Toast.LENGTH_SHORT).show()
        }
    }

    // Handle save success
    LaunchedEffect(uiState.isSaved) {
        if (uiState.isSaved) {
            Toast.makeText(context, "Purchase record saved successfully!", Toast.LENGTH_SHORT).show()
            onNavigateBack()
        }
    }

    // Handle errors
    LaunchedEffect(uiState.error) {
        uiState.error?.let {
            Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
            viewModel.clearError()
        }
    }

    if (showCamera) {
        BarcodeScannerScreen(
            onBarcodeDetected = { barcode ->
                viewModel.updateImeiNumber(barcode)
                showCamera = false
            },
            onClose = { showCamera = false }
        )
    } else {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { 
                        Column {
                            Text("Purchase Old Phone", fontWeight = FontWeight.Bold)
                            Text(
                                "Second Hand Purchase Form",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    },
                    navigationIcon = {
                        IconButton(onClick = onNavigateBack) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back")
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer
                    )
                )
            }
        ) { padding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Scan IMEI Button
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.secondaryContainer
                    ),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                "Scan IMEI Barcode",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.SemiBold
                            )
                            Text(
                                "Use camera to auto-fill IMEI",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSecondaryContainer.copy(alpha = 0.7f)
                            )
                        }
                        Button(
                            onClick = {
                                permissionLauncher.launch(Manifest.permission.CAMERA)
                            },
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Icon(Icons.Default.QrCodeScanner, null, Modifier.size(20.dp))
                            Spacer(Modifier.width(8.dp))
                            Text("Scan")
                        }
                    }
                }

                // Seller Information Section
                Text(
                    "Seller Information",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.primary
                )

                OutlinedTextField(
                    value = uiState.sellerName,
                    onValueChange = { viewModel.updateSellerName(it) },
                    label = { Text("Seller Name *") },
                    leadingIcon = { Icon(Icons.Default.Person, null) },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    singleLine = true
                )

                OutlinedTextField(
                    value = uiState.sellerCnic,
                    onValueChange = { viewModel.updateSellerCnic(it) },
                    label = { Text("Seller CNIC *") },
                    leadingIcon = { Icon(Icons.Default.Badge, null) },
                    placeholder = { Text("12345-1234567-1") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true
                )

                HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))

                // Phone Information Section
                Text(
                    "Phone Details",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.primary
                )

                OutlinedTextField(
                    value = uiState.mobileModel,
                    onValueChange = { viewModel.updateMobileModel(it) },
                    label = { Text("Mobile Model *") },
                    leadingIcon = { Icon(Icons.Default.PhoneAndroid, null) },
                    placeholder = { Text("e.g., Samsung Galaxy S21") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    singleLine = true
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    OutlinedTextField(
                        value = uiState.mobileColor,
                        onValueChange = { viewModel.updateMobileColor(it) },
                        label = { Text("Color *") },
                        leadingIcon = { Icon(Icons.Default.Palette, null) },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(12.dp),
                        singleLine = true
                    )

                    OutlinedTextField(
                        value = uiState.purchasePrice,
                        onValueChange = { viewModel.updatePurchasePrice(it) },
                        label = { Text("Price (PKR) *") },
                        leadingIcon = { Icon(Icons.Default.AttachMoney, null) },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(12.dp),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        singleLine = true
                    )
                }

                OutlinedTextField(
                    value = uiState.imeiNumber,
                    onValueChange = { viewModel.updateImeiNumber(it) },
                    label = { Text("IMEI Number *") },
                    leadingIcon = { Icon(Icons.Default.Numbers, null) },
                    trailingIcon = {
                        if (uiState.imeiNumber.isNotEmpty()) {
                            Icon(
                                Icons.Default.CheckCircle,
                                null,
                                tint = MaterialTheme.colorScheme.primary
                            )
                        }
                    },
                    placeholder = { Text("15-digit IMEI") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true
                )

                HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))

                // Accessories Section
                AccessoriesSection(
                    hasBox = uiState.hasBox,
                    hasCharger = uiState.hasCharger,
                    hasHandsfree = uiState.hasHandsfree,
                    customAccessory = uiState.customAccessory,
                    onBoxChange = { viewModel.updateHasBox(it) },
                    onChargerChange = { viewModel.updateHasCharger(it) },
                    onHandsfreeChange = { viewModel.updateHasHandsfree(it) },
                    onCustomAccessoryChange = { viewModel.updateCustomAccessory(it) }
                )

                HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))

                // Signature Pad Section
                SignaturePadSection(
                    signaturePaths = uiState.signaturePaths,
                    onSignatureChange = { viewModel.updateSignaturePaths(it) },
                    onClearSignature = { viewModel.clearSignature() }
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Save Button
                Button(
                    onClick = { viewModel.savePurchase() },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    shape = RoundedCornerShape(16.dp),
                    enabled = !uiState.isLoading
                ) {
                    if (uiState.isLoading) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(24.dp),
                            color = MaterialTheme.colorScheme.onPrimary
                        )
                    } else {
                        Icon(Icons.Default.Save, null, Modifier.size(24.dp))
                        Spacer(Modifier.width(12.dp))
                        Text("Save Purchase Record", fontWeight = FontWeight.SemiBold)
                    }
                }

                Spacer(modifier = Modifier.height(32.dp))
            }
        }
    }
}

@Composable
private fun AccessoriesSection(
    hasBox: Boolean,
    hasCharger: Boolean,
    hasHandsfree: Boolean,
    customAccessory: String,
    onBoxChange: (Boolean) -> Unit,
    onChargerChange: (Boolean) -> Unit,
    onHandsfreeChange: (Boolean) -> Unit,
    onCustomAccessoryChange: (String) -> Unit
) {
    var showCustomField by remember { mutableStateOf(customAccessory.isNotEmpty()) }

    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text(
            "Accessories Included",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.primary
        )

        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
            ),
            shape = RoundedCornerShape(12.dp)
        ) {
            Column(modifier = Modifier.padding(12.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    AccessoryCheckbox(
                        label = "📦 Mobile Box",
                        checked = hasBox,
                        onCheckedChange = onBoxChange,
                        modifier = Modifier.weight(1f)
                    )
                    AccessoryCheckbox(
                        label = "🔌 Charger",
                        checked = hasCharger,
                        onCheckedChange = onChargerChange,
                        modifier = Modifier.weight(1f)
                    )
                }
                
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    AccessoryCheckbox(
                        label = "🎧 Handsfree",
                        checked = hasHandsfree,
                        onCheckedChange = onHandsfreeChange,
                        modifier = Modifier.weight(1f)
                    )
                    AccessoryCheckbox(
                        label = "➕ Custom",
                        checked = showCustomField,
                        onCheckedChange = { 
                            showCustomField = it
                            if (!it) onCustomAccessoryChange("")
                        },
                        modifier = Modifier.weight(1f)
                    )
                }

                AnimatedVisibility(
                    visible = showCustomField,
                    enter = fadeIn() + expandVertically(),
                    exit = fadeOut() + shrinkVertically()
                ) {
                    OutlinedTextField(
                        value = customAccessory,
                        onValueChange = onCustomAccessoryChange,
                        label = { Text("Custom Accessory") },
                        placeholder = { Text("e.g., Back cover, Screen protector") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 8.dp),
                        shape = RoundedCornerShape(8.dp),
                        singleLine = true
                    )
                }
            }
        }
    }
}

@Composable
private fun AccessoryCheckbox(
    label: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.padding(4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Checkbox(
            checked = checked,
            onCheckedChange = onCheckedChange
        )
        Text(
            label,
            style = MaterialTheme.typography.bodyMedium
        )
    }
}

@Composable
private fun SignaturePadSection(
    signaturePaths: List<List<Offset>>,
    onSignatureChange: (List<List<Offset>>) -> Unit,
    onClearSignature: () -> Unit
) {
    var currentPath by remember { mutableStateOf<List<Offset>>(emptyList()) }

    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                "Seller Signature",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.primary
            )
            TextButton(
                onClick = {
                    onClearSignature()
                    currentPath = emptyList()
                }
            ) {
                Icon(Icons.Default.Clear, null, Modifier.size(18.dp))
                Spacer(Modifier.width(4.dp))
                Text("Clear")
            }
        }

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp),
            colors = CardDefaults.cardColors(
                containerColor = Color.White
            ),
            shape = RoundedCornerShape(12.dp)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .border(
                        width = 2.dp,
                        color = MaterialTheme.colorScheme.outline,
                        shape = RoundedCornerShape(12.dp)
                    )
            ) {
                Canvas(
                    modifier = Modifier
                        .fillMaxSize()
                        .pointerInput(Unit) {
                            detectDragGestures(
                                onDragStart = { offset ->
                                    currentPath = listOf(offset)
                                },
                                onDrag = { change, _ ->
                                    currentPath = currentPath + change.position
                                },
                                onDragEnd = {
                                    if (currentPath.isNotEmpty()) {
                                        onSignatureChange(signaturePaths + listOf(currentPath))
                                        currentPath = emptyList()
                                    }
                                }
                            )
                        }
                ) {
                    // Draw all completed paths
                    signaturePaths.forEach { pathPoints ->
                        if (pathPoints.size > 1) {
                            val path = Path().apply {
                                moveTo(pathPoints.first().x, pathPoints.first().y)
                                pathPoints.drop(1).forEach { point ->
                                    lineTo(point.x, point.y)
                                }
                            }
                            drawPath(
                                path = path,
                                color = Color.Black,
                                style = Stroke(
                                    width = 4f,
                                    cap = StrokeCap.Round,
                                    join = StrokeJoin.Round
                                )
                            )
                        }
                    }
                    
                    // Draw current path being drawn
                    if (currentPath.size > 1) {
                        val path = Path().apply {
                            moveTo(currentPath.first().x, currentPath.first().y)
                            currentPath.drop(1).forEach { point ->
                                lineTo(point.x, point.y)
                            }
                        }
                        drawPath(
                            path = path,
                            color = Color.Black,
                            style = Stroke(
                                width = 4f,
                                cap = StrokeCap.Round,
                                join = StrokeJoin.Round
                            )
                        )
                    }

                    // Draw placeholder text if empty
                    if (signaturePaths.isEmpty() && currentPath.isEmpty()) {
                        drawContext.canvas.nativeCanvas.apply {
                            drawText(
                                "Sign here with your finger",
                                size.width / 2 - 150f,
                                size.height / 2,
                                android.graphics.Paint().apply {
                                    color = android.graphics.Color.LTGRAY
                                    textSize = 36f
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}

@androidx.annotation.OptIn(ExperimentalGetImage::class)
@Composable
private fun BarcodeScannerScreen(
    onBarcodeDetected: (String) -> Unit,
    onClose: () -> Unit
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    
    var detectedBarcode by remember { mutableStateOf<String?>(null) }
    
    val cameraExecutor = remember { Executors.newSingleThreadExecutor() }
    val barcodeScanner = remember { BarcodeScanning.getClient() }

    DisposableEffect(Unit) {
        onDispose {
            cameraExecutor.shutdown()
            barcodeScanner.close()
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        AndroidView(
            factory = { ctx ->
                val previewView = PreviewView(ctx)
                
                val cameraProviderFuture = ProcessCameraProvider.getInstance(ctx)
                cameraProviderFuture.addListener({
                    val cameraProvider = cameraProviderFuture.get()
                    
                    val preview = Preview.Builder().build().also {
                        it.surfaceProvider = previewView.surfaceProvider
                    }
                    
                    val imageAnalyzer = ImageAnalysis.Builder()
                        .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                        .build()
                        .also { analysis ->
                            analysis.setAnalyzer(cameraExecutor) { imageProxy ->
                                val mediaImage = imageProxy.image
                                if (mediaImage != null && detectedBarcode == null) {
                                    val inputImage = InputImage.fromMediaImage(
                                        mediaImage,
                                        imageProxy.imageInfo.rotationDegrees
                                    )
                                    
                                    barcodeScanner.process(inputImage)
                                        .addOnSuccessListener { barcodes ->
                                            for (barcode in barcodes) {
                                                barcode.rawValue?.let { value ->
                                                    // Check if it looks like an IMEI (15 digits)
                                                    val cleanValue = value.replace(Regex("[^0-9]"), "")
                                                    if (cleanValue.length >= 15) {
                                                        detectedBarcode = cleanValue.take(15)
                                                        onBarcodeDetected(cleanValue.take(15))
                                                    } else if (value.isNotEmpty()) {
                                                        detectedBarcode = value
                                                        onBarcodeDetected(value)
                                                    }
                                                }
                                            }
                                        }
                                        .addOnCompleteListener {
                                            imageProxy.close()
                                        }
                                } else {
                                    imageProxy.close()
                                }
                            }
                        }
                    
                    val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA
                    
                    try {
                        cameraProvider.unbindAll()
                        cameraProvider.bindToLifecycle(
                            lifecycleOwner,
                            cameraSelector,
                            preview,
                            imageAnalyzer
                        )
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }, ContextCompat.getMainExecutor(ctx))
                
                previewView
            },
            modifier = Modifier.fillMaxSize()
        )

        // Overlay
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(32.dp)
        ) {
            // Scanner frame
            Box(
                modifier = Modifier
                    .size(280.dp)
                    .align(Alignment.Center)
                    .border(
                        width = 3.dp,
                        color = MaterialTheme.colorScheme.primary,
                        shape = RoundedCornerShape(16.dp)
                    )
            )

            // Instructions
            Card(
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .padding(top = 48.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Color.Black.copy(alpha = 0.7f)
                ),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text(
                    "Point camera at IMEI barcode",
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                    color = Color.White,
                    style = MaterialTheme.typography.bodyMedium
                )
            }

            // Close button
            IconButton(
                onClick = onClose,
                modifier = Modifier.align(Alignment.TopEnd)
            ) {
                Surface(
                    shape = RoundedCornerShape(50),
                    color = Color.Black.copy(alpha = 0.5f)
                ) {
                    Icon(
                        Icons.Default.Close,
                        contentDescription = "Close",
                        modifier = Modifier.padding(8.dp),
                        tint = Color.White
                    )
                }
            }
        }
    }
}
