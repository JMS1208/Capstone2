package com.capstone.Capstone2Project.ui.screen.home

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.outlined.Person
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.Color.Companion.DarkGray
import androidx.compose.ui.graphics.Color.Companion.Gray
import androidx.compose.ui.graphics.Color.Companion.White
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.capstone.Capstone2Project.R
import com.capstone.Capstone2Project.data.model.Achievement
import com.capstone.Capstone2Project.data.model.Attendance
import com.capstone.Capstone2Project.navigation.ROUTE_MY_PAGE
import com.capstone.Capstone2Project.navigation.ROUTE_SCRIPT_WRITING
import com.capstone.Capstone2Project.ui.screen.guide.InterviewIntroDialog
import com.capstone.Capstone2Project.utils.composable.HighlightText
import com.capstone.Capstone2Project.utils.etc.CustomFont
import com.capstone.Capstone2Project.utils.etc.CustomFont.nexonFont
import com.capstone.Capstone2Project.utils.extensions.WithEmojiView
import com.capstone.Capstone2Project.utils.extensions.clickableWithoutRipple
import com.capstone.Capstone2Project.utils.extensions.gradientBackground
import com.capstone.Capstone2Project.utils.extensions.shimmerEffect
import com.capstone.Capstone2Project.utils.theme.*
import com.kizitonwose.calendar.compose.HorizontalCalendar
import com.kizitonwose.calendar.compose.rememberCalendarState
import com.kizitonwose.calendar.core.CalendarDay
import com.kizitonwose.calendar.core.daysOfWeek
import com.kizitonwose.calendar.core.firstDayOfWeekFromLocale
import java.text.SimpleDateFormat
import java.time.DayOfWeek
import java.time.YearMonth
import java.util.*


@Preview(showBackground = true)
@Composable
fun HomeScreenPreview() {

    HomeScreen(rememberNavController())

}


@Composable
fun HomeScreen(
    navController: NavController
) {
    val scrollState = rememberScrollState()

    val spacing = LocalSpacing.current

    Scaffold(
        floatingActionButtonPosition = FabPosition.Center,
        isFloatingActionButtonDocked = true,
        floatingActionButton = {
            InterviewButton(navController = navController)
        }
    ) { innerPadding ->


        ConstraintLayout(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    color = bg_grey
                )
                .padding(innerPadding)
                .navigationBarsPadding()
                .padding(top = 30.dp)
        ) {


            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(scrollState),
                verticalArrangement = Arrangement.spacedBy(spacing.large, Alignment.Top)
            ) {

                LogoAndInfoContent(
                    modifier = Modifier.fillMaxWidth(),
                    navController
                )

                ScriptAndInfoContent(
                    modifier = Modifier.fillMaxWidth(),
                    navController
                )

                MyTopicAndTodayQuiz(
                    modifier = Modifier.fillMaxWidth()
                )

                AttendanceCheck(
                    modifier = Modifier.fillMaxWidth()
                )

                MyAchievement(
                    modifier = Modifier.fillMaxWidth()
                )

//                InterviewButton(
//                    modifier = Modifier,
//                    navController
//                )

                Spacer(modifier = Modifier.height(spacing.large))
            }
        }


    }


}

@Composable
private fun InterviewButton(
    modifier: Modifier = Modifier,
    navController: NavController
) {


    val spacing = LocalSpacing.current


    var showDialog by remember {
        mutableStateOf(false)
    }

    if (showDialog) {
        InterviewIntroDialog(navController = navController) {
            showDialog = false
        }
    }


    Box(
        modifier = modifier
            .fillMaxWidth()
            .wrapContentHeight()
    ) {
        FloatingActionButton(
            onClick = { showDialog = true },
            shape = RoundedCornerShape(30.dp),
            backgroundColor = bright_blue,
            modifier = Modifier.align(Alignment.Center),
            elevation = FloatingActionButtonDefaults.elevation(
                defaultElevation = 3.dp,
                pressedElevation = 6.dp,
                hoveredElevation = 4.dp,
                focusedElevation = 4.dp
            )
        ) {


            Row(
                modifier = Modifier.padding(horizontal = spacing.medium),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Image(
                    painter = painterResource(id = R.drawable.ic_microphone),
                    contentDescription = null,
                    modifier = Modifier.size(50.dp)

                )
                Text(
                    text = "???????????? ??????",
                    modifier = Modifier,
                    style = LocalTextStyle.current.copy(
                        color = White,
                        shadow = Shadow(
                            DarkGray,
                            Offset(4f, 4f),
                            blurRadius = 8f
                        ),
                        fontSize = 25.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = nexonFont
                    )
                )
            }


        }

    }


}

