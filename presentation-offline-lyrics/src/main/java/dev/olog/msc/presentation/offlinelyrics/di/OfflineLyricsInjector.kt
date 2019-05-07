package dev.olog.msc.presentation.offlinelyrics.di

import dagger.Module
import dagger.android.ContributesAndroidInjector
import dev.olog.msc.presentation.offlinelyrics.OfflineLyricsFragment

@Module
abstract class OfflineLyricsInjector {
    @ContributesAndroidInjector
    abstract fun provideOfflineLyricsFragment(): OfflineLyricsFragment
}