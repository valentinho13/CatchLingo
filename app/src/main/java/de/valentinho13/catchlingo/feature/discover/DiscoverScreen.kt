package de.valentinho13.catchlingo.feature.discover

import android.Manifest
import android.content.pm.PackageManager
import android.view.ViewGroup
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.EaseOutCubic
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.BoxWithConstraintsScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.MenuBook
import androidx.compose.material.icons.outlined.AutoAwesome
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material.icons.outlined.Explore
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.core.content.ContextCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.Observer
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.label.ImageLabeling
import com.google.mlkit.vision.label.ImageLabeler
import com.google.mlkit.vision.label.defaults.ImageLabelerOptions
import de.valentinho13.catchlingo.data.DiscoveredWord
import de.valentinho13.catchlingo.R
import de.valentinho13.catchlingo.designsystem.CatchLingoColor
import de.valentinho13.catchlingo.designsystem.CatchLingoMotion
import de.valentinho13.catchlingo.designsystem.components.CatchLingoButton
import de.valentinho13.catchlingo.designsystem.components.CatchLingoCard
import de.valentinho13.catchlingo.designsystem.components.CatchLingoHeroCard
import de.valentinho13.catchlingo.designsystem.components.CatchLingoSpecimenCard
import de.valentinho13.catchlingo.designsystem.components.MiniPill
import de.valentinho13.catchlingo.designsystem.rememberCatchLingoHaptics
import java.util.concurrent.Executors
import kotlin.math.roundToInt
import kotlin.math.sin
import kotlinx.coroutines.delay

@Composable
fun DiscoverScreen(
    modifier: Modifier = Modifier,
    exploreFullScreen: Boolean = false,
    state: DiscoverUiState = EmptyDiscoverState,
    exploreState: DiscoverUiState = PreviewDiscoverState,
    onStartExplore: () -> Unit = {},
    onLeaveExplore: () -> Unit = {},
    onWordCollected: (DiscoveredWord) -> Boolean = { false },
    onFeedback: (String) -> Unit = {},
) {
    if (exploreFullScreen) {
        ExploreScreen(
            state = exploreState,
            onLeaveExplore = onLeaveExplore,
            onWordCollected = onWordCollected,
            modifier = modifier,
        )
    } else {
        HomeScreen(
            state = state,
            onStartExplore = onStartExplore,
            onFeedback = onFeedback,
            modifier = modifier,
        )
    }
}

@Composable
private fun HomeScreen(
    state: DiscoverUiState,
    onStartExplore: () -> Unit,
    onFeedback: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(CatchLingoColor.Canvas),
        state = rememberLazyListState(),
        contentPadding = PaddingValues(horizontal = 24.dp, vertical = 24.dp),
        verticalArrangement = Arrangement.spacedBy(18.dp),
    ) {
        item {
            StableHomeHero(state = state, onStartExplore = onStartExplore)
        }
        item {
            FirstFindCard()
        }
        item {
            WarmPreviewCard(
                onPronounceClick = {
                    onFeedback("Aussprache kommt bald als sanfte Hörprobe dazu.")
                },
            )
        }
    }
}

@Composable
private fun StableHomeHero(state: DiscoverUiState, onStartExplore: () -> Unit) {
    CatchLingoHeroCard(modifier = Modifier.fillMaxWidth()) {
        BoxWithConstraints {
            val companionSize = if (maxWidth < 340.dp) 92.dp else 112.dp

            Column(modifier = Modifier.fillMaxWidth()) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.Top,
                ) {
                    Spacer(modifier = Modifier.weight(1f))
                    FloatingCompanion(size = companionSize)
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Noch keine Wörter",
                    style = MaterialTheme.typography.headlineLarge,
                    color = CatchLingoColor.GreenDeep,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                )
                Text(
                    text = state.companionLine,
                    style = MaterialTheme.typography.bodyMedium,
                    color = CatchLingoColor.TextMuted,
                    modifier = Modifier.padding(top = 8.dp),
                    maxLines = 3,
                    overflow = TextOverflow.Ellipsis,
                )
                Spacer(modifier = Modifier.height(18.dp))
                CatchLingoButton(
                    text = "Ersten Fund entdecken",
                    icon = Icons.Outlined.Explore,
                    onClick = onStartExplore,
                    modifier = Modifier.fillMaxWidth(),
                )
            }
        }
    }
}

