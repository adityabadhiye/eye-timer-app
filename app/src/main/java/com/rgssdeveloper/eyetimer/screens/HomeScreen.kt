package com.rgssdeveloper.eyetimer.screens

import android.content.Context
import android.content.Intent
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect.Companion.dashPathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.rgssdeveloper.eyetimer.model.TimerEvent
import com.rgssdeveloper.eyetimer.service.TimerService
import com.rgssdeveloper.eyetimer.tips
import com.rgssdeveloper.eyetimer.ui.theme.*
import com.rgssdeveloper.eyetimer.util.Constants.ACTION_START_SERVICE
import com.rgssdeveloper.eyetimer.util.Constants.ACTION_STOP_SERVICE
import com.rgssdeveloper.eyetimer.util.Constants.TOTAL_TIME
import com.rgssdeveloper.eyetimer.util.TimerUtil
import kotlin.random.Random

//@Preview
@Composable
fun HomeScreen(navHostController: NavHostController,) {
    val context = LocalContext.current
    val totalTime = TOTAL_TIME
    Box(modifier = Modifier
        .background(color = MaterialTheme.customColors.customBackground)
        .fillMaxSize()
    ){
        Box(modifier = Modifier
            .align(Alignment.TopEnd)
            .padding(0.dp, 8.dp, 8.dp, 0.dp)
        ) {
            var expanded by remember{ mutableStateOf(false)}
            IconButton(
                onClick = { expanded=true }
            ) {
                Icon(Icons.Filled.MoreVert,"more options",tint = MaterialTheme.customColors.timerText)
            }
            DropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false }
            ) {
                DropdownMenuItem(onClick = {
                    navHostController.navigate("setting_screen")
                }) {
                    Text(text = "Settings")
                }
            }
        }
        Column(modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 30.dp, vertical = 20.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceAround,
        ) {
            val currentTime by TimerService.timerInMillis.observeAsState(0L)
            val running by TimerService.timerEvent.observeAsState(TimerEvent.END)
            var tipIndex by remember{ mutableStateOf(0)}

            LaunchedEffect(key1 = currentTime){
//                if(currentTime==totalTime && running){//TODO:BUG USE IN SERVICE, pass total time in service maybe?
//                    //stop timer
//                }
                if((currentTime/1000L)%10==0L && currentTime>=10000L){
                    tipIndex= Random.nextInt(tips.size)
                }
            }
            Tips(tipIndex)
            Timer(currentTime,totalTime,MaterialTheme.customColors.startButton)
            if(running==TimerEvent.END){
                Button(text = "Start",
                    backgroundColor = MaterialTheme.customColors.startButton,
                    textColor = MaterialTheme.customColors.customBackground
                ){
                    sendCommandToService(ACTION_START_SERVICE,totalTime,context)
                }
            }
            else{
                Button(text = "Stop",
                    backgroundColor = Red,
                    textColor = MaterialTheme.customColors.customBackground
                ){
                    sendCommandToService(ACTION_STOP_SERVICE,context = context)
                }
            }
        }
    }
}

@Composable
fun Tips(tipInt: Int) {
    Column(
        Modifier
            .clip(RoundedCornerShape(10.dp))
            .background(color = MaterialTheme.customColors.tipsBackground)
            .padding(12.dp)
    ) {
        Text(
            text = "Tips",
            fontSize = 20.sp,
            color = MaterialTheme.customColors.tipsText,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = tips[tipInt],
            fontSize = 14.sp,
            color = MaterialTheme.customColors.tipsText,
        )
    }
}

@Composable
fun Timer(currentTime:Long, totalTime:Long, arcColor:Color) {
    val stroke = Stroke(
        width = 50f,
        pathEffect = dashPathEffect(floatArrayOf(14f, 90f), 90f)
    )
    val progress = if(((totalTime-currentTime)/totalTime.toFloat())==1f) 0.01f else currentTime.toFloat()/totalTime
    val currentTimeString = TimerUtil.getFormattedTime(totalTime-currentTime)

    Box(contentAlignment = Alignment.Center){
        Canvas(
            Modifier
                .fillMaxWidth(.9f)
                .aspectRatio(1f)
        ){
            drawArc(
                style = stroke,
                startAngle = -90f,
                sweepAngle = -359.99f,
                color = UnselectedColor,
                useCenter = false,
            )
            drawArc(
                style = stroke,
                startAngle = -90f,
                sweepAngle = (progress-1f)*360f,
                color = arcColor,
                useCenter = false,
            )
//            brush = Brush.linearGradient(listOf(Yellow, Red)),
        }
        Column(verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = currentTimeString,
                color = MaterialTheme.customColors.timerText,
                fontWeight = FontWeight.Bold,
                fontSize = 48.sp,
            )
            Text(text = "TIME LEFT",
                color= DarkText,
                fontSize = 12.sp,
            )
        }

    }
}

@Composable
fun Button(text:String,
           backgroundColor: Color = Yellow,
           textColor: Color = DarkBackground,
           onClick: () -> Unit
) {
    Button(shape = RoundedCornerShape(50),
        colors = ButtonDefaults.buttonColors(
            backgroundColor = backgroundColor,
            contentColor = textColor),
        onClick = onClick
    ) {
        Text(modifier = Modifier.padding(8.dp),
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            text = text)
    }
}

private fun sendCommandToService(action: String, totalTime:Long=0L, context: Context) {
    context.startService(Intent(context, TimerService::class.java).apply {
        this.action = action
        this.putExtra("total_time",totalTime)
    })
}