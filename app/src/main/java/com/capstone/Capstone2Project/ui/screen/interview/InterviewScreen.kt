package com.capstone.Capstone2Project.ui.screen.interview

import android.Manifest
import android.content.Intent
import android.os.Bundle
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.util.Log
import android.widget.Toast
import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color.Companion.Black
import androidx.compose.ui.graphics.Color.Companion.DarkGray
import androidx.compose.ui.graphics.Color.Companion.Transparent
import androidx.compose.ui.graphics.Color.Companion.White
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle.Event.*
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.airbnb.lottie.compose.*
import com.capstone.Capstone2Project.data.model.LogLine
import com.capstone.Capstone2Project.data.model.Script
import com.capstone.Capstone2Project.data.resource.Resource
import com.capstone.Capstone2Project.data.resource.successOrNull
import com.capstone.Capstone2Project.ui.screen.animation.NewLogContent
import com.capstone.Capstone2Project.ui.screen.animation.OldLogContent
import com.capstone.Capstone2Project.ui.screen.loading.LoadingScreen
import com.capstone.Capstone2Project.utils.InterviewManager
import com.capstone.Capstone2Project.utils.LocalInterviewManager
import com.capstone.Capstone2Project.utils.RequestPermissions
import com.capstone.Capstone2Project.utils.composable.ComposableLifecycle
import com.capstone.Capstone2Project.utils.etc.*
import com.capstone.Capstone2Project.utils.etc.CustomFont.nexonFont
import com.capstone.Capstone2Project.utils.extensions.clickableWithoutRipple
import com.capstone.Capstone2Project.utils.theme.LocalSpacing
import com.capstone.Capstone2Project.utils.theme.bright_blue
import com.capstone.Capstone2Project.utils.theme.darker_blue
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import com.google.mlkit.vision.face.FaceDetection
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.face.Face
import com.google.mlkit.vision.face.FaceDetectorOptions
import com.google.mlkit.vision.pose.Pose
import com.google.mlkit.vision.pose.PoseDetection
import com.google.mlkit.vision.pose.accurate.AccuratePoseDetectorOptions
import java.util.*
import kotlin.math.roundToInt

//@Preview(showBackground = true)
@Composable
fun CameraScreenPreview() {

    InterviewScreen(rememberNavController(), null)
}