@Composable
private fun FirstFindCard() {
    CatchLingoCard(modifier = Modifier.fillMaxWidth()) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Surface(
                modifier = Modifier.size(52.dp),
                shape = CircleShape,
                color = CatchLingoColor.GreenSoft,
                contentColor = CatchLingoColor.GreenDeep,
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Outlined.MenuBook,
                    contentDescription = null,
                    modifier = Modifier.padding(13.dp),
                )
            }
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 14.dp),
            ) {
                Text(text = "Dein Wörterbuch wartet", style = MaterialTheme.typography.titleMedium)
                Text(
                    text = "Sammle dein erstes Wort aus der echten Umgebung. Danach wächst hier dein Feldjournal.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = CatchLingoColor.TextMuted,
                )
            }
        }
    }
}

@Composable
private fun WarmPreviewCard(onPronounceClick: () -> Unit) {
    CatchLingoCard(modifier = Modifier.fillMaxWidth()) {
        Column {
        Text(text = "So fühlt sich ein Fund an", style = MaterialTheme.typography.titleMedium)
        Spacer(modifier = Modifier.height(12.dp))
        CatchLingoSpecimenCard(
            word = "kopi",
            source = "coffee",
            context = "Café",
            status = "Beispielfund",
            onPronounceClick = onPronounceClick,
            modifier = Modifier.fillMaxWidth(),
        )
        }
    }
}

@Composable
private fun ExploreScreen(
    state: DiscoverUiState,
    onLeaveExplore: () -> Unit,
    onWordCollected: (DiscoveredWord) -> Boolean,
    modifier: Modifier = Modifier,
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val haptics = rememberCatchLingoHaptics()
    var hasCameraPermission by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED,
        )
    }
    var permissionDenied by remember { mutableStateOf(false) }
    var cameraStreaming by remember { mutableStateOf(false) }
    var mlUnavailable by remember { mutableStateOf(false) }
    var magnetWord by remember { mutableStateOf<DiscoveredWord?>(null) }
    var caughtWord by remember { mutableStateOf<DiscoveredWord?>(null) }
    var catchVersion by remember { mutableIntStateOf(0) }
    val cameraPlaceholderAlpha by animateFloatAsState(
        targetValue = if (hasCameraPermission && cameraStreaming) 0f else 1f,
        animationSpec = tween(300, easing = CatchLingoMotion.EaseInOutWarm),
        label = "cameraPlaceholderAlpha",
    )
    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
    ) { granted ->
        hasCameraPermission = granted
        permissionDenied = !granted
        cameraStreaming = false
    }

    LaunchedEffect(Unit) {
        if (!hasCameraPermission) {
            permissionLauncher.launch(Manifest.permission.CAMERA)
        }
    }

    DisposableEffect(lifecycleOwner, context) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                val granted = ContextCompat.checkSelfPermission(
                    context,
                    Manifest.permission.CAMERA,
                ) == PackageManager.PERMISSION_GRANTED
                hasCameraPermission = granted
                if (granted) {
                    permissionDenied = false
                    cameraStreaming = false
                }
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }

    LaunchedEffect(catchVersion) {
        val word = magnetWord
        if (word != null) {
            caughtWord = null
            delay(680)
            caughtWord = word
            delay(2_500)
            magnetWord = null
            caughtWord = null
        }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(CatchLingoColor.Canvas),
    ) {
        if (hasCameraPermission) {
            CameraXPreviewLayer(
                onStreamStateChanged = { streaming ->
                    cameraStreaming = streaming
                },
                onMlUnavailable = {
                    mlUnavailable = true
                },
                onWordCollected = { word ->
                    mlUnavailable = false
                    if (onWordCollected(word)) {
                        haptics.softTick()
                        magnetWord = word
                        caughtWord = null
                        catchVersion += 1
                    }
                },
                modifier = Modifier.fillMaxSize(),
            )
        }
        DiscoveryPreviewScene(
            state = state,
            showPreviewSpecimen = !cameraStreaming,
            modifier = Modifier
                .fillMaxSize()
                .graphicsLayer { alpha = cameraPlaceholderAlpha },
        )
        WarmCameraGradeOverlay(modifier = Modifier.fillMaxSize())
        if (permissionDenied) {
            PermissionDeniedMessage(
                modifier = Modifier
                    .align(Alignment.Center)
                    .padding(horizontal = 26.dp),
            )
        }
        magnetWord?.let {
            MagnetSuctionLayer(
                trigger = catchVersion,
                visible = caughtWord == null,
                modifier = Modifier.fillMaxSize(),
            )
        }
        AnimatedVisibility(
            visible = caughtWord != null,
            enter = fadeIn(tween(180)) + scaleIn(initialScale = 0.96f),
            exit = fadeOut(tween(260)) + scaleOut(targetScale = 0.98f),
            modifier = Modifier
                .align(Alignment.Center)
                .padding(horizontal = 30.dp),
        ) {
            caughtWord?.let { word ->
                CatchConfirmationCard(word = word)
            }
        }
        ExploreChrome(
            state = state,
            cameraStreaming = cameraStreaming,
            permissionDenied = permissionDenied,
            mlUnavailable = mlUnavailable,
            onLeaveExplore = onLeaveExplore,
            modifier = Modifier.fillMaxSize(),
        )
    }
}

