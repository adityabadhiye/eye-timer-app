package com.rgssdeveloper.eyetimer.screens

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.rgssdeveloper.eyetimer.ui.theme.DarkText
import com.rgssdeveloper.eyetimer.ui.theme.White
import com.rgssdeveloper.eyetimer.ui.theme.customColors
import com.rgssdeveloper.eyetimer.util.Constants.CHANGELOG
import com.rgssdeveloper.eyetimer.viewmodels.MainViewModel

//@Preview
@Composable
fun SettingScreen(navHostController: NavHostController,mainViewModel: MainViewModel) {
    val context = LocalContext.current
    val version = context.packageManager.getPackageInfo(context.packageName, 0).versionName
    val themesList = listOf("System default", "Light", "Dark")

    val themeDialogOpen=rememberSaveable{mutableStateOf(false)}
    val aboutDialogOpen=rememberSaveable{mutableStateOf(false)}
//    val initialThemeIndex = mainViewModel.getCurrentTheme()//BUG
    val selectedThemeIndex by mainViewModel.currentTheme.collectAsState(0)

    ThemeSelectorDialog(themeDialogOpen, selectedThemeIndex, themesList){
        mainViewModel.changeTheme(it)
    }
    AboutDialog(aboutDialogOpen,"About",version, CHANGELOG)
//    LaunchedEffect(key1 = selectedThemeIndex.value){
//        mainViewModel.changeTheme(selectedThemeIndex.value)
////        Timber.d(themesList[selectedThemeIndex.value])//change theme logic here
//    }
    Scaffold(
        backgroundColor = MaterialTheme.customColors.customBackground,
        topBar = {
            TopAppBar(
                title = {
                    Text(text = "Settings")
                },
                navigationIcon = {
                    IconButton(onClick = { navHostController.popBackStack() }) {
                        Icon(Icons.Filled.ArrowBack,"back")
                    }
                },
                backgroundColor = MaterialTheme.customColors.customBackground,
                contentColor = MaterialTheme.customColors.timerText,
            )
        }
    ) { LazyColumn(
        Modifier
            .fillMaxWidth()
            .padding(0.dp, 2.dp)
        ) {
            item {
                Simple(
                    text = "Theme",
                    subText = themesList[selectedThemeIndex],
                    icon = Icons.Filled.BrightnessMedium
                ){themeDialogOpen.value=true}
            }
            item {
                Simple(
                    text = "Share App",
                    subText = "Share this app with your friends",
                    icon = Icons.Filled.Share
                ) {
                    val sendIntent: Intent = Intent().apply {
                        action = Intent.ACTION_SEND
                        putExtra(Intent.EXTRA_TEXT, "Hey checkout this app and relax your eyes \nhttps://play.google.com/store/apps/details?id=${context.packageName}")
                        type = "text/plain"
                    }
                    context.startActivity(Intent.createChooser(sendIntent, null))
                }
            }
            item {
                Simple(
                    text = "Rate App",
                    subText = "Rate this app on play store",
                    icon = Icons.Filled.Star
                ){
                    context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=${context.packageName}")))
                }
            }
            item {
                Simple(
                    text="About",
                    subText ="Version $version",
                    icon = Icons.Filled.Info
                ){
                    aboutDialogOpen.value=true
                }
            }
        }
    }
}

@Preview
@Composable
fun Simple(
    text: String="Text",
    subText: String="SubText",
    icon: ImageVector=Icons.Filled.ShoppingCart,
    onClick:()->Unit={},
) {
    Box(modifier = Modifier
        .fillMaxWidth()
        .clickable { onClick() }){
        Row(
            modifier = Modifier
                .padding(0.dp, 16.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Icon(modifier = Modifier.weight(1f),
                imageVector = icon,
                contentDescription = null,
                tint = MaterialTheme.customColors.timerText)
            Column(
                modifier = Modifier.weight(5f),
                verticalArrangement = Arrangement.Center
            ) {
                Text(text = text, color = MaterialTheme.customColors.timerText,maxLines = 1)
                Text(text = subText, color = DarkText, maxLines = 2)
            }
        }
    }
}

@Preview
@Composable
fun SimpleWithSwitch(
    text: String="Text",
    subText: String="SubText",
    icon: ImageVector=Icons.Filled.ShoppingCart,
    initState:Boolean=false,
    onStateChanged:(Boolean)->Unit={},
) {
    var switched by remember{mutableStateOf(initState)}
    LaunchedEffect(key1 = switched){onStateChanged(switched)}
    Box(modifier = Modifier
        .fillMaxWidth()
        .clickable { switched = !switched }){
        Row(
            modifier = Modifier.padding(0.dp, 16.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Icon(modifier = Modifier.weight(1f),
                imageVector = icon,
                contentDescription = null,
                tint = White)
            Column(
                modifier = Modifier.weight(4f),
                verticalArrangement = Arrangement.Center
            ) {
                Text(text = text, color = MaterialTheme.customColors.timerText,maxLines = 1)
                Text(text = subText, color = DarkText, maxLines = 2)
            }
            Switch(modifier = Modifier.weight(1f),checked=switched, onCheckedChange = {switched=it})
        }
    }
}

@Composable
fun ThemeSelectorDialog(
    openDialog:MutableState<Boolean>,
    selected: Int,
    list:List<String>,
    onConfirmSelect:(Int)->Unit={}
) {
    var localSelected by remember { mutableStateOf(0) }
    localSelected = selected
    if (openDialog.value) {
        AlertDialog(
            onDismissRequest = { openDialog.value = false;localSelected = selected },
            title = {Text(text="Select Theme", fontWeight = FontWeight.Bold)},
            text = {LazyColumn {
                itemsIndexed(list){index,item->
                    RadioButtonRow(item,localSelected==index){
                        localSelected=index
                    }
                }
            }},
            confirmButton = {
                TextButton(
                    onClick = {
                        openDialog.value = false
//                        selected.value = localSelected
                        onConfirmSelect(localSelected)
                    }
                ) {
                    Text("Ok")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        openDialog.value = false
                        localSelected = selected
                    }
                ) {
                    Text("Cancel")
                }
            }
        )
    }
}

@Composable
fun AboutDialog(
    openDialog:MutableState<Boolean>,
    title:String,
    version:String,
    text: String
) {
    if (openDialog.value) {
        AlertDialog(
            onDismissRequest = { openDialog.value = false },
            title = {Text(text=title)},
            text =
            {
                Column {
                    Text(text = "Version $version",fontSize = 16.sp)
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(text = text, fontSize = 16.sp,fontStyle = FontStyle.Italic)
                }
            },
            confirmButton = { TextButton(onClick = { openDialog.value = false }) { Text("Ok") } }
        )
    }
}

@Composable
fun RadioButtonRow(text:String="Text",selected:Boolean=false,onClick:() -> Unit) {
    Box(modifier = Modifier
        .fillMaxWidth()
        .clickable { onClick() }
    ){
        Row(
            modifier = Modifier.padding(0.dp,8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            RadioButton(
                modifier = Modifier.weight(1f),
                selected = selected,
                onClick = { onClick() },
//                colors = RadioButtonDefaults.colors(
//                    unselectedColor= White,
//                    selectedColor = MaterialTheme.colors.secondary,
//                    disabledColor = MaterialTheme.colors.onSurface.copy(alpha = ContentAlpha.disabled)
//                )
            )
            Text(
                modifier = Modifier.weight(5f),
                text = text
            )
        }
    }
}