package com.capstone.Capstone2Project.ui.screen.mypage

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.relocation.BringIntoViewRequester
import androidx.compose.foundation.relocation.bringIntoViewRequester
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusEvent
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Color.Companion.Black
import androidx.compose.ui.graphics.Color.Companion.DarkGray
import androidx.compose.ui.graphics.Color.Companion.LightGray
import androidx.compose.ui.graphics.Color.Companion.Transparent
import androidx.compose.ui.graphics.Color.Companion.White
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.*
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.airbnb.lottie.compose.*
import com.capstone.Capstone2Project.R
import com.capstone.Capstone2Project.data.model.InspiringKeyword
import com.capstone.Capstone2Project.data.model.InterviewLog
import com.capstone.Capstone2Project.data.model.Script
import com.capstone.Capstone2Project.data.model.TodayQuestion
import com.capstone.Capstone2Project.data.resource.Resource
import com.capstone.Capstone2Project.navigation.ROUTE_SCRIPT_WRITING
import com.capstone.Capstone2Project.ui.screen.auth.AuthViewModel
import com.capstone.Capstone2Project.ui.screen.home.ChartScreen
import com.capstone.Capstone2Project.utils.etc.AlertUtils
import com.capstone.Capstone2Project.utils.etc.CustomFont.nexonFont
import com.capstone.Capstone2Project.utils.etc.invokeVibration
import com.capstone.Capstone2Project.utils.extensions.clickableWithoutRipple
import com.capstone.Capstone2Project.utils.theme.*
import com.google.accompanist.flowlayout.FlowMainAxisAlignment
import com.google.accompanist.flowlayout.FlowRow
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

@Preview(showBackground = true)
@Composable
private fun Preview() {
    MyPageScreen(navController = rememberNavController())
}

