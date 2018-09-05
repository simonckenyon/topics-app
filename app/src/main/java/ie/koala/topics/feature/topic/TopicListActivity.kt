package ie.koala.topics.feature.topic

import android.content.Intent
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

    lateinit var itemTouchHelper: ItemTouchHelper

    var topicList: MutableList<Topic> = mutableListOf<Topic>()
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

        log.debug("onCreate:")

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
                topicList.add(topic!!)
                topicList.sortWith(object : Comparator<Topic> {
                    override fun compare(t1: Topic, t2: Topic): Int = t1.compareToByIndex(t2)
                })
                adapter.setItems(topicList)
                //snackbar(coordinator_layout_topic_list,"Topic \"${topic.title}\" added")
            }

            override fun onChildChanged(dataSnapshot: DataSnapshot, previousChildName: String?) {
                log.debug("onChildChanged:" + dataSnapshot.key)
                val topic = dataSnapshot.getValue(Topic::class.java)
                topicList.remove(topic!!)
                topicList.add(topic)
                topicList.sortWith(object : Comparator<Topic> {
                    override fun compare(t1: Topic, t2: Topic): Int = t1.compareToByIndex(t2)
                })
                adapter.setItems(topicList)
                //snackbar(coordinator_layout_topic_list,"Topic \"${topic.title}\" changed")
            }

            override fun onChildRemoved(dataSnapshot: DataSnapshot) {
                log.debug("onChildRemoved:" + dataSnapshot.key)
                val topic = dataSnapshot.getValue(Topic::class.java)
                topicList.remove(topic!!)
                topicList.sortWith(object : Comparator<Topic> {
                    override fun compare(t1: Topic, t2: Topic): Int = t1.compareToByIndex(t2)
                })
                adapter.setItems(topicList)
                //snackbar(coordinator_layout_topic_list,"Topic \"${topic.title}\" removed")
            }

            override fun onChildMoved(dataSnapshot: DataSnapshot, previousChildName: String?) {
                log.debug("onChildMoved:" + dataSnapshot.key)
                val topic = dataSnapshot.getValue(Topic::class.java)
                topicList.remove(topic!!)
                topicList.sortWith(object : Comparator<Topic> {
                    override fun compare(t1: Topic, t2: Topic): Int = t1.compareToByIndex(t2)
                })
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

    private fun setupRecyclerView(recyclerView: RecyclerView) {
        log.debug("setupRecyclerView: topicList.size=${topicList.size}")
        topicList.forEach { topic ->
            log.debug("setupRecyclerView: topic=${topic.title}")
        }

        adapter = TopicListAdapter(this, this)
        adapter.topicListener = this
        recyclerView.addItemDecoration(DividerItemDecoration(this, DividerItemDecoration.VERTICAL))
        recyclerView.setHasFixedSize(true)
        recyclerView.setAdapter(adapter)
        recyclerView.layoutManager = LinearLayoutManager(this)

        adapter.setItems(topicList)
        recyclerView.adapter = adapter

        // this was the original
//        val swipeHandler = object : SwipeToDeleteCallback(this) {
//            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
//                val topic: Topic = adapter.removeAt(viewHolder.adapterPosition)
//                topicsDatabaseReference.child(topic.id).removeValue()
//            }
//        }
//
//        val itemTouchHelper = ItemTouchHelper(swipeHandler)
//        itemTouchHelper.attachToRecyclerView(recyclerView)

        // and this is the latest
        val callback = ItemTouchHelperCallback(adapter)
        itemTouchHelper = ItemTouchHelper(callback)
        itemTouchHelper.attachToRecyclerView(recyclerView)
    }

    /**
     * Called when a view is requesting a start of a drag.
     *
     * @param viewHolder The holder of the view to drag.
     */
    override fun onStartDrag(viewHolder: RecyclerView.ViewHolder) {
        itemTouchHelper.startDrag(viewHolder)
    }

    /**
     * Returns clicked item position [RecyclerView.ViewHolder.getAdapterPosition]
     *
     * @param position clicked item position.
     */
    override fun onItemClick(position: Int) {
        val clickedTopic: Topic = adapter.getItem(position);
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
                topicsDatabaseReference.child(fromChildId).child("index").setValue(i + 1)
                topicsDatabaseReference.child(toChildId).child("index").setValue(i)
            }
        } else {
            for (i in fromPosition downTo toPosition + 1) {
                val fromChildId: String = topicList[i].id
                val toChildId: String = topicList[i - 1].id
                topicsDatabaseReference.child(fromChildId).child("index").setValue(i - 1)
                topicsDatabaseReference.child(toChildId).child("index").setValue(i)
            }
        }



    }

    companion object {
        private val log = LoggerFactory.getLogger(TopicListActivity::class.java)
    }
}