@androidx.annotation.OptIn(androidx.camera.core.ExperimentalGetImage::class)
@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun InterviewScreen(
    navController: NavController,
    script: Script?
) {

    val interviewViewModel: InterviewViewModel = hiltViewModel()

    val context = LocalContext.current

    LaunchedEffect(interviewViewModel) {
        script?.let {
            interviewViewModel.fetchCustomQuestionnaire(it)
        } ?: run {
            navController.popBackStack()
            AlertUtils.showToast(context, "???????????? ?????? ??????????????? ?????????.")
        }
    }

    val questionnaire = interviewViewModel.customQuestionnaireFlow.collectAsStateWithLifecycle()


    val lifecycleOwner = LocalLifecycleOwner.current


    val faceDetector = remember {

        val options = FaceDetectorOptions.Builder()
            .setClassificationMode(FaceDetectorOptions.CLASSIFICATION_MODE_ALL)
            .setContourMode(FaceDetectorOptions.CONTOUR_MODE_ALL)
            .setPerformanceMode(FaceDetectorOptions.PERFORMANCE_MODE_FAST)
            .enableTracking()
            .build()

        FaceDetection.getClient(options)
    }


    val poseDetector = remember {
        val options = AccuratePoseDetectorOptions.Builder()
            .setDetectorMode(AccuratePoseDetectorOptions.STREAM_MODE)
            .setPreferredHardwareConfigs(AccuratePoseDetectorOptions.CPU_GPU)
            .build()

        PoseDetection.getClient(options)
    }


    val imageAnalyzer = remember {
        InterviewManager.ImageAnalyzer { imageProxy ->
            imageProxy.image?.let { image ->

                val inputImage =
                    InputImage.fromMediaImage(image, imageProxy.imageInfo.rotationDegrees)

                var readyToDetectFace = true
                var readyToDetectPose = true

                fun completeDetection() {
                    if (!readyToDetectFace && !readyToDetectPose) {
                        readyToDetectFace = true
                        readyToDetectPose = true
                        image.close()
                        imageProxy.close()
                    }
                }

                faceDetector.process(inputImage)
                    .addOnSuccessListener { faces ->
                        if (readyToDetectFace) {
                            processFaceDetectionResult(
                                faces,
                                viewModel = interviewViewModel
                            )
                            readyToDetectFace = false
                        }

                    }
                    .addOnFailureListener { e ->
                        e.printStackTrace()
                    }
                    .addOnCompleteListener {
                        completeDetection()
                    }

                poseDetector.process(inputImage)
                    .addOnSuccessListener { pose ->
                        if (readyToDetectPose) {
                            processPoseDetectionResult(pose, viewModel = interviewViewModel)
                            readyToDetectPose = false
                        }
                    }
                    .addOnFailureListener { e ->
                        e.printStackTrace()
                    }
                    .addOnCompleteListener {
                        completeDetection()
                    }


            }


        }
    }


    val interviewManager = remember {
        InterviewManager.Builder(context)
            .setImageAnalyzer(imageAnalyzer)
            .setLifecycleOwner(lifecycleOwner)
            .build()
    }


    val permissions = remember {
        listOf(Manifest.permission.CAMERA, Manifest.permission.RECORD_AUDIO)
    }

    val permissionState = rememberMultiplePermissionsState(permissions = permissions)

    questionnaire.value?.let {
        when (it) {
            is Resource.Error -> {
                navController.popBackStack()
                AlertUtils.showToast(context, "${it.error} ????????? ?????????????????????", Toast.LENGTH_LONG)
            }
            Resource.Loading -> {
                LoadingScreen()
            }
            is Resource.Success -> {
                if (permissionState.allPermissionsGranted) {
                    CompositionLocalProvider(
                        LocalInterviewManager provides interviewManager,
                        LocalTextStyle provides TextStyle(
                            fontFamily = CustomFont.nexonFont
                        )
                    ) {
                        VideoScreenContent(navController)
                    }
                } else {
                    RequestPermissions(permissionState)
                }


            }
            else -> {}
        }
    }

}


private fun processPoseDetectionResult(pose: Pose?, viewModel: InterviewViewModel) {

    pose?.toLogLine()?.let {
        viewModel.loadInterviewLogLine(it)
    }

}

private fun processFaceDetectionResult(
    faces: List<Face>,
    viewModel: InterviewViewModel
) {
    if (faces.isEmpty()) {
        val logLine = LogLine(
            type = LogLine.Type.Error,
            "????????? ???????????? ???????????????"
        )

        viewModel.loadInterviewLogLine(logLine)
        return
    }

    val logLine = faces.last().toLogLine()

    logLine?.let {
        viewModel.loadInterviewLogLine(it)
    }

}


@Composable
private fun VideoScreenContent(
    navController: NavController
) {

    val showCameraPreview = remember {
        mutableStateOf(true)
    }

    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        CameraPreview()

        if (!showCameraPreview.value) {

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        color = Black
                    ),
                contentAlignment = Alignment.Center
            ) {
                val composition by rememberLottieComposition(LottieCompositionSpec.Asset("lottie/face.json"))
                val progress by animateLottieCompositionAsState(
                    composition,
                    iterations = LottieConstants.IterateForever,
                    speed = 0.5f
                )


                LottieAnimation(
                    composition = composition,
                    progress = { progress },
                    modifier = Modifier
                        .fillMaxWidth(0.5f)
                        .alpha(0.5f)
                )

            }
        }


        ComposableLifecycle { _, event ->

            when (event) {

                ON_CREATE -> {0
                }
                ON_START -> {
                }
                ON_RESUME -> {
                }
                ON_PAUSE -> {
                }
                ON_STOP -> {
                }
                ON_DESTROY -> {
                }
                else -> Unit
            }
        }
        InterviewUIScreenContent(
            exitButtonClick = {
                navController.popBackStack()
            },
            showPreviewClick = {
                showCameraPreview.value = it
            }
        )
    }
}

