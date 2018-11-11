package ie.koala.topics.ui

import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NavUtils
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.customview.customView
import com.afollestad.materialdialogs.customview.getCustomView
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.database.*
import ie.koala.topics.R
import ie.koala.topics.feature.topic.TopicReference.FIREBASE_TOPICS
import ie.koala.topics.adapter.ItemTouchHelperCallback
import ie.koala.topics.adapter.OnRecyclerItemClickListener
import ie.koala.topics.model.Topic
import kotlinx.android.synthetic.main.activity_topic_list.*
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


        fab.setOnClickListener {
            //addNewTopicDialog()
            addNewTopic()
        }

        setupRecyclerView(topic_list)
    }

    override fun onResume() {
        super.onResume()
        firebaseListenerInit()
    }

    override fun onPause() {
        super.onPause()

        topicsDatabaseReference.removeEventListener(topicListener)

        topicList.forEach { topic ->
            log.debug("onPause: topic=${topic.title}")
        }
    }

    private fun firebaseListenerInit() {
        log.debug("firebaseListenerInit:")
        topicList.clear()
        val childEventListener: ChildEventListener = object : ChildEventListener {

            override fun onChildAdded(dataSnapshot: DataSnapshot, previousChildName: String?) {
                val topic = dataSnapshot.getValue(Topic::class.java)
                log.debug("onChildAdded: topic added topic=${topic!!.title}")
                topicList.add(topic)
                topicList.sortWith(Comparator { t1, t2 -> t1.compareToByDisplayIndex(t2) })
                adapter.setItems(topicList)
            }

            override fun onChildChanged(dataSnapshot: DataSnapshot, previousChildName: String?) {
                log.debug("onChildChanged:" + dataSnapshot.key)
                val topic = dataSnapshot.getValue(Topic::class.java)
                topicList.remove(topic!!)
                topicList.add(topic)
                topicList.sortWith(Comparator { t1, t2 -> t1.compareToByDisplayIndex(t2) })
                adapter.setItems(topicList)
            }

            override fun onChildRemoved(dataSnapshot: DataSnapshot) {
                log.debug("onChildRemoved:" + dataSnapshot.key)
                val topic = dataSnapshot.getValue(Topic::class.java)
                topicList.remove(topic!!)
                topicList.sortWith(Comparator { t1, t2 -> t1.compareToByDisplayIndex(t2) })
                adapter.setItems(topicList)
            }

            override fun onChildMoved(dataSnapshot: DataSnapshot, previousChildName: String?) {
                log.debug("onChildMoved:" + dataSnapshot.key)
                val topic = dataSnapshot.getValue(Topic::class.java)
                topicList.remove(topic!!)
                topicList.sortWith(Comparator { t1, t2 -> t1.compareToByDisplayIndex(t2) })
                topicList.add(topic)
                adapter.setItems(topicList)
            }

            override fun onCancelled(databaseError: DatabaseError) {
                log.error("postTopics:onCancelled ", databaseError.toException())
                coordinator_layout_topic_list.snackbar(R.string.message_topic_load_fail)
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

    fun addNewTopicDialog() {
        MaterialDialog(this)
                .customView(R.layout.dialog_topic_add, scrollable = true)
                .title(R.string.title_add_topic)
                .positiveButton(R.string.button_ok) { dialog ->
                    val customView = dialog.getCustomView()!!
                    val inputTopicType: TextInputEditText = customView.findViewById(R.id.input_topic_type)
                    val inputParentId: TextInputEditText = customView.findViewById(R.id.input_parent_id)
                    val inputTitle: TextInputEditText = customView.findViewById(R.id.input_title)
                    val inputContent: TextInputEditText = customView.findViewById(R.id.input_content)

                    val newTopic = topicsDatabaseReference.push()
                    val id = newTopic.key
                    id?.let { nonNullId ->
                        val topicType = inputTopicType.text.toString()
                        val parentId = inputParentId.text.toString()
                        val index = topicList.size
                        val title = inputTitle.text.toString()
                        val content = inputContent.text.toString()
                        val topic = Topic(nonNullId, topicType, parentId, index, title, content)

                        newTopic.setValue(topic)
                        coordinator_layout_topic_list.snackbar(getString(R.string.message_topic_added, topic.title))
                    }
                }
                .negativeButton(R.string.button_cancel) { _ ->
                    // Do something
                }
                .show()

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

    fun addNewTopic() {
        log.debug("addNewTopic")
        val intent = Intent(this, TopicAddActivity::class.java).apply {
            putExtra(Topic.ARG_TOPIC_COUNT, topicList.size)
        }
        startActivity(intent)
    }

    companion object {
        private val log = LoggerFactory.getLogger(TopicListActivity::class.java)
    }
}
