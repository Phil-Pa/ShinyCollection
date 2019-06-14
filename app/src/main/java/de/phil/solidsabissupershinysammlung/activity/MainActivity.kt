package de.phil.solidsabissupershinysammlung.activity

import android.content.Intent
import android.databinding.DataBindingUtil
import android.os.Bundle
import android.support.design.widget.NavigationView
import android.support.design.widget.TabLayout
import android.support.v4.view.GravityCompat
import android.support.v4.view.ViewPager
import android.support.v4.widget.DrawerLayout
import android.support.v7.app.ActionBarDrawerToggle
import android.support.v7.app.AppCompatActivity
import android.support.v7.view.ActionMode
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.TextView
import android.widget.Toast
import de.phil.solidsabissupershinysammlung.R
import de.phil.solidsabissupershinysammlung.adapter.SectionsPagerAdapter
import de.phil.solidsabissupershinysammlung.core.App
import de.phil.solidsabissupershinysammlung.databinding.ActivityMainBinding
import de.phil.solidsabissupershinysammlung.model.PokemonData
import de.phil.solidsabissupershinysammlung.presenter.MainPresenter
import de.phil.solidsabissupershinysammlung.view.MainView
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity(), MainView {
    override fun getCurrentTabIndex(): Int {
        return view_pager.currentItem
    }

    override fun updateShinyStatistics() {
        textViewTotalShinys.text = ("Anzahl Shinys: ${presenter.getTotalNumberOfShinys()}")
        textViewTotalEggs.text = ("Eier gesamt: ${presenter.getTotalEggsCount()}")
        textViewAverageEggs.text = ("Eier durchschnittlich: ${presenter.getAverageEggsCount()}")
    }

    override fun showMessage(message: String) {
        Toast.makeText(applicationContext, message, Toast.LENGTH_LONG).show()
    }

    override fun startAddNewPokemonActivity() {
        val tabIndex = getCurrentTabIndex()
        if (tabIndex < 0 || tabIndex > 3) {
            Log.e(TAG, "tabIndex out of range")
            throw IllegalStateException()
        }

        val intent = Intent(applicationContext, AddNewPokemonActivity::class.java)
        intent.putExtra("tabIndex", tabIndex)
        startActivity(intent)
    }

    override fun onListEntryClick(data: PokemonData) {
        showMessage(data.toString())
    }

    override fun onListEntryLongClick(data: PokemonData) {

        startSupportActionMode(object : ActionMode.Callback {
            override fun onActionItemClicked(mode: ActionMode?, item: MenuItem?): Boolean {
                when (item?.itemId) {
                    R.id.delete_entry -> {
                        presenter.deletePokemonFromDatabase(data)
                        mode?.finish()

                    }
                }
                return true
            }

            override fun onCreateActionMode(mode: ActionMode?, menu: Menu?): Boolean {
                mode?.menuInflater?.inflate(R.menu.menu_actions, menu)
                return true
            }

            override fun onPrepareActionMode(mode: ActionMode?, menu: Menu?): Boolean {
                return false
            }

            override fun onDestroyActionMode(mode: ActionMode?) {

            }

        })
    }

    private val presenter: MainPresenter = MainPresenter(this)
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var navigationView: NavigationView
    private lateinit var textViewTotalEggs: TextView
    private lateinit var textViewAverageEggs: TextView
    private lateinit var textViewTotalShinys: TextView

    companion object {
        private const val TAG = "MainActivity"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.i(TAG, "App started")
        App.init(applicationContext)
        App.mainView = this
        val binding: ActivityMainBinding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        binding.presenter = presenter
        setSupportActionBar(toolbar)
        initTabs()
        initNavigationDrawer()
        initNavigationViewViews()
        updateShinyStatistics()
    }

    private fun initTabs() {
        // init tab view
        val sectionsPagerAdapter = SectionsPagerAdapter(supportFragmentManager)
        val viewPager: ViewPager = findViewById(R.id.view_pager)
        viewPager.adapter = sectionsPagerAdapter
        viewPager.offscreenPageLimit = App.NUM_TAB_VIEWS
        val tabs: TabLayout = findViewById(R.id.tabs)
        tabs.setupWithViewPager(viewPager)
    }

    private fun initNavigationDrawer() {
        // init navigation drawer
        drawerLayout = findViewById(R.id.drawer_layout)
        navigationView = findViewById(R.id.nav_view)
        val toggle = ActionBarDrawerToggle(
            this, drawerLayout, toolbar, R.string.app_name, R.string.dummy_text
        )
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()

        navigationView.setNavigationItemSelectedListener {
            when (it.itemId) {
                R.id.settings -> {
                    // TODO start settings
                }
            }
            findViewById<DrawerLayout>(R.id.drawer_layout).closeDrawer(GravityCompat.START)
            true
        }
    }

    private fun initNavigationViewViews() {
        val headerView = navigationView.getHeaderView(0)
        textViewAverageEggs = headerView.findViewById(R.id.textView_average_eggs)
        textViewTotalEggs = headerView.findViewById(R.id.textView_all_eggs)
        textViewTotalShinys = headerView.findViewById(R.id.textView_number_shinys)
    }

    override fun onDestroy() {
        super.onDestroy()
        App.finish()
        Log.i(TAG, "App closed")
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {

        return when (item?.itemId) {
            R.id.add_pokemon -> {
                startAddNewPokemonActivity()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}
