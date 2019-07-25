package dev.olog.presentation.base

import android.content.res.Resources
import android.os.Bundle
import android.view.View
import androidx.annotation.CallSuper
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import dagger.android.AndroidInjector
import dagger.android.DispatchingAndroidInjector
import dagger.android.support.HasSupportFragmentInjector
import dev.olog.presentation.R
import dev.olog.presentation.main.CustomResources
import dev.olog.presentation.utils.setLightStatusBar
import dev.olog.shared.android.theme.isImmersiveMode
import javax.inject.Inject

abstract class BaseActivity : AppCompatActivity(), ThemedActivity, HasSupportFragmentInjector {

    @Inject
    internal lateinit var supportFragmentInjector: DispatchingAndroidInjector<Fragment>

    private var customResources: Resources? = null

    @CallSuper
    override fun onCreate(savedInstanceState: Bundle?) {
        themeAccentColor(this, theme)
        super.onCreate(savedInstanceState)
        window.setLightStatusBar()
    }

    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)
        if (hasFocus && isImmersiveMode()) {
            window.decorView.systemUiVisibility = window.decorView.systemUiVisibility or
                    View.SYSTEM_UI_FLAG_HIDE_NAVIGATION or
                    View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION or
                    View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
        }
    }

    override fun getResources(): Resources {
        val res = super.getResources()
        if (customResources == null){
            val isDarkMode = res.getBoolean(R.bool.is_dark_mode)
            customResources = CustomResources(isDarkMode, res.assets, res.displayMetrics, res.configuration)
        }
        return customResources!!
    }

    override fun supportFragmentInjector(): AndroidInjector<Fragment> = supportFragmentInjector

}
