package ie.koala.topics.preferences

import android.os.Bundle
import androidx.preference.PreferenceFragmentCompat
import ie.koala.topics.R

class RootPreferencesFragment: PreferenceFragmentCompat() {

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.root_preferences, rootKey)

        activity?.title = "Settings"
    }

    override fun onResume() {
        super.onResume()

        activity?.title = "Settings"
    }
}
