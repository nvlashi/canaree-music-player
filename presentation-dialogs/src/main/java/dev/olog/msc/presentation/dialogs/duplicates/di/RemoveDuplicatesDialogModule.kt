package dev.olog.msc.presentation.dialogs.duplicates.di

import androidx.lifecycle.ViewModel
import dagger.Binds
import dagger.Module
import dagger.android.ContributesAndroidInjector
import dagger.multibindings.IntoMap
import dev.olog.msc.presentation.base.ViewModelKey
import dev.olog.msc.presentation.dialogs.duplicates.RemoveDuplicatesDialog
import dev.olog.msc.presentation.dialogs.duplicates.RemoveDuplicatesDialogViewModel

@Module
abstract class RemoveDuplicatesDialogModule {

    @ContributesAndroidInjector
    abstract fun provideFragment(): RemoveDuplicatesDialog

    @Binds
    @IntoMap
    @ViewModelKey(RemoveDuplicatesDialogViewModel::class)
    abstract fun provideViewModel(viewModel: RemoveDuplicatesDialogViewModel): ViewModel

}