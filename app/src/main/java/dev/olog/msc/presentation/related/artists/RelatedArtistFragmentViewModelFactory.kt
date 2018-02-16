package dev.olog.msc.presentation.related.artists

import android.arch.lifecycle.ViewModel
import android.arch.lifecycle.ViewModelProvider
import android.content.res.Resources
import dev.olog.msc.domain.interactor.GetRelatedArtistsUseCase
import dev.olog.msc.utils.MediaId
import javax.inject.Inject

class RelatedArtistFragmentViewModelFactory @Inject constructor(
        private val resources: Resources,
        private val mediaId: MediaId,
        private val getRelatedArtistsUseCase: GetRelatedArtistsUseCase

        ) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return RelatedArtistViewModel(
                resources,
                mediaId,
                getRelatedArtistsUseCase
        ) as T
    }
}