@Composable
private fun CameraPreview() {
    val interviewManger = LocalInterviewManager.current

    AndroidView(
        factory = {
            interviewManger.showPreview()
        },
        modifier = Modifier.fillMaxSize(),
        update = {
            interviewManger.updatePreview(it)
        }
    )


}

@Composable
fun InterviewUIScreenContent(
    exitButtonClick: () -> Unit,
    showPreviewClick: (Boolean) -> Unit
) {

    val spacing = LocalSpacing.current

    val interviewViewModel: InterviewViewModel = hiltViewModel()

    val newInterviewLogLine =
        interviewViewModel.newInterviewLogLineFlow.collectAsStateWithLifecycle()

    val oldInterviewLogLine =
        interviewViewModel.oldInterviewLogLineFlow.collectAsStateWithLifecycle()

    val composition by rememberLottieComposition(LottieCompositionSpec.Asset("lottie/mic.json"))

    val isPlaying = remember {
        mutableStateOf(false)
    }

    val lottieState = animateLottieCompositionAsState(
        composition = composition,
        isPlaying = isPlaying.value,
        iterations = 1
    )

    LaunchedEffect(lottieState.isPlaying) {
        isPlaying.value = lottieState.isPlaying
    }

    val showPreview = remember {
        mutableStateOf(true)
    }

    LaunchedEffect(showPreview.value) {
        showPreviewClick(showPreview.value)
    }


    val currentPage = interviewViewModel.currentPageFlow.collectAsStateWithLifecycle()

    val customQuestionnaire =
        interviewViewModel.customQuestionnaireFlow.collectAsStateWithLifecycle()

    val questions = remember {
        customQuestionnaire.value?.successOrNull()?.questions
    }

    val interviewState = interviewViewModel.interviewStateFlow.collectAsStateWithLifecycle()

    val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH)

    val context = LocalContext.current

    intent.apply {
        //?????? ???????????? ???????????? ???????????? ?????? ??????
        putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_WEB_SEARCH)
        putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE, context.packageName)
        //?????? ???????????? ???????????? ?????? ??????
        putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault())
        putExtra(RecognizerIntent.EXTRA_LANGUAGE_PREFERENCE, "ko-KR")

        putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 1)

        //?????? ??????????????? ????????? ???????????? ?????? default??? false?????? ???????????? true??? ??????
        putExtra(RecognizerIntent.EXTRA_PARTIAL_RESULTS, true)

    }

    var speechText by remember {
        mutableStateOf("")
    }

    LaunchedEffect(speechText) {
        interviewViewModel.updateAnswer(speechText)
    }

    var partialText by remember {
        mutableStateOf("")
    }

    val soundVolume = remember {
        mutableStateOf<Int?>(null)
    }

    val speechRecognizer: SpeechRecognizer = remember {
        SpeechRecognizer.createSpeechRecognizer(context)
    }

    val listener = object : RecognitionListener {

        override fun onReadyForSpeech(params: Bundle?) {
            //????????? ????????? ????????? ?????? ??????

        }

        override fun onBeginningOfSpeech() {
            //????????? ???????????? ??? ??????

        }

        override fun onRmsChanged(rmsdB: Float) {
            //???????????? ????????? ????????? ?????????
            if (rmsdB >= 0.0f) {
                soundVolume.value = rmsdB.roundToInt()
            }
        }

        override fun onBufferReceived(buffer: ByteArray?) {
            //?????? ???????????? ????????? ??? ????????? buffer??? ??????
        }

        override fun onEndOfSpeech() {
            //???????????? ?????? ???????????? ??????
            Log.d("TAG", "onEndOfSpeech ??????")
        }

        override fun onError(error: Int) {
            //???????????? ?????? ?????? ????????? ???????????? ??? ??????
            when (error) {
                SpeechRecognizer.ERROR_AUDIO -> { //3
                    //????????? ??????
                }
                SpeechRecognizer.ERROR_CLIENT -> { //5
                    //??????????????? ??????
                    //start, cancel, stop ?????? ????????? ??????

                }
                SpeechRecognizer.ERROR_INSUFFICIENT_PERMISSIONS -> {
                    //????????? ??????
                }
                SpeechRecognizer.ERROR_NETWORK -> {
                    //???????????? ??????
                }
                SpeechRecognizer.ERROR_NETWORK_TIMEOUT -> {
                    //???????????? ?????? ??????
                }
                SpeechRecognizer.ERROR_NO_MATCH -> { //7
                    //?????? ?????? ??? ??????

                }
                SpeechRecognizer.ERROR_RECOGNIZER_BUSY -> { //8
                    //Recognizer ??? ??????

                }
                SpeechRecognizer.ERROR_SERVER -> {
                    //????????? ?????????
                }
                SpeechRecognizer.ERROR_SPEECH_TIMEOUT -> {
                    //????????? ?????? ??????
                }
                else -> {
                    //??? ??? ?????? ??????
                }
            }

        }

        override fun onResults(results: Bundle?) {
            //?????? ????????? ???????????? ??????
            //?????? ?????? ArrayList??? ????????? ?????? textView??? ????????? ?????????
            results ?: return
            val matches = results.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
            //val confidenceScore = results.getFloat(RecognizerIntent.EXTRA_CONFIDENCE_SCORES)
            matches?.let {
                it.forEach { match ->
                    speechText += " $match"
                }
            }


        }

        override fun onPartialResults(partialResults: Bundle?) {
            //?????? ?????? ????????? ????????? ??? ?????? ??? ??????
            partialResults ?: return
            val matches = partialResults.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)

            matches?.forEach { match ->
                partialText = match
            }
        }

        override fun onEvent(eventType: Int, params: Bundle?) {
            //???????????? ???????????? ?????? ??????
        }

    }

    fun startSpeechRecognize() {
        speechRecognizer.apply {
            stopListening()
            setRecognitionListener(listener)
            startListening(intent)
        }
    }

    CompositionLocalProvider(
        LocalTextStyle provides TextStyle(
            fontFamily = nexonFont
        )
    ) {

        interviewState.value.let {
            when (it) {
                InterviewViewModel.InterviewState.Ended -> {
                    val interviewResult = remember {
                        interviewViewModel.interviewResultFlow.value
                    }

                    interviewResult?.apply {
                        Dialog(
                            onDismissRequest = {},
                            properties = DialogProperties(usePlatformDefaultWidth = false)
                        ) {

                        }
                    }

                }
                else -> {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(
                                color = Transparent
                            )
                            .padding(spacing.small)
                            .padding(top = 20.dp)
                    ) {

                        IconButton(
                            onClick = {
                                showPreview.value = !showPreview.value
                            },
                            modifier = Modifier
                                .align(Alignment.TopStart)
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier
                                    .border(
                                        width = 1.dp,
                                        color = White,
                                        shape = RoundedCornerShape(10.dp)

                                    )
                                    .background(
                                        color = Transparent,
                                        shape = RoundedCornerShape(10.dp)
                                    )
                                    .padding(horizontal = spacing.medium, vertical = spacing.small)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.CameraAlt,
                                    contentDescription = null,
                                    tint = White
                                )

                                Spacer(modifier = Modifier.width(spacing.small))

                                Text(
                                    if (showPreview.value) "????????? ??????" else "????????? ??????",
                                    style = LocalTextStyle.current.copy(
                                        color = White,
                                        fontWeight = FontWeight(550),
                                        shadow = Shadow(
                                            color = DarkGray,
                                            offset = Offset(1f, 1f),
                                            blurRadius = 4f
                                        )
                                    )
                                )
                            }
                        }

                        IconButton(
                            onClick = {
                                exitButtonClick()
                            },
                            modifier = Modifier
                                .align(Alignment.TopEnd)
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier
                                    .border(
                                        width = 1.dp,
                                        color = White,
                                        shape = RoundedCornerShape(10.dp)

                                    )
                                    .background(
                                        color = Transparent,
                                        shape = RoundedCornerShape(10.dp)
                                    )
                                    .padding(horizontal = spacing.medium, vertical = spacing.small)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Close,
                                    contentDescription = null,
                                    tint = White
                                )

                                Spacer(modifier = Modifier.width(spacing.small))

                                Text(
                                    "?????????",
                                    style = LocalTextStyle.current.copy(
                                        color = White,
                                        fontWeight = FontWeight(550),
                                        shadow = Shadow(
                                            color = DarkGray,
                                            offset = Offset(1f, 1f),
                                            blurRadius = 4f
                                        )
                                    )
                                )
                            }
                        }

                        Column(
                            modifier = Modifier
                                .fillMaxSize(),
                            verticalArrangement = Arrangement.spacedBy(
                                spacing.medium,
                                Alignment.Bottom
                            )
                        ) {

                            /*?????? ??????*/

                            Column(
                                modifier = Modifier
                                    .align(Alignment.End)
                                    .padding(spacing.small)
                                    .clickable {
                                        interviewViewModel.moveNextPage()
                                    },
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {


                                Icon(
                                    imageVector = Icons.Default.ChevronRight,
                                    contentDescription = null,
                                    modifier = Modifier.size(70.dp),
                                    tint = White
                                )


                                Text(
                                    "?????? ??????",
                                    style = LocalTextStyle.current.copy(
                                        fontWeight = FontWeight(550),
                                        shadow = Shadow(
                                            color = DarkGray,
                                            offset = Offset(1f, 1f),
                                            blurRadius = 8f
                                        ),
                                        fontSize = 18.sp,
                                        color = White
                                    )
                                )
                            }



                            Spacer(modifier = Modifier.height(spacing.medium))

                            /*
                            ?????? ?????????
                             */

                            Column(
                                modifier = Modifier
                                    .fillMaxWidth(),
                                verticalArrangement = Arrangement.spacedBy(
                                    spacing.medium,
                                    Alignment.CenterVertically
                                ),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Column(
                                    verticalArrangement = Arrangement.spacedBy(
                                        spacing.small,
                                        Alignment.CenterVertically
                                    ),
                                    horizontalAlignment = Alignment.End,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                ) {
                                    oldInterviewLogLine.value?.let {
                                        OldLogContent(interviewLogLine = it)
                                    }
                                    newInterviewLogLine.value?.let {
                                        NewLogContent(interviewLogLine = it)
                                    }

                                }

                            }


                            /*
                            ??????
                             */
                            questions?.let { questions ->
                                currentPage.value?.let {
                                    QuestionContent(
                                        question = questions[it].question
                                    )
                                }
                            }


                            /*
                            ??????
                             */
                            if (speechText.isNotBlank()) {

                                AnswerContent(
                                    answer = speechText
                                )

                            }
                            /*
                            Text(
                                "${soundVolume.value ?: 0} dB",
                                style = LocalTextStyle.current.copy(
                                    fontWeight = FontWeight(550),
                                    shadow = Shadow(
                                        color = DarkGray,
                                        offset = Offset(1f, 1f),
                                        blurRadius = 8f
                                    ),
                                    fontSize = 18.sp,
                                    color = White
                                )
                            )

                             */

                            /*
                            ????????? ??????
                             */
                            Box(
                                modifier = Modifier.fillMaxWidth(),
                                contentAlignment = Alignment.Center
                            ) {
                                LottieAnimation(
                                    modifier = Modifier.clickableWithoutRipple {
                                        isPlaying.value = true
                                        invokeVibration(context)
                                        playButtonSound(context)
                                        startSpeechRecognize()
                                    },
                                    composition = composition,
                                    progress = {
                                        lottieState.progress
                                    }

                                )
                            }
                        }

                    }
                }
            }
        }


    }
}


