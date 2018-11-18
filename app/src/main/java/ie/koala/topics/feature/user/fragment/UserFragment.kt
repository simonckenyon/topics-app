package ie.koala.topics.feature.user.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import ie.koala.topics.R
import ie.koala.topics.feature.user.model.User
import ie.koala.topics.feature.user.viewmodel.UserViewModel
import ie.koala.topics.firebase.nonNull
import ie.koala.topics.firebase.observe
import kotlinx.android.synthetic.main.fragment_user.*
import org.slf4j.LoggerFactory
import java.util.*

class UserFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_user, null)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        val viewModel = ViewModelProviders.of(this).get(UserViewModel::class.java)
        val liveData = viewModel.dataSnapshotLiveData

        liveData
                .nonNull()
                .observe(this) { dataSnapshot ->
                    log.debug("onChanged: dataSnapshot=" + dataSnapshot)
                    val user = dataSnapshot.getValue(User::class.java)
                    log.debug("onChanged: user=$user")

                    // update the UI here with values in the snapshot
                    ticker.text = user?.ticker ?: "ticker not found"
                    price.text = if (user?.price != null) String.format(Locale.getDefault(), "%.2f", user.price) else "price not found"
                }
    }

    companion object {
        private val LOG_TAG = "FirebaseQueryLiveData"
        private val log = LoggerFactory.getLogger(UserFragment::class.java)
    }
}