@Composable
private fun MyAchievement(
    modifier: Modifier = Modifier
) {

    val spacing = LocalSpacing.current

    val configuration = LocalConfiguration.current

    val screenWidth = configuration.screenWidthDp.dp

    CompositionLocalProvider(
        LocalTextStyle provides TextStyle(
            fontFamily = nexonFont
        )
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = spacing.medium)
        ) {


            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .shadow(
                        3.dp,
                        shape = RoundedCornerShape(5.dp)
                    )
                    .background(
                        brush = Brush.linearGradient(
                            colors = listOf(
                                bright_blue,
                                darker_blue
                            )
                        ),
                        shape = RoundedCornerShape(5.dp)
                    )
                    .shimmerEffect(2000)

            ) {

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(spacing.small)
                ) {

                    Spacer(modifier = Modifier.height(spacing.medium))

                    Text(
                        "?????? ?????? ??????",
                        color = White,
                        fontWeight = FontWeight(550),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = spacing.small),
                        textAlign = TextAlign.Start,
                        fontSize = 20.sp,
                        style = LocalTextStyle.current.copy(
                            shadow = Shadow(
                                DarkGray,
                                offset = Offset(1f, 1f),
                                blurRadius = 4f
                            )
                        )
                    )
                    val achievement = remember {
                        listOf(
                            Achievement(System.currentTimeMillis(), "????????????"),
                            Achievement(System.currentTimeMillis(), "??????????????? 1??? ?????? ?????? !")
                        )
                    }

                    LazyColumn(
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(100.dp, 150.dp)
                            .shadow(
                                3.dp,
                                shape = RoundedCornerShape(5.dp)
                            )
                            .background(
                                color = White,
                                shape = RoundedCornerShape(5.dp)
                            ),
                        contentPadding = PaddingValues(spacing.small),
                        verticalArrangement = Arrangement.spacedBy(
                            spacing.extraSmall,
                            Alignment.CenterVertically
                        ),
                        horizontalAlignment = Alignment.Start
                    ) {

                        items(achievement) {
                            Text(
                                "${it.timeToString()} - ${it.text}",
                                color = DarkGray,
                                fontSize = 13.sp
                            )
                        }
                    }

                }

            }

            Image(
                painter = painterResource(id = R.drawable.ic_trophy),
                contentDescription = null,
                modifier = Modifier
                    .size(
                        screenWidth
                            .div(5)
                            .times(2)
                    )
                    .align(Alignment.TopEnd)
                    .padding(start = spacing.medium)
                    .offset(y = (-50).dp)
            )

        }
    }

}

