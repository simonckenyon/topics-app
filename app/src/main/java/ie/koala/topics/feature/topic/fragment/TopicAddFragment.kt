package ie.koala.topics.feature.topic.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import ie.koala.topics.R
import ie.koala.topics.feature.topic.firebase.TopicReference.FIREBASE_TOPICS
import ie.koala.topics.model.Topic
import kotlinx.android.synthetic.main.fragment_topic_edit.*
import org.slf4j.LoggerFactory

class TopicAddFragment : Fragment() {

    private lateinit var database: FirebaseDatabase
    private lateinit var topicsDatabaseReference: DatabaseReference

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_topic_add, null)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        database = FirebaseDatabase.getInstance()
        topicsDatabaseReference = database.getReference(FIREBASE_TOPICS)

        val topicCount = TopicAddFragmentArgs.fromBundle(arguments).topicCount
        log.debug("onCreate: topicCount=$topicCount")
        btn_save.setOnClickListener {
            topicAdded(topicCount)
            findNavController().popBackStack()
        }
    }

    private fun topicAdded(topicCount: Int) {
        val newTopic = topicsDatabaseReference.push()
        val id = newTopic.key
        id?.let { nonNullId ->
            val index = topicCount + 1
            val topicType = "TOPIC"
            val parentId = 111
            val title = input_title.text.toString()
            val content = input_content.text.toString()
            val topic = Topic(nonNullId, index.toString(), topicType, parentId, title, content)

            newTopic.setValue(topic)
        }
    }

    companion object {
        private val log = LoggerFactory.getLogger(TopicDetailFragment::class.java)
    }
}
