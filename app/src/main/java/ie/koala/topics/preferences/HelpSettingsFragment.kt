package ie.koala.topics.preferences

import android.os.Bundle
import androidx.preference.PreferenceFragmentCompat
import ie.koala.topics.R

class HelpSettingsFragment: PreferenceFragmentCompat() {

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.help_preferences, rootKey)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        activity?.title = "Help"
    }
}
