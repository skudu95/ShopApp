package com.kudu.shopapp.util

import android.content.Context
import android.graphics.Typeface
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatTextView

class CustomTextViewBold(context: Context, attrs: AttributeSet) : AppCompatTextView(context, attrs) {

    //initialising the class
    init {
        //call the function to apply to font to the components
        applyFont()
    }

    private fun applyFont() {
        val typeface: Typeface =
            Typeface.createFromAsset(context.assets, "Montserrat-Bold.ttf")
        setTypeface(typeface)
    }
}