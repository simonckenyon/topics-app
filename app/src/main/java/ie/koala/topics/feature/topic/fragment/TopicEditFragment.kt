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

class TopicEditFragment : Fragment() {

    private lateinit var database: FirebaseDatabase
    private lateinit var topicsDatabaseReference: DatabaseReference
    private lateinit var topic: Topic

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_topic_edit, null)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        database = FirebaseDatabase.getInstance()
        topicsDatabaseReference = database.getReference(FIREBASE_TOPICS)

        topic = TopicEditFragmentArgs.fromBundle(arguments).topic
        log.debug("onCreate: topic=$topic")
        input_title.setText(topic.title)
        input_content.setText(topic.content)
        btn_save.setOnClickListener {
            topicUpdated()
            findNavController().popBackStack()
        }
    }

    private fun topicUpdated() {
        topic.title = input_title.text.toString()
        topic.content = input_content.text.toString()
        topicsDatabaseReference.child(topic.id).child("title").setValue(topic.title)
        topicsDatabaseReference.child(topic.id).child("content").setValue(topic.content)
    }

    companion object {
        private val log = LoggerFactory.getLogger(TopicEditFragment::class.java)
    }
}
