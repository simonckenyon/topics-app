package ie.koala.topics.app

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import com.google.firebase.auth.FirebaseAuth
import com.hendraanggrian.pikasso.picasso
import com.hendraanggrian.pikasso.transformations.circle
import fr.tkeunebr.gravatar.Gravatar
import ie.koala.topics.R
import ie.koala.topics.feature.animal.AnimalActivity
import ie.koala.topics.auth.SignInActivity
import ie.koala.topics.auth.SignUpActivity
import ie.koala.topics.feature.github.ui.GithubActivity
import ie.koala.topics.feature.stocks.StocksActivity
import ie.koala.topics.feature.topic.TopicListActivity
import ie.koala.topics.feature.user.UserActivity
import ie.koala.topics.preferences.PreferenceHelper.defaultPrefs
import ie.koala.topics.preferences.PreferenceKeys.NAV_MODE_NORMAL
import ie.koala.topics.preferences.PreferencesActivity
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.nav_header_main.view.*
import org.jetbrains.anko.startActivity
import org.slf4j.LoggerFactory


class MainActivity : AppCompatActivity() {

    enum class MenuState { APP, ACCOUNT_SWITCHER }

    private var navigationDrawerMenuState: MenuState = MenuState.APP

    private var auth: FirebaseAuth? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main)
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
        nav_view.setNavigationItemSelectedListener { menuItem ->
            // set item as selected to persist highlight
            menuItem.isChecked = true
            // close drawer when item is tapped
            drawer_layout.closeDrawer(GravityCompat.START)

            when (menuItem.itemId) {
                R.id.nav_topics -> {
                    startActivity<TopicListActivity>()
                    true
                }
                R.id.nav_user -> {
                    startActivity<UserActivity>()
                    true
                }
                R.id.nav_animals -> {
                    startActivity<AnimalActivity>()
                    true
                }
                R.id.nav_search_github -> {
                    startActivity<GithubActivity>()
                    true
                }
                R.id.nav_stocks -> {
                    startActivity<StocksActivity>()
                    true
                }
                R.id.nav_sign_in -> {
                    startActivity<SignInActivity>()
                    true
                }
                R.id.nav_sign_up -> {
                    startActivity<SignUpActivity>()
                    true
                }
                R.id.menu_sign_out -> {
                    auth!!.signOut()
                    updateNavigationMenu()
                    true
                }
                else -> false
            }
        }
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

        if (auth != null && auth?.currentUser != null) {
            // signed in
            val user = auth!!.currentUser
            log.debug("email=${user?.email}")
            val gravatarUrl = Gravatar.init().with(user?.email).defaultImage(4).size(Gravatar.MAX_IMAGE_SIZE_PIXEL).build()
            log.debug("gravatarUrl=$gravatarUrl")

            picasso.load(gravatarUrl).circle().into(headerView.avatar)

            emailAddress.text = user?.email

            if (navigationDrawerMenuState == MenuState.ACCOUNT_SWITCHER) {
                log.debug("updateNavigationMenu: set sign out menu visible")
                emailAddress.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_arrow_drop_up, 0)
                menu.setGroupVisible(R.id.menu_sign_in, false)
                menu.setGroupVisible(R.id.menu_sign_out, true)
                menu.setGroupVisible(R.id.menu_authenticated, false)
            } else {
                log.debug("updateNavigationMenu: set authenticated menu visible")
                emailAddress.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_arrow_drop_down, 0)
                menu.setGroupVisible(R.id.menu_sign_in, false)
                menu.setGroupVisible(R.id.menu_sign_out, false)
                menu.setGroupVisible(R.id.menu_authenticated, true)
            }
        } else {
            // not logged in
            log.debug("updateNavigationMenu: set sign in menu visible")
            emailAddress.text = ""
            emailAddress.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0)
            menu.setGroupVisible(R.id.menu_sign_in, true)
            menu.setGroupVisible(R.id.menu_sign_out, false)
            menu.setGroupVisible(R.id.menu_authenticated, false)
        }
    }

    private fun toggleNavigationMenu() {
        navigationDrawerMenuState = if (navigationDrawerMenuState == MenuState.APP) {
            MenuState.ACCOUNT_SWITCHER
        } else {
            MenuState.APP
        }
    }

    companion object {
        private val log = LoggerFactory.getLogger(MainActivity::class.java)
    }
}
