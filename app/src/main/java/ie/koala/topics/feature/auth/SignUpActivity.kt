package ie.koala.topics.feature.auth

import android.app.Activity
import android.content.Intent
import android.database.Cursor
import android.os.Bundle
import android.provider.ContactsContract
import android.support.v4.app.LoaderManager
import android.support.v4.content.CursorLoader
import android.support.v4.content.Loader
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.ArrayAdapter
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import ie.koala.topics.R
import kotlinx.android.synthetic.main.activity_sign_up.*
import org.slf4j.LoggerFactory
import java.util.*

class SignUpActivity : AppCompatActivity(), LoaderManager.LoaderCallbacks<Cursor> {

    private var auth: FirebaseAuth? = null

    private val log = LoggerFactory.getLogger(SignUpActivity::class.java)

    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up)

        setSupportActionBar(toolbar)
        toolbar.title = "Sign Up"

        auth = FirebaseAuth.getInstance()

        btn_sign_up.setOnClickListener { signup() }

        btn_sign_in.setOnClickListener { signin() }
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        input_reEnterPassword.setOnEditorActionListener { _, actionId, _ ->
            when (actionId) {
            EditorInfo.IME_ACTION_DONE -> signin()
            else ->false
            }
        }
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
            onSignupFailed()
            false
        } else {
            btn_sign_up.isEnabled = false
            val email = input_email.text.toString()
            val password = input_password.text.toString()
            createAccount(email, password)
            true
        }
    }

    private fun onSignupSuccess() {
        btn_sign_up.isEnabled = true

        setResult(Activity.RESULT_OK, null)
        finish()
    }

    private fun onSignupFailed() {
        btn_sign_up.isEnabled = true

        Toast.makeText(baseContext, "Login failed", Toast.LENGTH_LONG).show()
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

    private fun createAccount(email: String, password: String) {
        log.debug("createAccount: $email")

        if (!validate()) {
            return
        }

        progress_bar.visibility = View.VISIBLE

        auth!!.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        log.debug("createAccount: success")

                        progress_bar.visibility = View.INVISIBLE
                        onSignupSuccess()
                    } else {
                        log.debug("createAccount: failed", task.exception)
                        Toast.makeText(applicationContext, "Authentication failed", Toast.LENGTH_LONG).show()

                        progress_bar.visibility = View.INVISIBLE
                        onSignupFailed()
                    }
                }
    }

    private interface ProfileQuery {
        companion object {
            val PROJECTION = arrayOf(ContactsContract.CommonDataKinds.Email.ADDRESS, ContactsContract.CommonDataKinds.Email.IS_PRIMARY)

            const val ADDRESS = 0
        }
    }

    /**
     * see https://stackoverflow.com/a/42001556
     *
     * @param i
     * @param bundle
     * @return
     */
    override fun onCreateLoader(i: Int, bundle: Bundle?): Loader<Cursor> {
        return CursorLoader(this,
                // Retrieve data rows for the device user's 'profile' contact.
                ContactsContract.Data.CONTENT_URI, ProfileQuery.PROJECTION,

                // Select only email addresses.
                ContactsContract.Contacts.Data.MIMETYPE + " = ?", arrayOf(ContactsContract.CommonDataKinds.Email
                .CONTENT_ITEM_TYPE),

                // Show primary email addresses first. Note that there won't be
                // a primary email address if the user hasn't specified one.
                ContactsContract.Contacts.Data.IS_PRIMARY + " DESC")
    }

    override fun onLoadFinished(loader: Loader<Cursor>, cursor: Cursor) {
        log.debug("onLoadFinished")

        val emails = ArrayList<String>()
        cursor.moveToFirst()
        while (!cursor.isAfterLast) {
            emails.add(cursor.getString(ProfileQuery.ADDRESS))
            cursor.moveToNext()
        }

        addEmailsToAutoComplete(emails)
    }

    override fun onLoaderReset(loader: Loader<Cursor>) {
        log.debug("onLoaderReset")
    }

    /**
     * Create adapter to tell the AutoCompleteTextView what to show in its dropdown list.
     *
     * @param emailAddressCollection
     */
    private fun addEmailsToAutoComplete(emailAddressCollection: List<String>) {
        log.debug("addEmailsToAutoComplete: email count=" + emailAddressCollection.size)

        val adapter = ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, emailAddressCollection)

        input_email.setAdapter(adapter)
    }

}