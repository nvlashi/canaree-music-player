package dev.olog.msc.presentation.popup.podcast

import android.view.View
import dev.olog.msc.R
import dev.olog.msc.core.entity.podcast.Podcast
import dev.olog.msc.presentation.popup.AbsPopup
import dev.olog.msc.presentation.popup.AbsPopupListener
import dev.olog.msc.shared.TrackUtils

class PodcastPopup(
        view: View,
        podcast: Podcast,
        listener: AbsPopupListener

) : AbsPopup(view) {

    init {
        inflate(R.menu.dialog_podcast)

        addPlaylistChooser(view.context, listener.playlists)

        setOnMenuItemClickListener(listener)

        if (podcast.artist == TrackUtils.UNKNOWN){
            menu.removeItem(R.id.viewArtist)
        }
        if (podcast.album == TrackUtils.UNKNOWN){
            menu.removeItem(R.id.viewAlbum)
        }
    }

}