@Composable
private fun CameraXPreviewLayer(
    onStreamStateChanged: (Boolean) -> Unit,
    onMlUnavailable: () -> Unit,
    onWordCollected: (DiscoveredWord) -> Unit,
    modifier: Modifier = Modifier,
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val previewView = remember {
        PreviewView(context).apply {
            layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT,
            )
            scaleType = PreviewView.ScaleType.FILL_CENTER
            implementationMode = PreviewView.ImplementationMode.PERFORMANCE
        }
    }
    val imageLabeler = remember {
        ImageLabeling.getClient(ImageLabelerOptions.DEFAULT_OPTIONS)
    }
    val analysisExecutor = remember {
        Executors.newSingleThreadExecutor()
    }
    val discoveryGate = remember {
        DiscoveryGate()
    }

    AndroidView(
        factory = { previewView },
        modifier = modifier,
    )

    DisposableEffect(context, lifecycleOwner, previewView, imageLabeler, analysisExecutor, discoveryGate) {
        onStreamStateChanged(false)
        val cameraProviderFuture = ProcessCameraProvider.getInstance(context)
        val streamObserver = Observer<PreviewView.StreamState> { streamState ->
            onStreamStateChanged(streamState == PreviewView.StreamState.STREAMING)
        }
        previewView.previewStreamState.observe(lifecycleOwner, streamObserver)
        cameraProviderFuture.addListener(
            {
                runCatching {
                    val cameraProvider = cameraProviderFuture.get()
                    val preview = Preview.Builder().build().also { cameraPreview ->
                        cameraPreview.setSurfaceProvider(previewView.surfaceProvider)
                    }
                    val analysis = ImageAnalysis.Builder()
                        .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                        .build()
                        .also { imageAnalysis ->
                            imageAnalysis.setAnalyzer(analysisExecutor) { imageProxy ->
                                analyzeDiscoveryFrame(
                                    imageProxy = imageProxy,
                                    imageLabeler = imageLabeler,
                                    discoveryGate = discoveryGate,
                                    onMlUnavailable = onMlUnavailable,
                                    onWordCollected = onWordCollected,
                                )
                            }
                        }
                    cameraProvider.unbindAll()
                    cameraProvider.bindToLifecycle(
                        lifecycleOwner,
                        CameraSelector.DEFAULT_BACK_CAMERA,
                        preview,
                        analysis,
                    )
                }.onFailure {
                    onMlUnavailable()
                }
            },
            ContextCompat.getMainExecutor(context),
        )
        onDispose {
            previewView.previewStreamState.removeObserver(streamObserver)
            onStreamStateChanged(false)
            imageLabeler.close()
            analysisExecutor.shutdown()
            if (cameraProviderFuture.isDone) {
                runCatching {
                    cameraProviderFuture.get().unbindAll()
                }
            }
        }
    }
}

