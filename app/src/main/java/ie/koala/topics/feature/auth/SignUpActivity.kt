package ie.koala.topics.feature.auth

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.database.Cursor
import android.os.Bundle
import android.provider.ContactsContract
import android.support.v4.app.LoaderManager
import android.support.v4.content.CursorLoader
import android.support.v4.content.Loader
import android.support.v7.app.AppCompatActivity
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.ArrayAdapter
import com.google.firebase.auth.FirebaseAuth
import ie.koala.topics.R
import kotlinx.android.synthetic.main.activity_sign_up.*
import org.jetbrains.anko.design.snackbar
import org.slf4j.LoggerFactory
import java.util.*
import android.Manifest.permission
import android.Manifest.permission.WRITE_CALENDAR
import android.content.pm.PackageManager
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat


class SignUpActivity : AppCompatActivity(), LoaderManager.LoaderCallbacks<Cursor> {

    private var auth: FirebaseAuth? = null
    private var adapter: ArrayAdapter<String>? =  null

    private val log = LoggerFactory.getLogger(SignUpActivity::class.java)

    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up)

        setSupportActionBar(toolbar)
        toolbar.title = "Sign Up"

        auth = FirebaseAuth.getInstance()

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
     * Dispatch onResume() to fragments.  Note that for better inter-operation
     * with older versions of the platform, at the point of this call the
     * fragments attached to the activity are *not* resumed.  This means
     * that in some cases the previous state may still be saved, not allowing
     * fragment transactions that modify the state.  To correctly interact
     * with fragments in their proper state, you should instead override
     * [.onResumeFragments].
     */
    override fun onResume() {
        log.debug("onResume")
        super.onResume()
        val permissionCheck = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_CONTACTS)
        if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
            // User may have declined earlier, ask Android if we should show him a reason
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.READ_CONTACTS)) {
                // show an explanation to the user
                // Good practise: don't block thread after the user sees the explanation, try again to request the permission.
            } else {
                // request the permission.
                // MY_PERMISSIONS_REQUEST_READ_CONTACTS is a integer constant
                ActivityCompat.requestPermissions(this, arrayOf<String>(Manifest.permission.READ_CONTACTS), MY_PERMISSIONS_REQUEST_READ_CONTACTS)
                // The callback method gets the result of the request.
            }
        } else {
            // got permission use it
            startLoader()
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int,
                                            permissions: Array<String>, grantResults: IntArray) {
        when (requestCode) {
            MY_PERMISSIONS_REQUEST_READ_CONTACTS -> {
                // If request is cancelled, the result arrays are empty.
                if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.
                    startLoader()
                } else {
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return
            }

            // Add other 'when' lines to check for other
            // permissions this app might request.
            else -> {
                // Ignore all other requests.
            }
        }
    }

    private fun startLoader() {
        getSupportLoaderManager().initLoader(LOADER_ID, null, this);
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
                            log.debug("createAccount: failed", message)
                            snackbar(coordinator_layout_sign_up, message)
                        }
                    }
            true
        }
    }

    companion object {
        private const val LOADER_ID = 1

        private const val MY_PERMISSIONS_REQUEST_READ_CONTACTS = 100
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
        log.debug("onCreateLoader")

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

        adapter = ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, emailAddressCollection)

        input_email.setAdapter(adapter)
    }

}