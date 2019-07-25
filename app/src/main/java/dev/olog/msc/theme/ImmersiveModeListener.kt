package dev.olog.msc.theme

import android.content.Context
import android.content.SharedPreferences
import dev.olog.core.dagger.ApplicationContext
import dev.olog.msc.theme.observer.ActivityLifecycleCallbacks
import dev.olog.msc.theme.observer.CurrentActivityObserver
import dev.olog.presentation.R
import dev.olog.shared.android.delegates.mutableLazy
import javax.inject.Inject

internal class ImmersiveModeListener @Inject constructor(
    @ApplicationContext context: Context,
    prefs: SharedPreferences
) : BaseThemeUpdater<Boolean>(context, prefs, context.getString(R.string.prefs_immersive_key)),
    ActivityLifecycleCallbacks by CurrentActivityObserver(context) {

    var isImmersive by mutableLazy { getValue() }
        private set

    override fun onPrefsChanged() {
        isImmersive = getValue()
        currentActivity?.recreate()
    }

    override fun getValue(): Boolean {
        return prefs.getBoolean(key, false)
    }

}