private fun analyzeDiscoveryFrame(
    imageProxy: ImageProxy,
    imageLabeler: ImageLabeler,
    discoveryGate: DiscoveryGate,
    onMlUnavailable: () -> Unit,
    onWordCollected: (DiscoveredWord) -> Unit,
) {
    val now = System.currentTimeMillis()
    if (!discoveryGate.shouldAnalyze(now)) {
        imageProxy.close()
        return
    }

    val mediaImage = imageProxy.image
    if (mediaImage == null) {
        discoveryGate.finish()
        imageProxy.close()
        return
    }

    val image = InputImage.fromMediaImage(mediaImage, imageProxy.imageInfo.rotationDegrees)
    imageLabeler.process(image)
        .addOnSuccessListener { labels ->
            val bestMatch = labels
                .asSequence()
                .mapNotNull { label ->
                    mapLabelToVocabulary(label.text)?.let { match -> match to label.confidence }
                }
                .maxByOrNull { it.second }
            val accepted = bestMatch?.let { (match, confidence) ->
                discoveryGate.accept(match = match, confidence = confidence, nowMillis = System.currentTimeMillis())
            }
            if (accepted != null) {
                onWordCollected(accepted.toDiscoveredWord(System.currentTimeMillis()))
            }
        }
        .addOnFailureListener {
            onMlUnavailable()
        }
        .addOnCompleteListener {
            discoveryGate.finish()
            imageProxy.close()
        }
}

private class DiscoveryGate {
    private var inFlight = false
    private var lastAnalysisAtMillis = 0L
    private var candidateId: String? = null
    private var candidateCount = 0
    private var candidateSeenAtMillis = 0L
    private val recentlyCollectedAtMillis = mutableMapOf<String, Long>()

    @Synchronized
    fun shouldAnalyze(nowMillis: Long): Boolean {
        if (inFlight || nowMillis - lastAnalysisAtMillis < ANALYSIS_INTERVAL_MILLIS) {
            return false
        }
        inFlight = true
        lastAnalysisAtMillis = nowMillis
        return true
    }

    @Synchronized
    fun accept(match: VocabularyMatch, confidence: Float, nowMillis: Long): VocabularyMatch? {
        if (confidence < MIN_LABEL_CONFIDENCE) {
            return null
        }

        recentlyCollectedAtMillis.entries.removeAll { (_, collectedAt) ->
            nowMillis - collectedAt > DUPLICATE_WINDOW_MILLIS
        }
        if (nowMillis - (recentlyCollectedAtMillis[match.id] ?: 0L) < DUPLICATE_WINDOW_MILLIS) {
            return null
        }

        if (candidateId == match.id && nowMillis - candidateSeenAtMillis < STABLE_MATCH_WINDOW_MILLIS) {
            candidateCount += 1
        } else {
            candidateId = match.id
            candidateCount = 1
        }
        candidateSeenAtMillis = nowMillis

        if (candidateCount < REQUIRED_STABLE_MATCHES) {
            return null
        }

        recentlyCollectedAtMillis[match.id] = nowMillis
        candidateId = null
        candidateCount = 0
        candidateSeenAtMillis = 0L
        return match
    }

    @Synchronized
    fun finish() {
        inFlight = false
    }

    private companion object {
        const val ANALYSIS_INTERVAL_MILLIS = 900L
        const val STABLE_MATCH_WINDOW_MILLIS = 4_500L
        const val DUPLICATE_WINDOW_MILLIS = 30_000L
        const val MIN_LABEL_CONFIDENCE = 0.62f
        const val REQUIRED_STABLE_MATCHES = 2
    }
}

@Composable
private fun WarmCameraGradeOverlay(modifier: Modifier = Modifier) {
    Canvas(modifier = modifier) {
        drawRect(
            brush = Brush.verticalGradient(
                colors = listOf(
                    CatchLingoColor.AmberSoft.copy(alpha = 0.16f),
                    Color.Transparent,
                    CatchLingoColor.TextPrimary.copy(alpha = 0.30f),
                ),
            ),
        )
        drawRect(
            brush = Brush.radialGradient(
                colors = listOf(
                    Color.Transparent,
                    CatchLingoColor.TextPrimary.copy(alpha = 0.18f),
                ),
                center = Offset(size.width * 0.5f, size.height * 0.52f),
                radius = size.width * 0.74f,
            ),
        )
    }
}

