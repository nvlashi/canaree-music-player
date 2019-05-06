package dev.olog.msc.presentation.shortcuts.playlist.chooser

import android.app.Activity
import androidx.databinding.ViewDataBinding
import androidx.lifecycle.Lifecycle
import dev.olog.msc.BR
import dev.olog.msc.R
import dev.olog.msc.core.AppShortcuts
import dev.olog.msc.core.dagger.qualifier.ActivityLifecycle
import dev.olog.msc.shared.ui.ThemedDialog
import dev.olog.presentation.base.adapter.AbsAdapter
import dev.olog.presentation.base.adapter.DataBoundViewHolder
import dev.olog.presentation.base.extensions.setOnClickListener
import dev.olog.presentation.base.model.DisplayableItem
import javax.inject.Inject

class PlaylistChooserActivityAdapter @Inject constructor(
        private val activity: Activity,
        @ActivityLifecycle lifecycle: Lifecycle,
        private var appShortcuts: AppShortcuts

) : AbsAdapter<DisplayableItem>(lifecycle) {

    override fun initViewHolderListeners(viewHolder: DataBoundViewHolder, viewType: Int) {
        viewHolder.setOnClickListener(controller) { item, _, _ ->
            askConfirmation(item)
        }
    }

    private fun askConfirmation(item: DisplayableItem) {
        ThemedDialog.builder(activity)
                .setTitle(R.string.playlist_chooser_dialog_title)
                .setMessage(activity.getString(R.string.playlist_chooser_dialog_message, item.title))
                .setPositiveButton(R.string.popup_positive_ok, { _, _ ->
                    appShortcuts.addDetailShortcut(item.mediaId, item.title, item.image)
                    activity.finish()
                })
                .setNegativeButton(R.string.popup_negative_no, null)
                .show()
    }

    override fun bind(binding: ViewDataBinding, item: DisplayableItem, position: Int) {
        binding.setVariable(BR.item, item)
    }
}