@Composable
private fun MyTopicAndTodayQuiz(
    modifier: Modifier = Modifier
) {

    val spacing = LocalSpacing.current

    CompositionLocalProvider(
        LocalTextStyle provides TextStyle(
            fontFamily = nexonFont
        )
    ) {
        Column(
            modifier = modifier.padding(horizontal = spacing.medium),
            verticalArrangement = Arrangement.Center
        ) {
            Row(
                verticalAlignment = Alignment.Bottom,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    "?????? ????????????",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Medium
                )

                Row(
                    modifier = Modifier.clickableWithoutRipple {

                    },
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("????????????", fontSize = 16.sp, color = Gray)
                    Icon(
                        imageVector = Icons.Default.ChevronRight,
                        contentDescription = null
                    )
                }


            }

            Spacer(modifier = Modifier.height(spacing.small))

            Column(
                modifier = Modifier
                    .shadow(3.dp, shape = RoundedCornerShape(5.dp))
                    .background(
                        color = Color.White,
                        shape = RoundedCornerShape(5.dp)
                    )
                    .fillMaxWidth()
                    .padding(spacing.medium),
                verticalArrangement = Arrangement.spacedBy(spacing.medium)
            ) {
                Row(
                    verticalAlignment = Alignment.Bottom,
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("????????? ???????????? ????????????\n????????? ????????? ?????????????????? !", color = Gray, fontSize = 16.sp)
                    Image(
                        painter = painterResource(id = R.drawable.ic_quiz),
                        contentDescription = null,
                        modifier = Modifier.height(60.dp)
                    )
                }

                LazyRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(7) {
                        Box(
                            modifier = Modifier
                                .shadow(
                                    2.dp,
                                    shape = RoundedCornerShape(15.dp)
                                )
                                .background(
                                    brush = Brush.linearGradient(
                                        colors = listOf(
                                            Color(0xFFE3EDFF),
                                            Color.White
                                        )
                                    ),
                                    shape = RoundedCornerShape(15.dp)
                                )
                                .border(
                                    border = BorderStroke(
                                        1.dp,
                                        brush = Brush.linearGradient(
                                            colors = listOf(
                                                bright_sky_blue,
                                                bright_violet
                                            )
                                        )
                                    ),
                                    shape = RoundedCornerShape(15.dp)
                                )
                                .padding(vertical = 5.dp, horizontal = 10.dp)
                        ) {
                            Text("#??????$it", fontSize = 15.sp)
                        }
                    }
                }




                TodayQuestionCard(
                    question = "??????????????? ???????????? ????????? ???????????????",
                    modifier = Modifier.fillMaxWidth()
                )


//                ChartScreen()

            }

        }
    }


}

@Composable
private fun AttendanceCheck(
    modifier: Modifier = Modifier
) {

    val spacing = LocalSpacing.current


    CompositionLocalProvider(
        LocalTextStyle provides TextStyle(
            fontFamily = nexonFont
        )
    ) {
        Column(
            modifier = modifier
                .padding(horizontal = spacing.medium),
            verticalArrangement = Arrangement.spacedBy(spacing.small)
        ) {

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.Bottom,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("Weekly ?????? !", fontSize = 20.sp, fontWeight = FontWeight.Medium)

                Text("?????? 2??? ?????? !", fontSize = 16.sp, fontWeight = FontWeight.Normal, color = Gray)

            }


            val weeklyData = remember {
                listOf<Attendance>(
                    Attendance(date = System.currentTimeMillis(), false),
                    Attendance(date = System.currentTimeMillis(), true),
                    Attendance(date = System.currentTimeMillis(), false),
                    Attendance(date = System.currentTimeMillis(), true),
                    Attendance(date = System.currentTimeMillis(), true),
                    Attendance(date = System.currentTimeMillis(), false),
                    Attendance(date = System.currentTimeMillis(), true)
                )
            }

            LazyRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .shadow(3.dp, shape = RoundedCornerShape(5.dp))
                    .background(
                        color = White,
                        shape = RoundedCornerShape(5.dp)
                    ),
                horizontalArrangement = Arrangement.spacedBy(spacing.extraSmall),
                contentPadding = PaddingValues(
                    start = spacing.small,
                    end = spacing.small,
                    top = spacing.small
                )
            ) {
                items(weeklyData) {
                    ItemAttendance(attendance = it)
                }
            }


            Text(
                "????????? ${
                    SimpleDateFormat(
                        "yyyy.MM.dd (E)",
                        Locale.getDefault()
                    ).format(System.currentTimeMillis())
                }",
                fontSize = 14.sp,
                color = text_blue
            )

        }


    }
}

