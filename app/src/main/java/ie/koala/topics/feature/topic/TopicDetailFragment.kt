package ie.koala.topics.feature.topic

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import ie.koala.topics.R
import ie.koala.topics.feature.topic.TopicDetailFragmentArgs
import ie.koala.topics.feature.topic.TopicDetailFragmentDirections
import ie.koala.topics.model.Topic
import ie.koala.topics.web.AppWikiModel
import ie.koala.topics.web.ViewClient
import kotlinx.android.synthetic.main.fragment_topic_detail.*
import org.slf4j.LoggerFactory

class TopicDetailFragment : Fragment() {

    private lateinit var mDatabase: DatabaseReference
    private lateinit var topic: Topic

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_topic_detail, null)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        mDatabase = FirebaseDatabase.getInstance().reference

        topic = TopicDetailFragmentArgs.fromBundle(arguments).topic
        log.debug("onCreate: topic=$topic")

        fab.setOnClickListener {
            log.debug("onClickListener: editTopic")
            val action = TopicDetailFragmentDirections.actionTopicDetailFragmentToTopicEditFragment(topic)
            findNavController().navigate(action)
        }
    }

    override fun onResume() {
        super.onResume()
        updateContent()
    }

    private fun updateContent() {
        log.debug("updateContent:")
        try {
            //toolbar_layout.title = t.title

            wiki.webViewClient = ViewClient(null)
            //val webSettings = wiki.settings
            //webSettings.javaScriptEnabled = true
            val wikiModel = AppWikiModel()
            val htmlStr = wikiModel.render(topic.content)
            wiki.loadData(htmlStr, "text/html; charset=utf-8", "UTF-8")
        } catch (e: Exception) {
            log.debug("onCreateView: exception ", e)
            wiki.loadData("Unable to show wiki page", "text/html", "")
        }
    }

    companion object {
        private const val REQUEST_TOPIC_EDIT = 0
        val log = LoggerFactory.getLogger(TopicDetailFragment::class.java)
    }
}
