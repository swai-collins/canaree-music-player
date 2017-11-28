package dev.olog.presentation.dialog.di

import dagger.Subcomponent
import dagger.android.AndroidInjector
import dev.olog.presentation.dagger.PerFragment
import dev.olog.presentation.dialog.DialogItemFragment

@Subcomponent(modules = arrayOf(
        DialogItemModule::class
))
@PerFragment
interface DialogItemSubComponent : AndroidInjector<DialogItemFragment> {

    @Subcomponent.Builder
    abstract class Builder : AndroidInjector.Builder<DialogItemFragment>() {

        abstract fun module(module: DialogItemModule): Builder

        override fun seedInstance(instance: DialogItemFragment) {
            module(DialogItemModule(instance))
        }
    }
}
