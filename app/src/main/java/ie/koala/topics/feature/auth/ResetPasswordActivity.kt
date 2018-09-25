package ie.koala.topics.feature.auth

import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.widget.ArrayAdapter
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import ie.koala.topics.R
import ie.koala.topics.framework.ui.snackbar
import kotlinx.android.synthetic.main.activity_reset_password.*
import org.slf4j.LoggerFactory

class ResetPasswordActivity : LoaderActivity() {

    private val log = LoggerFactory.getLogger(SignInActivity::class.java)

    private var auth: FirebaseAuth? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_reset_password)

        setSupportActionBar(toolbar)
        toolbar.title = "Reset Password"

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
                        log.debug("onCreate: reset isSuccessful")
                        coordinator_layout_reset_password.snackbar(R.string.message_check_email_for_password_reset, duration = Snackbar.LENGTH_INDEFINITE, actionText = R.string.button_dismiss) { _ ->
                            log.debug("onCreate: snackbar dismiss")
                            finish()
                        }
                    } else {
                        progress_bar.visibility = View.INVISIBLE
                        log.debug("onCreate: reset !isSuccessful")
                        coordinator_layout_reset_password.snackbar(R.string.message_could_not_send_email_for_password_reset, duration = Snackbar.LENGTH_INDEFINITE, actionText = R.string.button_dismiss) { _ ->
                            log.debug("onCreate: snackbar dismiss")
                            finish()
                        }
                    }
                }
            }
        }
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }


    /**
     * Create adapter to tell the AutoCompleteTextView what to show in its dropdown list.
     *
     * @param emailAddressCollection
     */
    override fun addEmailsToAutoComplete(emailAddressCollection: List<String>) {
        log.debug("addEmailsToAutoComplete: email count=" + emailAddressCollection.size)
        adapter = ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, emailAddressCollection)
        input_email.setAdapter(adapter)
    }

}
