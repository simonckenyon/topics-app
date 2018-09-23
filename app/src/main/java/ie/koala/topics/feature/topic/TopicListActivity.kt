package ie.koala.topics.feature.topic

import android.content.Intent
import android.os.Bundle
import androidx.core.app.NavUtils
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.ItemTouchHelper
import android.view.MenuItem
import com.google.firebase.database.*
import ie.koala.topics.R
import ie.koala.topics.app.Constants.FIREBASE_TOPICS
import ie.koala.topics.app.adapter.OnRecyclerItemClickListener
import ie.koala.topics.app.adapter.ItemTouchHelperCallback
import kotlinx.android.synthetic.main.activity_topic_list.*
import org.jetbrains.anko.AnkoContext
import org.jetbrains.anko.contentView
import org.jetbrains.anko.ctx
import org.jetbrains.anko.design.snackbar
import org.slf4j.LoggerFactory
import java.util.*


/**
 * An activity representing a list of Topics.
 */
class TopicListActivity : AppCompatActivity(), OnRecyclerItemClickListener, TopicListener {

    private var topicList: MutableList<Topic> = mutableListOf()

    private lateinit var itemTouchHelper: ItemTouchHelper
    private lateinit var adapter: TopicListAdapter
    private lateinit var database: FirebaseDatabase
    private lateinit var topicsDatabaseReference: DatabaseReference
    private lateinit var topicListener: ChildEventListener

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_topic_list)

        setSupportActionBar(toolbar)
        toolbar.title = "Topics"

        // Show the Up button in the action bar.
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        log.debug("onCreate:")

        database = FirebaseDatabase.getInstance()
        topicsDatabaseReference = database.getReference(FIREBASE_TOPICS)

        firebaseListenerInit()

        fab.setOnClickListener {
            addNewTopicDialog(topicsDatabaseReference)
        }

        setupRecyclerView(topic_list)
    }

    override fun onStop() {
        super.onStop()

        topicsDatabaseReference.removeEventListener(topicListener)

        topicList.forEach { topic ->
            log.debug("onStop: topic=${topic.title}")
        }
    }

    private fun firebaseListenerInit() {
        log.debug("firebaseListenerInit:")
        val childEventListener: ChildEventListener = object : ChildEventListener {

            override fun onChildAdded(dataSnapshot: DataSnapshot, previousChildName: String?) {
                val topic = dataSnapshot.getValue(Topic::class.java)
                log.debug("onChildAdded: topic added topic=${topic!!.title}")
                topicList.add(topic)
                topicList.sortWith(Comparator { t1, t2 -> t1.compareToByDisplayIndex(t2) })
                adapter.setItems(topicList)
                //snackbar(coordinator_layout_topic_list,"Topic \"${topic.title}\" added")
            }

            override fun onChildChanged(dataSnapshot: DataSnapshot, previousChildName: String?) {
                log.debug("onChildChanged:" + dataSnapshot.key)
                val topic = dataSnapshot.getValue(Topic::class.java)
                topicList.remove(topic!!)
                topicList.add(topic)
                topicList.sortWith(Comparator { t1, t2 -> t1.compareToByDisplayIndex(t2) })
                adapter.setItems(topicList)
                //snackbar(coordinator_layout_topic_list,"Topic \"${topic.title}\" changed")
            }

            override fun onChildRemoved(dataSnapshot: DataSnapshot) {
                log.debug("onChildRemoved:" + dataSnapshot.key)
                val topic = dataSnapshot.getValue(Topic::class.java)
                topicList.remove(topic!!)
                topicList.sortWith(Comparator { t1, t2 -> t1.compareToByDisplayIndex(t2) })
                adapter.setItems(topicList)
                //snackbar(coordinator_layout_topic_list,"Topic \"${topic.title}\" removed")
            }

            override fun onChildMoved(dataSnapshot: DataSnapshot, previousChildName: String?) {
                log.debug("onChildMoved:" + dataSnapshot.key)
                val topic = dataSnapshot.getValue(Topic::class.java)
                topicList.remove(topic!!)
                topicList.sortWith(Comparator { t1, t2 -> t1.compareToByDisplayIndex(t2) })
                topicList.add(topic)
                adapter.setItems(topicList)
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
        return when (item.itemId) {
            android.R.id.home -> {
                NavUtils.navigateUpFromSameTask(this)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun addNewTopicDialog(databaseReference: DatabaseReference) {
        val addNewTopicDialogUi by lazy {
            contentView?.let {
                AddTopicDialog(AnkoContext.create(ctx, it))
            }
        }

        addNewTopicDialogUi?.okButton?.setOnClickListener {
            //We first make a push so that a new item is made with a unique ID
            val newTopic = databaseReference.push()
            val id = newTopic.key
            id?.let { nonNullId ->
                val index = topicList.size
                val title = addNewTopicDialogUi.topicTitleText.text.toString()
                val content = addNewTopicDialogUi.topicContentText.text.toString()
                val topic = Topic(nonNullId, index, title, content)

                newTopic.setValue(topic)
                addNewTopicDialogUi.dialog.dismiss()
                snackbar(coordinator_layout_topic_list, "Topic \"${topic.title}\" added")
            }
        }

        addNewTopicDialogUi?.cancelButton?.setOnClickListener {
            addNewTopicDialogUi.dialog.dismiss()
        }
    }

    private fun setupRecyclerView(recyclerView: androidx.recyclerview.widget.RecyclerView) {
        log.debug("setupRecyclerView: topicList.size=${topicList.size}")
        topicList.forEach { topic ->
            log.debug("setupRecyclerView: topic=${topic.title}")
        }

        adapter = TopicListAdapter(this, this)
        adapter.topicListener = this
        recyclerView.addItemDecoration(androidx.recyclerview.widget.DividerItemDecoration(this, androidx.recyclerview.widget.DividerItemDecoration.VERTICAL))
        recyclerView.setHasFixedSize(true)
        recyclerView.adapter = adapter
        recyclerView.layoutManager = androidx.recyclerview.widget.LinearLayoutManager(this)

        adapter.setItems(topicList)
        recyclerView.adapter = adapter

        val callback = ItemTouchHelperCallback(adapter)
        itemTouchHelper = ItemTouchHelper(callback)
        itemTouchHelper.attachToRecyclerView(recyclerView)
    }

    /**
     * Called when a view is requesting a start of a drag.
     *
     * @param viewHolder The holder of the view to drag.
     */
    override fun onStartDrag(viewHolder: androidx.recyclerview.widget.RecyclerView.ViewHolder) {
        itemTouchHelper.startDrag(viewHolder)
    }

    /**
     * Returns clicked item position [RecyclerView.ViewHolder.getAdapterPosition]
     *
     * @param position clicked item position.
     */
    override fun onItemClick(position: Int) {
        val clickedTopic: Topic = adapter.getItem(position)
        log.debug("onItemClick: topic=${clickedTopic.title}")
        val intent = Intent(this, TopicDetailActivity::class.java).apply {
            putExtra(Topic.ARG_TOPIC, clickedTopic)
        }
        startActivity(intent)

    }

    override fun onItemDeleted(topic: Topic) {
        log.debug("onItemDeleted: topic=${topic.title}")
        topicsDatabaseReference.child(topic.id).removeValue()
    }

    override fun onItemMoved(fromPosition: Int, toPosition: Int) {
        log.debug("onItemMove: fromPosition=$fromPosition toPosition=$toPosition")
        if (fromPosition < toPosition) {
            for (i in fromPosition until toPosition) {
                val fromChildId: String = topicList[i].id
                val toChildId: String = topicList[i + 1].id
                topicsDatabaseReference.child(fromChildId).child("displayIndex").setValue(i + 1)
                topicsDatabaseReference.child(toChildId).child("displayIndex").setValue(i)
            }
        } else {
            for (i in fromPosition downTo toPosition + 1) {
                val fromChildId: String = topicList[i].id
                val toChildId: String = topicList[i - 1].id
                topicsDatabaseReference.child(fromChildId).child("displayIndex").setValue(i - 1)
                topicsDatabaseReference.child(toChildId).child("displayIndex").setValue(i)
            }
        }



    }

    companion object {
        private val log = LoggerFactory.getLogger(TopicListActivity::class.java)
    }
}
