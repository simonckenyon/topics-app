package ie.koala.topics.feature.auth

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.ArrayAdapter
import com.google.firebase.auth.FirebaseAuth
import ie.koala.topics.R
import ie.koala.topics.framework.ui.snackbar
import kotlinx.android.synthetic.main.activity_sign_in.*
import org.slf4j.LoggerFactory

class SignInActivity : LoaderActivity() {

    private val log = LoggerFactory.getLogger(SignInActivity::class.java)

    private var auth: FirebaseAuth? = null

    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_in)

        setSupportActionBar(toolbar)
        toolbar.title = "Sign In"

        auth = FirebaseAuth.getInstance()

        ContactReadPermission.get(this, coordinator_layout_sign_in)

        input_email.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                log.debug("afterTextChanged s=${s}")
                adapter?.run {
                    notifyDataSetChanged()
                }
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }
        })

        btn_sign_in.setOnClickListener { login() }

        btn_sign_up.setOnClickListener {
            val intent = Intent(applicationContext, SignUpActivity::class.java)
            startActivityForResult(intent, REQUEST_SIGN_UP)
            finish()
            overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out)
        }

        btn_forgot_password.setOnClickListener {
            val intent = Intent(applicationContext, ResetPasswordActivity::class.java)
            startActivityForResult(intent, REQUEST_FORGOT_PASSWORD)
            finish()
            overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out)
        }
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == REQUEST_SIGN_UP) {
            if (resultCode == Activity.RESULT_OK) {
                // a successful signup logs the user in
                // so no need to do anything more
                this.finish()
            }
        }
    }

    override fun onBackPressed() {
        // Disable going back to the WelcomeActivity
        moveTaskToBack(true)
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

    private fun login() {
        log.debug("login:")

        if (!validate()) {
            btn_sign_in.isEnabled = true
            coordinator_layout_sign_in.snackbar(R.string.message_login_failed)
            return
        } else {
            btn_sign_in.isEnabled = false
            val email = input_email.text.toString()
            val password = input_password.text.toString()
            signIn(email, password)
        }
    }

//    // save the user's profile into Firebase so we can list users,
//    // use them in Security and Firebase Rules, and show profiles
//    fun writeUserData(userId, name, email, imageUrl) {
//        firebase.database().ref('users/' + userId).set({
//            username: name,
//            email: email
//            //some more user data
//        });
//    }

    private fun validate(): Boolean {
        var valid = true

        val email = input_email.text.toString()
        val password = input_password.text.toString()

        if (email.isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            input_email.error = getString(R.string.message_enter_valid_email_address)
            valid = false
        } else {
            input_email.error = null
        }

        if (password.isEmpty() || password.length < 4 || password.length > 10) {
            input_password.error = getString(R.string.message_password_length)
            valid = false
        } else {
            input_password.error = null
        }

        return valid
    }

    private fun signIn(email: String, password: String) {
        log.debug("signIn: $email")

        if (!validate()) {
            btn_sign_in.isEnabled = true
            coordinator_layout_sign_in.snackbar(R.string.message_login_failed)
            return
        }

        progress_bar.visibility = View.VISIBLE

        auth!!.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this) { task ->
                    progress_bar.visibility = View.INVISIBLE
                    if (task.isSuccessful) {
                        log.debug( "signIn: success")
                        btn_sign_in.isEnabled = true
                        finish()
                    } else {
                        log.debug( "signIn: failed", task.exception)
                        btn_sign_in.isEnabled = true
                        coordinator_layout_sign_in.snackbar(R.string.message_login_failed)
                    }
                }
    }

    companion object {
        private const val REQUEST_SIGN_UP = 0
        private const val REQUEST_FORGOT_PASSWORD = 1
    }
}