@OptIn(
    ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class,
    ExperimentalAnimationApi::class
)
@Composable
fun MyPageScreen(
    navController: NavController
) {
    val authViewModel: AuthViewModel = hiltViewModel()
    val viewModel: MyPageViewModel = hiltViewModel()

    val spacing = LocalSpacing.current

    val keywordsFlow = viewModel.myInspiringKeywords.collectAsStateWithLifecycle()

    LaunchedEffect(authViewModel) {
        authViewModel.currentUser?.uid?.let {
            with(viewModel) {
                fetchMyInspiringKeywords(it)
            }
        }
    }

    val context = LocalContext.current

    val showKeywordAddingDialog = remember {
        mutableStateOf(false)
    }

    CompositionLocalProvider(
        LocalTextStyle provides TextStyle(
            fontFamily = nexonFont
        )
    ) {
        Scaffold(
            modifier = Modifier.fillMaxSize(),
            topBar = {
                CenterAlignedTopAppBar(
                    colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                        containerColor = Color.Transparent
                    ),
                    title = {
                        Text(
                            "?????? ?????????",
                            style = LocalTextStyle.current.copy(
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 16.sp
                            )
                        )
                    },
                    navigationIcon = {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "????????????",
                            tint = Color.DarkGray,
                            modifier = Modifier.clickable {
                                navController.popBackStack()
                            }
                        )
                    }
                )
            }
        ) { innerPadding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .background(
                        color = bg_grey
                    )
                    .verticalScroll(rememberScrollState())
                    .padding(spacing.small)
            ) {

                Spacer(modifier = Modifier.height(spacing.medium))

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = spacing.small)
                        .wrapContentHeight()
                        .shadow(3.dp, shape = RoundedCornerShape(5.dp))
                        .clip(
                            shape = RoundedCornerShape(5.dp)
                        )
                ) {
                    val composition by rememberLottieComposition(LottieCompositionSpec.Asset("lottie/astronaut.json"))
                    val progress by animateLottieCompositionAsState(
                        composition,
                        iterations = LottieConstants.IterateForever
                    )

                    LottieAnimation(
                        composition = composition,
                        progress = { progress },
                        contentScale = ContentScale.FillWidth,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(200.dp)
                    )

                    Text(
                        "?????????????????? ????????????\nAI ??????????????? ?????????????????? !",
                        style = LocalTextStyle.current.copy(
                            color = White,
                            fontSize = 20.sp,
                            fontWeight = FontWeight(550),
                            shadow = Shadow(
                                color = DarkGray,
                                offset = Offset(1f, 1f),
                                blurRadius = 4f
                            ),
                            textAlign = TextAlign.Start
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = spacing.medium, vertical = spacing.large)
                    )

                }

                Column(
                    modifier = Modifier
                        .padding(spacing.small)
                        .shadow(3.dp, shape = RoundedCornerShape(5.dp))
                        .background(
                            color = White,
                            shape = RoundedCornerShape(5.dp)
                        )
                        .padding(spacing.small)
                ) {

                    Spacer(modifier = Modifier.height(spacing.small))

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = spacing.small)
                            .border(
                                1.dp,
                                color = LightGray,
                                shape = RoundedCornerShape(5.dp)
                            )
                            .padding(spacing.small)
                            .wrapContentHeight(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {

                        Column(
                            modifier = Modifier.weight(1f),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Text(
                                "????????????",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = DarkGray
                            )
                            Spacer(modifier = Modifier.height(spacing.small))
                            Text(
                                "email@email.com",
                                color = text_blue,
                                textDecoration = TextDecoration.Underline,
                                fontSize = 14.sp
                            )
                        }


                        Divider(
                            color = LightGray,
                            modifier = Modifier
                                .padding(spacing.small)
                                .height(40.dp)
                                .width(1.dp)
                        )

                        Column(
                            modifier = Modifier.weight(1f),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.Center
                            ) {
                                Icon(
                                    painter = painterResource(id = R.drawable.ic_github),
                                    contentDescription = null,
                                    tint = Black,
                                    modifier = Modifier.size(15.dp)
                                )
                                Spacer(modifier = Modifier.width(spacing.small))
                                Text(
                                    "????????????",
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = DarkGray
                                )
                            }
                            Spacer(modifier = Modifier.height(spacing.small))
                            Text(
                                "????????? ?????? ??????",
                                color = text_blue,
                                textDecoration = TextDecoration.Underline,
                                fontSize = 14.sp
                            )
                        }

                    }

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = spacing.small),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            "????????? ?????????????????? ?????????????????? !",
                            color = Color(0xFF2238FF),
                            fontSize = 14.sp,
                            modifier = Modifier
                                .background(
                                    color = Color(0xFFBFC6FF)
                                )
                                .padding(4.dp),
                            fontWeight = FontWeight.SemiBold
                        )

                        IconButton(
                            onClick = {
                                //TODO(????????? ??????)
                            }
                        ) {
                            Row(
                                modifier = Modifier
                                    .wrapContentSize()
                                    .border(
                                        1.dp,
                                        color = text_blue,
                                        shape = RoundedCornerShape(50)
                                    )
                                    .padding(
                                        horizontal = spacing.small,
                                        vertical = spacing.extraSmall
                                    ),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    painter = painterResource(id = R.drawable.ic_link),
                                    contentDescription = null,
                                    tint = text_blue,
                                    modifier = Modifier.size(15.dp)
                                )
                                Spacer(modifier = Modifier.width(spacing.extraSmall))
                                Text(
                                    "??????",
                                    color = text_blue,
                                    fontSize = 14.sp
                                )
                            }

                        }

                    }
                }

                Spacer(modifier = Modifier.height(spacing.large))

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = spacing.small)
                        .shadow(3.dp, shape = RoundedCornerShape(5.dp))
                        .background(
                            brush = Brush.linearGradient(
                                colors = listOf(
                                    bright_blue,
                                    bright_purple
                                )
                            ),
                            shape = RoundedCornerShape(5.dp)
                        )
                        .padding(spacing.medium)
                ) {
                    Text(
                        "??????????????? ?????????",
                        fontWeight = FontWeight(550),
                        color = White,
                        modifier = Modifier.fillMaxWidth(),
                        textAlign = TextAlign.Start,
                        fontSize = 20.sp
                    )

                    Spacer(modifier = Modifier.height(spacing.small))

                    Text(
                        "?????????????????? ???????????? ???????????????????\n?????? ???????????? ?????????????????? !",
                        color = White,
                        modifier = Modifier.fillMaxWidth(),
                        textAlign = TextAlign.Start,
                        fontSize = 14.sp
                    )

                    Spacer(modifier = Modifier.height(spacing.large))

                    FlowRow(
                        crossAxisSpacing = 8.dp,
                        mainAxisSpacing = 0.dp,
                        modifier = Modifier.fillMaxWidth(),
                        mainAxisAlignment = FlowMainAxisAlignment.SpaceAround,
                        lastLineMainAxisAlignment = FlowMainAxisAlignment.SpaceEvenly
                    ) {

                        keywordsFlow.value?.let {
                            when (it) {
                                is Resource.Error -> {
                                    Text("${it.error?.message}", style = LocalTextStyle.current.copy(
                                        color = White,
                                        fontWeight = FontWeight(550),
                                        fontFamily = nexonFont
                                    ))
                                }
                                Resource.Loading -> {
                                    CircularProgressIndicator(
                                        color = White
                                    )
                                }
                                is Resource.Success -> {
                                    it.data.forEach { keyword ->
                                        AnimatedContent(targetState = keyword) {
                                            KeywordItem(keyword)
                                        }
                                    }
                                }
                            }
                        }

                        KeywordItemAdd {
                            showKeywordAddingDialog.value = true
                        }
                    }

                    Spacer(modifier = Modifier.height(spacing.small))


                }
                Spacer(modifier = Modifier.height(spacing.large))

                Spacer(modifier = Modifier.height(spacing.small))

                MyTodayQuestion(navController, authViewModel)

                Spacer(modifier = Modifier.height(spacing.large))

                Spacer(modifier = Modifier.height(spacing.small))

                MyScriptList(navController, authViewModel)


                Spacer(modifier = Modifier.height(spacing.large))

                Spacer(modifier = Modifier.height(spacing.small))

                MyInterviewLogs(navController, authViewModel)

                Spacer(modifier = Modifier.height(spacing.small))

                MyInterviewScores(navController, authViewModel)

                Spacer(modifier = Modifier.height(spacing.large))

                if (showKeywordAddingDialog.value) {
                    KeywordAddingDialog(
                        authViewModel, viewModel
                    ) {
                        showKeywordAddingDialog.value = false
                    }
                }
            }
        }
    }

}

