package dev.olog.msc.presentation.base.activity

import android.os.Bundle
import android.view.View
import androidx.annotation.CallSuper
import androidx.appcompat.app.AppCompatActivity
import dev.olog.msc.presentation.base.extensions.setLightStatusBar
import dev.olog.msc.shared.ui.theme.immersive

abstract class BaseActivity : AppCompatActivity(), ThemedActivity {

    @CallSuper
    override fun onCreate(savedInstanceState: Bundle?) {
        injectComponents()
        themeAccentColor(this, theme)
        super.onCreate(savedInstanceState)
        window.setLightStatusBar()
    }

    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)
        if (hasFocus && immersive().isEnabled()) {
            window.decorView.systemUiVisibility = window.decorView.systemUiVisibility or
                    View.SYSTEM_UI_FLAG_HIDE_NAVIGATION or
                    View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION or
                    View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
        }
    }

    protected open fun injectComponents(){
        // TODO make abstract
    }

}
