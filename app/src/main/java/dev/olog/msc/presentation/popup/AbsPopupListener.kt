package dev.olog.msc.presentation.popup

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.support.v4.content.FileProvider
import android.widget.PopupMenu
import dev.olog.msc.R
import dev.olog.msc.domain.entity.Playlist
import dev.olog.msc.domain.entity.Song
import dev.olog.msc.domain.interactor.dialog.AddToPlaylistUseCase
import dev.olog.msc.presentation.navigator.Navigator
import dev.olog.msc.utils.MediaId
import dev.olog.msc.utils.k.extension.asHtml
import io.reactivex.android.schedulers.AndroidSchedulers
import org.jetbrains.anko.toast
import java.io.File

abstract class AbsPopupListener(
        val playlists: List<Playlist>,
        private val addToPlaylistUseCase: AddToPlaylistUseCase

) : PopupMenu.OnMenuItemClickListener {

    protected fun onPlaylistSubItemClick(context: Context, itemId: Int, mediaId: MediaId, listSize: Int, title: String){
        playlists.firstOrNull { it.id == itemId.toLong() }?.run {
            addToPlaylistUseCase.execute(this to mediaId)
                    .observeOn(AndroidSchedulers.mainThread())
                    .doOnComplete { createSuccessMessage(context, itemId.toLong(), mediaId, listSize, title) }
                    .doOnError { createErrorMessage(context) }
                    .subscribe({}, Throwable::printStackTrace)
        }
    }

    private fun createSuccessMessage(context: Context, playlistId: Long, mediaId: MediaId, listSize: Int, title: String){
        val playlist = playlists.first { it.id == playlistId }.title
        val message = if (mediaId.isLeaf){
            context.getString(R.string.added_song_x_to_playlist_y, title, playlist)
        } else {
            context.resources.getQuantityString(R.plurals.xx_songs_added_to_playlist_y, listSize, listSize, playlist)
        }
        context.toast(message)
    }

    private fun createErrorMessage(context: Context){
        context.toast(context.getString(R.string.popup_error_message))
    }

    protected fun share(activity: Activity, song: Song){
        val intent = Intent()
        intent.action = Intent.ACTION_SEND
        intent.putExtra(Intent.EXTRA_STREAM,
                FileProvider.getUriForFile(activity, activity.packageName, File(song.path)))
        intent.type = "audio/*"
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        if (intent.resolveActivity(activity.packageManager) != null){
            val string = activity.getString(R.string.share_song_x, song.title)
            activity.startActivity(Intent.createChooser(intent, string.asHtml()))
        } else {
            activity.toast("Could not share this song")
        }
    }

    protected  fun viewInfo(navigator: Navigator, mediaId: MediaId){
        navigator.toEditInfoFragment(mediaId)
    }

    protected  fun viewAlbum(navigator: Navigator, mediaId: MediaId){
        navigator.toDetailFragment(mediaId)
    }

    protected  fun viewArtist(navigator: Navigator, mediaId: MediaId){
        navigator.toDetailFragment(mediaId)
    }

    protected fun setRingtone(navigator: Navigator, mediaId: MediaId, song: Song){
        navigator.toSetRingtoneDialog(mediaId, song.title)
    }


}