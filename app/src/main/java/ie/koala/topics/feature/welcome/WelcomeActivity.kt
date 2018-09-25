package ie.koala.topics.feature.welcome

import android.content.Intent
import android.os.Bundle
import androidx.core.view.GravityCompat
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import com.google.firebase.auth.FirebaseAuth
import com.hendraanggrian.pikasso.picasso
import com.hendraanggrian.pikasso.transformations.circle
import fr.tkeunebr.gravatar.Gravatar
import ie.koala.topics.R
import ie.koala.topics.framework.preferences.PreferenceKeys.NAV_MODE_NORMAL
import ie.koala.topics.framework.preferences.PreferenceHelper.defaultPrefs
import ie.koala.topics.app.TopicsApplication
import ie.koala.topics.feature.auth.SignInActivity
import ie.koala.topics.feature.auth.SignUpActivity
import ie.koala.topics.feature.user.UserActivity
import ie.koala.topics.framework.preferences.PreferencesActivity
import ie.koala.topics.feature.topic.TopicListActivity
import kotlinx.android.synthetic.main.activity_welcome.*
import kotlinx.android.synthetic.main.nav_header_main.view.*
import org.slf4j.LoggerFactory

class WelcomeActivity : AppCompatActivity() {

    enum class MenuState { APP, ACCOUNT_SWITCHER }

    private var navigationDrawerMenuState: MenuState = MenuState.APP

    private var auth: FirebaseAuth? = null

    private val log = LoggerFactory.getLogger(WelcomeActivity::class.java)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_welcome)
        setSupportActionBar(toolbar)

        auth = FirebaseAuth.getInstance()

        val headerView = nav_view.getHeaderView(0)
        headerView.setOnClickListener {
            toggleNavigationMenu()
            updateNavigationMenu()
        }

        version_name.text = TopicsApplication.versionName
        version_code.text = TopicsApplication.versionCode
        version_build_timestamp.text = TopicsApplication.versionBuildTimestamp
        version_git_hash.text = TopicsApplication.versionGitHash

        val toggle = ActionBarDrawerToggle(
                this, drawer_layout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close)
        drawer_layout.addDrawerListener(toggle)
        toggle.syncState()

        updateNavigationMenu()
    }

    override fun onBackPressed() {
        val prefs = defaultPrefs(this)
        val navModeNormal: Boolean = prefs.getBoolean(NAV_MODE_NORMAL, false)
        when (navModeNormal) {
            true ->
                if (drawer_layout.isDrawerOpen(GravityCompat.START)) {
                    drawer_layout.closeDrawer(GravityCompat.START)
                } else {
                    super.onBackPressed()
                }
            else ->
                if (!drawer_layout.isDrawerOpen(GravityCompat.START)) {
                    drawer_layout.openDrawer(GravityCompat.START)
                } else {
                    super.onBackPressed()
                }
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

    override fun onOptionsItemSelected(item: MenuItem) = when (item.itemId) {
        R.id.action_settings -> consume {
            val intent = Intent(this, PreferencesActivity::class.java)
            startActivity(intent)
        }
        else -> super.onOptionsItemSelected(item)
    }

    override fun onSaveInstanceState(outState: Bundle?) {
        super.onSaveInstanceState(outState)

        outState?.putSerializable("navigationDrawerMenuState", navigationDrawerMenuState)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle?) {
        super.onRestoreInstanceState(savedInstanceState)

        navigationDrawerMenuState = savedInstanceState?.getSerializable("navigationDrawerMenuState") as MenuState
    }

    private inline fun consume(f: () -> Unit): Boolean {
        f()
        return true
    }

    private fun updateNavigationMenu() {
        val menu = nav_view.menu
        val headerView = nav_view.getHeaderView(0)
        val emailAddress = headerView.email_address

        menu.clear()

        if (auth != null && auth?.currentUser != null) {
            // signed in
            val user = auth!!.currentUser
            log.debug("email=${user?.email}")
            val gravatarUrl = Gravatar.init().with(user?.email).defaultImage(4).size(Gravatar.MAX_IMAGE_SIZE_PIXEL).build()
            log.debug("gravatarUrl=$gravatarUrl")

            picasso.load(gravatarUrl).circle().into(headerView.avatar)

            emailAddress.text = user?.email

            if (navigationDrawerMenuState == MenuState.ACCOUNT_SWITCHER) {
                emailAddress.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_arrow_drop_up, 0)

                val signOut: MenuItem = menu.add(Menu.NONE, Menu.NONE, Menu.FIRST, R.string.menu_sign_out)
                signOut.setIcon(R.drawable.ic_menu_sign_out)
                signOut.setOnMenuItemClickListener {
                    drawer_layout.closeDrawer(GravityCompat.START)
                    auth!!.signOut()
                    updateNavigationMenu()
                    true
                }
            } else {
                emailAddress.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_arrow_drop_down, 0)

                val topicList: MenuItem = menu.add(Menu.NONE, Menu.NONE, Menu.FIRST, R.string.menu_topics)
                topicList.setIcon(R.drawable.ic_menu_list)
                topicList.setOnMenuItemClickListener {
                    drawer_layout.closeDrawer(GravityCompat.START)
                    val intent = Intent(this, TopicListActivity::class.java)
                    startActivity(intent)
                    true
                }
                val user: MenuItem = menu.add(Menu.NONE, Menu.NONE, Menu.FIRST, R.string.menu_users)
                user.setIcon(R.drawable.ic_menu_user)
                user.setOnMenuItemClickListener {
                    drawer_layout.closeDrawer(GravityCompat.START)
                    val intent = Intent(this, UserActivity::class.java)
                    startActivity(intent)
                    true
                }
            }
        } else {
            // not logged in

            emailAddress.text = ""
            emailAddress.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0)

            val signIn: MenuItem = menu.add(Menu.NONE, Menu.NONE, Menu.FIRST, R.string.menu_sign_in)
            signIn.setIcon(R.drawable.ic_menu_sign_in)
            signIn.setOnMenuItemClickListener {
                drawer_layout.closeDrawer(GravityCompat.START)
                val intent = Intent(this, SignInActivity::class.java)
                startActivity(intent)
                true
            }

            val signUp: MenuItem = menu.add(Menu.NONE, Menu.NONE, Menu.FIRST, R.string.menu_sign_up)
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
        navigationDrawerMenuState = if (navigationDrawerMenuState == MenuState.APP) {
            MenuState.ACCOUNT_SWITCHER
        } else {
            MenuState.APP
        }
    }
}