@Preview(showBackground = true)
@Composable
private fun DialogPreview() {
//    KeywordAddingDialog(onKeywordInput = {})
}

@OptIn(ExperimentalComposeUiApi::class, ExperimentalFoundationApi::class)
@Composable
private fun KeywordAddingDialog(
    authViewModel: AuthViewModel,
    viewModel: MyPageViewModel,
    onDismissRequest: () -> Unit
) {

    val keyword = remember {
        mutableStateOf("")
    }

    val focusManager = LocalFocusManager.current

    val keyboardController = LocalSoftwareKeyboardController.current

    val spacing = LocalSpacing.current


    val bringIntoViewRequester = remember {
        BringIntoViewRequester()
    }

    val coroutineScope = rememberCoroutineScope()

    val focusRequester = remember { FocusRequester() }

    val context = LocalContext.current

    fun insertKeyword() {
        if (keyword.value.isNotBlank()) {
            authViewModel.currentUser?.uid?.let { hostUUID ->
                val inspiringKeyword = InspiringKeyword(
                    hostUUID = hostUUID,
                    date = System.currentTimeMillis(),
                    keywordUUID = UUID.randomUUID().toString(),
                    keyword = keyword.value
                )

                viewModel.insertInspiringKeyword(inspiringKeyword)
                AlertUtils.showToast(context, "???????????? ??????????????????")
            } ?: AlertUtils.showToast(context, "???????????? ?????? ??????????????????.")

        } else {
            AlertUtils.showToast(context, "???????????? ???????????? ???????????????.")
        }
    }

    CompositionLocalProvider(
        LocalTextStyle provides TextStyle(
            fontFamily = nexonFont,
            fontSize = 18.sp
        )
    ) {
        Dialog(
            onDismissRequest = {},
            properties = DialogProperties(
                usePlatformDefaultWidth = false,
                dismissOnClickOutside = true,
                dismissOnBackPress = true
            )
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .clickableWithoutRipple {
                        focusManager.clearFocus()
                        keyboardController?.hide()
                    }
                    .padding(spacing.medium)
            ) {


                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(spacing.medium),
                    verticalArrangement = Arrangement.Bottom
                ) {

                    Icon(
                        imageVector = Icons.Default.Close,
                        modifier = Modifier
                            .align(Alignment.End)
                            .clickable {
                                onDismissRequest()
                            }
                            .padding(spacing.medium),
                        contentDescription = "??????",
                        tint = White
                    )

                    Text(
                        "????????????????????? ??????\n??????????????? ???????????? ??????????????????.",
                        modifier = Modifier.fillMaxWidth(),
                        style = LocalTextStyle.current.copy(
                            textAlign = TextAlign.Start,
                            color = White,
                            shadow = Shadow(
                                color = DarkGray,
                                offset = Offset(1f, 1f),
                                blurRadius = 8f
                            ),
                            fontWeight = FontWeight(550)
                        )
                    )

                    Spacer(modifier = Modifier.height(spacing.medium))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.Bottom
                    ) {


                        TextField(
                            value = keyword.value,
                            onValueChange = {
                                keyword.value = it
                            },
                            keyboardActions = KeyboardActions(
                                onDone = {
                                    insertKeyword()
                                }
                            ),
                            keyboardOptions = KeyboardOptions(
                                keyboardType = KeyboardType.Text,
                                imeAction = ImeAction.Done
                            ),
                            modifier = Modifier
                                .weight(1f)
                                .padding(bottom = spacing.small)
                                .height(50.dp)
                                .focusRequester(focusRequester)
                                .bringIntoViewRequester(bringIntoViewRequester)
                                .onFocusEvent { focusState ->
                                    if (focusState.isFocused) {
                                        coroutineScope.launch {
                                            bringIntoViewRequester.bringIntoView()
                                        }
                                    }
                                },
                            colors = TextFieldDefaults.textFieldColors(
                                textColor = White,
                                disabledTextColor = White,
                                backgroundColor = Transparent,
                                cursorColor = White,
                                focusedIndicatorColor = bright_blue,
                                unfocusedIndicatorColor = White
                            ),
                        )

                        Spacer(modifier = Modifier.width(spacing.small))

                        IconButton(
                            onClick = {
                                insertKeyword()
                            }
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier
                                    .shadow(
                                        3.dp,
                                        shape = RoundedCornerShape(5.dp)
                                    )
                                    .background(
                                        color = White,
                                        shape = RoundedCornerShape(5.dp)
                                    )
                                    .padding(vertical = 5.dp, horizontal = 10.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.CheckCircle,
                                    contentDescription = null,
                                    tint = bright_blue
                                )

                                Spacer(modifier = Modifier.width(spacing.small))

                                Text(
                                    "??????",
                                    style = LocalTextStyle.current.copy(
                                        color = bright_blue,
                                        fontSize = 16.sp,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                )
                            }
                        }

                        LaunchedEffect(Unit) {
                            focusRequester.requestFocus()
                        }
                    }
                }

            }
        }
    }


}

