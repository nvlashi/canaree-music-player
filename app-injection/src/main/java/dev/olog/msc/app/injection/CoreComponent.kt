package dev.olog.msc.app.injection

import android.app.Activity
import android.app.Application
import android.app.Service
import android.content.Context
import android.content.SharedPreferences
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProvider
import dagger.BindsInstance
import dagger.Component
import dev.olog.msc.apilastfm.LastFmModule
import dev.olog.msc.app.injection.viewmodel.ViewModelModule
import dev.olog.msc.core.dagger.qualifier.ApplicationContext
import dev.olog.msc.core.dagger.qualifier.ProcessLifecycle
import dev.olog.msc.core.executors.ComputationDispatcher
import dev.olog.msc.core.executors.IoDispatcher
import dev.olog.msc.core.gateway.LastFmGateway
import dev.olog.msc.core.gateway.PlayingQueueGateway
import dev.olog.msc.core.gateway.UsedImageGateway
import dev.olog.msc.core.gateway.podcast.PodcastAlbumGateway
import dev.olog.msc.core.gateway.podcast.PodcastArtistGateway
import dev.olog.msc.core.gateway.podcast.PodcastGateway
import dev.olog.msc.core.gateway.podcast.PodcastPlaylistGateway
import dev.olog.msc.core.gateway.prefs.AppPreferencesGateway
import dev.olog.msc.core.gateway.prefs.EqualizerPreferencesGateway
import dev.olog.msc.core.gateway.prefs.MusicPreferencesGateway
import dev.olog.msc.core.gateway.prefs.TutorialPreferenceGateway
import dev.olog.msc.core.gateway.track.*
import dev.olog.msc.data.di.PreferenceModule
import dev.olog.msc.data.di.RepositoryHelperModule
import dev.olog.msc.data.di.RepositoryModule
import dev.olog.msc.presentation.navigator.Navigator
import dev.olog.msc.presentation.navigator.NavigatorAbout
import javax.inject.Singleton

@Component(
    modules = arrayOf(
        CoreModule::class,
        SchedulersModule::class,
//        AppShortcutsModule::class,
        LastFmModule::class,

//        // data
        RepositoryModule::class,
        RepositoryHelperModule::class,
        PreferenceModule::class,
//
//        // presentation
//        SleepTimerModule::class,
        DialogModules::class,
//        PresentationModules::class,
        NavigatorModule::class,
//        WidgetBindingModule::class,
        ViewModelModule::class

//        // music service
//        MusicServiceInjector::class,
//        EqualizerModule::class,

//        // floating info service
//        FloatingWindowServiceInjector::class
    )
)
@Singleton
interface CoreComponent {

    @ApplicationContext
    fun context(): Context

    @ProcessLifecycle
    fun lifecycle(): Lifecycle

    fun viewModelFactory(): ViewModelProvider.Factory

    fun prefs(): AppPreferencesGateway
    fun musicPrefs(): MusicPreferencesGateway
    fun tutorialPrefs(): TutorialPreferenceGateway
    fun equalizerPrefs(): EqualizerPreferencesGateway

    fun folderGateway(): FolderGateway
    fun playlistGateway(): PlaylistGateway
    fun songGateway(): SongGateway
    fun albumGateway(): AlbumGateway
    fun artistGateway(): ArtistGateway
    fun genreGateway(): GenreGateway
    fun podcastPlaylistGateway(): PodcastPlaylistGateway
    fun podcastGateway(): PodcastGateway
    fun podcastAlbumGateway(): PodcastAlbumGateway
    fun podcastArtistGateway(): PodcastArtistGateway

    fun lastFmGateway(): LastFmGateway
    fun usedImageGateway(): UsedImageGateway
    fun playingQueueGateway(): PlayingQueueGateway

    fun sharedPreferences(): SharedPreferences

//    fun appShortcuts(): AppShortcuts TODO restore


    fun navigator(): Navigator
    fun navigatorAbout(): NavigatorAbout

    fun cpuDispatcher(): ComputationDispatcher
    fun ioDispatcher(): IoDispatcher


    @Component.Factory
    interface Factory {

        fun create(@BindsInstance instance: Application): CoreComponent
    }

    companion object {

        private var coreComponent: CoreComponent? = null

        fun appComponent(app: Application): CoreComponent {
            if (coreComponent == null){
                // not double checking because it will be created in App.kt on main thread at app startup
                coreComponent = DaggerCoreComponent.factory().create(app)
            }
            return coreComponent!!
        }

        fun safeCoreComponent(): CoreComponent = coreComponent!!

    }

}

fun Activity.coreComponent(): CoreComponent = CoreComponent.safeCoreComponent()
fun Service.coreComponent(): CoreComponent = CoreComponent.safeCoreComponent()