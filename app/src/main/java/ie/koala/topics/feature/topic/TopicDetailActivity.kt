package ie.koala.topics.feature.topic

import android.content.Intent
import android.os.Bundle
import android.support.v4.app.NavUtils
import android.support.v7.app.AppCompatActivity
import android.view.MenuItem
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import ie.koala.topics.R
import kotlinx.android.synthetic.main.activity_topic_detail.*

/**
 * An activity representing a single Topic detail screen. This
 * activity is only used on narrow width devices. On tablet-size devices,
 * item details are presented side-by-side with a list of items
 * in a [TopicListActivity].
 */
class TopicDetailActivity : AppCompatActivity() {

    //Get Access to Firebase database, no need of any URL, Firebase
    //identifies the connection via the package name of the app
    lateinit var mDatabase: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_topic_detail)

        setSupportActionBar(toolbar)
        toolbar.title = "Topic Detail"

        fab.setOnClickListener {
            //addNewTopicDialog(database)
        }

        // Show the Up button in the action bar.
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        mDatabase = FirebaseDatabase.getInstance().reference

        // savedInstanceState is non-null when there is fragment state
        // saved from previous configurations of this activity
        // (e.g. when rotating the screen from portrait to landscape).
        // In this case, the fragment will automatically be re-added
        // to its container so we don't need to manually add it.
        // For more information, see the Fragments API guide at:
        //
        // http://developer.android.com/guide/components/fragments.html
        //
        if (savedInstanceState == null) {
            // Create the detail fragment and add it to the activity
            // using a fragment transaction.
            val fragment = TopicDetailFragment().apply {
                arguments = Bundle().apply {
                    putParcelable(TopicDetailFragment.ARG_TOPIC,
                            intent.getParcelableExtra<Topic>(TopicDetailFragment.ARG_TOPIC))
                }
            }

            supportFragmentManager.beginTransaction()
                    .add(R.id.topic_detail_container, fragment)
                    .commit()
        }
    }

    override fun onOptionsItemSelected(item: MenuItem) =
            when (item.itemId) {
                android.R.id.home -> {
                    // This ID represents the Home or Up button. In the case of this
                    // activity, the Up button is shown. Use NavUtils to allow users
                    // to navigate up one level in the application structure. For
                    // more details, see the Navigation pattern on Android Design:
                    //
                    // http://developer.android.com/design/patterns/navigation.html#up-vs-back

                    NavUtils.navigateUpTo(this, Intent(this, TopicListActivity::class.java))
                    true
                }
                else -> super.onOptionsItemSelected(item)
            }
}