@Composable
private fun PermissionDeniedMessage(modifier: Modifier = Modifier) {
    CatchLingoCard(
        modifier = modifier.fillMaxWidth(),
        elevated = false,
    ) {
        Text(
            text = "Kamera-Zugriff fehlt",
            style = MaterialTheme.typography.titleMedium,
            color = CatchLingoColor.TextPrimary,
        )
        Text(
            text = "Du kannst die warme Vorschau ansehen. Erlaube die Kamera in den Android-Einstellungen, wenn du die echte Welt live entdecken mÃ¶chtest.",
            style = MaterialTheme.typography.bodyMedium,
            color = CatchLingoColor.TextMuted,
            modifier = Modifier.padding(top = 8.dp),
        )
    }
}

@Composable
private fun MagnetSuctionLayer(
    trigger: Int,
    visible: Boolean,
    modifier: Modifier = Modifier,
) {
    val progress = remember { Animatable(0f) }

    LaunchedEffect(trigger) {
        progress.snapTo(0f)
        progress.animateTo(
            targetValue = 1f,
            animationSpec = tween(640, easing = EaseOutCubic),
        )
    }

    Canvas(
        modifier = modifier.alpha(if (visible) 1f else 0f),
    ) {
        val target = Offset(size.width * 0.50f, size.height * 0.48f)
        val p = progress.value
        repeat(9) { index ->
            val angle = index * 0.72f
            val startRadiusX = size.width * (0.34f + (index % 3) * 0.04f)
            val startRadiusY = size.height * (0.25f + (index % 2) * 0.04f)
            val start = Offset(
                x = target.x + kotlin.math.cos(angle) * startRadiusX,
                y = target.y + sin(angle) * startRadiusY,
            )
            val drift = Offset(
                x = start.x + (target.x - start.x) * p,
                y = start.y + (target.y - start.y) * p,
            )
            drawCircle(
                color = CatchLingoColor.AmberSoft.copy(alpha = (1f - p) * 0.48f),
                radius = (3.2f + p * 4.8f).dp.toPx(),
                center = drift,
            )
        }
        drawCircle(
            color = CatchLingoColor.AmberSoft.copy(alpha = (1f - p) * 0.18f),
            radius = (80f + p * 34f).dp.toPx(),
            center = target,
        )
    }
}

@Composable
private fun CatchConfirmationCard(
    word: DiscoveredWord,
    modifier: Modifier = Modifier,
) {
    CatchLingoCard(
        modifier = modifier.fillMaxWidth(),
        contentPadding = PaddingValues(horizontal = 18.dp, vertical = 16.dp),
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Surface(
                modifier = Modifier.size(48.dp),
                shape = CircleShape,
                color = CatchLingoColor.AmberSoft,
                contentColor = CatchLingoColor.AmberDeep,
            ) {
                Icon(
                    imageVector = Icons.Outlined.AutoAwesome,
                    contentDescription = null,
                    modifier = Modifier.padding(12.dp),
                )
            }
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 14.dp),
            ) {
                Text(
                    text = "Gesammelt",
                    style = MaterialTheme.typography.labelMedium,
                    color = CatchLingoColor.AmberDeep,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
                Text(
                    text = word.word,
                    style = MaterialTheme.typography.headlineMedium,
                    color = CatchLingoColor.TextPrimary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
                Text(
                    text = word.source,
                    style = MaterialTheme.typography.bodyMedium,
                    color = CatchLingoColor.TextMuted,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
            }
            MiniPill(
                text = "Neu",
                color = CatchLingoColor.GreenSoft,
                contentColor = CatchLingoColor.GreenDeep,
            )
        }
    }
}

