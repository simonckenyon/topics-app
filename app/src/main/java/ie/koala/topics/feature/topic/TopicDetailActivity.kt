package ie.koala.topics.feature.topic

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.core.app.NavUtils
import androidx.appcompat.app.AppCompatActivity
import android.view.MenuItem
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import ie.koala.topics.R
import ie.koala.topics.feature.topic.Topic.Factory.ARG_TOPIC
import ie.koala.topics.web.AppWikiModel
import ie.koala.topics.web.ViewClient
import kotlinx.android.synthetic.main.activity_topic_detail.*
import org.slf4j.LoggerFactory

class TopicDetailActivity : AppCompatActivity() {

    private lateinit var mDatabase: DatabaseReference
    private lateinit var topic: Topic

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_topic_detail)

        log.debug("onCreate:")

        setSupportActionBar(toolbar)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        mDatabase = FirebaseDatabase.getInstance().reference

        topic = intent.getParcelableExtra(ARG_TOPIC)
        log.debug("onCreate: topic=$topic")
        updateContent(topic)

        fab.setOnClickListener {
            val intent = Intent(this, TopicEditActivity::class.java)
            intent.putExtra(Topic.ARG_TOPIC, topic)
            startActivityForResult(intent, REQUEST_TOPIC_EDIT)
        }
    }

    override fun onBackPressed() {
        log.debug("onBackPressed:")
        super.onBackPressed()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        log.debug("onActivityResult:")

        if (requestCode == REQUEST_TOPIC_EDIT) {
            if (resultCode == Activity.RESULT_OK) {
                if (data != null) {
                    val t: Topic = data.getParcelableExtra(ARG_TOPIC)
                    log.debug("onActivityResult: t=$t")
                    updateContent(t)
                }
            }
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        log.debug("onOptionsItemSelected:")
        return when (item.itemId) {
            android.R.id.home -> {
                NavUtils.navigateUpFromSameTask(this)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun updateContent(t: Topic) {
        log.debug("updateContent: t=$t")
        topic = t
        try {
            toolbar_layout.title = t.title

            wiki.webViewClient = ViewClient(null)
            //val webSettings = wiki.settings
            //webSettings.javaScriptEnabled = true
            val wikiModel = AppWikiModel()
            val htmlStr = wikiModel.render(t.content)
            wiki.loadData(htmlStr, "text/html; charset=utf-8", "UTF-8")
        } catch (e: Exception) {
            log.debug("onCreateView: exception ", e)
            wiki.loadData("Unable to show wiki page", "text/html", "")
        }
    }

    companion object {
        private const val REQUEST_TOPIC_EDIT = 0
        val log = LoggerFactory.getLogger(TopicDetailActivity::class.java)
    }
}
