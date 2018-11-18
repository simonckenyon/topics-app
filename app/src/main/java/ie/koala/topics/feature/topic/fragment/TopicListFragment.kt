package ie.koala.topics.feature.topic.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.*
import ie.koala.topics.R
import ie.koala.topics.adapter.ItemTouchHelperCallback
import ie.koala.topics.adapter.OnRecyclerItemClickListener
import ie.koala.topics.feature.topic.firebase.TopicReference.FIREBASE_TOPICS
import ie.koala.topics.model.Topic
import ie.koala.topics.feature.topic.adapter.TopicListAdapter
import ie.koala.topics.feature.topic.adapter.TopicListener
import ie.koala.topics.ui.snackbar
import kotlinx.android.synthetic.main.fragment_topic_list.*
import org.slf4j.LoggerFactory
import java.util.*

/**
 * A fragment representing a list of Topics.
 */
class TopicListFragment : Fragment(), OnRecyclerItemClickListener, TopicListener {

    private var topicList: MutableList<Topic> = mutableListOf()

    private lateinit var itemTouchHelper: ItemTouchHelper
    private lateinit var adapter: TopicListAdapter
    private lateinit var database: FirebaseDatabase
    private lateinit var topicsDatabaseReference: DatabaseReference
    private lateinit var topicListener: ChildEventListener

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_topic_list, null)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        database = FirebaseDatabase.getInstance()
        topicsDatabaseReference = database.getReference(FIREBASE_TOPICS)

        fab.setOnClickListener {
            log.debug("onClickListener: addNewTopic")
            val action = TopicListFragmentDirections.actionTopicListFragmentToTopicAddFragment(topicList.size)
            findNavController().navigate(action)
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

    private fun setupRecyclerView(recyclerView: androidx.recyclerview.widget.RecyclerView) {
        log.debug("setupRecyclerView: topicList.size=${topicList.size}")
        topicList.forEach { topic ->
            log.debug("setupRecyclerView: topic=${topic.title}")
        }

        val context = recyclerView.context
        adapter = TopicListAdapter(context, this)
        adapter.topicListener = this
        recyclerView.addItemDecoration(androidx.recyclerview.widget.DividerItemDecoration(context, androidx.recyclerview.widget.DividerItemDecoration.VERTICAL))
        recyclerView.setHasFixedSize(true)
        recyclerView.adapter = adapter
        recyclerView.layoutManager = androidx.recyclerview.widget.LinearLayoutManager(context)

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
        val action = TopicListFragmentDirections.actionTopicListFragmentToTopicDetailFragment(clickedTopic)
        findNavController().navigate(action)
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
        private val log = LoggerFactory.getLogger(TopicListFragment::class.java)
    }
}