@Composable
private fun ExploreChrome(
    state: DiscoverUiState,
    cameraStreaming: Boolean,
    permissionDenied: Boolean,
    mlUnavailable: Boolean,
    onLeaveExplore: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val haptics = rememberCatchLingoHaptics()
    Column(
        modifier = modifier
            .statusBarsPadding()
            .navigationBarsPadding()
            .padding(18.dp),
        verticalArrangement = Arrangement.SpaceBetween,
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            MiniPill(
                text = if (cameraStreaming) "Live-Vorschau" else "Entdeckungsvorschau",
                color = CatchLingoColor.GreenDeep.copy(alpha = 0.72f),
                contentColor = CatchLingoColor.WarmSurfaceRaised,
            )
            Spacer(modifier = Modifier.weight(1f))
            IconButton(
                onClick = {
                    haptics.softTick()
                    onLeaveExplore()
                },
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(CatchLingoColor.WarmSurfaceRaised.copy(alpha = 0.88f)),
            ) {
                Icon(
                    imageVector = Icons.Outlined.Close,
                    contentDescription = "Explore schließen",
                    tint = CatchLingoColor.TextPrimary,
                )
            }
        }

        Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
            Text(
                text = when {
                    mlUnavailable -> "Worterkennung ist gerade nicht verfuegbar."
                    cameraStreaming -> "Worterkennung aktiv. Richte dein Handy auf einfache Alltagsobjekte."
                    permissionDenied -> "Kamera-Zugriff fehlt. Die Vorschau bleibt sichtbar."
                    else -> state.sceneTitle
                },
                style = if (cameraStreaming || permissionDenied || mlUnavailable) {
                    MaterialTheme.typography.bodyMedium
                } else {
                    MaterialTheme.typography.labelMedium
                },
                color = CatchLingoColor.WarmSurfaceRaised.copy(alpha = 0.82f),
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
            )
            Row(verticalAlignment = Alignment.Bottom) {
                CatchOrb(cameraStreaming = cameraStreaming)
                Spacer(modifier = Modifier.weight(1f))
                MiniPill(
                    text = "Automatisch sammeln",
                    color = CatchLingoColor.WarmSurfaceRaised.copy(alpha = 0.86f),
                    contentColor = CatchLingoColor.TextMuted,
                )
            }
        }
    }
}

@Composable
private fun FloatingCompanion(size: Dp = 148.dp) {
    val transition = rememberInfiniteTransition(label = "catFloat")
    val lift by transition.animateFloat(
        initialValue = 0f,
        targetValue = -8f,
        animationSpec = infiniteRepeatable(
            animation = tween(2400, easing = CatchLingoMotion.EaseInOutWarm),
            repeatMode = RepeatMode.Reverse,
        ),
        label = "catLift",
    )

    Image(
        painter = painterResource(R.drawable.welcome_cat),
        contentDescription = "CatchLingo Begleiter",
        modifier = Modifier
            .size(size)
            .graphicsLayer {
                translationY = lift
                shadowElevation = 18f
            },
        contentScale = ContentScale.Fit,
    )
}

@Composable
private fun DiscoveryPreviewScene(
    state: DiscoverUiState,
    showPreviewSpecimen: Boolean,
    modifier: Modifier = Modifier,
) {
    val transition = rememberInfiniteTransition(label = "exploreScene")
    val trailPhase by transition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(3200, easing = CatchLingoMotion.EaseInOutWarm),
            repeatMode = RepeatMode.Restart,
        ),
        label = "trailPhase",
    )

    BoxWithConstraints(
        modifier = modifier
            .background(Color(0xFF6D7D4B)),
    ) {
        Canvas(modifier = Modifier.matchParentSize()) {
            drawSunnyRoom()
            drawMagnetTrails(words = state.noticedWords, phase = trailPhase)
        }

        if (showPreviewSpecimen) {
            AnimatedVisibility(
            visible = true,
            enter = fadeIn(tween(420)) + scaleIn(initialScale = 0.94f),
        ) {
            SceneWordChip(word = state.noticedWords.firstOrNull() ?: PreviewExampleWord)
        }

        Text(
            text = "Bald: Richte dein Handy auf die Welt — entdeckte Wörter werden sanft eingesammelt.",
            style = MaterialTheme.typography.bodyMedium,
            color = CatchLingoColor.WarmSurfaceRaised.copy(alpha = 0.86f),
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(horizontal = 22.dp, vertical = 168.dp),
            textAlign = TextAlign.Center,
        )
        }
    }
}

