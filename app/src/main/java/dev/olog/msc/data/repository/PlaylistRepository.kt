package dev.olog.msc.data.repository

import android.content.ContentResolver
import android.content.Context
import android.provider.MediaStore
import com.squareup.sqlbrite3.BriteContentResolver
import dev.olog.msc.R
import dev.olog.msc.constants.PlaylistConstants
import dev.olog.msc.dagger.qualifier.ApplicationContext
import dev.olog.msc.data.db.AppDatabase
import dev.olog.msc.data.entity.PlaylistMostPlayedEntity
import dev.olog.msc.data.mapper.extractId
import dev.olog.msc.data.mapper.toPlaylist
import dev.olog.msc.data.mapper.toPlaylistSong
import dev.olog.msc.domain.entity.Playlist
import dev.olog.msc.domain.entity.Song
import dev.olog.msc.domain.gateway.FavoriteGateway
import dev.olog.msc.domain.gateway.PlaylistGateway
import dev.olog.msc.domain.gateway.SongGateway
import dev.olog.msc.utils.MediaId
import dev.olog.msc.utils.img.ImagesFolderUtils
import dev.olog.msc.utils.img.MergedImagesCreator
import dev.olog.msc.utils.k.extension.emitThenDebounce
import io.reactivex.Completable
import io.reactivex.CompletableSource
import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.rxkotlin.toFlowable
import io.reactivex.schedulers.Schedulers
import kotlinx.coroutines.experimental.Deferred
import kotlinx.coroutines.experimental.async
import kotlinx.coroutines.experimental.runBlocking
import javax.inject.Inject
import javax.inject.Singleton

private val MEDIA_STORE_URI = MediaStore.Audio.Playlists.EXTERNAL_CONTENT_URI
private val PROJECTION = arrayOf(
        MediaStore.Audio.Playlists._ID,
        MediaStore.Audio.Playlists.NAME
)
private val SELECTION: String? = null
private val SELECTION_ARGS: Array<String>? = null
private const val SORT_ORDER = "lower(${MediaStore.Audio.Playlists.DEFAULT_SORT_ORDER})"

private val SONG_PROJECTION = arrayOf(
        MediaStore.Audio.Playlists.Members._ID,
        MediaStore.Audio.Playlists.Members.AUDIO_ID
)
private val SONG_SELECTION = null
private val SONG_SELECTION_ARGS: Array<String>? = null
private const val SONG_SORT_ORDER = MediaStore.Audio.Playlists.Members.DEFAULT_SORT_ORDER

