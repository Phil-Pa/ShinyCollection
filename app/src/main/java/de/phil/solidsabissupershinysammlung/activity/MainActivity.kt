package de.phil.solidsabissupershinysammlung.activity

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.*
import android.databinding.DataBindingUtil
import android.os.Bundle
import android.support.design.widget.NavigationView
import android.support.design.widget.TabLayout
import android.support.v4.view.GravityCompat
import android.support.v4.view.ViewPager
import android.support.v4.widget.DrawerLayout
import android.support.v7.app.ActionBarDrawerToggle
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.support.v7.view.ActionMode
import android.support.v7.widget.AppCompatSpinner
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
import de.phil.solidsabissupershinysammlung.model.PokemonSortMethod
import de.phil.solidsabissupershinysammlung.presenter.MainPresenter
import de.phil.solidsabissupershinysammlung.view.MainView
import kotlinx.android.synthetic.main.activity_main.*
import kotlin.math.round


fun Float.round(decimals: Int): Float {
    var multiplier = 1.0
    repeat(decimals) { multiplier *= 10 }
    return (round(this * multiplier) / multiplier).toFloat()
}

class MainActivity : AppCompatActivity(), MainView {
    override fun getCurrentTabIndex(): Int {
        return view_pager.currentItem
    }

    override fun updateShinyStatistics() {
        textViewTotalShinys.text = (resources.getString(R.string.num_shinys) + ": ${presenter.getTotalNumberOfShinys()}")
        textViewTotalEggs.text = (resources.getString(R.string.num_eggs) + ": ${presenter.getTotalEggsCount()}")
        textViewAverageEggs.text = (resources.getString(R.string.avg_eggs) + ": ${presenter.getAverageEggsCount().round(2)}")
    }

    override fun showMessage(message: String) {
        Toast.makeText(applicationContext, message, Toast.LENGTH_LONG).show()
    }

    override fun startAddNewPokemonActivity() {
        val tabIndex = getCurrentTabIndex()
        if (tabIndex < 0 || tabIndex > App.NUM_TAB_VIEWS) {
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
        App.init(applicationContext, this)
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
        val sectionsPagerAdapter = SectionsPagerAdapter(applicationContext, supportFragmentManager)
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
            this, drawerLayout, toolbar, R.string.app_name, R.string.app_name
        )
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()

        navigationView.setNavigationItemSelectedListener {
            when (it.itemId) {
                R.id.settings -> {
                    // TODO start settings
                }
                R.id.importData -> {

                    val clipboard = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager

                    // 0 -> text, 1 -> uri, 2 -> intent
                    val data = clipboard.primaryClip?.getItemAt(0)?.text

                    if (data == null || data.isBlank() || data.isEmpty()) {
                        showMessage("Clipboard data not valid")
                    } else {
                        val success = presenter.importData(data.toString())
                        if (success) {
                            showMessage("Successfully imported data")
                        } else {
                            showMessage("Could not import data")
                        }
                    }
                }
                R.id.exportData -> {
                    val exportData = presenter.exportData()
                    val clipboard = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                    val clip = ClipData.newPlainText("label", exportData)
                    clipboard.primaryClip = clip
                    showMessage("Copied data to clipboard")
                }
                R.id.sortData -> {

                    val builder = AlertDialog.Builder(this)
                    builder.setTitle(resources.getString(R.string.sort_dialog_title))
                    builder.setMessage(R.string.dialog_sort_message)

                    val customView = layoutInflater.inflate(R.layout.dialog_sort, null)
                    builder.setView(customView)

                    builder.setNegativeButton(R.string.sort_dialog_negative_button,
                        DialogInterface.OnClickListener { _, _ -> return@OnClickListener })

                    builder.setPositiveButton(R.string.sort_dialog_positive_button, object : DialogInterface.OnClickListener {
                        override fun onClick(dialog: DialogInterface?, which: Int) {
                            val spinner = customView.findViewById<AppCompatSpinner>(R.id.dialog_sort_spinner)

                            // 0 = name, 1 = encounter, 2 = pokedexId, 3 = original
                            when (spinner.selectedItemPosition) {
                                0 -> App.setSortMethod(PokemonSortMethod.Name)
                                1 -> App.setSortMethod(PokemonSortMethod.Encounter)
                                2 -> App.setSortMethod(PokemonSortMethod.PokedexId)
                                3 -> App.setSortMethod(PokemonSortMethod.InternalId)
                                else -> return
                            }

                            restartApp()
                        }
                    })

                    val dialog = builder.create()
                    dialog.show()
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

    private fun restartApp() {

        Thread.sleep(1000)

        val mStartActivity = Intent(applicationContext, MainActivity::class.java)
        val mPendingIntentId = 123456
        val mPendingIntent =
            PendingIntent.getActivity(applicationContext, mPendingIntentId, mStartActivity, PendingIntent.FLAG_CANCEL_CURRENT)
        val mgr = applicationContext.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        mgr.set(AlarmManager.RTC, System.currentTimeMillis() + 100, mPendingIntent)
        System.exit(0)
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
