package com.ravenshade.iconpack

import android.app.Activity
import android.os.Bundle
import android.widget.TextView
import android.view.Gravity

class MainActivity : Activity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val textView = TextView(this).apply {
            text = "My Custom Icon Pack\n\nTo apply:\n1. Long-press your home screen\n2. Tap 'Customisation'\n3. Select 'Icon pack' and choose this pack!"
            gravity = Gravity.CENTER
            textSize = 18f
            setPadding(50, 50, 50, 50)
        }
        setContentView(textView)
    }
}
