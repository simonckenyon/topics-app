package ie.koala.topics.auth

import android.app.Activity
import android.os.Bundle
import ie.koala.topics.R

import kotlinx.android.synthetic.main.activity_auth.*

class AuthActivity : Activity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_auth)
        actionBar?.setDisplayHomeAsUpEnabled(true)
    }

}