@Composable
private fun ItemAttendance(attendance: Attendance) {

    val spacing = LocalSpacing.current

    CompositionLocalProvider(
        LocalTextStyle provides TextStyle(
            fontFamily = nexonFont,
            color = White,
            fontWeight = FontWeight(550)
        )
    ) {
        attendance.date?.let {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(spacing.extraSmall),
                modifier = Modifier.padding(vertical = 10.dp)
            ) {
                Box(
                    modifier = Modifier
                        .wrapContentSize()
                        .background(
                            color = bg_darker_gray,
                            shape = RoundedCornerShape(5.dp)
                        )
                        .padding(horizontal = 6.dp, vertical = 3.dp)
                ) {
                    Text(attendance.timeToDayOfWeek()!!)
                }

                Image(
                    painter = painterResource(id = if (attendance.isPresent) R.drawable.ic_check_att else R.drawable.ic_close_att),
                    contentDescription = null,
                    modifier = Modifier
                        .padding(5.dp)
                        .size(40.dp)
                )


            }
        }

    }
}

@Composable
private fun ScriptAndInfoContent(
    modifier: Modifier = Modifier,
    navController: NavController
) {

    val spacing = LocalSpacing.current


    CompositionLocalProvider(
        LocalTextStyle provides androidx.compose.ui.text.TextStyle(
            fontFamily = nexonFont
        )
    ) {
        ConstraintLayout(
            modifier = modifier
        ) {

            val (boxScriptRef, boxInfoRef, imageRef, scriptContentRef, infoContentRef) = createRefs()


            Box(
                modifier = Modifier
                    .constrainAs(boxScriptRef) {
                        start.linkTo(parent.start, margin = spacing.medium)
                        end.linkTo(boxInfoRef.start, margin = spacing.small)
                        top.linkTo(parent.top)
                        bottom.linkTo(imageRef.bottom, margin = spacing.extraSmall)
                        width = Dimension.fillToConstraints
                        height = Dimension.fillToConstraints
                    }
                    .shadow(4.dp, shape = RoundedCornerShape(5.dp))
                    .background(
                        color = Color.White,
                        shape = RoundedCornerShape(5.dp)
                    ),
            ) {

            }

            Box(
                modifier = Modifier
                    .constrainAs(boxInfoRef) {
                        start.linkTo(boxScriptRef.end, margin = spacing.small)
                        end.linkTo(parent.end, margin = spacing.medium)

                        top.linkTo(parent.top)
                        bottom.linkTo(boxScriptRef.bottom)
                        width = Dimension.fillToConstraints
                        height = Dimension.fillToConstraints
                    }
                    .shadow(5.dp, shape = RoundedCornerShape(5.dp))
                    .gradientBackground(
                        colors = listOf(
                            bright_blue,
                            bright_purple
                        ),
                        angle = 45f
                    )
                    .shimmerEffect(1500)

            ) {

            }

            Column(
                modifier = Modifier
                    .constrainAs(scriptContentRef) {
                        start.linkTo(boxScriptRef.start, margin = spacing.small)
                        top.linkTo(boxScriptRef.top, margin = spacing.small)
                        width = Dimension.wrapContent
                        height = Dimension.wrapContent
                    }
                    .padding(spacing.small)
                    .clickable {
                        navController.navigate(ROUTE_SCRIPT_WRITING)
                    },
                verticalArrangement = Arrangement.spacedBy(
                    spacing.small,
                    Alignment.CenterVertically
                ),
                horizontalAlignment = Alignment.Start
            ) {
                Text("??????????????? ??????", fontWeight = FontWeight.Medium, fontSize = 20.sp)
                Text("AI??? ?????? ?????? ?????????\n??????????????????", fontSize = 15.sp, color = Gray)
            }

            Column(
                modifier = Modifier
                    .constrainAs(infoContentRef) {
                        start.linkTo(boxInfoRef.start)
                        end.linkTo(boxInfoRef.end)
                        top.linkTo(boxInfoRef.top)
                        bottom.linkTo(boxInfoRef.bottom)
                        width = Dimension.fillToConstraints
                        height = Dimension.fillToConstraints
                    }
                    .padding(spacing.small),
                verticalArrangement = Arrangement.SpaceAround
            ) {
                Text(
                    "????????????",
                    fontWeight = FontWeight(550),
                    fontSize = 18.sp,
                    textAlign = TextAlign.End,
                    color = White,
                    modifier = Modifier.fillMaxWidth()
                )
                Text(
                    "????????? ????????????",
                    fontWeight = FontWeight.Medium,
                    fontSize = 16.sp,
                    color = White
                )
                GitLanguageAnalysis(

                )
            }


            Image(
                painter = painterResource(id = R.drawable.ic_script),
                contentDescription = null,
                modifier = Modifier
                    .height(spacing.image_100)
                    .constrainAs(imageRef) {
                        top.linkTo(scriptContentRef.bottom)
                        bottom.linkTo(parent.bottom)
                        start.linkTo(parent.start)
                        width = Dimension.wrapContent
                        height = Dimension.wrapContent
                    }
            )


        }
    }


}


