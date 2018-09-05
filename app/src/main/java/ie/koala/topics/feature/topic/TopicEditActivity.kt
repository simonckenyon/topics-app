package ie.koala.topics.feature.topic

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.support.v4.app.NavUtils
import android.support.v7.app.AppCompatActivity
import android.view.MenuItem
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import ie.koala.topics.R
import ie.koala.topics.feature.topic.Topic.Factory.ARG_TOPIC
import kotlinx.android.synthetic.main.activity_topic_edit.*
import org.slf4j.LoggerFactory

class TopicEditActivity : AppCompatActivity() {

    lateinit var database: FirebaseDatabase
    lateinit var topicsDatabaseReference: DatabaseReference

    lateinit var topic: Topic

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_topic_edit)

        setSupportActionBar(toolbar)
        toolbar.title = "Edit Topic"

        // Show the Up button in the action bar.
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        database = FirebaseDatabase.getInstance()
        topicsDatabaseReference = database.getReference("topics")

        topic = intent.getParcelableExtra<Topic>(ARG_TOPIC)
        log.debug("onCreate: topic=${topic}")
        input_title.setText(topic.title)
        input_content.setText(topic.content)
        btn_save.setOnClickListener {
            topicUpdated()
            returnToDetailActivity()
        }
    }

    fun topicUpdated() {
        topic.title = input_title.text.toString()
        topic.content = input_content.text.toString()
        topicsDatabaseReference.child(topic.id).child("title").setValue(topic.title)
        topicsDatabaseReference.child(topic.id).child("content").setValue(topic.content)
    }

    fun returnToDetailActivity() {
        val resultCode: Int = Activity.RESULT_OK
        val resultIntent = Intent()
        resultIntent.putExtra(ARG_TOPIC, topic)
        setResult(resultCode, resultIntent)
        log.debug("topicUpdated: about to finish()")
        finish()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        log.debug("onOptionsItemSelected:")
        when (item.getItemId()) {
            android.R.id.home -> {
                returnToDetailActivity()
                return true
            }
            else -> return super.onOptionsItemSelected(item)
        }
    }

    companion object {
        private val log = LoggerFactory.getLogger(TopicEditActivity::class.java)
    }
}
