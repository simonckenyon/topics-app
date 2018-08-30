package ie.koala.topics.feature.welcome

import android.content.Intent
import android.os.Bundle
import android.support.design.widget.NavigationView
import android.support.v4.view.GravityCompat
import android.support.v7.app.ActionBarDrawerToggle
import android.support.v7.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import com.google.firebase.auth.FirebaseAuth
import com.hendraanggrian.pikasso.picasso
import com.hendraanggrian.pikasso.transformations.circle
import fr.tkeunebr.gravatar.Gravatar
import ie.koala.topics.R
import ie.koala.topics.feature.topic.TopicListActivity
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.app_bar_main.*
import kotlinx.android.synthetic.main.nav_header_main.view.*
import org.jetbrains.anko.design.longSnackbar
import org.slf4j.LoggerFactory

import ie.koala.topics.feature.auth.SignInActivity
import ie.koala.topics.feature.auth.SignUpActivity

class MainActivity : AppCompatActivity() {

    enum class MenuState { APP, ACCOUNT_SWITCHER }

    private var navigationDrawerMenuState: MenuState = MenuState.APP

    private var auth: FirebaseAuth? = null

    private val log = LoggerFactory.getLogger(MainActivity::class.java)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        auth = FirebaseAuth.getInstance()

        val navigationView = findViewById<View>(R.id.nav_view) as NavigationView
        val headerView = navigationView.getHeaderView(0)
        headerView.setOnClickListener {
            toggleNavigationMenu()
            updateNavigationMenu()
        }

        fab.setOnClickListener { view ->
            longSnackbar(view, "Replace with your own action", "Action") {}
        }

        val toggle = ActionBarDrawerToggle(
                this, drawer_layout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close)
        drawer_layout.addDrawerListener(toggle)
        toggle.syncState()

        updateNavigationMenu()
    }

    override fun onBackPressed() {
        if (drawer_layout.isDrawerOpen(GravityCompat.START)) {
            drawer_layout.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }

    override fun onResume() {
        super.onResume()

        updateNavigationMenu()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu
        // this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        when (item.itemId) {
            R.id.action_settings -> return true
            else -> return super.onOptionsItemSelected(item)
        }
    }

    override fun onSaveInstanceState(outState: Bundle?) {
        super.onSaveInstanceState(outState)

        outState?.putSerializable("navigationDrawerMenuState", navigationDrawerMenuState)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle?) {
        super.onRestoreInstanceState(savedInstanceState)

        navigationDrawerMenuState = savedInstanceState?.getSerializable("navigationDrawerMenuState") as MenuState
    }

    private fun updateNavigationMenu() {
        val navigationView = findViewById<View>(R.id.nav_view) as NavigationView
        val menu = navigationView.menu
        val headerView = navigationView.getHeaderView(0)
        val emailAddress = headerView.email_address

        menu.clear()

        if (auth != null && auth?.currentUser != null) {
            // signed in
            val user = auth!!.currentUser
            val gravatarUrl = Gravatar.init().with(user!!.email).force404().size(Gravatar.MAX_IMAGE_SIZE_PIXEL).build()
            log.debug("gravatarUrl=$gravatarUrl")

            val avatarImage = headerView.findViewById(R.id.avatar) as ImageView
            picasso.load(gravatarUrl).circle().into(avatarImage)

            emailAddress.text = user.email

            if (navigationDrawerMenuState == MenuState.ACCOUNT_SWITCHER) {
                emailAddress.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_arrow_drop_up, 0)

                val signOut: MenuItem = menu.add(Menu.NONE, Menu.NONE, Menu.FIRST, "Sign Out")
                signOut.setIcon(R.drawable.ic_menu_sign_out)
                signOut.setOnMenuItemClickListener {
                    drawer_layout.closeDrawer(GravityCompat.START)
                    auth!!.signOut()
                    updateNavigationMenu()
                    true
                }
            } else {
                emailAddress.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_arrow_drop_down, 0)

                val topicList = menu.add(Menu.NONE, Menu.NONE, Menu.FIRST, "Topic List")
                topicList.setIcon(R.drawable.ic_menu_list)
                topicList.setOnMenuItemClickListener {
                    drawer_layout.closeDrawer(GravityCompat.START)
                    val intent = Intent(this, TopicListActivity::class.java)
                    startActivity(intent)
                    true
                }
            }
        } else {
            // not logged in

            emailAddress.text = ""
            emailAddress.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0)

            val signIn: MenuItem = menu.add(Menu.NONE, Menu.NONE, Menu.FIRST, "Sign In")
            signIn.setIcon(R.drawable.ic_menu_sign_in)
            signIn.setOnMenuItemClickListener {
                drawer_layout.closeDrawer(GravityCompat.START)
                val intent = Intent(this, SignInActivity::class.java)
                startActivity(intent)
                true
            }

            val signUp: MenuItem = menu.add(Menu.NONE, Menu.NONE, Menu.FIRST, "Sign Up")
            signUp.setIcon(R.drawable.ic_menu_sign_up)
            signUp.setOnMenuItemClickListener {
                drawer_layout.closeDrawer(GravityCompat.START)
                val intent = Intent(this, SignUpActivity::class.java)
                startActivity(intent)
                true
            }
        }
    }

    private fun toggleNavigationMenu() {
        if (navigationDrawerMenuState == MenuState.APP) {
            navigationDrawerMenuState = MenuState.ACCOUNT_SWITCHER
        } else {
            navigationDrawerMenuState = MenuState.APP
        }
    }
}
