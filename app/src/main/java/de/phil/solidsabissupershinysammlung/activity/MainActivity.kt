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
import android.view.Menu
import android.view.MenuItem
import de.phil.solidsabissupershinysammlung.core.App
import de.phil.solidsabissupershinysammlung.R
import de.phil.solidsabissupershinysammlung.adapter.SectionsPagerAdapter
import de.phil.solidsabissupershinysammlung.model.PokemonData
import de.phil.solidsabissupershinysammlung.presenter.MainPresenter
import de.phil.solidsabissupershinysammlung.view.MainView
import de.phil.solidsabissupershinysammlung.databinding.ActivityMainBinding
import kotlinx.android.synthetic.main.activity_main.*
import android.support.v7.app.AlertDialog
import android.widget.*
import java.lang.IllegalStateException

class MainActivity : AppCompatActivity(), MainView {

    override fun updateShinyStatistics() {
        textViewTotalShinys.text = ("Anzahl Shinys: ${App.getTotalNumberOfShinys()}")
        textViewTotalEggs.text = ("Eier gesamt: ${App.getTotalEggsCount()}")
        textViewAverageEggs.text = ("Eier durchschnittlich: ${App.getAverageEggsCount()}")
    }

    override fun showMessage(message: String) {
        Toast.makeText(applicationContext, message, Toast.LENGTH_LONG).show()
    }

    override fun startAddNewPokemonActivity() {
        var tabIndex = -1

        val alert = AlertDialog.Builder(this)
        val inflater = layoutInflater
        val alertLayout = inflater.inflate(R.layout.dialog_choose_tab, null)
        val shiny_list_button = alertLayout.findViewById<Button>(R.id.shiny_list_button)
        val cycle_15_button = alertLayout.findViewById<Button>(R.id.cycle_15_button)
        val cycle_20_button = alertLayout.findViewById<Button>(R.id.cycle_20_button)
        val sos_button = alertLayout.findViewById<Button>(R.id.sos_button)

        val startActivityInternal = {
            if (tabIndex < 0 || tabIndex > 3)
                throw IllegalStateException()

            val intent = Intent(applicationContext, AddNewPokemonActivity::class.java)
            intent.putExtra("tabIndex", tabIndex)
            startActivity(intent)
        }

        var dialog: AlertDialog? = null

        shiny_list_button.setOnClickListener {
            tabIndex = 0
            dialog?.cancel()
            startActivityInternal()
        }
        cycle_15_button.setOnClickListener {
            tabIndex = 1
            dialog?.cancel()
            startActivityInternal()
        }

        cycle_20_button.setOnClickListener {
            tabIndex = 2
            dialog?.cancel()
            startActivityInternal()
        }

        sos_button.setOnClickListener {
            tabIndex = 3
            dialog?.cancel()
            startActivityInternal()
        }

        alert.setView(alertLayout)

        alert.setTitle("Pokemon hinzufügen zu:")

        dialog = alert.create()

        dialog.show()
    }

    override fun onListEntryClick(data: PokemonData?) {
        if (data != null)
            showMessage(data.toString())
    }

    override fun onListEntryLongClick(data: PokemonData?) {
        /* TODO use modal bottom sheet for long holding list entry
          number of tab views -> number of list options in bottom sheet
          for choosing in which pokemon list the pokemon should be */

        startSupportActionMode(object : ActionMode.Callback {
            override fun onActionItemClicked(mode: ActionMode?, item: MenuItem?): Boolean {
                when (item?.itemId) {
                    R.id.delete_entry -> {
                        if (data != null) {
                            val tabIndex = view_pager.currentItem
                            presenter.deletePokemonFromDatabase(data, tabIndex)
                            mode?.finish()

                        }
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        App.init(applicationContext)
        App.mainView = this

        val binding: ActivityMainBinding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        binding.presenter = presenter

//        setContentView(R.layout.activity_main_navigation_drawer)
        setSupportActionBar(toolbar)

        // init tab view
        val sectionsPagerAdapter = SectionsPagerAdapter(this,applicationContext, supportFragmentManager)
        val viewPager: ViewPager = findViewById(R.id.view_pager)
        viewPager.adapter = sectionsPagerAdapter
        val tabs: TabLayout = findViewById(R.id.tabs)
        tabs.setupWithViewPager(viewPager)

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
                R.id.add_pokemon -> {
                    startAddNewPokemonActivity()
                }
                R.id.settings -> {
                    // TODO start settings
                }
            }
            findViewById<DrawerLayout>(R.id.drawer_layout).closeDrawer(GravityCompat.START)
            true
        }

        initNavigationViewViews()
        updateShinyStatistics()
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
    }

//    override fun onCreateOptionsMenu(menu: Menu): Boolean {
//        // Inflate the menu; this adds items to the action bar if it is present.
////        menuInflater.inflate(R.menu.menu_main, menu)
//        return true
//    }
//
//    override fun onOptionsItemSelected(item: MenuItem): Boolean {
//        return super.onOptionsItemSelected(item)
//    }
}
