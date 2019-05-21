package dev.olog.msc

import dev.olog.msc.core.PrefsKeys
import javax.inject.Inject

class PrefsKeysImpl @Inject constructor(): PrefsKeys {
    override fun defaultBottomNavigationPage(): Int = R.id.navigation_songs
    override fun defaultDetailSections(): List<Int> = listOf(
            R.string.prefs_detail_section_entry_value_most_played,
            R.string.prefs_detail_section_entry_value_recently_added,
            R.string.prefs_detail_section_entry_value_related_artists
    )
    override fun defaultColorAccent(): Int = R.color.defaultColorAccent
    override fun defaultQuickAction(): Int = R.string.prefs_quick_action_entry_value_hide
    override fun defaultAutoDownloadImages(): Int = R.string.prefs_auto_download_images_entry_value_wifi
    override fun defaultPlayerAppearance(): Int = R.string.prefs_appearance_entry_value_default
    override fun defaultDarkMode(): Int = R.string.prefs_dark_mode_2_value_follow_system
    override fun defaultVisibleDetailSections(): Int = R.array.prefs_detail_sections_entry_values_default

    override fun visibleDetailSections(): Int = R.string.prefs_detail_sections_key
    override fun playerControlsVisibility(): Int = R.string.prefs_player_controls_visibility_key
    override fun showLockscreenArtwork(): Int = R.string.prefs_lockscreen_artwork_key
    override fun adaptiveColors(): Int = R.string.prefs_adaptive_colors_key
    override fun showPodcast(): Int = R.string.prefs_show_podcasts_key
    override fun showNewAlbumsArtists(): Int = R.string.prefs_show_new_albums_artists_key
    override fun showRecentAlbumsArtists(): Int = R.string.prefs_show_recent_albums_artists_key
    override fun colorAccent(): Int = R.string.prefs_color_accent_key
    override fun autoCreateImages(): Int = R.string.prefs_auto_create_images_key
    override fun autoDownloadImages(): Int = R.string.prefs_auto_download_images_key
    override fun showFoldersAsTree(): Int = R.string.prefs_folder_tree_view_key
    override fun quickAction(): Int = R.string.prefs_quick_action_key
    override fun playerAppearance(): Int = R.string.prefs_appearance_key
    override fun darkMode(): Int = R.string.prefs_dark_mode_2_key
    override fun showFolderAsTreeView(): Int = R.string.prefs_folder_tree_view_key
    override fun usedEqualizer(): Int = R.string.prefs_used_equalizer_key

    override fun autoDownloadImageNever(): Int = R.string.prefs_auto_download_images_entry_value_never

    override fun autoDownloadImageWifiOnly(): Int = R.string.prefs_auto_download_images_entry_value_wifi

    override fun autoDownloadImageAlways(): Int = R.string.prefs_auto_download_images_entry_value_always

    override fun autoPlaylist(): Int = R.array.common_auto_playlists

    override fun midnightMode(): Int = R.string.prefs_midnight_mode_key
    override fun crossfade(): Int = R.string.prefs_cross_fade_key
    override fun gapless(): Int = R.string.prefs_gapless_key
}