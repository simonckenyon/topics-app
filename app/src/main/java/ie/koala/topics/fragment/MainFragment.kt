package ie.koala.topics.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import ie.koala.topics.app.TopicsApplication
import kotlinx.android.synthetic.main.fragment_main.*
import org.slf4j.LoggerFactory
import ie.koala.topics.R

class MainFragment: Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_main, null)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        version_name.text = TopicsApplication.versionName
        version_code.text = TopicsApplication.versionCode
        version_build_timestamp.text = TopicsApplication.versionBuildTimestamp
        version_git_hash.text = TopicsApplication.versionGitHash

    }

    companion object {
        private val log = LoggerFactory.getLogger(MainFragment::class.java)
    }
}
