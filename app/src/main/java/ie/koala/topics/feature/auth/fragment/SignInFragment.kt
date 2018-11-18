package ie.koala.topics.feature.auth.fragment

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.navigation.fragment.findNavController
import com.google.firebase.auth.FirebaseAuth
import ie.koala.topics.R
import ie.koala.topics.contacts.ContactLoaderFragment
import ie.koala.topics.contacts.ContactReadPermission
import ie.koala.topics.ui.snackbar
import kotlinx.android.synthetic.main.fragment_sign_in.*
import org.slf4j.LoggerFactory

class SignInFragment : ContactLoaderFragment() {

    private val log = LoggerFactory.getLogger(SignInFragment::class.java)

    private var auth: FirebaseAuth? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_sign_in, null)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        auth = FirebaseAuth.getInstance()

        ContactReadPermission.get(this, coordinator_layout_sign_in)

        input_email.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                //log.debug("afterTextChanged s=${s}")
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
            //val intent = Intent(applicationContext, SignUpFragment::class.java)
            //startActivityForResult(intent, REQUEST_SIGN_UP)
            findNavController().popBackStack()
        }

        btn_forgot_password.setOnClickListener {
            //val intent = Intent(applicationContext, ResetPasswordFragment::class.java)
            //startActivityForResult(intent, REQUEST_FORGOT_PASSWORD)
            findNavController().popBackStack()
        }
    }

    /**
     * Create adapter to tell the AutoCompleteTextView what to show in its dropdown list.
     *
     * @param emailAddressCollection
     */
    override fun addEmailsToAutoComplete(emailAddressCollection: List<String>) {
        //log.debug("addEmailsToAutoComplete: email count=" + emailAddressCollection.size)

        context?.let { nonNullContext ->
            adapter = ArrayAdapter(nonNullContext, android.R.layout.simple_dropdown_item_1line, emailAddressCollection)
            input_email.setAdapter(adapter)
        }
    }

    private fun login() {
        //log.debug("login:")

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
        //log.debug("signIn: $email")

        if (!validate()) {
            btn_sign_in.isEnabled = true
            coordinator_layout_sign_in.snackbar(R.string.message_login_failed)
            return
        }

        progress_bar.visibility = View.VISIBLE

        activity?.let { nonNullActivity ->
            auth!!
                    .signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(nonNullActivity) { task ->
                        progress_bar.visibility = View.INVISIBLE
                        if (task.isSuccessful) {
                            //log.debug("signIn: success")
                            btn_sign_in.isEnabled = true
                            findNavController().popBackStack()
                        } else {
                            //log.debug("signIn: failed", task.exception)
                            btn_sign_in.isEnabled = true
                            coordinator_layout_sign_in.snackbar(R.string.message_login_failed)
                        }
                    }
        }
    }

    companion object {
        private const val REQUEST_SIGN_UP = 0
        private const val REQUEST_FORGOT_PASSWORD = 1
    }
}