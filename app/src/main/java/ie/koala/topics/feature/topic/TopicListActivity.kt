package ie.koala.topics.feature.topic

import android.os.Bundle
import android.support.v4.app.NavUtils
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.helper.ItemTouchHelper
import android.view.MenuItem
import com.google.firebase.database.*
import ie.koala.topics.R
import kotlinx.android.synthetic.main.activity_topic_list.*
import org.jetbrains.anko.AnkoContext
import org.jetbrains.anko.contentView
import org.jetbrains.anko.ctx
import org.jetbrains.anko.design.snackbar
import org.jetbrains.anko.toast
import org.slf4j.LoggerFactory


/**
 * An activity representing a list of Topics. This activity
 * has different presentations for handset and tablet-size devices. On
 * handsets, the activity presents a list of items, which when touched,
 * lead to a [TopicDetailActivity] representing
 * item details. On tablets, the activity presents the list of items and
 * item details side-by-side using two vertical panes.
 */
class TopicListActivity : AppCompatActivity() {

    var topicList: MutableList<Topic>? = null
    lateinit var adapter: TopicListAdapter
    lateinit var database: FirebaseDatabase
    lateinit var topicsDatabaseReference: DatabaseReference
    lateinit var topicListener: ChildEventListener

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_topic_list)

        setSupportActionBar(toolbar)
        toolbar.title = "Topics"

        // Show the Up button in the action bar.
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        topicList = mutableListOf<Topic>()

        database = FirebaseDatabase.getInstance()
        topicsDatabaseReference = database.getReference("topics")

        firebaseListenerInit()

        fab.setOnClickListener {
            addNewTopicDialog(topicsDatabaseReference)
        }

        setupRecyclerView(topic_list)
    }

    override fun onStop() {
        super.onStop()

        topicsDatabaseReference.removeEventListener(topicListener)

        topicList?.forEach { topic ->
            log.debug("onStop: topic=${topic.title}")
        }
    }

    private fun firebaseListenerInit() {

        val childEventListener: ChildEventListener = object : ChildEventListener {

            override fun onChildAdded(dataSnapshot: DataSnapshot, previousChildName: String?) {
                val topic = dataSnapshot.getValue(Topic::class.java)
                topicList!!.add(topic!!)
                adapter.notifyDataSetChanged()
                //snackbar(coordinator_layout_topic_list,"Topic \"${topic.title}\" added")
            }

            override fun onChildChanged(dataSnapshot: DataSnapshot, previousChildName: String?) {
                log.debug("onChildChanged:" + dataSnapshot.key)
                val topic = dataSnapshot.getValue(Topic::class.java)
                topicList!!.remove(topic!!)
                topicList!!.add(topic)
                adapter.notifyDataSetChanged()
                //snackbar(coordinator_layout_topic_list,"Topic \"${topic.title}\" changed")
            }

            override fun onChildRemoved(dataSnapshot: DataSnapshot) {
                log.debug("onChildRemoved:" + dataSnapshot.key)
                val topic = dataSnapshot.getValue(Topic::class.java)
                topicList!!.remove(topic!!)
                adapter.notifyDataSetChanged()
                //snackbar(coordinator_layout_topic_list,"Topic \"${topic.title}\" removed")
            }

            override fun onChildMoved(dataSnapshot: DataSnapshot, previousChildName: String?) {
                log.debug("onChildMoved:" + dataSnapshot.key)
                val topic = dataSnapshot.getValue(Topic::class.java)
                topicList!!.remove(topic!!)
                topicList!!.add(topic)
                adapter.notifyDataSetChanged()
                //snackbar(coordinator_layout_topic_list, "Topic \"${topic.title}\" moved")
            }

            override fun onCancelled(databaseError: DatabaseError) {
                log.error("postTopics:onCancelled ", databaseError.toException())
                snackbar(coordinator_layout_topic_list, "Failed to load topic")
            }
        }

        topicsDatabaseReference.addChildEventListener(childEventListener)

        // copy for removing at onStop()
        topicListener = childEventListener
    }

    override fun onBackPressed() {
        log.debug("onBackPressed:")
        super.onBackPressed()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        log.debug("onOptionsItemSelected:")
        when (item.getItemId()) {
            android.R.id.home -> {
                NavUtils.navigateUpFromSameTask(this)
                return true
            }
            else -> return super.onOptionsItemSelected(item)
        }
    }

    private fun addNewTopicDialog(databaseReference: DatabaseReference) {
        val addNewTopicDialogUi by lazy {
            contentView?.let {
                AddTopicDialog(AnkoContext.create(ctx, it))
            }
        }

        addNewTopicDialogUi?.okButton?.setOnClickListener {
            val topic = Topic.create()
            topic.title = addNewTopicDialogUi.topicTitleText.text.toString()
            topic.content = addNewTopicDialogUi.topicContentText.text.toString()

            //We first make a push so that a new item is made with a unique ID
            val newTopic = databaseReference.push()
            topic.id = newTopic.key
            //then, we used the reference to set the value on that ID
            newTopic.setValue(topic)
            addNewTopicDialogUi.dialog.dismiss()
            toast("Topic saved with ID " + topic.id)
        }

        addNewTopicDialogUi?.cancelButton?.setOnClickListener {
            addNewTopicDialogUi.dialog.dismiss()
        }
    }

    private fun setupRecyclerView(recyclerView: RecyclerView) {
        adapter = TopicListAdapter(this, topicList!!)
        recyclerView.addItemDecoration(DividerItemDecoration(this, DividerItemDecoration.VERTICAL))
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter

        val swipeHandler = object : SwipeToDeleteCallback(this) {
            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val topic: Topic = adapter.removeAt(viewHolder.adapterPosition)
                val id: String? = topic.id
                if (id != null) {
                    topicsDatabaseReference.child(id).removeValue()
                }
            }
        }

        val itemTouchHelper = ItemTouchHelper(swipeHandler)
        itemTouchHelper.attachToRecyclerView(recyclerView)
    }

    companion object {
        //const val KEY_TWO_PANE: String = "KEY_TWO_PANE"
        private val log = LoggerFactory.getLogger(TopicListActivity::class.java)
    }
}
