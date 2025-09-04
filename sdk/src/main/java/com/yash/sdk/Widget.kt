package com.yash.sdk

import android.content.Context
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

// -------------------- Base Widget --------------------
abstract class BaseWidget {

    /** Developers implement their widget content here */
    @Composable
    protected abstract fun Render(modifier: Modifier)

    /** Default size = 1x1 cell */
    protected open fun modifierRules(context: Context): Modifier {
        val screenWidth = context.resources.displayMetrics.widthPixels
        val cell = (screenWidth * 0.9f / 2).dp // base unit = half of 90% width
        return Modifier.width(cell).height(cell)
    }

    @Composable
    fun Content(context: Context = AppRegistry.getAppContext()) {
        Box(modifier = modifierRules(context)) {
            Render(modifierRules(context))
        }
    }
}

// -------------------- Extended Widgets --------------------

// 1x1 → one cell
abstract class Widget1x1 : BaseWidget()

// 1x2 → full width, half height
abstract class Widget1x2 : BaseWidget() {
    override fun modifierRules(context: Context): Modifier {
        val screenWidth = context.resources.displayMetrics.widthPixels
        val cell = (screenWidth * 0.9f / 2).dp
        return Modifier.width(cell * 2).height(cell)
    }
}

// 2x1 → half width, double height
abstract class Widget2x1 : BaseWidget() {
    override fun modifierRules(context: Context): Modifier {
        val screenWidth = context.resources.displayMetrics.widthPixels
        val cell = (screenWidth * 0.9f / 2).dp
        return Modifier.width(cell).height(cell * 2)
    }
}

// 2x2 → full square block
abstract class Widget2x2 : BaseWidget() {
    override fun modifierRules(context: Context): Modifier {
        val screenWidth = context.resources.displayMetrics.widthPixels
        val cell = (screenWidth * 0.9f / 2).dp
        return Modifier.width(cell * 2).height(cell * 2)
    }
}