private val PreviewExampleWord = NoticedWord(
    word = "kopi",
    source = "coffee",
    x = 0.50f,
    y = 0.58f,
)

@Composable
private fun BoxWithConstraintsScope.SceneWordPosition(
    word: NoticedWord,
    content: @Composable () -> Unit,
) {
    val density = LocalDensity.current
    Box(
        modifier = Modifier.offset {
            with(density) {
                IntOffset(
                    x = (maxWidth.toPx() * word.x).roundToInt() - 54.dp.roundToPx(),
                    y = (maxHeight.toPx() * word.y).roundToInt() - 26.dp.roundToPx(),
                )
            }
        },
    ) {
        content()
    }
}

@Composable
private fun BoxWithConstraintsScope.SceneWordChip(word: NoticedWord) {
    val haptics = rememberCatchLingoHaptics()
    SceneWordPosition(word = word) {
        CatchLingoSpecimenCard(
            word = word.word,
            source = word.source,
            context = "Café",
            status = "Beispielfund",
            onPronounceClick = { haptics.softTick() },
            modifier = Modifier.fillMaxWidth(0.66f),
        )
    }
}

@Composable
private fun CatchOrb(
    cameraStreaming: Boolean,
    modifier: Modifier = Modifier,
) {
    val transition = rememberInfiniteTransition(label = "catchOrb")
    val pulse by transition.animateFloat(
        initialValue = 0.96f,
        targetValue = 1.03f,
        animationSpec = infiniteRepeatable(
            animation = tween(1800, easing = CatchLingoMotion.EaseInOutWarm),
            repeatMode = RepeatMode.Reverse,
        ),
        label = "orbPulse",
    )
    Box(
        modifier = modifier
            .size(104.dp)
            .graphicsLayer {
                scaleX = pulse
                scaleY = pulse
            }
            .clip(CircleShape)
            .background(
                Brush.radialGradient(
                    listOf(
                        CatchLingoColor.WarmSurfaceRaised.copy(alpha = 0.92f),
                        CatchLingoColor.AmberSoft.copy(alpha = 0.82f),
                        CatchLingoColor.Amber.copy(alpha = 0.24f),
                    ),
                ),
            ),
        contentAlignment = Alignment.Center,
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(imageVector = Icons.Outlined.AutoAwesome, contentDescription = null, tint = CatchLingoColor.Amber)
            Text(
                text = if (cameraStreaming) "Bereit" else "Vorschau",
                style = MaterialTheme.typography.labelMedium,
                color = CatchLingoColor.TextMuted,
                textAlign = TextAlign.Center,
            )
        }
    }
}

