package ie.koala.topics.feature.auth

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_sign_up.*
import kotlinx.android.synthetic.main.content_sign_up.*
import org.slf4j.LoggerFactory

import ie.koala.topics.R

class SignUpActivity : AppCompatActivity() {

    private var auth: FirebaseAuth? = null

    private val log = LoggerFactory.getLogger(SignUpActivity::class.java)

    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up)

        setSupportActionBar(toolbar)
        toolbar.title = "Sign Up"

        auth = FirebaseAuth.getInstance()

        btn_sign_up.setOnClickListener { signup() }

        btn_sign_in.setOnClickListener {
            // Finish the registration screen and return to the Login activity
            val intent = Intent(applicationContext, SignInActivity::class.java)
            startActivity(intent)
            finish()
            overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out)
        }
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    private fun signup() {
        log.debug("Signup")

        if (!validate()) {
            onSignupFailed()
            return
        }

        btn_sign_up.isEnabled = false

        val email = input_email.text.toString()
        val password = input_password.text.toString()
        createAccount(email, password)
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
}