@OptIn(ExperimentalAnimationApi::class)
@Composable
private fun QuestionContent(
    modifier: Modifier = Modifier,
    question: String
) {

    val spacing = LocalSpacing.current

    CompositionLocalProvider(
        LocalTextStyle provides TextStyle(
            fontFamily = nexonFont
        )
    ) {
        Column(
            modifier = modifier
                .fillMaxWidth()
                .padding(horizontal = spacing.small)
                .shadow(
                    3.dp,
                    shape = RoundedCornerShape(
                        topStart = 0.dp,
                        topEnd = 15.dp,
                        bottomEnd = 15.dp,
                        bottomStart = 15.dp
                    )
                )
                .background(
                    brush = Brush.linearGradient(
                        colors = listOf(
                            darker_blue,
                            bright_blue
                        )
                    ),
                    shape = RoundedCornerShape(
                        topStart = 0.dp,
                        topEnd = 15.dp,
                        bottomEnd = 15.dp,
                        bottomStart = 15.dp
                    )
                )
                .padding(spacing.small),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Row(
                modifier = Modifier
                    .fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Start
            ) {
                Text(
                    "??????",
                    style = LocalTextStyle.current.copy(
                        color = bright_blue,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium,
                    ),
                    modifier = Modifier
                        .shadow(
                            3.dp,
                            shape = RoundedCornerShape(10.dp)
                        )
                        .background(
                            color = White,
                            shape = RoundedCornerShape(10.dp)
                        )
                        .padding(horizontal = 10.dp, vertical = 4.dp)
                )


            }

            Spacer(modifier = Modifier.height(spacing.small))

            AnimatedContent(
                targetState = question,
                transitionSpec = {
                    fadeIn() + slideInVertically(animationSpec = tween(400),
                        initialOffsetY = { fullHeight -> fullHeight }) with
                            fadeOut(animationSpec = tween(200))
                }
            ) {
                Text(
                    question,
                    style = LocalTextStyle.current.copy(
                        color = White,
                        fontSize = 16.sp,
                        fontWeight = FontWeight(550),
                        textAlign = TextAlign.Center
                    ),
                    modifier = Modifier.fillMaxWidth(),
                )
            }


            Spacer(modifier = Modifier.height(spacing.small))

        }
    }

}

