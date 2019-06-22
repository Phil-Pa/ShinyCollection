package de.phil.solidsabissupershinysammlung.activity

import android.content.*
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.view.ActionMode
import androidx.appcompat.widget.AppCompatSpinner
import androidx.databinding.DataBindingUtil
import androidx.drawerlayout.widget.DrawerLayout
import androidx.viewpager.widget.ViewPager
import com.google.android.material.navigation.NavigationView
import com.google.android.material.tabs.TabLayout
import de.phil.solidsabissupershinysammlung.R
import de.phil.solidsabissupershinysammlung.adapter.SectionsPagerAdapter
import de.phil.solidsabissupershinysammlung.core.App
import de.phil.solidsabissupershinysammlung.databinding.ActivityMainBinding
import de.phil.solidsabissupershinysammlung.model.PokemonData
import de.phil.solidsabissupershinysammlung.model.PokemonSortMethod
import de.phil.solidsabissupershinysammlung.presenter.MainPresenter
import de.phil.solidsabissupershinysammlung.view.MainView
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity(), MainView {
    override fun showDialog(action: (PokemonSortMethod) -> Unit) {
        val builder = AlertDialog.Builder(this)
        builder.setTitle(resources.getString(R.string.sort_dialog_title))
        builder.setMessage(R.string.dialog_sort_message)

        val customView = layoutInflater.inflate(R.layout.dialog_sort, drawerLayout, false)
        builder.setView(customView)

        builder.setNegativeButton(R.string.sort_dialog_negative_button,
            DialogInterface.OnClickListener { _, _ -> return@OnClickListener })

        builder.setPositiveButton(
            R.string.sort_dialog_positive_button
        ) { _, _ ->
            val spinner = customView.findViewById<AppCompatSpinner>(R.id.dialog_sort_spinner)

            // 0 = name, 1 = encounter, 2 = pokedexId, 3 = original
            val sortMethod = when (spinner.selectedItemPosition) {
                0 -> PokemonSortMethod.Name
                1 -> PokemonSortMethod.Encounter
                2 -> PokemonSortMethod.PokedexId
                3 -> PokemonSortMethod.InternalId
                else -> PokemonSortMethod.InternalId
            }

            action(sortMethod)
        }

        val dialog = builder.create()
        dialog.show()
    }

    override fun getClipboardStringData(): String? {
        val clipboard = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager

        // 0 -> text, 1 -> uri, 2 -> intent

        return clipboard.primaryClip?.getItemAt(0)?.text?.toString()
    }

    override fun copyToClipboard(data: String) {
        val clipboard = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val clip = ClipData.newPlainText("label", data)
        clipboard.primaryClip = clip
        showMessage("Copied data to clipboard")
    }

    override fun getCurrentTabIndex() = view_pager.currentItem

    override fun updateShinyStatistics(numberOfShinys: Int, totalEggsCount: Int, averageEggsCount: Float) {
        textViewTotalShinys.text = (resources.getString(R.string.num_shinys) + ": $numberOfShinys")
        textViewTotalEggs.text = (resources.getString(R.string.num_eggs) + ": $totalEggsCount")
        textViewAverageEggs.text = (resources.getString(R.string.avg_eggs) + ": $averageEggsCount")
    }

    override fun showMessage(message: String) {
        Toast.makeText(applicationContext, message, Toast.LENGTH_LONG).show()
    }

    override fun startAddNewPokemonActivity(tabIndex: Int) {
        val intent = Intent(applicationContext, AddNewPokemonActivity::class.java)
        intent.putExtra("tabIndex", tabIndex)
        startActivity(intent)
    }

    override fun onListEntryClick(data: PokemonData) {
        if (actionMode != null) {
            pokemonToDelete = data
            actionMode?.title = data.name + " " + resources.getString(R.string.action_mode_title)
        } else {
            showMessage(data.toString())
        }
    }

    private var actionMode: ActionMode? = null
    private var pokemonToDelete: PokemonData? = null

    override fun onListEntryLongClick(data: PokemonData) {
        if (actionMode == null) {
            startSupportActionMode(object : ActionMode.Callback {
                override fun onActionItemClicked(mode: ActionMode?, item: MenuItem?): Boolean {
                    when (item?.itemId) {
                        R.id.delete_entry -> {
                            if (pokemonToDelete != null)
                                presenter.deletePokemonFromDatabase(pokemonToDelete!!)
                            mode?.finish()
                        }
                    }
                    return true
                }

                override fun onCreateActionMode(mode: ActionMode?, menu: Menu?): Boolean {
                    mode?.menuInflater?.inflate(R.menu.menu_actions, menu)
                    actionMode = mode
                    return true
                }

                override fun onPrepareActionMode(mode: ActionMode?, menu: Menu?): Boolean {
                    return false
                }

                override fun onDestroyActionMode(mode: ActionMode?) {
                    actionMode = null
                    pokemonToDelete = null
                }
            })
        }

        pokemonToDelete = data
        actionMode?.title = data.name + " " + resources.getString(R.string.action_mode_title)
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
        presenter.setNavigationViewData()
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
                    startActivity(Intent(applicationContext, SettingsActivity::class.java))
                }
                R.id.importData -> {
                    presenter.importData()
                }
                R.id.exportData -> {
                    presenter.exportData()
                }
                R.id.sortData -> {
                    presenter.sortData()
                }
            }
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

    override fun onResume() {
        super.onResume()
        presenter.setNavigationViewData()

        App.performAutoSort()
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {

        return when (item?.itemId) {
            R.id.random_pokemon -> {
                presenter.showRandomPokemon()
                true
            }
            R.id.add_pokemon -> {
                presenter.startAddNewPokemonActivity()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}
