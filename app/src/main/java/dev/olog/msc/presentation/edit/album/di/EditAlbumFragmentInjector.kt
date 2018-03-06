package dev.olog.msc.presentation.edit.album.di

import android.support.v4.app.Fragment
import dagger.Binds
import dagger.Module
import dagger.android.AndroidInjector
import dagger.android.support.FragmentKey
import dagger.multibindings.IntoMap
import dev.olog.msc.presentation.edit.album.EditAlbumFragment

@Module(subcomponents = arrayOf(EditAlbumFragmentSubComponent::class))
abstract class EditAlbumFragmentInjector {

    @Binds
    @IntoMap
    @FragmentKey(EditAlbumFragment::class)
    internal abstract fun injectorFactory(builder: EditAlbumFragmentSubComponent.Builder)
            : AndroidInjector.Factory<out Fragment>

}