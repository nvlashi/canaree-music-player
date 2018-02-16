package dev.olog.msc.presentation.recently.added

import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.view.View
import dev.olog.msc.R
import dev.olog.msc.presentation.base.BaseFragment
import dev.olog.msc.utils.MediaId
import dev.olog.msc.utils.k.extension.subscribe
import dev.olog.msc.utils.k.extension.withArguments
import kotlinx.android.synthetic.main.fragment_recently_added.*
import kotlinx.android.synthetic.main.fragment_recently_added.view.*
import javax.inject.Inject

class RecentlyAddedFragment : BaseFragment() {

    companion object {
        const val TAG = "RecentlyAddedFragment"
        const val ARGUMENTS_MEDIA_ID = "$TAG.arguments.media_id"


        fun newInstance(mediaId: MediaId): RecentlyAddedFragment {
            return RecentlyAddedFragment().withArguments(
                    ARGUMENTS_MEDIA_ID to mediaId.toString()
            )
        }
    }

    @Inject lateinit var viewModel: RecentlyAddedFragmentViewModel
    @Inject lateinit var adapter: RecentlyAddedFragmentAdapter

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel.data.subscribe(this, adapter::updateDataSet)
    }

    override fun onViewBound(view: View, savedInstanceState: Bundle?) {
        view.list.adapter = adapter
        view.list.layoutManager = LinearLayoutManager(context)
        view.list.setHasFixedSize(true)
    }

    override fun onResume() {
        super.onResume()
        back.setOnClickListener { activity!!.onBackPressed() }
    }

    override fun onPause() {
        super.onPause()
        back.setOnClickListener(null)
    }

    override fun provideLayoutId(): Int = R.layout.fragment_recently_added
}