private fun androidx.compose.ui.graphics.drawscope.DrawScope.drawSunnyRoom() {
    drawRect(
        brush = Brush.verticalGradient(
            colors = listOf(
                Color(0xFFFFDFA3),
                Color(0xFFB9894D),
                Color(0xFF536D43),
                Color(0xFF23423A),
            ),
        ),
    )

    drawRect(
        brush = Brush.radialGradient(
            colors = listOf(
                CatchLingoColor.AmberSoft.copy(alpha = 0.86f),
                CatchLingoColor.Amber.copy(alpha = 0.24f),
                Color.Transparent,
            ),
            center = Offset(size.width * 0.18f, size.height * 0.16f),
            radius = size.width * 0.86f,
        ),
    )

    drawRect(
        color = Color(0xFF715238).copy(alpha = 0.62f),
        topLeft = Offset(0f, size.height * 0.62f),
        size = androidx.compose.ui.geometry.Size(size.width, size.height * 0.38f),
    )

    repeat(8) { index ->
        val y = size.height * (0.65f + index * 0.043f)
        drawLine(
            color = Color.White.copy(alpha = 0.08f),
            start = Offset(0f, y),
            end = Offset(size.width, y + size.height * 0.035f),
            strokeWidth = 1.dp.toPx(),
            cap = StrokeCap.Round,
        )
    }

    drawRoundRect(
        color = Color(0xFFFFF2C8).copy(alpha = 0.58f),
        topLeft = Offset(size.width * 0.08f, size.height * 0.10f),
        size = androidx.compose.ui.geometry.Size(size.width * 0.26f, size.height * 0.26f),
        cornerRadius = androidx.compose.ui.geometry.CornerRadius(24.dp.toPx()),
    )
    repeat(4) { index ->
        val start = Offset(size.width * (0.12f + index * 0.055f), size.height * 0.11f)
        drawLine(
            color = Color.White.copy(alpha = 0.34f),
            start = start,
            end = Offset(start.x + size.width * 0.26f, size.height * 0.72f),
            strokeWidth = 18.dp.toPx(),
            cap = StrokeCap.Round,
        )
    }

    drawRoundRect(
        color = Color(0xFF5E3A24).copy(alpha = 0.92f),
        topLeft = Offset(size.width * 0.18f, size.height * 0.58f),
        size = androidx.compose.ui.geometry.Size(size.width * 0.52f, size.height * 0.10f),
        cornerRadius = androidx.compose.ui.geometry.CornerRadius(18.dp.toPx()),
    )
    drawRoundRect(
        color = Color(0xFF4B2C1B).copy(alpha = 0.82f),
        topLeft = Offset(size.width * 0.22f, size.height * 0.68f),
        size = androidx.compose.ui.geometry.Size(size.width * 0.06f, size.height * 0.19f),
        cornerRadius = androidx.compose.ui.geometry.CornerRadius(8.dp.toPx()),
    )
    drawRoundRect(
        color = Color(0xFF4B2C1B).copy(alpha = 0.82f),
        topLeft = Offset(size.width * 0.58f, size.height * 0.68f),
        size = androidx.compose.ui.geometry.Size(size.width * 0.06f, size.height * 0.19f),
        cornerRadius = androidx.compose.ui.geometry.CornerRadius(8.dp.toPx()),
    )

    drawCircle(
        color = Color(0xFFF6EFE4).copy(alpha = 0.96f),
        radius = size.width * 0.07f,
        center = Offset(size.width * 0.36f, size.height * 0.54f),
    )
    drawOval(
        color = Color(0xFF5B351E).copy(alpha = 0.92f),
        topLeft = Offset(size.width * 0.31f, size.height * 0.51f),
        size = androidx.compose.ui.geometry.Size(size.width * 0.10f, size.height * 0.035f),
    )

    drawCircle(
        color = Color(0xFF254E38).copy(alpha = 0.92f),
        radius = size.width * 0.09f,
        center = Offset(size.width * 0.82f, size.height * 0.50f),
    )
    repeat(5) { index ->
        val angle = -0.8f + index * 0.38f
        drawLine(
            color = Color(0xFF89A95B).copy(alpha = 0.76f),
            start = Offset(size.width * 0.82f, size.height * 0.50f),
            end = Offset(
                size.width * (0.82f + kotlin.math.cos(angle) * 0.13f),
                size.height * (0.50f + kotlin.math.sin(angle) * 0.12f),
            ),
            strokeWidth = 10.dp.toPx(),
            cap = StrokeCap.Round,
        )
    }

    drawRect(
        brush = Brush.verticalGradient(
            colors = listOf(
                Color.Transparent,
                CatchLingoColor.TextPrimary.copy(alpha = 0.26f),
            ),
            startY = size.height * 0.52f,
            endY = size.height,
        ),
    )
}

private fun androidx.compose.ui.graphics.drawscope.DrawScope.drawMagnetTrails(
    words: List<NoticedWord>,
    phase: Float,
) {
    val target = Offset(size.width * 0.5f, size.height * 0.78f)
    val moteCount = words.size.coerceIn(4, 7)
    repeat(moteCount) { index ->
        val angle = index * 0.92f
        val progress = ((phase + index * 0.13f) % 1f)
        val radiusX = size.width * (0.36f - progress * 0.24f)
        val radiusY = size.height * (0.30f - progress * 0.19f)
        val dot = Offset(
            x = target.x + kotlin.math.cos(angle) * radiusX,
            y = target.y + kotlin.math.sin(angle) * radiusY,
        )
        drawCircle(
            color = CatchLingoColor.AmberSoft.copy(alpha = 0.22f + progress * 0.28f),
            radius = (2.6f + progress * 2.2f).dp.toPx(),
            center = dot,
        )
    }
}
