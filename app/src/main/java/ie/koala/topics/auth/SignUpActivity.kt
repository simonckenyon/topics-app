package ie.koala.topics.auth

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.ArrayAdapter
import com.google.firebase.auth.FirebaseAuth
import ie.koala.topics.R
import ie.koala.topics.contacts.ContactLoaderActivity
import ie.koala.topics.contacts.ContactReadPermission
import ie.koala.topics.ui.snackbar
import kotlinx.android.synthetic.main.activity_sign_up.*
import org.slf4j.LoggerFactory


class SignUpActivity : ContactLoaderActivity() {

    private var auth: FirebaseAuth? = null

    private val log = LoggerFactory.getLogger(SignUpActivity::class.java)

    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up)

        setSupportActionBar(toolbar)
        toolbar.title = "Sign Up"

        auth = FirebaseAuth.getInstance()

        ContactReadPermission.get(this, coordinator_layout_sign_up)

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

        btn_sign_up.setOnClickListener { signup() }

        btn_sign_in.setOnClickListener { signin() }
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        input_reEnterPassword.setOnEditorActionListener { _, actionId, _ ->
            when (actionId) {
                EditorInfo.IME_ACTION_DONE -> signin()
                else -> false
            }
        }
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

    /**
     * Finish the registration screen and return to the Login activity
     */
    private fun signin(): Boolean {

        log.debug("signin")

        val intent = Intent(applicationContext, SignInActivity::class.java)
        startActivity(intent)
        finish()
        overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out)
        return true
    }

    private fun signup(): Boolean {
        log.debug("signup")

        return if (!validate()) {
            btn_sign_up.isEnabled = true
            false
        } else {
            btn_sign_up.isEnabled = false
            val email = input_email.text.toString()
            val password = input_password.text.toString()
            createAccount(email, password)
        }
    }

    private fun validate(): Boolean {
        var valid = true

        val email = input_email.text.toString()
        val password = input_password.text.toString()
        val reEnterPassword = input_reEnterPassword.text.toString()

        if (email.isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            input_email.error = "Enter a valid email address"
            valid = false
        } else {
            input_email.error = null
        }

        if (password.isEmpty() || password.length < 4 || password.length > 10) {
            input_password.error = "Password must be between 4 and 10 alphanumeric characters"
            valid = false
        } else {
            input_password.error = null
        }

        if (reEnterPassword.isEmpty() || reEnterPassword.length < 4 || reEnterPassword.length > 10 || reEnterPassword != password) {
            input_reEnterPassword.error = "Passwords do not match"
            valid = false
        } else {
            input_reEnterPassword.error = null
        }

        return valid
    }

    private fun createAccount(email: String, password: String): Boolean {
        log.debug("createAccount: $email")

        return if (!validate()) {
            false
        } else {
            progress_bar.visibility = View.VISIBLE
            auth!!
                    .createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this) { task ->
                        progress_bar.visibility = View.INVISIBLE
                        btn_sign_up.isEnabled = true
                        if (task.isSuccessful) {
                            log.debug("createAccount: success")
                            setResult(Activity.RESULT_OK, null)
                            finish()
                        } else {
                            val message = task.exception?.message ?: "Authentication failed"
                            log.debug("createAccount: failed (${message})")
                            coordinator_layout_sign_up.snackbar(message)
                        }
                    }
            true
        }
    }
}