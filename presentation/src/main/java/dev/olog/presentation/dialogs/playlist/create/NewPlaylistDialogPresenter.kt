package dev.olog.presentation.dialogs.playlist.create

import dev.olog.core.MediaId
import dev.olog.core.entity.PlaylistType
import dev.olog.core.gateway.PlayingQueueGateway
import dev.olog.core.interactor.playlist.GetPlaylistsUseCase
import dev.olog.core.interactor.playlist.InsertCustomTrackListToPlaylist
import dev.olog.core.interactor.songlist.ObserveSongListByParamUseCase
import javax.inject.Inject

class NewPlaylistDialogPresenter @Inject constructor(
    private val mediaId: MediaId,
    playlists: GetPlaylistsUseCase,
    private val insertCustomTrackListToPlaylist: InsertCustomTrackListToPlaylist,
    private val getSongListByParamUseCase: ObserveSongListByParamUseCase,
    private val playingQueueGateway: PlayingQueueGateway

) {

    private val playlistType = if (mediaId.isPodcast) PlaylistType.PODCAST else PlaylistType.TRACK

    private val existingPlaylists = playlists.execute(playlistType)
            .map { it.title.toLowerCase() }

    fun execute(playlistTitle: String) {
        TODO()
//        if (mediaId.isPlayingQueue){
//            val playingQueue = playingQueueGateway.getAll().map { it.song.id }
//            insertCustomTrackListToPlaylist.execute(
//                InsertCustomTrackListRequest(
//                    playlistTitle,
//                    playingQueue,
//                    playlistType
//                )
//            )
//        }
//
//        return if (mediaId.isLeaf && mediaId.isPodcast) {
//            getPodcastUseCase.execute(mediaId).firstOrError().map { listOf(it.id) }
//                    .flatMapCompletable {
//                        insertCustomTrackListToPlaylist.execute(
//                            InsertCustomTrackListRequest(
//                                playlistTitle,
//                                it,
//                                playlistType
//                            )
//                        )
//                    }
//        } else if (mediaId.isLeaf) {
//            getSongUseCase.execute(mediaId).firstOrError().map { listOf(it.id) }
//                    .flatMapCompletable {
//                        insertCustomTrackListToPlaylist.execute(
//                            InsertCustomTrackListRequest(
//                                playlistTitle,
//                                it,
//                                playlistType
//                            )
//                        )
//                    }
//        } else {
//            getSongListByParamUseCase(mediaId)
//                    .asFlowable()
//                    .firstOrError().mapToList { it.id }
//                    .flatMapCompletable {
//                        insertCustomTrackListToPlaylist.execute(
//                            InsertCustomTrackListRequest(
//                                playlistTitle,
//                                it,
//                                playlistType
//                            )
//                        )
//                    }
//        }
    }

    fun isStringValid(string: String): Boolean {
        return !existingPlaylists.contains(string.toLowerCase())
    }

}