@Composable
private fun GitLanguageAnalysis(
    modifier: Modifier = Modifier
) {

    CompositionLocalProvider(
        LocalTextStyle provides TextStyle(
            fontWeight = FontWeight.Medium,
            color = White,
            fontSize = 12.sp
        )
    ) {
        Column(
            verticalArrangement = Arrangement.SpaceEvenly,
            horizontalAlignment = Alignment.Start
        ) {
            Text("Kotlin 40%")
            Text("??? ??? 60%")
            Text(
                "??? ??????",
                textAlign = TextAlign.End,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }


}


@Composable
private fun LogoAndInfoContent(
    modifier: Modifier = Modifier,
    navController: NavController
) {

    val spacing = LocalSpacing.current

    CompositionLocalProvider(
        LocalTextStyle provides androidx.compose.ui.text.TextStyle(
            fontFamily = nexonFont
        )
    ) {
        ConstraintLayout(
            modifier = modifier.padding(horizontal = spacing.medium)
        ) {
            val (textRef, divRef, infoRef) = createRefs()

            Text("LOGO", color = DarkGray,
                modifier = Modifier.constrainAs(textRef) {
                    top.linkTo(parent.top, margin = spacing.small)
                    start.linkTo(parent.start, margin = spacing.small)
                    this.width = Dimension.wrapContent
                    this.height = Dimension.wrapContent
                })
            Divider(
                color = DarkGray,
                modifier = Modifier.constrainAs(divRef) {
                    start.linkTo(textRef.start)
                    end.linkTo(textRef.end)
                    top.linkTo(textRef.bottom, margin = spacing.extraSmall)
                    bottom.linkTo(parent.bottom)
                    width = Dimension.fillToConstraints
                    height = Dimension.wrapContent
                }
            )

            Icon(
                imageVector = Icons.Outlined.Person,
                contentDescription = null,
                tint = Color.Black,
                modifier = Modifier
                    .size(25.dp)
                    .constrainAs(infoRef) {
                        top.linkTo(parent.top, margin = spacing.small)
                        end.linkTo(parent.end, margin = spacing.small)
                    }
                    .clickable {
                        navController.navigate(ROUTE_MY_PAGE) {}
                    }
            )
        }
    }


}

@Composable
fun ChartData() {
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .shadow(
                5.dp,
                shape = RoundedCornerShape(bottomStart = 20.dp, bottomEnd = 20.dp)
            )
            .clip(
                shape = RoundedCornerShape(bottomStart = 20.dp, bottomEnd = 20.dp)
            )
            .wrapContentHeight()
            .fillMaxWidth()

    ) {
        ChartScreen()
    }
}

@Composable
fun WeeklyData() {

    val spacing = LocalSpacing.current

    CompositionLocalProvider(
        LocalTextStyle provides androidx.compose.ui.text.TextStyle(
            fontFamily = CustomFont.nexonFont
        )
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(spacing.medium),
            horizontalAlignment = Alignment.Start,
            modifier = Modifier.padding(spacing.medium),
        ) {

            Row(
                verticalAlignment = Alignment.Bottom,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                WithEmojiView(
                    unicode = 0x1F4C6
                ) {
                    HighlightText("Weekly ??????", fontSize = 18.sp)
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.End,
                    modifier = Modifier
                        .wrapContentSize()
                        .offset(y = 5.dp)
                ) {
                    Text("?????? ??????", fontSize = 14.sp)
                    Icon(
                        imageVector = Icons.Default.ChevronRight,
                        contentDescription = null,
                    )
                }

            }



            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(
                    spacing.medium,
                    Alignment.CenterHorizontally
                ),
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth(),
                contentPadding = PaddingValues(horizontal = spacing.small)
            ) {
                items(7) {
                    DailyData()
                }
            }
        }
    }


}

