package ie.koala.topics.feature.auth

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import com.google.firebase.auth.FirebaseAuth
import android.text.TextUtils
import android.view.View
import kotlinx.android.synthetic.main.activity_reset_password.*
import kotlinx.android.synthetic.main.content_reset_password.*
import org.jetbrains.anko.design.longSnackbar
import org.jetbrains.anko.design.snackbar

import ie.koala.topics.R

class ResetPasswordActivity : AppCompatActivity() {

    private var auth: FirebaseAuth? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_reset_password)

        setSupportActionBar(toolbar)
        toolbar.title = "Reset Password"

        auth = FirebaseAuth.getInstance()

        btn_reset_password.setOnClickListener { _ ->
            val email = input_email.text.toString().trim()

            if (TextUtils.isEmpty(email)) {
                snackbar(coordinator_layout_reset_password, "Enter your email address")
            } else {
                progress_bar.visibility = View.VISIBLE

                auth!!.sendPasswordResetEmail(email).addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        progress_bar.visibility = View.INVISIBLE
                        longSnackbar(coordinator_layout_reset_password, "Check your email to complete the password reset", "Dismiss") { _ ->
                            finish()
                        }
                        finish()
                    } else {
                        progress_bar.visibility = View.INVISIBLE
                        longSnackbar(coordinator_layout_reset_password, "The reset password email could not be sent", "Dismiss") { _ ->
                            finish()
                        }
                    }
                }
            }
        }
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }
}