@Singleton
class PlaylistRepository @Inject constructor(
        @ApplicationContext private val context: Context,
        private val contentResolver: ContentResolver,
        private val rxContentResolver: BriteContentResolver,
        private val songGateway: SongGateway,
        private val favoriteGateway: FavoriteGateway,
        appDatabase: AppDatabase,
        private val helper: PlaylistRepositoryHelper,
        private val imagesCreator: ImagesCreator

) : BaseRepository<Playlist, Long>(), PlaylistGateway {

    private val resources = context.resources

    private val mostPlayedDao = appDatabase.playlistMostPlayedDao()
    private val historyDao = appDatabase.historyDao()

    private val autoPlaylistTitles = resources.getStringArray(R.array.auto_playlists)

    private fun autoPlaylist() = listOf(
            createAutoPlaylist(PlaylistConstants.LAST_ADDED_ID, autoPlaylistTitles[0]),
            createAutoPlaylist(PlaylistConstants.FAVORITE_LIST_ID, autoPlaylistTitles[1]),
            createAutoPlaylist(PlaylistConstants.HISTORY_LIST_ID, autoPlaylistTitles[2])
    )

    private fun createAutoPlaylist(id: Long, title: String) : Playlist {
//        val image = FileUtils.playlistImagePath(context, id) todo
//        val file = File(image)
        return Playlist(id, title, -1, "")
    }

    override fun queryAllData(): Observable<List<Playlist>> {
        return rxContentResolver.createQuery(
                MEDIA_STORE_URI, PROJECTION, SELECTION,
                SELECTION_ARGS, SORT_ORDER, false
        ).mapToList {
            val id = it.extractId()
            val uri = MediaStore.Audio.Playlists.Members.getContentUri("external", id)
            val size = CommonQuery.getSize(contentResolver, uri)
            it.toPlaylist(context, size)
        }
                .onErrorReturn { listOf() }
                .doOnNext { imagesCreator.subscribe(createImages()) }
                .doOnTerminate { imagesCreator.unsubscribe() }
    }

    override fun createImages() : Single<Any> {
        return getAll().firstOrError()
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io())
                .flattenAsFlowable { it }
                .parallel()
                .runOn(Schedulers.io())
                .map {
                    val uri = MediaStore.Audio.Playlists.Members.getContentUri("external", it.id)
                    Pair(it, CommonQuery.extractAlbumIdsFromSongs(contentResolver, uri))
                }
                .map { (playlist, albumsId) -> try {
                    runBlocking { makeImage(this@PlaylistRepository.context, playlist, albumsId).await() }
                } catch (ex: Exception){/*amen*/}
                }.sequential()
                .toList()
                .map { it.contains(true) }
                .onErrorReturnItem(false)
                .doOnSuccess { created ->
                    if (created) {
                        contentResolver.notifyChange(MEDIA_STORE_URI, null)
                    }
                }.map { Unit }
    }

    private fun makeImage(context: Context, playlist: Playlist, albumsId: List<Long>) : Deferred<Boolean> = async {
        val folderName = ImagesFolderUtils.getFolderName(ImagesFolderUtils.PLAYLIST)
        MergedImagesCreator.makeImages2(context, albumsId, folderName, "${playlist.id}")
    }

    override fun getAllAutoPlaylists(): Observable<List<Playlist>> {
        return Observable.just(autoPlaylist().sortedWith(compareByDescending { it.id }))
    }

    override fun insertSongToHistory(songId: Long): Completable {
        return historyDao.insert(songId)
    }

    override fun getPlaylistsBlocking(): List<Playlist> {
        val cursor = contentResolver.query(MEDIA_STORE_URI, PROJECTION,
                SELECTION, SELECTION_ARGS, SORT_ORDER)
        val list = mutableListOf<Playlist>()
        while (cursor.moveToNext()){
            list.add(cursor.toPlaylist(context, -1))
        }
        cursor.close()
        return list
    }

    override fun getByParam(param: Long): Observable<Playlist> {
        val result = if (PlaylistConstants.isAutoPlaylist(param)){
            getAllAutoPlaylists()
        } else getAll()

        return result.map { getByParamImpl(it, param) }
    }

    override fun getByParamImpl(list: List<Playlist>, param: Long): Playlist {
        return list.first { it.id == param }
    }

    @Suppress("PARAMETER_NAME_CHANGED_ON_OVERRIDE")
    override fun observeSongListByParam(playlistId: Long): Observable<List<Song>> {
        return when (playlistId){
            PlaylistConstants.LAST_ADDED_ID -> getLastAddedSongs()
            PlaylistConstants.FAVORITE_LIST_ID -> favoriteGateway.getAll()
            PlaylistConstants.HISTORY_LIST_ID -> historyDao.getAllAsSongs(songGateway.getAll().firstOrError())
            else -> getPlaylistSongs(playlistId)
        }
    }

    private fun getLastAddedSongs() : Observable<List<Song>>{
        return songGateway.getAll().flatMapSingle {
            it.toFlowable().toSortedList { o1, o2 ->  (o2.dateAdded - o1.dateAdded).toInt() }
        }
    }

    private fun getPlaylistSongs(playlistId: Long) : Observable<List<Song>> {
        val uri = MediaStore.Audio.Playlists.Members.getContentUri("external", playlistId)

        val observable = rxContentResolver.createQuery(
                uri, SONG_PROJECTION, SONG_SELECTION,
                SONG_SELECTION_ARGS, SONG_SORT_ORDER, true

        ).mapToList { it.toPlaylistSong() }
                .flatMapSingle { playlistSongs -> songGateway.getAll().firstOrError().map { songs ->
                    playlistSongs.asSequence()
                            .mapNotNull { playlistSong ->
                                val song = songs.firstOrNull { it.id == playlistSong.songId }
                                song?.copy(trackNumber = playlistSong.idInPlaylist.toInt())
                            }.toList()
                }}

        return observable.emitThenDebounce()
    }

    override fun getMostPlayed(mediaId: MediaId): Observable<List<Song>> {
        val playlistId = mediaId.categoryValue.toLong()
        if (PlaylistConstants.isAutoPlaylist(playlistId)){
            return Observable.just(listOf())
        }
        val observable = mostPlayedDao.getAll(playlistId, songGateway.getAll())

        return observable.emitThenDebounce()
    }

    override fun insertMostPlayed(mediaId: MediaId): Completable {
        val songId = mediaId.leaf!!
        val playlistId = mediaId.categoryValue.toLong()
        return songGateway.getByParam(songId)
                .flatMapCompletable { song ->
                    CompletableSource { mostPlayedDao.insertOne(PlaylistMostPlayedEntity(0, song.id, playlistId)) }
                }
    }

    override fun deletePlaylist(playlistId: Long): Completable {
        return helper.deletePlaylist(playlistId)
    }

    override fun addSongsToPlaylist(playlistId: Long, songIds: List<Long>): Completable {
        return Completable.fromCallable { helper.addSongsToPlaylist(playlistId, songIds) }
    }

    override fun clearPlaylist(playlistId: Long): Completable {
        return helper.clearPlaylist(playlistId)
    }

    override fun removeFromPlaylist(playlistId: Long, idInPlaylist: Long) : Completable{
        return helper.removeSongFromPlaylist(playlistId, idInPlaylist)
    }

    override fun createPlaylist(playlistName: String): Single<Long> {
        return helper.createPlaylist(playlistName)
    }

    override fun renamePlaylist(playlistId: Long, newTitle: String): Completable {
        return helper.renamePlaylist(playlistId, newTitle)
    }

    override fun moveItem(playlistId: Long, from: Int, to: Int): Boolean {
        return helper.moveItem(playlistId, from, to)
    }
}