package de.valentinho13.catchlingo.feature.discover

import android.Manifest
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.ImageFormat
import android.graphics.Matrix
import android.graphics.Rect
import android.graphics.YuvImage
import android.util.Log
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
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
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
import com.google.android.gms.tasks.Tasks
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.label.ImageLabeling
import com.google.mlkit.vision.label.ImageLabeler
import com.google.mlkit.vision.label.defaults.ImageLabelerOptions
import com.google.mlkit.vision.objects.DetectedObject
import com.google.mlkit.vision.objects.ObjectDetection
import com.google.mlkit.vision.objects.ObjectDetector
import com.google.mlkit.vision.objects.defaults.ObjectDetectorOptions
import de.valentinho13.catchlingo.R
import de.valentinho13.catchlingo.data.DiscoveredWord
import de.valentinho13.catchlingo.designsystem.CatchLingoColor
import de.valentinho13.catchlingo.designsystem.CatchLingoMotion
import de.valentinho13.catchlingo.designsystem.components.CatchLingoButton
import de.valentinho13.catchlingo.designsystem.components.CatchLingoButtonStyle
import de.valentinho13.catchlingo.designsystem.components.CatchLingoCard
import de.valentinho13.catchlingo.designsystem.components.CatchLingoHeroCard
import de.valentinho13.catchlingo.designsystem.components.CatchLingoSpecimenCard
import de.valentinho13.catchlingo.designsystem.components.MiniPill
import de.valentinho13.catchlingo.designsystem.rememberCatchLingoHaptics
import java.io.ByteArrayOutputStream
import java.util.Locale
import java.util.concurrent.Executors
import kotlin.math.cos
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
    val diagnosticsRepository = remember(context) {
        DiscoveryDiagnosticsRepository(context.applicationContext)
    }
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
    // Hält den letzten Fund fest, damit die Karte während der Exit-Animation befüllt bleibt.
    var heldSpecimen by remember { mutableStateOf<DiscoveredWord?>(null) }
    caughtWord?.let { heldSpecimen = it }
    var alreadyKnownWord by remember { mutableStateOf<DiscoveredWord?>(null) }
    var pendingConfirmation by remember { mutableStateOf<PendingDiscoveryConfirmation?>(null) }
    // Während der Haltephase aufgeschobene Confirmation (nur die letzte, keine Queue):
    // der Accepted-Pfad wäre nach dem DiscoveryGate sonst bis zu 30 s DuplicateBlocked.
    var deferredConfirmation by remember { mutableStateOf<PendingDiscoveryConfirmation?>(null) }
    var correctionHistory by remember { mutableStateOf(DiscoveryCorrectionHistory()) }
    var catchVersion by remember { mutableIntStateOf(0) }
    var alreadyKnownVersion by remember { mutableIntStateOf(0) }
    var debugRecognitionLine by remember { mutableStateOf<String?>(null) }
    val confirmWord: (PendingDiscoveryConfirmation, VocabularyMatch?) -> Unit = { confirmation, selectedMatch ->
        correctionHistory = correctionHistory.add(
            DiscoveryCorrectionEvent(
                timestampMillis = System.currentTimeMillis(),
                labels = confirmation.originalLabels,
                shownCandidateIds = confirmation.candidates.map { it.match.id },
                selectedCandidateId = selectedMatch?.id,
            ),
        )
        diagnosticsRepository.addEvent(
            buildDiagnosticEvent(
                timestampMillis = System.currentTimeMillis(),
                labels = confirmation.originalLabels,
                candidates = confirmation.candidates,
                decision = if (selectedMatch == null) {
                    DiscoveryDiagnosticDecision.UserRejectedNoneOfThese
                } else {
                    DiscoveryDiagnosticDecision.UserConfirmed
                },
                proposedCandidateId = confirmation.proposedWord.id,
                finalCandidateId = selectedMatch?.id,
                selectedCandidateId = selectedMatch?.id,
                reasons = confirmation.reasons.map { it.name },
            ),
        )
        pendingConfirmation = null
        if (selectedMatch != null) {
            val word = selectedMatch.toDiscoveredWord(System.currentTimeMillis())
            if (onWordCollected(word)) {
                // Neuer Catch hat Vorrang: gepufferte Confirmation verwerfen.
                deferredConfirmation = null
                haptics.catchHold()
                magnetWord = word
                caughtWord = null
                catchVersion += 1
            } else {
                haptics.softTick()
                alreadyKnownWord = word
                alreadyKnownVersion += 1
            }
        }
    }
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
            delay(CatchLingoMotion.CatchHold.toLong())
            magnetWord = null
            caughtWord = null
        }
    }

    LaunchedEffect(alreadyKnownVersion) {
        val word = alreadyKnownWord
        if (word != null) {
            delay(2_200)
            alreadyKnownWord = null
        }
    }

    // Nach dem Loslassen der SpecimenCard die aufgeschobene Confirmation präsentieren.
    LaunchedEffect(magnetWord) {
        if (magnetWord == null) {
            deferredConfirmation?.let { confirmation ->
                deferredConfirmation = null
                pendingConfirmation = confirmation
            }
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
                    pendingConfirmation = null
                    if (onWordCollected(word)) {
                        // Neuer Catch hat Vorrang: gepufferte Confirmation verwerfen.
                        deferredConfirmation = null
                        haptics.catchHold()
                        magnetWord = word
                        caughtWord = null
                        catchVersion += 1
                    } else {
                        // Während der Haltephase keine zweite Card und kein Tick (A6);
                        // das Diagnostik-Event bleibt unverändert bestehen.
                        if (magnetWord == null) {
                            haptics.softTick()
                            alreadyKnownWord = word
                            alreadyKnownVersion += 1
                        }
                        diagnosticsRepository.addEvent(
                            buildAlreadyKnownDiagnosticEvent(
                                timestampMillis = System.currentTimeMillis(),
                                word = word,
                                reasons = listOf("repositoryDuplicate"),
                            ),
                        )
                    }
                },
                onConfirmationPending = { confirmation ->
                    mlUnavailable = false
                    if (magnetWord == null) {
                        pendingConfirmation = confirmation
                    } else {
                        // Aufschieben statt verwerfen: Accepted-Pfad-Confirmations kämen wegen
                        // des Duplikat-Fensters im DiscoveryGate sonst bis zu 30 s nicht wieder.
                        deferredConfirmation = confirmation
                        diagnosticsRepository.addEvent(
                            buildDiagnosticEvent(
                                timestampMillis = System.currentTimeMillis(),
                                labels = confirmation.originalLabels,
                                candidates = confirmation.candidates,
                                decision = DiscoveryDiagnosticDecision.PendingConfirmation,
                                proposedCandidateId = confirmation.proposedWord.id,
                                reasons = confirmation.reasons.map { it.name } +
                                    "confirmationDeferredDuringHeldSpecimenCard",
                            ),
                        )
                    }
                },
                onDiagnosticEvent = { event ->
                    diagnosticsRepository.addEvent(event)
                },
                onDebugRecognition = { line ->
                    debugRecognitionLine = line
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
        caughtWord?.let {
            SpecimenBloomLayer(
                trigger = catchVersion,
                modifier = Modifier.fillMaxSize(),
            )
        }
        AnimatedVisibility(
            visible = caughtWord != null,
            enter = fadeIn(tween(CatchLingoMotion.Screen, easing = CatchLingoMotion.EaseOutSoft)) +
                scaleIn(
                    initialScale = 0.92f,
                    animationSpec = tween(CatchLingoMotion.Screen, easing = CatchLingoMotion.EaseOutSoft),
                ),
            exit = fadeOut(tween(CatchLingoMotion.Chip, easing = CatchLingoMotion.EaseOutSoft)) +
                scaleOut(
                    targetScale = 0.98f,
                    animationSpec = tween(CatchLingoMotion.Chip, easing = CatchLingoMotion.EaseOutSoft),
                ),
            modifier = Modifier
                .align(Alignment.Center)
                .padding(horizontal = 30.dp),
        ) {
            heldSpecimen?.let { word ->
                CatchLingoSpecimenCard(
                    word = word.word,
                    source = word.source,
                    context = word.category,
                    status = "Neu im Journal",
                    // Vorzeitiges Loslassen; kein zusätzlicher Haptik-Tick (der onTap-Wrapper
                    // der Komponente tickt selbst). Während des Exits nicht mehr klickbar.
                    onTap = if (caughtWord == null) {
                        null
                    } else {
                        {
                            magnetWord = null
                            caughtWord = null
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .semantics {
                            contentDescription =
                                "Fund festgehalten: ${word.word}. Zum Weiterentdecken antippen."
                        },
                )
            }
        }
        AnimatedVisibility(
            visible = alreadyKnownWord != null,
            enter = fadeIn(tween(180)) + scaleIn(initialScale = 0.97f),
            exit = fadeOut(tween(220)) + scaleOut(targetScale = 0.98f),
            modifier = Modifier
                .align(Alignment.Center)
                .padding(horizontal = 30.dp),
        ) {
            alreadyKnownWord?.let { word ->
                AlreadyKnownCard(word = word)
            }
        }
        AnimatedVisibility(
            visible = pendingConfirmation != null,
            enter = fadeIn(tween(180)) + scaleIn(initialScale = 0.98f),
            exit = fadeOut(tween(180)) + scaleOut(targetScale = 0.98f),
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .navigationBarsPadding()
                .padding(horizontal = 24.dp, vertical = 28.dp),
        ) {
            pendingConfirmation?.let { confirmation ->
                CandidateConfirmationCard(
                    confirmation = confirmation,
                    onSelectCandidate = { match -> confirmWord(confirmation, match) },
                    onSelectNone = { confirmWord(confirmation, null) },
                )
            }
        }
        AnimatedVisibility(
            visible = debugRecognitionLine != null && pendingConfirmation == null,
            enter = fadeIn(tween(160)),
            exit = fadeOut(tween(180)),
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .navigationBarsPadding()
                .padding(horizontal = 24.dp, vertical = 28.dp),
        ) {
            debugRecognitionLine?.let { line ->
                DebugRecognitionCard(line = line)
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
private fun AlreadyKnownCard(
    word: DiscoveredWord,
    modifier: Modifier = Modifier,
) {
    CatchLingoCard(modifier = modifier.fillMaxWidth()) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            MiniPill(
                text = "Schon im Feldjournal",
                color = CatchLingoColor.GreenSoft,
                contentColor = CatchLingoColor.GreenDeep,
            )
            Spacer(modifier = Modifier.height(10.dp))
            Text(
                text = "Kennst du schon: ${word.word}",
                style = MaterialTheme.typography.titleLarge,
                color = CatchLingoColor.GreenDeep,
                textAlign = TextAlign.Center,
            )
            Text(
                text = word.source,
                style = MaterialTheme.typography.bodyMedium,
                color = CatchLingoColor.TextMuted,
                textAlign = TextAlign.Center,
            )
        }
    }
}

@Composable
private fun DebugRecognitionCard(
    line: String,
    modifier: Modifier = Modifier,
) {
    CatchLingoCard(modifier = modifier.fillMaxWidth(), elevated = false) {
        Text(
            text = line,
            style = MaterialTheme.typography.labelMedium,
            color = CatchLingoColor.TextMuted,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis,
        )
    }
}

@Composable
private fun CandidateConfirmationCard(
    confirmation: PendingDiscoveryConfirmation,
    onSelectCandidate: (VocabularyMatch) -> Unit,
    onSelectNone: () -> Unit,
    modifier: Modifier = Modifier,
) {
    CatchLingoCard(modifier = modifier.fillMaxWidth()) {
        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            MiniPill(
                text = "Bitte bestätigen",
                color = CatchLingoColor.AmberSoft,
                contentColor = CatchLingoColor.AmberDeep,
            )
            Text(
                text = "Was hast du gerade gefangen?",
                style = MaterialTheme.typography.titleLarge,
                color = CatchLingoColor.GreenDeep,
            )
            Text(
                text = "Die Kamera ist sich nicht ganz sicher. Wähle den passenden Fund aus.",
                style = MaterialTheme.typography.bodyMedium,
                color = CatchLingoColor.TextMuted,
            )
            confirmation.options.forEach { option ->
                when (option) {
                    is CandidateConfirmationOption.Candidate -> CatchLingoButton(
                        text = "${option.match.word} · ${option.match.source}",
                        onClick = { onSelectCandidate(option.match) },
                        style = CatchLingoButtonStyle.Quiet,
                        modifier = Modifier.fillMaxWidth(),
                    )

                    CandidateConfirmationOption.NoneOfThese -> CatchLingoButton(
                        text = "Nicht dabei",
                        onClick = onSelectNone,
                        style = CatchLingoButtonStyle.Quiet,
                        modifier = Modifier.fillMaxWidth(),
                    )
                }
            }
        }
    }
}

@Composable
private fun CameraXPreviewLayer(
    onStreamStateChanged: (Boolean) -> Unit,
    onMlUnavailable: () -> Unit,
    onWordCollected: (DiscoveredWord) -> Unit,
    onConfirmationPending: (PendingDiscoveryConfirmation) -> Unit,
    onDiagnosticEvent: (DiscoveryDiagnosticEvent) -> Unit,
    onDebugRecognition: (String?) -> Unit,
    modifier: Modifier = Modifier,
) {
    val context = LocalContext.current
    val mlDiagnosticsEnabled = remember(context) {
        context.applicationInfo.flags and ApplicationInfo.FLAG_DEBUGGABLE != 0
    }
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
    val objectDetector = remember {
        ObjectDetection.getClient(
            ObjectDetectorOptions.Builder()
                .setDetectorMode(ObjectDetectorOptions.STREAM_MODE)
                .enableMultipleObjects()
                .enableClassification()
                .build(),
        )
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

    DisposableEffect(context, lifecycleOwner, previewView, imageLabeler, objectDetector, analysisExecutor, discoveryGate) {
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
                                    objectDetector = objectDetector,
                                    discoveryGate = discoveryGate,
                                    mlDiagnosticsEnabled = mlDiagnosticsEnabled,
                                    onMlUnavailable = onMlUnavailable,
                                    onWordCollected = onWordCollected,
                                    onConfirmationPending = onConfirmationPending,
                                    onDiagnosticEvent = onDiagnosticEvent,
                                    onDebugRecognition = onDebugRecognition,
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
            objectDetector.close()
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
    objectDetector: ObjectDetector,
    discoveryGate: DiscoveryGate,
    mlDiagnosticsEnabled: Boolean,
    onMlUnavailable: () -> Unit,
    onWordCollected: (DiscoveredWord) -> Unit,
    onConfirmationPending: (PendingDiscoveryConfirmation) -> Unit,
    onDiagnosticEvent: (DiscoveryDiagnosticEvent) -> Unit,
    onDebugRecognition: (String?) -> Unit,
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
    val objectDetectionTask = objectDetector.process(image)
    val imageLabelingTask = imageLabeler.process(image)
    Tasks.whenAllComplete(objectDetectionTask, imageLabelingTask)
        .addOnSuccessListener {
            if (!imageLabelingTask.isSuccessful) {
                onMlUnavailable()
                discoveryGate.finish()
                imageProxy.close()
                return@addOnSuccessListener
            }

            val labels = imageLabelingTask.result.orEmpty()
            val objectDiagnostics = if (objectDetectionTask.isSuccessful) {
                objectDetectionTask.result?.toObjectDetectionDiagnostics(
                    frameWidth = imageProxy.rotatedFrameWidth(),
                    frameHeight = imageProxy.rotatedFrameHeight(),
                )
            } else {
                null
            }
            logObjectDetectionDiagnostics(
                diagnostics = objectDiagnostics,
                enabled = mlDiagnosticsEnabled,
            )

            val originalLabels = labels.toMlLabelObservations()
            val directCandidates = labels.mapNotNull { label ->
                mapLabelToVocabulary(label.text)?.let { match ->
                    DiscoveryCandidate(labelText = label.text, confidence = label.confidence, match = match)
                }
            }
            val rankedCandidates = rankDiscoveryCandidates(
                labels = originalLabels,
                directCandidates = directCandidates,
            )
            val mappedLabels = rankedCandidates.candidates
            val bestEligible = mappedLabels
                .asSequence()
                .filter { it.canAutoCatch() }
                .maxByOrNull { it.score }
            val decision = bestEligible?.let { candidate ->
                discoveryGate.accept(
                    match = candidate.match,
                    confidence = candidate.confidence,
                    nowMillis = System.currentTimeMillis(),
                )
            } ?: DiscoveryDecision.Ignored
            val acceptedPendingConfirmation = if (
                decision.status == DiscoveryDecisionStatus.Accepted &&
                decision.match != null
            ) {
                buildPendingConfirmation(
                    originalLabels = originalLabels,
                    candidates = mappedLabels,
                    proposedWord = decision.match,
                    acceptedConfidence = decision.confidence,
                )
            } else {
                null
            }
            val rankedPendingConfirmation = if (
                acceptedPendingConfirmation == null &&
                decision.status != DiscoveryDecisionStatus.DuplicateBlocked
            ) {
                val confirmationCandidates = mappedLabels
                    .filter { candidate ->
                        candidate.requiresConfirmation ||
                            (
                                mlDiagnosticsEnabled &&
                                    candidate.confidence < candidate.match.minConfidence &&
                                    candidate.confidence >= DEBUG_CONFIRMATION_MIN_CONFIDENCE
                                )
                    }
                    .filter { it.confidence >= CONFIRMATION_MIN_CONFIDENCE }
                buildDebugPendingConfirmation(
                    originalLabels = originalLabels,
                    candidates = confirmationCandidates,
                )
            } else {
                null
            }
            val pendingConfirmation = acceptedPendingConfirmation ?: rankedPendingConfirmation

            onDebugRecognition(
                if (mlDiagnosticsEnabled && labels.isNotEmpty() && pendingConfirmation == null) {
                    labels.debugRecognitionLine()
                } else {
                    null
                },
            )

            logMlDiagnostics(
                labels = labels,
                mappedLabels = mappedLabels,
                decision = decision,
                pendingConfirmation = pendingConfirmation,
                enabled = mlDiagnosticsEnabled,
            )
            val diagnosticEvent = buildDiagnosticEvent(
                timestampMillis = System.currentTimeMillis(),
                labels = originalLabels,
                candidates = mappedLabels,
                decision = decision.toDiagnosticDecision(pendingConfirmation),
                proposedCandidateId = decision.match?.id,
                finalCandidateId = if (
                    decision.status == DiscoveryDecisionStatus.Accepted &&
                    pendingConfirmation == null
                ) {
                    decision.match?.id
                } else {
                    null
                },
                reasons = buildList {
                    add("context=${rankedCandidates.context.name}")
                    if (pendingConfirmation != null) {
                        addAll(pendingConfirmation.reasons.map { it.name })
                    } else {
                        addAll(decision.diagnosticReasons())
                    }
                },
                objectDetection = objectDiagnostics,
            )
            labelSelectedObjectCropForDiagnostics(
                imageProxy = imageProxy,
                imageLabeler = imageLabeler,
                objectDetection = objectDiagnostics,
                wholeFrameLabels = originalLabels,
                enabled = mlDiagnosticsEnabled,
            ) { cropLabeling ->
                onDiagnosticEvent(diagnosticEvent.copy(cropLabeling = cropLabeling))
                discoveryGate.finish()
                imageProxy.close()
            }

            if (decision.status == DiscoveryDecisionStatus.Accepted && decision.match != null) {
                if (pendingConfirmation == null) {
                    onWordCollected(decision.match.toDiscoveredWord(System.currentTimeMillis()))
                } else {
                    onConfirmationPending(pendingConfirmation)
                }
            } else if (pendingConfirmation != null) {
                onConfirmationPending(pendingConfirmation)
            }
        }
        .addOnFailureListener {
            onMlUnavailable()
            discoveryGate.finish()
            imageProxy.close()
        }
}

private fun labelSelectedObjectCropForDiagnostics(
    imageProxy: ImageProxy,
    imageLabeler: ImageLabeler,
    objectDetection: ObjectDetectionDiagnostics?,
    wholeFrameLabels: List<MlLabelObservation>,
    enabled: Boolean,
    onComplete: (CropLabelingDiagnostics) -> Unit,
) {
    if (!enabled) {
        onComplete(buildSkippedCropLabelingDiagnostics(CROP_FAILURE_NO_TARGET, wholeFrameLabels))
        return
    }

    val selectedBox = objectDetection?.selected?.box
    val boundsResult = calculateCropBounds(
        selectedBox = selectedBox,
        frameWidth = imageProxy.rotatedFrameWidth(),
        frameHeight = imageProxy.rotatedFrameHeight(),
    )
    val bounds = boundsResult.getOrElse { error ->
        val reason = error.message ?: CROP_FAILURE_INVALID_BOUNDS
        logCropLabelingSkippedOrFailed(reason = reason, wholeFrameLabels = wholeFrameLabels)
        onComplete(buildSkippedCropLabelingDiagnostics(reason, wholeFrameLabels))
        return
    }

    val cropBitmap = runCatching {
        imageProxy.createRotatedCrop(bounds)
    }.getOrElse {
        logCropLabelingSkippedOrFailed(reason = CROP_FAILURE_BITMAP_CONVERSION, wholeFrameLabels = wholeFrameLabels)
        onComplete(buildSkippedCropLabelingDiagnostics(CROP_FAILURE_BITMAP_CONVERSION, wholeFrameLabels))
        return
    }

    imageLabeler.process(InputImage.fromBitmap(cropBitmap, 0))
        .addOnSuccessListener { cropLabels ->
            val cropObservations = cropLabels.toMlLabelObservations()
            val diagnostics = buildSuccessfulCropLabelingDiagnostics(
                wholeFrameLabels = wholeFrameLabels,
                cropLabels = cropObservations,
            )
            logCropLabelingDiagnostics(diagnostics)
            onComplete(diagnostics)
        }
        .addOnFailureListener {
            logCropLabelingSkippedOrFailed(reason = CROP_FAILURE_LABELING_FAILED, wholeFrameLabels = wholeFrameLabels)
            onComplete(buildSkippedCropLabelingDiagnostics(CROP_FAILURE_LABELING_FAILED, wholeFrameLabels))
        }
        .addOnCompleteListener {
            cropBitmap.recycle()
        }
}

private fun ImageProxy.createRotatedCrop(bounds: CropBounds): Bitmap {
    val source = toJpegBackedBitmap()
    val rotated = if (imageInfo.rotationDegrees == 0) {
        source
    } else {
        val matrix = Matrix().apply { postRotate(imageInfo.rotationDegrees.toFloat()) }
        Bitmap.createBitmap(source, 0, 0, source.width, source.height, matrix, true).also {
            source.recycle()
        }
    }
    return Bitmap.createBitmap(rotated, bounds.left, bounds.top, bounds.width, bounds.height).also {
        rotated.recycle()
    }
}

private fun ImageProxy.toJpegBackedBitmap(): Bitmap {
    val nv21 = toNv21ByteArray()
    val yuvImage = YuvImage(nv21, ImageFormat.NV21, width, height, null)
    val output = ByteArrayOutputStream()
    check(yuvImage.compressToJpeg(Rect(0, 0, width, height), 90, output)) {
        "Unable to convert frame to JPEG"
    }
    val bytes = output.toByteArray()
    return checkNotNull(BitmapFactory.decodeByteArray(bytes, 0, bytes.size)) {
        "Unable to decode frame bitmap"
    }
}

private fun ImageProxy.toNv21ByteArray(): ByteArray {
    val yPlane = planes[0]
    val uPlane = planes[1]
    val vPlane = planes[2]
    val output = ByteArray(width * height * 3 / 2)

    var outputOffset = 0
    for (row in 0 until height) {
        val rowOffset = row * yPlane.rowStride
        for (col in 0 until width) {
            output[outputOffset++] = yPlane.buffer.get(rowOffset + col * yPlane.pixelStride)
        }
    }

    val chromaWidth = width / 2
    val chromaHeight = height / 2
    for (row in 0 until chromaHeight) {
        val uRowOffset = row * uPlane.rowStride
        val vRowOffset = row * vPlane.rowStride
        for (col in 0 until chromaWidth) {
            output[outputOffset++] = vPlane.buffer.get(vRowOffset + col * vPlane.pixelStride)
            output[outputOffset++] = uPlane.buffer.get(uRowOffset + col * uPlane.pixelStride)
        }
    }

    return output
}

private fun buildDebugConfirmationCandidates(
    labels: List<com.google.mlkit.vision.label.ImageLabel>,
    mappedLabels: List<DiscoveryCandidate>,
): List<DiscoveryCandidate> {
    val softCandidates = labels.mapNotNull { label ->
        if (mappedLabels.any { it.labelText == label.text }) {
            null
        } else {
            mapLabelToSoftVocabulary(label.text)?.let { match ->
                DiscoveryCandidate(labelText = label.text, confidence = label.confidence, match = match)
            }
        }
    }
    return (mappedLabels + softCandidates)
        .sortedByDescending { it.confidence }
        .distinctBy { it.match.id }
}

private fun List<com.google.mlkit.vision.label.ImageLabel>.debugRecognitionLine(): String {
    val labelSummary = take(3).joinToString(", ") { label ->
        "${label.text.lowercase()} ${label.confidence.formatConfidence()}"
    }
    return "Erkannt: $labelSummary"
}

private fun List<com.google.mlkit.vision.label.ImageLabel>.toMlLabelObservations(): List<MlLabelObservation> =
    map { label -> MlLabelObservation(text = label.text, confidence = label.confidence) }

private fun List<DetectedObject>.toObjectDetectionDiagnostics(
    frameWidth: Int,
    frameHeight: Int,
): ObjectDetectionDiagnostics =
    selectObjectDetectionTarget(
        candidates = map { detectedObject ->
            val box = detectedObject.boundingBox
            ObjectDetectionCandidateBox(
                left = box.left,
                top = box.top,
                right = box.right,
                bottom = box.bottom,
                hasCategoryLabels = detectedObject.labels.isNotEmpty(),
            )
        },
        frameSize = ObjectDetectionFrameSize(width = frameWidth, height = frameHeight),
    )

private fun ImageProxy.rotatedFrameWidth(): Int =
    if (imageInfo.rotationDegrees == 90 || imageInfo.rotationDegrees == 270) height else width

private fun ImageProxy.rotatedFrameHeight(): Int =
    if (imageInfo.rotationDegrees == 90 || imageInfo.rotationDegrees == 270) width else height

private enum class DiscoveryDecisionStatus {
    Accepted,
    WaitingForStability,
    DuplicateBlocked,
    Ignored,
}

private data class DiscoveryDecision(
    val status: DiscoveryDecisionStatus,
    val match: VocabularyMatch? = null,
    val confidence: Float = 0f,
    val stableCount: Int = 0,
) {
    companion object {
        val Ignored = DiscoveryDecision(status = DiscoveryDecisionStatus.Ignored)
    }
}

private fun logMlDiagnostics(
    labels: List<com.google.mlkit.vision.label.ImageLabel>,
    mappedLabels: List<DiscoveryCandidate>,
    decision: DiscoveryDecision,
    pendingConfirmation: PendingDiscoveryConfirmation?,
    enabled: Boolean,
) {
    if (!enabled || labels.isEmpty()) return

    val mappedByLabel = mappedLabels.associateBy { it.labelText }
    val topLabels = labels
        .take(ML_DIAGNOSTIC_LABEL_LIMIT)
        .joinToString(separator = " | ") { label ->
            val mapped = mappedByLabel[label.text]
            val confidence = label.confidence.formatConfidence()
            if (mapped == null) {
                "${label.text}:$confidence -> unmapped"
            } else {
                val thresholdState = if (mapped.confidence >= mapped.match.minConfidence) "eligible" else "below-threshold"
                "${label.text}:$confidence -> ${mapped.match.id}/${mapped.match.word}/${mapped.match.category} " +
                    "$thresholdState score=${mapped.score.formatConfidence()} context=${mapped.context.name} " +
                    "boost=${mapped.contextBoost.formatConfidence()} risk=${mapped.riskPenalty.formatConfidence()} " +
                    "confirm=${mapped.requiresConfirmation}"
            }
        }
    Log.d(
        ML_LOG_TAG,
        "labels=[$topLabels] decision=${decision.describe()} confirmation=${pendingConfirmation.describe()}",
    )
}

private fun logObjectDetectionDiagnostics(
    diagnostics: ObjectDetectionDiagnostics?,
    enabled: Boolean,
) {
    if (!enabled) return

    if (diagnostics == null) {
        Log.d(ML_LOG_TAG, "ObjectDetection unavailable")
        return
    }

    val selected = diagnostics.selected
    if (selected == null) {
        Log.d(ML_LOG_TAG, "ObjectDetection objects=${diagnostics.objectCount} selected=none")
    } else {
        Log.d(
            ML_LOG_TAG,
            "ObjectDetection objects=${diagnostics.objectCount} " +
                "selected=${selected.reason.name.lowercase(Locale.US)} " +
                "area=${selected.areaRatio.formatConfidence()} " +
                "distance=${selected.centerDistance.formatConfidence()} " +
                "box=${selected.box.compactString()} " +
                "labels=${selected.hasCategoryLabels} " +
                "frame=${diagnostics.frameWidth}x${diagnostics.frameHeight}",
        )
    }
}

private fun logCropLabelingDiagnostics(diagnostics: CropLabelingDiagnostics) {
    val comparison = diagnostics.labelComparison
    Log.d(
        ML_LOG_TAG,
        "CropLabeling success " +
            "top=${comparison.topCropLabel.orEmpty()} ${diagnostics.cropLabels.firstOrNull()?.confidence?.formatConfidence() ?: "0.00"} " +
            "wholeTop=${comparison.topWholeFrameLabel.orEmpty()} " +
            "${diagnostics.wholeFrameLabels.firstOrNull()?.confidence?.formatConfidence() ?: "0.00"} " +
            "changed=${comparison.didCropChangeTopLabel}",
    )
}

private fun logCropLabelingSkippedOrFailed(
    reason: String,
    wholeFrameLabels: List<MlLabelObservation>,
) {
    val prefix = if (reason == CROP_FAILURE_NO_TARGET || reason == CROP_FAILURE_TINY_CROP) {
        "skipped"
    } else {
        "failed"
    }
    Log.d(
        ML_LOG_TAG,
        "CropLabeling $prefix reason=$reason wholeTop=${wholeFrameLabels.firstOrNull()?.text.orEmpty()}",
    )
}

private fun DiscoveryDecision.describe(): String = when (status) {
    DiscoveryDecisionStatus.Accepted -> "accepted ${match?.id}/${match?.word}"
    DiscoveryDecisionStatus.WaitingForStability -> {
        "waiting ${match?.id}/${match?.word} stable=$stableCount/$REQUIRED_STABLE_MATCHES"
    }
    DiscoveryDecisionStatus.DuplicateBlocked -> "duplicate-blocked ${match?.id}/${match?.word}"
    DiscoveryDecisionStatus.Ignored -> "ignored"
}

private fun PendingDiscoveryConfirmation?.describe(): String =
    if (this == null) {
        "none"
    } else {
        "pending ${proposedWord.id}/${proposedWord.word} reasons=${reasons.joinToString("+")}"
    }

private fun DiscoveryDecision.toDiagnosticDecision(
    pendingConfirmation: PendingDiscoveryConfirmation?,
): DiscoveryDiagnosticDecision = when {
    pendingConfirmation != null -> DiscoveryDiagnosticDecision.PendingConfirmation
    status == DiscoveryDecisionStatus.Accepted -> DiscoveryDiagnosticDecision.AutoAccepted
    status == DiscoveryDecisionStatus.DuplicateBlocked -> DiscoveryDiagnosticDecision.DuplicateBlocked
    else -> DiscoveryDiagnosticDecision.Ignored
}

private fun DiscoveryDecision.diagnosticReasons(): List<String> = when (status) {
    DiscoveryDecisionStatus.WaitingForStability -> listOf("waitingForStability")
    DiscoveryDecisionStatus.Ignored -> listOf("noEligibleCandidate")
    else -> emptyList()
}

private fun Float.formatConfidence(): String = String.format(Locale.US, "%.2f", this)

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
    fun accept(match: VocabularyMatch, confidence: Float, nowMillis: Long): DiscoveryDecision {
        if (confidence < match.minConfidence) {
            return DiscoveryDecision.Ignored
        }
        recentlyCollectedAtMillis.entries.removeAll { (_, collectedAt) ->
            nowMillis - collectedAt > DUPLICATE_WINDOW_MILLIS
        }
        if (nowMillis - (recentlyCollectedAtMillis[match.id] ?: 0L) < DUPLICATE_WINDOW_MILLIS) {
            return DiscoveryDecision(
                status = DiscoveryDecisionStatus.DuplicateBlocked,
                match = match,
                confidence = confidence,
            )
        }

        if (candidateId == match.id && nowMillis - candidateSeenAtMillis < STABLE_MATCH_WINDOW_MILLIS) {
            candidateCount += 1
        } else {
            candidateId = match.id
            candidateCount = 1
        }
        candidateSeenAtMillis = nowMillis

        if (candidateCount < REQUIRED_STABLE_MATCHES) {
            return DiscoveryDecision(
                status = DiscoveryDecisionStatus.WaitingForStability,
                match = match,
                confidence = confidence,
                stableCount = candidateCount,
            )
        }

        recentlyCollectedAtMillis[match.id] = nowMillis
        candidateId = null
        candidateCount = 0
        candidateSeenAtMillis = 0L
        return DiscoveryDecision(
            status = DiscoveryDecisionStatus.Accepted,
            match = match,
            confidence = confidence,
            stableCount = REQUIRED_STABLE_MATCHES,
        )
    }

    @Synchronized
    fun finish() {
        inFlight = false
    }

    private companion object {
        const val ANALYSIS_INTERVAL_MILLIS = 900L
        const val STABLE_MATCH_WINDOW_MILLIS = 4_500L
        const val DUPLICATE_WINDOW_MILLIS = 30_000L
    }
}

private const val ML_DIAGNOSTIC_LABEL_LIMIT = 5
private const val REQUIRED_STABLE_MATCHES = 2
private const val CONFIRMATION_MIN_CONFIDENCE = 0.50f
private const val DEBUG_CONFIRMATION_MIN_CONFIDENCE = 0.42f

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
        val targetX = size.width * 0.50f
        val targetY = size.height * 0.48f
        val p = progress.value
        val fade = sin(p * Math.PI).toFloat().coerceIn(0f, 1f)

        repeat(MagnetMoteCount) { index ->
            val startX = size.width * MagnetMoteStarts[index * 2]
            val startY = size.height * MagnetMoteStarts[index * 2 + 1]
            val curve = MagnetMoteCurves[index]
            val controlX = (startX + targetX) * 0.5f + size.width * curve
            val controlY = (startY + targetY) * 0.5f - size.height * (0.08f + (index % 3) * 0.018f)
            val oneMinus = 1f - p
            val driftX = oneMinus * oneMinus * startX + 2f * oneMinus * p * controlX + p * p * targetX
            val driftY = oneMinus * oneMinus * startY + 2f * oneMinus * p * controlY + p * p * targetY
            drawCircle(
                color = CatchLingoColor.AmberSoft.copy(alpha = fade * 0.52f),
                radius = (2.6f + p * 3.6f).dp.toPx(),
                center = Offset(driftX, driftY),
            )
        }
        drawCircle(
            color = CatchLingoColor.AmberSoft.copy(alpha = p * 0.20f),
            radius = (42f + p * 44f).dp.toPx(),
            center = Offset(targetX, targetY),
        )
    }
}

private const val MagnetMoteCount = 12

private val MagnetMoteStarts = floatArrayOf(
    0.08f, 0.30f,
    0.18f, 0.18f,
    0.36f, 0.12f,
    0.68f, 0.14f,
    0.88f, 0.28f,
    0.94f, 0.52f,
    0.82f, 0.72f,
    0.64f, 0.84f,
    0.40f, 0.86f,
    0.16f, 0.74f,
    0.06f, 0.54f,
    0.28f, 0.42f,
)

private val MagnetMoteCurves = floatArrayOf(
    0.05f,
    -0.04f,
    0.06f,
    -0.05f,
    -0.08f,
    0.04f,
    -0.03f,
    0.07f,
    -0.06f,
    0.05f,
    -0.04f,
    0.03f,
)

@Composable
private fun SpecimenBloomLayer(
    trigger: Int,
    modifier: Modifier = Modifier,
) {
    val progress = remember { Animatable(0f) }

    LaunchedEffect(trigger) {
        progress.snapTo(0f)
        progress.animateTo(
            targetValue = 1f,
            animationSpec = tween(CatchLingoMotion.Bloom, easing = CatchLingoMotion.EaseOutSoft),
        )
    }

    Canvas(modifier = modifier) {
        val p = progress.value
        if (p >= 1f) return@Canvas
        val fade = 1f - p
        val center = Offset(size.width * 0.5f, size.height * 0.5f)
        val radius = size.width * (0.30f + p * 0.34f)
        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(
                    CatchLingoColor.AmberSoft.copy(alpha = fade * 0.42f),
                    CatchLingoColor.AmberSoft.copy(alpha = 0f),
                ),
                center = center,
                radius = radius,
            ),
            radius = radius,
            center = center,
        )
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
