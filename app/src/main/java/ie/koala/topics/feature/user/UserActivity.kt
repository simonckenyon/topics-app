package ie.koala.topics.feature.user

import android.os.Bundle
import androidx.annotation.Nullable
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProviders
import ie.koala.topics.R
import ie.koala.topics.framework.firebase.nonNull
import ie.koala.topics.framework.firebase.observe
import kotlinx.android.synthetic.main.activity_user.*
import org.slf4j.LoggerFactory
import java.util.*

class UserActivity : AppCompatActivity() {

    override fun onCreate(@Nullable savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user)

        val viewModel = ViewModelProviders.of(this).get(UserViewModel::class.java)
        val liveData = viewModel.dataSnapshotLiveData

        liveData
                .nonNull()
                .observe(this) { dataSnapshot ->
                    log.debug("onChanged: dataSnapshot=" + dataSnapshot)
                    val user = dataSnapshot.getValue(User::class.java)
                    log.debug( "onChanged: user=$user")

                    // update the UI here with values in the snapshot
                    ticker.text = user?.ticker ?: "ticker not found"
                    price.text = if (user?.price != null) String.format(Locale.getDefault(), "%.2f", user.price) else "price not found"
                }
    }

    companion object {
        private val LOG_TAG = "FirebaseQueryLiveData"
        private val log = LoggerFactory.getLogger(UserActivity::class.java)
    }
}