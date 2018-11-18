package ie.koala.topics.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import ie.koala.topics.R
import ie.koala.topics.model.Wiki
import ie.koala.topics.ui.TopicActivity
import ie.koala.topics.web.ViewClient
import kotlinx.android.synthetic.main.fragment_wiki.*
import org.slf4j.LoggerFactory


class WikiFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_wiki, null)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        val w = arguments?.getParcelable<Wiki>("wiki")
        if (w != null) {
            //log.debug("onViewCreated: w=$w")

            val url = "assets://wiki/" + w.url + ".wiki"

            try {
                //toolbar_layout.title = w.title
                val topicActivity: TopicActivity? = activity as TopicActivity
                if (topicActivity != null) {
                    topicActivity.updateTitle(w.title)

                    wiki.webViewClient = ViewClient(null)
                    wiki.loadUrl(url)
                }
            } catch (e: Exception) {
                log.error("onViewCreated: exception ", e)
                wiki.loadData("Unable to show wiki page", "text/html", "")
            }
        }
    }

    companion object {
        const val ARG_WIKI = "ARG_WIKI"
        val log = LoggerFactory.getLogger(WikiFragment::class.java)
    }
}
