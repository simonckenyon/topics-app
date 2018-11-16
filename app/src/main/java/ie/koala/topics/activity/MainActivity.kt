package ie.koala.topics.activity

import android.app.SearchManager
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.PersistableBundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.navigation.Navigation.findNavController
import androidx.navigation.ui.NavigationUI.*
import com.google.firebase.auth.FirebaseAuth
import com.hendraanggrian.pikasso.picasso
import com.hendraanggrian.pikasso.transformations.circle
import fr.tkeunebr.gravatar.Gravatar
import ie.koala.topics.R
import ie.koala.topics.auth.SignInActivity
import ie.koala.topics.auth.SignUpActivity
import ie.koala.topics.feature.user.UserActivity
import ie.koala.topics.fragment.MainFragmentDirections
import ie.koala.topics.model.Wiki
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

        val toggle = ActionBarDrawerToggle(
                this, drawer_layout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close)
        drawer_layout.addDrawerListener(toggle)
        toggle.syncState()

        log.debug("onCreate:")
        Log.d("MainActivity", "in onCreate")
        updateNavigationMenu()
        setupNavigation()
    }

//    override fun onSupportNavigateUp(): Boolean {
//        return navigateUp(findNavController(this, R.id.container), drawer_layout)
//    }

    override fun onResume() {
        super.onResume()

        updateNavigationMenu()
    }

    /*
 * begin DUMB Navigation Component hack
 *
 * This fixes an IllegalArgumentException that can sometimes be thrown from within the
 * Navigation Architecture Component when you try to navigate after the Fragment has had its
 * state restored. It occurs because the navController's currentDestination field is null,
 * which stores where we currently are in the navigation graph. Because it's null, the
 * Navigation Component can't figure out our current position in relation to where we're
 * trying to navigate to, causing the exception to be thrown.
 *
 * This fix gives the navController a little nudge by gently setting it to where we currently
 * are in the navigation graph.
 *
 * This fix is verified as both working AND necessary as of Navigation Components version
 * 1.0.0-alpha07.
 *
 * There's a tiny bit more information at this thread, but it's pretty limited:
 * https://stackoverflow.com/questions/52101617/navigation-destination-unknown-to-this-navcontroller-after-an-activity-result
 */
    private var checkCurrentDestination = false

    override fun onStart() {
        super.onStart()
        log.debug("onStart:")

        val navController = findNavController(this, R.id.container)

        if (checkCurrentDestination && navController.currentDestination == null) {
            log.debug("onStart: currentDestination is null")
            navController.navigate(navController.graph.startDestination)
        }

        checkCurrentDestination = false
    }

    override fun onStop() {
        super.onStop()
        log.debug("onStop:")
        checkCurrentDestination = true
    }
    /*
     * end DUMB Navigation Component hack
     */

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu
        // this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_main, menu)
        // Associate searchable configuration with the SearchView
        val searchManager = getSystemService(Context.SEARCH_SERVICE) as SearchManager
        (menu.findItem(R.id.search).actionView as SearchView).apply {
            setSearchableInfo(searchManager.getSearchableInfo(componentName))
            isSubmitButtonEnabled = true
        }
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem) = when (item.itemId) {
        R.id.action_settings -> consume {
            val intent = Intent(this, PreferencesActivity::class.java)
            startActivity(intent)
        }
        else -> super.onOptionsItemSelected(item)
    }

    override fun onSaveInstanceState(outState: Bundle?, outPersistentState: PersistableBundle?) {
        super.onSaveInstanceState(outState, outPersistentState)
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

    private fun setupNavigation() {
        val navController = findNavController(this, R.id.container)

        // Update action bar to reflect navigation
        setupActionBarWithNavController(this, navController, drawer_layout)

        // Tie nav graph to items in nav drawer
        //setupWithNavController(nav_view, navController)

        // Handle nav drawer item clicks
        log.debug("setupNavigation: register listener")
        nav_view.setNavigationItemSelectedListener { menuItem ->
            log.debug("onNavigationItemSelected: menuItem=$menuItem")
            // set item as selected to persist highlight
            //menuItem.isChecked = true
            // close menu_drawer when item is tapped
            drawer_layout.closeDrawers()

            when (menuItem.itemId) {
                R.id.nav_topics -> {
                    val action = MainFragmentDirections.actionTopicList()
                    findNavController(this, R.id.container).navigate(action)
                    true
                }
                R.id.nav_user -> {
                    startActivity<UserActivity>()
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
                R.id.nav_settings -> {
                    startActivity<PreferencesActivity>()
                    true
                }
                R.id.nav_copyright -> {
                    val wiki = Wiki("Copyright Statement", "copyright")
                    val action = MainFragmentDirections.actionWiki(wiki)
                    findNavController(this, R.id.container).navigate(action)
                    true
                }
                R.id.nav_about -> {
                    val wiki = Wiki("About this app", "about")
                    val action = MainFragmentDirections.actionWiki(wiki)
                    findNavController(this, R.id.container).navigate(action)
                    true
                }
                else -> false
            }
        }
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
