package com.yash.kagitham

import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.rememberPagerState
import com.yash.kagitham.screens.Install
import com.yash.kagitham.screens.NavBar
import com.yash.kagitham.screens.Paper
import com.yash.kagitham.screens.Widgets
import kotlinx.coroutines.launch
import com.yash.sdk.AppRegistry


@OptIn(ExperimentalPagerApi::class)
@Composable
fun App() {
    val pagerState = rememberPagerState(initialPage = 0)
    val coroutineScope = rememberCoroutineScope()
    val appContext = LocalContext.current.applicationContext

    LaunchedEffect(Unit) {
        AppRegistry.setAppContext(appContext)
    }

    Scaffold(
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
        bottomBar = {
            NavBar(
                selectedIndex = pagerState.currentPage,
                onItemSelected = { index ->
                    coroutineScope.launch {
                        pagerState.animateScrollToPage(index)
                    }
                }
            )
        }
    ) { paddingValues ->
        HorizontalPager(
            count = 3,
            state = pagerState,
            modifier = Modifier
                .padding(paddingValues)
        ) { page ->
            when (page) {
                0 -> Widgets()
                1 -> Paper()
                2 -> Install()
            }
        }
    }

}

@Preview(showSystemUi = true)
@Composable
fun pre(){
    App()
}