package ie.koala.topics.feature.auth.fragment

import android.os.Bundle
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.navigation.fragment.findNavController
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import ie.koala.topics.R
import ie.koala.topics.contacts.ContactLoaderFragment
import ie.koala.topics.contacts.ContactReadPermission
import ie.koala.topics.ui.snackbar
import kotlinx.android.synthetic.main.fragment_reset_password.*
import org.slf4j.LoggerFactory

class ResetPasswordFragment: ContactLoaderFragment() {

    private val log = LoggerFactory.getLogger(SignInFragment::class.java)

    private var auth: FirebaseAuth? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_reset_password, null)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        auth = FirebaseAuth.getInstance()

        ContactReadPermission.get(this, coordinator_layout_reset_password)

        btn_reset_password.setOnClickListener { _ ->
            val email = input_email.text.toString().trim()

            if (TextUtils.isEmpty(email)) {
                coordinator_layout_reset_password.snackbar(R.string.message_email_is_empty)
            } else {
                progress_bar.visibility = View.VISIBLE

                auth!!.sendPasswordResetEmail(email).addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        progress_bar.visibility = View.INVISIBLE
                        //log.debug("onCreate: reset isSuccessful")
                        coordinator_layout_reset_password.snackbar(R.string.message_check_email_for_password_reset, duration = Snackbar.LENGTH_INDEFINITE, actionText = R.string.button_dismiss) { _ ->
                            //log.debug("onCreate: snackbar dismiss")
                            findNavController().popBackStack()
                        }
                    } else {
                        progress_bar.visibility = View.INVISIBLE
                        //log.debug("onCreate: reset !isSuccessful")
                        coordinator_layout_reset_password.snackbar(R.string.message_could_not_send_email_for_password_reset, duration = Snackbar.LENGTH_INDEFINITE, actionText = R.string.button_dismiss) { _ ->
                            //log.debug("onCreate: snackbar dismiss")
                            findNavController().popBackStack()
                        }
                    }
                }
            }
        }
    }


    /**
     * Create adapter to tell the AutoCompleteTextView what to show in its dropdown list.
     *
     * @param emailAddressCollection
     */
    override fun addEmailsToAutoComplete(emailAddressCollection: List<String>) {
        //log.debug("addEmailsToAutoComplete: email count=" + emailAddressCollection.size)
        activity?.let { nonNullActivity ->
            adapter = ArrayAdapter(nonNullActivity, android.R.layout.simple_dropdown_item_1line, emailAddressCollection)
            input_email.setAdapter(adapter)
        }
    }

}