@OptIn(ExperimentalAnimationApi::class)
@Composable
private fun AnswerContent(
    modifier: Modifier = Modifier,
    answer: String
) {
    val spacing = LocalSpacing.current

    CompositionLocalProvider(
        LocalTextStyle provides TextStyle(
            fontFamily = nexonFont
        )
    ) {
        Column(
            modifier = modifier
                .fillMaxWidth()
                .padding(horizontal = spacing.small)
                .shadow(
                    3.dp,
                    RoundedCornerShape(
                        topStart = 15.dp,
                        topEnd = 0.dp,
                        bottomEnd = 15.dp,
                        bottomStart = 15.dp
                    )
                )
                .background(
                    color = White,
                    shape = RoundedCornerShape(
                        topStart = 15.dp,
                        topEnd = 0.dp,
                        bottomEnd = 15.dp,
                        bottomStart = 15.dp
                    )
                )
                .border(
                    1.dp,
                    brush = Brush.linearGradient(
                        colors = listOf(
                            bright_blue,
                            darker_blue
                        )
                    ),
                    shape = RoundedCornerShape(
                        topStart = 15.dp,
                        topEnd = 0.dp,
                        bottomEnd = 15.dp,
                        bottomStart = 15.dp
                    )
                )
                .padding(spacing.small),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Row(
                modifier = Modifier
                    .fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.End
            ) {
                Text(
                    "????????????",
                    style = LocalTextStyle.current.copy(
                        color = White,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium,
                        shadow = Shadow(
                            color = DarkGray,
                            offset = Offset(1f, 1f),
                            blurRadius = 4f
                        )
                    ),
                    modifier = Modifier
                        .shadow(
                            3.dp,
                            shape = RoundedCornerShape(10.dp)
                        )
                        .background(
                            brush = Brush.linearGradient(
                                colors = listOf(
                                    bright_blue,
                                    darker_blue
                                )
                            ),
                            shape = RoundedCornerShape(10.dp)
                        )
                        .padding(horizontal = 10.dp, vertical = 4.dp)
                )
            }

            Spacer(modifier = Modifier.height(spacing.small))

            AnimatedContent(
                targetState = answer,
                transitionSpec = {
                    fadeIn() + slideInVertically(animationSpec = tween(400),
                        initialOffsetY = { fullHeight -> fullHeight }) with
                            fadeOut(animationSpec = tween(200))
                }
            ) {
                Text(
                    answer,
                    style = LocalTextStyle.current.copy(
                        color = DarkGray,
                        fontSize = 16.sp,
                        fontWeight = FontWeight(550),
                        textAlign = TextAlign.Center
                    ),
                    modifier = Modifier.fillMaxWidth(),
                )
            }
            Spacer(modifier = Modifier.height(spacing.small))

        }
    }
}