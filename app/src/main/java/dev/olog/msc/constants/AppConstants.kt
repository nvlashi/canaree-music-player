package dev.olog.msc.constants

import android.content.Context
import android.preference.PreferenceManager
import dev.olog.msc.R
import dev.olog.msc.presentation.widget.QuickActionView

object AppConstants {

    enum class ImageShape {
        RECTANGLE, ROUND
    }

    private const val TAG = "AppConstants"

    var IGNORE_MEDIA_STORE_COVERS = false

    const val SHORTCUT_SEARCH = "$TAG.shortcut.search"
    const val SHORTCUT_DETAIL = "$TAG.shortcut.detail"
    const val SHORTCUT_DETAIL_MEDIA_ID = "$TAG.shortcut.detail.media.id"
    const val SHORTCUT_PLAYLIST_CHOOSER = "$TAG.shortcut.playlist.chooser"

    const val NO_IMAGE = "NO_IMAGE"

    var QUICK_ACTION = QuickActionView.Type.NONE
    var IMAGE_SHAPE = ImageShape.ROUND

    const val PROGRESS_BAR_INTERVAL = 250

    fun initialize(context: Context){
        updateQuickAction(context)
        updateIconShape(context)
        updateIgnoreMediaStoreCovers(context)
    }

    fun updateQuickAction(context: Context){
        QUICK_ACTION = getQuickAction(context)
    }

    fun updateIconShape(context: Context){
        IMAGE_SHAPE = getIconShape(context)
    }

    fun updateIgnoreMediaStoreCovers(context: Context) {
        IGNORE_MEDIA_STORE_COVERS = getIgnoreMediaStoreCovers(context)
    }

    private fun getQuickAction(context: Context): QuickActionView.Type {
        val preferences = PreferenceManager.getDefaultSharedPreferences(context.applicationContext)
        val quickAction = preferences.getString(context.getString(R.string.prefs_quick_action_key), context.getString(R.string.prefs_quick_action_entry_value_hide))
        return when (quickAction) {
            context.getString(R.string.prefs_quick_action_entry_value_hide) -> QuickActionView.Type.NONE
            context.getString(R.string.prefs_quick_action_entry_value_play) -> QuickActionView.Type.PLAY
            else ->  QuickActionView.Type.SHUFFLE
        }
    }

    private fun getIconShape(context: Context): ImageShape {
        val prefs = PreferenceManager.getDefaultSharedPreferences(context.applicationContext)
        val shape = prefs.getString(context.getString(R.string.prefs_icon_shape_key), context.getString(R.string.prefs_icon_shape_rounded))!!
        return when (shape){
            context.getString(R.string.prefs_icon_shape_rounded) -> ImageShape.ROUND
            context.getString(R.string.prefs_icon_shape_square) -> ImageShape.RECTANGLE
            else -> throw IllegalArgumentException("image shape not valid=$shape")
        }
    }

    private fun getIgnoreMediaStoreCovers(context: Context): Boolean {
        val prefs = PreferenceManager.getDefaultSharedPreferences(context.applicationContext)
        return prefs.getBoolean(context.getString(R.string.prefs_ignore_media_store_cover_key), false)
    }

}