@Composable
fun DailyData(
    modifier: Modifier = Modifier
) {


    Box(
        modifier = modifier
            .shadow(4.dp, shape = CircleShape)
            .clip(CircleShape)
            .size(40.dp)
            .background(
                color = unselected_grey,
                shape = CircleShape
            )
            .border(
                BorderStroke(1.dp, color = Color.White),
                shape = CircleShape
            ),
        contentAlignment = Alignment.Center
    ) {

        Text(
            "???", style = LocalTextStyle.current.copy(
                shadow = Shadow(
                    color = shadow_color,
                    offset = Offset(1f, 1f),
                    blurRadius = 8f
                )
            ), color = Color.White, fontWeight = FontWeight.SemiBold,
            fontSize = 15.sp
        )

    }

}


@Composable
fun CalendarScreen() {

    val currentMonth = remember {
        YearMonth.now()
    }

    val startMonth = remember {
        currentMonth.minusMonths(100)
    }

    val endMonth = remember {
        currentMonth.plusMonths(100)
    }

    val firstDayOfWeek = remember {
        firstDayOfWeekFromLocale()
    }

    val daysOfWeek = remember {
        daysOfWeek()
    }

    val state = rememberCalendarState(
        startMonth = startMonth,
        endMonth = endMonth,
        firstVisibleMonth = currentMonth,
        firstDayOfWeek = daysOfWeek.first()
    )

    Column(
        modifier = Modifier
            .wrapContentHeight()
            .fillMaxWidth(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            state.firstVisibleMonth.yearMonth.month.getDisplayName(
                java.time.format.TextStyle.SHORT,
                Locale.getDefault()
            ),
            textAlign = TextAlign.Center
        )
        DaysOfWeekTitle(daysOfWeek = daysOfWeek)
        HorizontalCalendar(
            state = state,
            dayContent = { calendarDay ->
                Day(calendarDay)
            }
        )
    }


}

@Composable
fun DaysOfWeekTitle(daysOfWeek: List<DayOfWeek>) {
    Row(modifier = Modifier.fillMaxWidth()) {
        for (dayOfWeek in daysOfWeek) {
            Text(
                modifier = Modifier.weight(1f),
                textAlign = TextAlign.Center,
                text = dayOfWeek.getDisplayName(
                    java.time.format.TextStyle.SHORT,
                    Locale.getDefault()
                ),
            )
        }
    }
}

@Composable
fun MonthHeader(daysOfWeek: List<DayOfWeek>) {
    LazyRow(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceAround
    ) {
        items(daysOfWeek.size) { idx ->
            Text(daysOfWeek[idx].name.substring(0, 3))
        }
    }
}

@Composable
fun Day(day: CalendarDay) {
    Box(
        modifier = Modifier
            .aspectRatio(1f), // This is important for square sizing!
        contentAlignment = Alignment.Center
    ) {
        Text(text = day.date.dayOfMonth.toString())
    }
}