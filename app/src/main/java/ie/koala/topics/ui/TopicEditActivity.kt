package ie.koala.topics.ui

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.view.MenuItem
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import ie.koala.topics.R
import ie.koala.topics.model.Topic
import ie.koala.topics.feature.topic.TopicReference.FIREBASE_TOPICS
import ie.koala.topics.model.Topic.Factory.ARG_TOPIC
import kotlinx.android.synthetic.main.activity_topic_edit.*
import org.slf4j.LoggerFactory

class TopicEditActivity : AppCompatActivity() {

    private lateinit var database: FirebaseDatabase
    private lateinit var topicsDatabaseReference: DatabaseReference
    private lateinit var topic: Topic

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_topic_edit)

        setSupportActionBar(toolbar)
        toolbar.title = getString(R.string.title_topic_edit)

        // Show the Up button in the action bar.
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        database = FirebaseDatabase.getInstance()
        topicsDatabaseReference = database.getReference(FIREBASE_TOPICS)

        topic = intent.getParcelableExtra(ARG_TOPIC)
        log.debug("onCreate: topic=$topic")
        input_title.setText(topic.title)
        input_content.setText(topic.content)
        btn_save.setOnClickListener {
            topicUpdated()
            returnToDetailActivity()
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        log.debug("onOptionsItemSelected:")
        return when (item.itemId) {
            android.R.id.home -> {
                returnToDetailActivity()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun topicUpdated() {
        topic.title = input_title.text.toString()
        topic.content = input_content.text.toString()
        topicsDatabaseReference.child(topic.id).child("title").setValue(topic.title)
        topicsDatabaseReference.child(topic.id).child("content").setValue(topic.content)
    }

    private fun returnToDetailActivity() {
        val resultCode: Int = Activity.RESULT_OK
        val resultIntent = Intent()
        resultIntent.putExtra(ARG_TOPIC, topic)
        setResult(resultCode, resultIntent)
        log.debug("topicUpdated: about to finish()")
        finish()
    }

    companion object {
        private val log = LoggerFactory.getLogger(TopicEditActivity::class.java)
    }
}
