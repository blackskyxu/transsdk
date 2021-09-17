package com.transsnet.transsdktest.utils

import android.content.Context

object ScreenUtils {

    fun dpToPx(context: Context, dp: Float): Float {
        context.resources.displayMetrics.let {
            return (dp * it.density + 0.5f)
        }
    }

    fun pxToDp(context: Context, px: Float): Float {
        context.resources.displayMetrics.let {
            return (px / it.density + 0.5f)
        }
    }
}