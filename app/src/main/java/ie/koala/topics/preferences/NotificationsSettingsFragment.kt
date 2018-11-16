package ie.koala.topics.preferences

import android.os.Bundle
import androidx.preference.PreferenceFragmentCompat
import ie.koala.topics.R

class NotificationsSettingsFragment: PreferenceFragmentCompat() {

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.notifications_preferences, rootKey)

        activity?.title = "Notifications"
    }
}
