package dev.olog.msc.presentation.library.tab.span.size.lookup

import android.content.Context
import dev.olog.msc.R
import dev.olog.msc.shared.extensions.configuration
import dev.olog.presentation.base.adapter.AbsAdapter
import dev.olog.presentation.base.model.DisplayableItem

class ArtistSpanSizeLookup(
        context: Context,
        private val isPortrait: Boolean,
        private val adapter: AbsAdapter<DisplayableItem>

) : AbsSpanSizeLookup() {

    private val smallestWidthDip = context.configuration.smallestScreenWidthDp
    private val isTablet = smallestWidthDip >= 600

    override fun getSpanSize(position: Int): Int {
        val itemType = adapter.elementAt(position).type
        when (itemType){
            R.layout.item_tab_header,
            R.layout.item_tab_last_played_artist_horizontal_list,
            R.layout.item_tab_new_artist_horizontal_list -> return spanCount
        }

        var span = if (isPortrait) 3 else 4

        if (isTablet) {
            span++
        }

        return spanCount / span
    }
}