@Composable
fun MyTodayQuestion(navController: NavController, authViewModel: AuthViewModel) {

    val viewModel: MyPageViewModel = hiltViewModel()

    val myTodayQuestionsFlow = viewModel.myTodayQuestionsFlow.collectAsStateWithLifecycle()

    val spacing = LocalSpacing.current

    LaunchedEffect(authViewModel) {
        authViewModel.currentUser?.uid?.let {
            with(viewModel) {
                fetchMyTodayQuestions(it)
            }
        }
    }

    CompositionLocalProvider(
        LocalTextStyle provides TextStyle(
            fontFamily = nexonFont
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = spacing.small)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = buildAnnotatedString {
                        withStyle(
                            style = SpanStyle(
                                color = text_blue,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        ) {
                            append(authViewModel.currentUser?.displayName)
                        }

                        withStyle(
                            style = SpanStyle(
                                color = DarkGray,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        ) {
                            append("?????? ????????? ??????")
                        }
                    }
                )

            }

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = spacing.small),
                contentAlignment = Alignment.Center
            ) {
                myTodayQuestionsFlow.value?.let {
                    when (it) {
                        is Resource.Error -> {
                            Text("${it.error}")
                        }
                        Resource.Loading -> {
                            CircularProgressIndicator(
                                color = bright_blue
                            )
                        }
                        is Resource.Success -> {
                            LazyRow(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(spacing.medium)
                            ) {
                                item {
                                    MyTodayQuestionsFirstItem(count = it.data.size)
                                }

                                items(it.data.size) { idx ->
                                    val todayQuestion = it.data[idx]
                                    MyTodayQuestionsItem(todayQuestion)
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
private fun MyTodayQuestionsItem(todayQuestion: TodayQuestion) {
    val spacing = LocalSpacing.current

    CompositionLocalProvider(
        LocalTextStyle provides TextStyle(
            fontFamily = nexonFont
        )
    ) {
        Column(
            modifier = Modifier
                .size(200.dp, 100.dp)
                .shadow(
                    3.dp,
                    shape = RoundedCornerShape(5.dp)
                )
                .background(
                    color = White,
                    shape = RoundedCornerShape(5.dp)
                )
                .padding(spacing.small),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {

            Text(
                "Q. ${todayQuestion.question}",
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium,
                color = text_blue
            )

            Spacer(modifier = Modifier.height(spacing.small))

            Text(
                "A. ${todayQuestion.answer}",
                fontSize = 14.sp,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        }
    }
}

@Composable
private fun MyTodayQuestionsFirstItem(count: Int) {
    val spacing = LocalSpacing.current

    CompositionLocalProvider(
        LocalTextStyle provides TextStyle(
            fontFamily = nexonFont
        )
    ) {
        ConstraintLayout(
            modifier = Modifier
                .size(200.dp, 100.dp)
                .shadow(
                    3.dp,
                    shape = RoundedCornerShape(5.dp)
                )
                .background(
                    color = bright_purple,
                    shape = RoundedCornerShape(5.dp)
                )
        ) {
            val (titleRef, countRef, imgRef) = createRefs()

            Text(
                "????????? ??????",
                style = LocalTextStyle.current.copy(
                    color = White,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 16.sp
                ),
                modifier = Modifier.constrainAs(titleRef) {
                    top.linkTo(parent.top, margin = spacing.medium)
                    start.linkTo(parent.start, margin = spacing.medium)
                }
            )

            Text(
                "$count ???",
                style = LocalTextStyle.current.copy(
                    color = White,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 16.sp
                ),
                modifier = Modifier.constrainAs(countRef) {
                    bottom.linkTo(parent.bottom, margin = spacing.medium)
                    start.linkTo(parent.start, margin = spacing.medium)
                }
            )

            Image(
                painter = painterResource(R.drawable.ic_memo),
                contentDescription = null,
                modifier = Modifier.constrainAs(imgRef) {
                    top.linkTo(parent.top, margin = spacing.small)
                    bottom.linkTo(parent.bottom, margin = spacing.small)
                    end.linkTo(parent.end, margin = spacing.small)
                    start.linkTo(titleRef.end, margin = spacing.small)
                }
            )


        }
    }
}

@Composable
fun MyInterviewScores(navController: NavController, authViewModel: AuthViewModel) {

    val spacing = LocalSpacing.current

    val viewModel: MyPageViewModel = hiltViewModel()

    val myInterviewScoresFlow = viewModel.myInterviewScoresFlow.collectAsStateWithLifecycle()

    LaunchedEffect(authViewModel) {
        authViewModel.currentUser?.uid?.let {
            with(viewModel) {
                fetchMyInterviewScores(it)
            }
        }
    }

    CompositionLocalProvider(
        LocalTextStyle provides TextStyle(
            fontFamily = nexonFont
        )
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(spacing.small),
            contentAlignment = Alignment.Center
        ) {
            myInterviewScoresFlow.value?.let {
                when (it) {
                    is Resource.Error -> {
                        Text("${it.error}")
                    }
                    Resource.Loading -> {
                        CircularProgressIndicator(
                            color = bright_blue
                        )
                    }
                    is Resource.Success -> {
                        ChartScreen()
                    }
                }
            }
        }

    }

}

@Composable
fun MyInterviewLogs(navController: NavController, authViewModel: AuthViewModel) {
    val spacing = LocalSpacing.current

    val viewModel: MyPageViewModel = hiltViewModel()

    val myInterviewLogsFlow = viewModel.myInterviewLogsFlow.collectAsStateWithLifecycle()

    LaunchedEffect(authViewModel) {
        authViewModel.currentUser?.uid?.let {
            with(viewModel) {
                fetchMyInterviewLogs(it)
            }
        }
    }

    CompositionLocalProvider(
        LocalTextStyle provides TextStyle(
            fontFamily = nexonFont
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = spacing.small)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Start
            ) {
                Text(
                    text = buildAnnotatedString {
                        withStyle(
                            style = SpanStyle(
                                color = text_blue,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        ) {
                            append(authViewModel.currentUser?.displayName)
                        }

                        withStyle(
                            style = SpanStyle(
                                color = DarkGray,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        ) {
                            append("?????? ????????? ?????? ??????")
                        }
                    }
                )

            }

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = spacing.small),
                contentAlignment = Alignment.Center
            ) {
                myInterviewLogsFlow.value?.let {
                    when (it) {
                        is Resource.Error -> {
                            Text("${it.error}")
                        }
                        Resource.Loading -> {
                            CircularProgressIndicator(
                                color = bright_blue
                            )
                        }
                        is Resource.Success -> {
                            LazyRow(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(spacing.medium)
                            ) {
                                item {
                                    MyInterviewLogFirstItem(count = it.data.size)
                                }

                                items(it.data.size) { idx ->
                                    val interviewLog = it.data[idx]
                                    MyInterviewLogItem(interviewLog)
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
fun MyInterviewLogItem(interviewLog: InterviewLog) {
    val spacing = LocalSpacing.current

    val simpleDateFormat = remember {
        SimpleDateFormat("yy.MM.dd (E) hh:mm", Locale.getDefault())
    }

    CompositionLocalProvider(
        LocalTextStyle provides TextStyle(
            fontFamily = nexonFont
        )
    ) {
        Column(
            modifier = Modifier
                .size(200.dp, 100.dp)
                .shadow(
                    3.dp,
                    shape = RoundedCornerShape(5.dp)
                )
                .background(
                    color = White,
                    shape = RoundedCornerShape(5.dp)
                )
                .padding(spacing.small),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {

            Text(
                interviewLog.scriptName,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium
            )

            Spacer(modifier = Modifier.height(spacing.small))

            Text(
                simpleDateFormat.format(Date(interviewLog.date)),
                fontSize = 14.sp
            )
        }
    }
}

@Composable
fun MyInterviewLogFirstItem(count: Int) {
    val spacing = LocalSpacing.current

    CompositionLocalProvider(
        LocalTextStyle provides TextStyle(
            fontFamily = nexonFont
        )
    ) {
        ConstraintLayout(
            modifier = Modifier
                .size(200.dp, 100.dp)
                .shadow(
                    3.dp,
                    shape = RoundedCornerShape(5.dp)
                )
                .background(
                    color = bright_blue,
                    shape = RoundedCornerShape(5.dp)
                )
        ) {
            val (titleRef, countRef, imgRef) = createRefs()

            Text(
                "?????? ??????",
                style = LocalTextStyle.current.copy(
                    color = White,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 16.sp
                ),
                modifier = Modifier.constrainAs(titleRef) {
                    top.linkTo(parent.top, margin = spacing.medium)
                    start.linkTo(parent.start, margin = spacing.medium)
                }
            )

            Text(
                "$count ???",
                style = LocalTextStyle.current.copy(
                    color = White,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 16.sp
                ),
                modifier = Modifier.constrainAs(countRef) {
                    bottom.linkTo(parent.bottom, margin = spacing.medium)
                    start.linkTo(parent.start, margin = spacing.medium)
                }
            )

            Image(
                painter = painterResource(R.drawable.ic_blue_microphone),
                contentDescription = null,
                modifier = Modifier.constrainAs(imgRef) {
                    top.linkTo(parent.top, margin = spacing.small)
                    bottom.linkTo(parent.bottom, margin = spacing.small)
                    end.linkTo(parent.end, margin = spacing.small)
                    start.linkTo(titleRef.end, margin = spacing.small)
                }
            )


        }
    }
}

@Composable
private fun MyScriptList(navController: NavController, authViewModel: AuthViewModel) {

    val spacing = LocalSpacing.current

    val viewModel: MyPageViewModel = hiltViewModel()

    val myScriptsFlow = viewModel.myScriptsFlow.collectAsStateWithLifecycle()

    LaunchedEffect(authViewModel) {
        authViewModel.currentUser?.uid?.let {
            with(viewModel) {
                fetchMyScripts(it)
            }
        }
    }


    CompositionLocalProvider(
        LocalTextStyle provides TextStyle(
            fontFamily = nexonFont
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = spacing.small)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = buildAnnotatedString {
                        withStyle(
                            style = SpanStyle(
                                color = text_blue,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        ) {
                            append(authViewModel.currentUser?.displayName)
                        }

                        withStyle(
                            style = SpanStyle(
                                color = DarkGray,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        ) {
                            append("?????? ???????????????")
                        }
                    }
                )

                Text(
                    "?????? ??????",
                    style = LocalTextStyle.current.copy(
                        color = text_blue,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Normal
                    ),
                    modifier = Modifier.clickable {
                        navController.navigate(ROUTE_SCRIPT_WRITING)
                    }
                )
            }

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = spacing.small),
                contentAlignment = Alignment.Center
            ) {
                myScriptsFlow.value?.let {
                    when (it) {
                        is Resource.Error -> {
                            Text("${it.error}")
                        }
                        Resource.Loading -> {
                            CircularProgressIndicator(
                                color = bright_blue
                            )
                        }
                        is Resource.Success -> {
                            LazyRow(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(spacing.medium)
                            ) {
                                item {
                                    MyScriptFirstItem(count = it.data.size)
                                }

                                items(it.data.size) { idx ->
                                    val script = it.data[idx]
                                    MyScriptItem(script)
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
private fun MyScriptItem(
    script: Script
) {

    val spacing = LocalSpacing.current

    val simpleDateFormat = remember {
        SimpleDateFormat("yy.MM.dd (E) hh:mm", Locale.getDefault())
    }

    CompositionLocalProvider(
        LocalTextStyle provides TextStyle(
            fontFamily = nexonFont
        )
    ) {
        Column(
            modifier = Modifier
                .size(200.dp, 100.dp)
                .shadow(
                    3.dp,
                    shape = RoundedCornerShape(5.dp)
                )
                .background(
                    color = White,
                    shape = RoundedCornerShape(5.dp)
                )
                .padding(spacing.small),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {

            Text(
                script.name,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium
            )

            Spacer(modifier = Modifier.height(spacing.small))

            Text(
                simpleDateFormat.format(Date(script.date)),
                fontSize = 14.sp
            )
        }
    }

}

@Composable
private fun MyScriptFirstItem(
    count: Int
) {

    val spacing = LocalSpacing.current

    CompositionLocalProvider(
        LocalTextStyle provides TextStyle(
            fontFamily = nexonFont
        )
    ) {
        ConstraintLayout(
            modifier = Modifier
                .size(200.dp, 100.dp)
                .shadow(
                    3.dp,
                    shape = RoundedCornerShape(5.dp)
                )
                .background(
                    color = deep_darker_blue,
                    shape = RoundedCornerShape(5.dp)
                )
        ) {
            val (titleRef, countRef, imgRef) = createRefs()

            Text(
                "????????? ??????",
                style = LocalTextStyle.current.copy(
                    color = White,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 16.sp
                ),
                modifier = Modifier.constrainAs(titleRef) {
                    top.linkTo(parent.top, margin = spacing.medium)
                    start.linkTo(parent.start, margin = spacing.medium)
                }
            )

            Text(
                "$count ???",
                style = LocalTextStyle.current.copy(
                    color = White,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 16.sp
                ),
                modifier = Modifier.constrainAs(countRef) {
                    bottom.linkTo(parent.bottom, margin = spacing.medium)
                    start.linkTo(parent.start, margin = spacing.medium)
                }
            )

            Image(
                painter = painterResource(R.drawable.ic_blue_script),
                contentDescription = null,
                modifier = Modifier.constrainAs(imgRef) {
                    top.linkTo(parent.top, margin = spacing.small)
                    bottom.linkTo(parent.bottom, margin = spacing.small)
                    end.linkTo(parent.end, margin = spacing.small)
                    start.linkTo(titleRef.end, margin = spacing.small)
                }
            )


        }
    }

}


@Composable
private fun KeywordItemAdd(
    onItemClick: () -> Unit
) {

    val spacing = LocalSpacing.current


    CompositionLocalProvider(
        LocalTextStyle provides TextStyle(
            fontFamily = nexonFont
        )
    ) {
        Row(
            modifier = Modifier
                .wrapContentSize()
                .height(30.dp)
                .shadow(
                    3.dp,
                    shape = RoundedCornerShape(50)
                )
                .background(
                    brush = Brush.linearGradient(
                        colors = listOf(
                            mustard_yellow,
                            orange_yellow
                        )
                    ),
                    shape = RoundedCornerShape(50)
                )
                .clickable {
                    onItemClick()
                }
                .padding(vertical = spacing.small, horizontal = spacing.medium),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {

            Icon(
                imageVector = Icons.Outlined.Add,
                contentDescription = null,
                tint = White
            )
            Spacer(modifier = Modifier.width(spacing.extraSmall))
            Text("??????", fontSize = 14.sp, color = White, fontWeight = FontWeight(550))
        }
    }

}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun KeywordItem(
    keyword: InspiringKeyword,
    viewModel: MyPageViewModel = hiltViewModel()
) {

    val spacing = LocalSpacing.current

    val context = LocalContext.current

    CompositionLocalProvider(
        LocalTextStyle provides TextStyle(
            fontFamily = nexonFont
        )
    ) {
        Box(
            modifier = Modifier
                .wrapContentSize()
                .height(30.dp)
                .shadow(
                    3.dp,
                    shape = RoundedCornerShape(50)
                )
                .background(
                    color = White,
                    shape = RoundedCornerShape(50)
                )
                .combinedClickable(
                    onClick = {
                        invokeVibration(context)
                        AlertUtils.showToast(context, "?????? ????????? ????????????!")
                    },
                    onLongClick = {
                        invokeVibration(context)
                        viewModel.deleteInspiringKeyword(keyword)
                        AlertUtils.showToast(context, "?????? ??????")
                    }
                )
                .padding(vertical = spacing.small, horizontal = spacing.medium)

        ) {
            Text(keyword.keyword, fontSize = 14.sp, color = DarkGray)
        }
    }

}