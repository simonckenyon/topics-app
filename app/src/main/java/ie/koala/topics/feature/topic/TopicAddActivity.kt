package ie.koala.topics.feature.topic

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.view.MenuItem
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import ie.koala.topics.R
import ie.koala.topics.feature.topic.Topic.Factory.ARG_TOPIC_COUNT
import ie.koala.topics.feature.topic.TopicReference.FIREBASE_TOPICS
import kotlinx.android.synthetic.main.activity_topic_edit.*
import org.slf4j.LoggerFactory

class TopicAddActivity : AppCompatActivity() {

    private lateinit var database: FirebaseDatabase
    private lateinit var topicsDatabaseReference: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_topic_add)

        setSupportActionBar(toolbar)
        toolbar.title = getString(R.string.title_topic_add)

        // Show the Up button in the action bar.
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        database = FirebaseDatabase.getInstance()
        topicsDatabaseReference = database.getReference(FIREBASE_TOPICS)

        val topicCount = intent.getIntExtra(ARG_TOPIC_COUNT, 0)
        log.debug("onCreate: topicCount=$topicCount")
        btn_save.setOnClickListener {
            topicAdded(topicCount)
            finish()
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        log.debug("onOptionsItemSelected:")
        return when (item.itemId) {
            android.R.id.home -> {
                finish()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun topicAdded(topicCount: Int) {
        val newTopic = topicsDatabaseReference.push()
        val id = newTopic.key
        id?.let { nonNullId ->
            val index = topicCount + 1
            val title = input_title.text.toString()
            val content = input_content.text.toString()
            val topic = Topic(nonNullId, index, title, content)

            newTopic.setValue(topic)
        }
    }

    companion object {
        private val log = LoggerFactory.getLogger(TopicDetailActivity::class.java)
    }
}
