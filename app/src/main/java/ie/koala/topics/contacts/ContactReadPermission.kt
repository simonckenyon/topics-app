package ie.koala.topics.contacts

import android.Manifest
import android.content.Intent
import android.net.Uri
import android.provider.Settings
import android.view.View
import com.fondesa.kpermissions.extension.listeners
import com.fondesa.kpermissions.extension.permissionsBuilder
import ie.koala.topics.R
import ie.koala.topics.auth.SignUpActivity
import ie.koala.topics.ui.snackbar
import org.jetbrains.anko.alert

import org.slf4j.LoggerFactory

class ContactReadPermission {

    companion object {
        private val log = LoggerFactory.getLogger(SignUpActivity::class.java)

        fun get(activity: ContactLoaderActivity, coordinatorLayout: View) {
            val request = activity.permissionsBuilder(Manifest.permission.READ_CONTACTS).build()
            request.listeners {
                onAccepted { _ ->
                    // Notified when the permissions are accepted.
                    log.debug("onPermissionsAccepted")

                    coordinatorLayout.snackbar(R.string.message_contact_read_permission_granted)

                    // we have permission to use the contacts to fill the email addresss dropdown
                    // start the loader
                    activity.getSupportLoaderManager().initLoader(ContactLoaderActivity.LOADER_ID, null, activity)
                }
                onDenied { _ ->
                    // Notified when the permissions are denied.
                    log.debug("onPermissionsDenied")

                    coordinatorLayout.snackbar(R.string.message_contact_read_permission_denied)
                }
                onPermanentlyDenied { _ ->
                    log.debug("onPermissionsPermanentlyDenied")

                    activity.alert(activity.getString(R.string.message_contact_read_permission_requested)) {
                        titleResource = R.string.title_contact_read_permission
                        positiveButton(R.string.action_settings) {
                            // Open the app's settings.
                            val intent = createAppSettingsIntent(activity)
                            activity.startActivity(intent)
                        }
                        negativeButton(android.R.string.cancel) {}
                    }.show()
                }

                onShouldShowRationale { _, nonce ->
                    log.debug("onPermissionsShouldShowRationale")

                    activity.alert(activity.getString(R.string.message_contact_read_permission_requested)) {
                        titleResource = R.string.title_contact_read_permission
                        positiveButton(R.string.button_contact_read_permission_requested_again) {
                            // Send the request again.
                            nonce.use()
                        }
                        negativeButton(android.R.string.cancel) {}
                    }.show()
                }
            }
            request.send()
        }

        private fun createAppSettingsIntent(activity: ContactLoaderActivity) = Intent().apply {
            action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
            data = Uri.fromParts("package", activity.packageName, null)
        }
    }

}