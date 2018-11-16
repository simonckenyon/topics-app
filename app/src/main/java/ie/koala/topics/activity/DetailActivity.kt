package ie.koala.topics.activity

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NavUtils
import ie.koala.topics.R
import ie.koala.topics.api.TopicService
import ie.koala.topics.model.Topic
import ie.koala.topics.web.AppWikiModel
import ie.koala.topics.web.ViewClient
import kotlinx.android.synthetic.main.activity_detail.*
import org.slf4j.Logger
import org.slf4j.LoggerFactory

class DetailActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        // FIXME!
        // the title transition doesn't really play well with the CollapsingToolbarLayout
        for (i in 0 until toolbar.childCount) {
            val child = toolbar.getChildAt(i)
            if (child is TextView) {
                child.transitionName = "topicTitle"
                break
            }
        }

        val topic: Topic = intent.getParcelableExtra(Topic.ARG_TOPIC)
        log.debug("onCreate: topic=$topic")
        updateContent(topic)

        fab.setOnClickListener {
            val url = TopicService.WEBSITE_BASE_URL +  topic.id
            val message = """Here is a link to \"${topic.title}\" on thetopicdb.org:
                |
                |$url
                |
                |Enjoy!""".trimMargin()
            val subject = "Topic: ${topic.title}"
            log.debug("getDefaultIntent: subject=\"$subject\" message=\"$message\"")

            val intent = Intent(Intent.ACTION_SEND)
            intent.putExtra(Intent.EXTRA_TEXT, message)
            intent.putExtra(Intent.EXTRA_SUBJECT, subject)
            intent.type = "text/plain"
            startActivity(intent)
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                NavUtils.navigateUpFromSameTask(this)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun updateContent(topic: Topic) {
        try {
            toolbar_layout.title = topic.title


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
        val log: Logger = LoggerFactory.getLogger(DetailActivity::class.java)

        fun newIntent(context: Context, topic: Topic): Intent {
            val intent = Intent(context, DetailActivity::class.java)
            intent.putExtra(Topic.ARG_TOPIC, topic)
            return intent
        }
    }
}
