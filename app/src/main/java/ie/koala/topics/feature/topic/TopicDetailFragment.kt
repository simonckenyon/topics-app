package ie.koala.topics.feature.topic

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import ie.koala.topics.R
import kotlinx.android.synthetic.main.activity_topic_detail.*
import kotlinx.android.synthetic.main.topic_detail.view.*
import org.slf4j.LoggerFactory

/**
 * A fragment representing a single Topic detail screen.
 * This fragment is either contained in a [TopicListActivity]
 * in two-pane mode (on tablets) or a [TopicDetailActivity]
 * on handsets.
 */
class TopicDetailFragment : Fragment() {

    /**
     * The topic this fragment is presenting.
     */
    private var topic: Topic? = null

    private val log = LoggerFactory.getLogger(TopicDetailFragment::class.java)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        arguments?.let {
            if (it.containsKey(ARG_TOPIC)) {
                // Load the dummy content specified by the fragment
                // arguments. In a real-world scenario, use a Loader
                // to load content from a content provider.
                topic = it.getParcelable(ARG_TOPIC)
                activity?.toolbar_layout?.title = topic?.title
            }
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val rootView = inflater.inflate(R.layout.topic_detail, container, false)

        // Show the topic as text in a TextView.
        topic?.let {
            log.debug("topic=${topic}")
            rootView.topic_detail.text = it.content
        }

        return rootView
    }

    companion object {
        /**
         * The fragment argument representing the topic ID that this fragment
         * represents.
         */
        const val ARG_TOPIC = "topic"
    }
}
