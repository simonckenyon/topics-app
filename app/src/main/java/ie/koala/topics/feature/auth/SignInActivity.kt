package ie.koala.topics.feature.auth

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import ie.koala.topics.R
import kotlinx.android.synthetic.main.activity_sign_in.*
import org.slf4j.LoggerFactory

class SignInActivity : AppCompatActivity() {

    private var auth: FirebaseAuth? = null

    private val log = LoggerFactory.getLogger(SignInActivity::class.java)

    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_in)

        setSupportActionBar(toolbar)
        toolbar.title = "Sign In"

        auth = FirebaseAuth.getInstance()

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

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent) {
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

    private fun login() {
        log.debug("login:")

        if (!validate()) {
            onLoginFailed()
            return
        }

        btn_sign_in.isEnabled = false

        val email = input_email.text.toString()
        val password = input_password.text.toString()
        signIn(email, password)
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

    private fun onLoginSuccess() {
        btn_sign_in.isEnabled = true

        finish()
    }

    private fun onLoginFailed() {
        btn_sign_in.isEnabled = true

        Toast.makeText(baseContext, "Login failed", Toast.LENGTH_LONG).show()
    }

    private fun validate(): Boolean {
        var valid = true

        val email = input_email.text.toString()
        val password = input_password.text.toString()

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

        return valid
    }

    private fun signIn(email: String, password: String) {
        log.debug("signIn: $email")

        if (!validate()) {
            onLoginFailed()
            return
        }

        progress_bar.visibility = View.VISIBLE

        auth!!.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        log.debug( "signIn: success")

                        progress_bar.visibility = View.INVISIBLE
                        onLoginSuccess()
                    } else {
                        log.debug( "signIn: failed", task.exception)
                        Toast.makeText(applicationContext, "Authentication failed", Toast.LENGTH_LONG).show()

                        progress_bar.visibility = View.INVISIBLE
                        onLoginFailed()
                    }
                }
    }

    companion object {
        private const val REQUEST_SIGN_UP = 0
        private const val REQUEST_FORGOT_PASSWORD = 1
    }
}