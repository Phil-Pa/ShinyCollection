package de.phil.solidsabissupershinysammlung.activity

import android.annotation.SuppressLint
import android.app.Activity
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.Gravity
import android.view.Menu
import android.view.MenuItem
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.appcompat.view.ActionMode
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatSpinner
import androidx.drawerlayout.widget.DrawerLayout
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.preference.PreferenceManager
import androidx.viewpager.widget.ViewPager
import com.google.android.material.navigation.NavigationView
import com.google.android.material.tabs.TabLayout
import de.phil.solidsabissupershinysammlung.R
import de.phil.solidsabissupershinysammlung.adapter.SectionsPagerAdapter
import de.phil.solidsabissupershinysammlung.core.App
import de.phil.solidsabissupershinysammlung.database.DummyRepository
import de.phil.solidsabissupershinysammlung.model.HuntMethod
import de.phil.solidsabissupershinysammlung.model.PokemonData
import de.phil.solidsabissupershinysammlung.model.PokemonEdition
import de.phil.solidsabissupershinysammlung.model.PokemonSortMethod
import de.phil.solidsabissupershinysammlung.utils.MessageType
import de.phil.solidsabissupershinysammlung.viewmodel.MainViewModel
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_main_header_navigation_drawer.*

class MainActivity : AppCompatActivity() {

    //region global stuff
    companion object {
        private const val TAG = "MainActivity"
    }

    interface OnListChangedListener {
        fun sort(pokemonSortMethod: PokemonSortMethod)
        fun addPokemon(pokemonData: PokemonData)
        fun updatePokemonEncounter(pokemonData: PokemonData)
        fun deletePokemon(pokemonData: PokemonData)
        fun deleteAllPokemon(tabIndex: Int)
        fun refreshRecyclerView()
    }
    //endregion

    private fun showConfirmDeleteDialog() {

        // vibrate to get attention from the user for deleting a pokemon
        vibrate(200)

        val builder = AlertDialog.Builder(this)
        builder.setTitle(getString(R.string.dialog_watch_out))

        // refactor
        builder.setMessage("Möchtest du ${selectedPokemon!!.name} wirklich löschen?")

        builder.setNegativeButton(R.string.sort_dialog_negative_button,
            DialogInterface.OnClickListener { _, _ -> return@OnClickListener })

        builder.setPositiveButton(
            R.string.sort_dialog_positive_button
        ) { _, _ ->
            recyclerViewChangedListeners.forEach {
                it.deletePokemon(selectedPokemon!!)
            }
            viewModel.deletePokemon(selectedPokemon!!)
        }

        val dialog = builder.create()
        dialog.show()

    }

    // TODO: give better method name
    private fun showDialog(action: (PokemonSortMethod) -> Unit) {
        val customView = layoutInflater.inflate(R.layout.dialog_sort, drawerLayout, false)

        val builder = AlertDialog.Builder(this)
            .setTitle(resources.getString(R.string.sort_dialog_title))
            .setMessage(R.string.dialog_sort_message)
            .setView(customView)
            .setNegativeButton(R.string.sort_dialog_negative_button,
                DialogInterface.OnClickListener { _, _ -> return@OnClickListener })
            .setPositiveButton(
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

    private fun updatePokemonEdition(edition: PokemonEdition) {

        with (imageViewPokemonEdition) {
            when (edition) {
                PokemonEdition.ORAS -> setImageResource(R.drawable.cover_oras)
                PokemonEdition.SM -> setImageResource(R.drawable.cover_sm)
                PokemonEdition.USUM -> setImageResource(R.drawable.cover_usum)
                PokemonEdition.SWSH -> setImageResource(R.drawable.cover_swsh)
            }
        }

        recyclerViewChangedListeners.forEach {
            it.refreshRecyclerView()
        }

    }

    private fun changeEdition() {

        drawerLayout.closeDrawers()
        val customView = layoutInflater.inflate(R.layout.dialog_change_edition, drawerLayout, false)

        val builder = AlertDialog.Builder(this)
            .setTitle("Edition ändern")
            .setMessage("Wähle, zu welcher Edition du wechseln möchtest")
            .setView(customView)

        val imageViews = listOf<AppCompatImageView>(
            customView.findViewById(R.id.dialog_edition_oras),
            customView.findViewById(R.id.dialog_edition_sm),
            customView.findViewById(R.id.dialog_edition_usum),
            customView.findViewById(R.id.dialog_edition_swsh)
        )

        val dialog = builder.create()

        for ((index, view) in imageViews.withIndex()) {
            view.setOnClickListener {
                val updatedPokemonEdition = PokemonEdition.fromInt(index)!!
                viewModel.setPokemonEdition(updatedPokemonEdition)
                updatePokemonEdition(updatedPokemonEdition)
                dialog.dismiss()
            }
        }

        dialog.show()
    }

    private fun getCurrentTabIndex(): Int {
        return view_pager.currentItem
    }

    private fun updateShinyStatistics(
        numberOfShinys: Int,
        numberOfEggShinys: Int,
        numberOfSosShinys: Int,
        averageSosCount: Float,
        totalEggsCount: Int,
        averageEggsCount: Float
    ) {
        textViewTotalShinys.text = (resources.getString(R.string.num_shinys) + ": $numberOfShinys")
        textViewTotalEggShinys.text =
            (resources.getString(R.string.num_shinys_eggs) + ": $numberOfEggShinys")
        textViewTotalSosShinys.text =
            (resources.getString(R.string.num_shinys_sos) + ": $numberOfSosShinys")
        textViewTotalEggs.text = (resources.getString(R.string.num_eggs) + ": $totalEggsCount")
        textViewAverageEggs.text = (resources.getString(R.string.avg_eggs) + ": $averageEggsCount")
        textViewAverageSosShinys.text =
            (resources.getString(R.string.avg_shinys_sos) + ": $averageSosCount")
    }

    fun onListEntryClick(data: PokemonData) {
        if (actionMode != null) {
            selectedPokemon = data
            actionMode?.title = data.name + " " + resources.getString(R.string.action_mode_title)
        } else {
            data.encounterNeeded++
            recyclerViewChangedListeners.forEach { it.updatePokemonEncounter(data) }
            viewModel.updatePokemon(data)

            if (viewModel.shouldAutoSort())
                recyclerViewChangedListeners.forEach { it.sort(viewModel.getSortMethod()) }
        }
    }

    private var actionMode: ActionMode? = null
    private var selectedPokemon: PokemonData? = null

    fun onListEntryLongClick(data: PokemonData) {
        if (actionMode == null) {
            startSupportActionMode(object : ActionMode.Callback {


                override fun onActionItemClicked(mode: ActionMode?, item: MenuItem?): Boolean {
                    when (item?.itemId) {
                        R.id.delete_entry -> {
                            if (selectedPokemon != null) {
                                showConfirmDeleteDialog()
                            }
                        }
                        R.id.decrease_encounter -> {
                            if (selectedPokemon != null) {
                                if (selectedPokemon!!.encounterNeeded > 0) {
                                    selectedPokemon!!.encounterNeeded--
                                    recyclerViewChangedListeners.forEach {
                                        it.updatePokemonEncounter(selectedPokemon!!)
                                    }
                                    viewModel.updatePokemon(data)
                                }
                            }
                        }
                        R.id.move_to_shiny_list -> {
                            if (selectedPokemon != null && selectedPokemon?.tabIndex != App.TAB_INDEX_SHINY_LIST) {
                                recyclerViewChangedListeners.forEach {
                                    it.deletePokemon(selectedPokemon!!)
                                }
                                viewModel.deletePokemon(selectedPokemon!!)

                                selectedPokemon!!.tabIndex = App.TAB_INDEX_SHINY_LIST

                                recyclerViewChangedListeners.forEach {
                                    it.addPokemon(selectedPokemon!!)
                                }
                                viewModel.addPokemon(selectedPokemon!!)

                                if (viewModel.shouldAutoSort())
                                    recyclerViewChangedListeners.forEach { it.sort(viewModel.getSortMethod()) }
                            }
                        }
                    }
                    mode?.finish()
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
                }
            })
        }

        selectedPokemon = data
        actionMode?.title = data.name + " " + resources.getString(R.string.action_mode_title)
    }

    private lateinit var drawerLayout: DrawerLayout
    private lateinit var navigationView: NavigationView

    private lateinit var imageViewPokemonEdition: ImageView

    private lateinit var textViewTotalEggs: TextView
    private lateinit var textViewTotalEggShinys: TextView
    private lateinit var textViewTotalSosShinys: TextView
    private lateinit var textViewAverageSosShinys: TextView
    private lateinit var textViewAverageEggs: TextView
    private lateinit var textViewTotalShinys: TextView

    lateinit var viewModel: MainViewModel
    private val recyclerViewChangedListeners = mutableListOf<OnListChangedListener>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.i(TAG, "App started")
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        viewModel = ViewModelProviders.of(this).get(MainViewModel::class.java)

        viewModel.init(DummyRepository(application))
        viewModel.getShinyListData().observe(this, Observer {

            val updateData = viewModel.getStatisticsData()

            updateShinyStatistics(
                updateData.totalNumberOfShiny,
                updateData.totalNumberOfEggShiny,
                updateData.totalNumberOfSosShiny,
                updateData.averageSos,
                updateData.totalEggs,
                updateData.averageEggs
            )
        })

        viewModel.getPokemonEdition().observe(this, Observer {

            updatePokemonEdition(it)

        })

        initTabs()
        initNavigationDrawer()
        initNavigationViewViews()
    }

    private fun initPreferences() {

        val prefs = PreferenceManager.getDefaultSharedPreferences(this)

        if (!prefs.contains(App.PREFERENCES_SORT_METHOD)) {
            viewModel.setSortMethod(PokemonSortMethod.InternalId)
        }

        viewModel.setShouldAutoSort(prefs.getBoolean(App.PREFERENCES_AUTO_SORT, false))
        viewModel.setDataCompression(prefs.getBoolean(App.PREFERENCES_COMPRESS_EXPORT_IMPORT, true))

        if (prefs.getBoolean(App.PREFERENCES_USE_DARK_MODE, false)) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            toolbar.popupTheme = android.R.style.ThemeOverlay_Material_Dark
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
            toolbar.popupTheme = android.R.style.ThemeOverlay_Material_Light
        }
    }

    private fun initTabs() {
        val sectionsPagerAdapter = SectionsPagerAdapter(applicationContext, supportFragmentManager)
        val viewPager: ViewPager = findViewById(R.id.view_pager)
        viewPager.adapter = sectionsPagerAdapter
        viewPager.offscreenPageLimit = App.NUM_TAB_VIEWS
        val tabs: TabLayout = findViewById(R.id.tabs)
        tabs.setupWithViewPager(viewPager)
    }

    private fun initNavigationDrawer() {
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
                    val data = getClipboardStringData()

                    val success = viewModel.import(data)
                    if (!success)
                        showMessage(getString(R.string.import_error), MessageType.Error)
                    else {
                        finish()
                        startActivity(intent)
                        showMessage(getString(R.string.import_success), MessageType.Success)
                    }
                }
                R.id.exportData -> {
                    val data = viewModel.export()

                    if (data == null)
                        showMessage(getString(R.string.export_error), MessageType.Info)
                    else
                        copyToClipboard("Pokemon Data", data)
                }
                R.id.sortData -> {
                    showDialog { sortMethod ->
                        recyclerViewChangedListeners.forEach { listener -> listener.sort(sortMethod) }
                        viewModel.setSortMethod(sortMethod)
                    }
                }
                R.id.changeEdition -> {
                    changeEdition()
                }
            }
            true
        }
    }

    private fun initNavigationViewViews() {
        val headerView = navigationView.getHeaderView(0)
        imageViewPokemonEdition = headerView.findViewById(R.id.imageView_pokemon_edition)
        textViewTotalShinys = headerView.findViewById(R.id.textView_number_shinys)
        textViewTotalEggShinys = headerView.findViewById(R.id.textView_number_shinys_eggs)
        textViewTotalSosShinys = headerView.findViewById(R.id.textView_number_shinys_sos)
        textViewAverageSosShinys = headerView.findViewById(R.id.textView_average_shinys_sos)
        textViewTotalEggs = headerView.findViewById(R.id.textView_all_eggs)
        textViewAverageEggs = headerView.findViewById(R.id.textView_average_eggs)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)

        Handler().post {
            //            menuItemAdd = findViewById(R.id.add_pokemon)
//            menuItemRandom = findViewById(R.id.random_pokemon)

            if (!viewModel.isGuideShown()) {
//                showGuide()
                viewModel.setGuideShown()
            }
        }

        return true
    }

    override fun onResume() {
        super.onResume()

        initPreferences()

        if (viewModel.shouldAutoSort())
            recyclerViewChangedListeners.forEach { it.sort(viewModel.getSortMethod()) }
    }

    @SuppressLint("RtlHardcoded")
    override fun onBackPressed() {
        if (drawerLayout.isDrawerOpen(Gravity.LEFT))
            drawerLayout.closeDrawers()
        else
            super.onBackPressed()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        return when (item.itemId) {
            android.R.id.home -> {
                closeDrawer()
                true
            }
            R.id.random_pokemon -> {
                onRandomPokemonClicked()
                true
            }
            R.id.add_pokemon -> {
                startAddNewPokemonActivity()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    fun addRecyclerViewChangedListener(listener: OnListChangedListener) {
        recyclerViewChangedListeners.add(listener)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode != Activity.RESULT_CANCELED) {
            if (requestCode == App.REQUEST_ADD_POKEMON) {
                if (data == null)
                    throw Exception()

                val huntMethod = data.getIntExtra(AddNewPokemonActivity.INTENT_EXTRA_HUNT_METHOD, 0)
                val name = data.getStringExtra(AddNewPokemonActivity.INTENT_EXTRA_NAME)
                    ?: throw Exception()
                val encounters = data.getIntExtra(AddNewPokemonActivity.INTENT_EXTRA_ENCOUNTERS, 0)
                val id = data.getIntExtra(AddNewPokemonActivity.INTENT_EXTRA_POKEDEX_ID, 0)
                val generation = data.getIntExtra(AddNewPokemonActivity.INTENT_EXTRA_GENERATION, 0)
                val tabIndex = data.getIntExtra(AddNewPokemonActivity.INTENT_EXTRA_TAB_INDEX, 0)
                val pokemonEdition =
                    data.getIntExtra(AddNewPokemonActivity.INTENT_EXTRA_POKEMON_EDITION, 0)

                val pokemonData = PokemonData(
                    name, id, generation, encounters, HuntMethod.fromInt(huntMethod)!!,
                    PokemonEdition.fromInt(pokemonEdition)!!, tabIndex
                )
                pokemonData.internalId =
                    data.getIntExtra(AddNewPokemonActivity.INTENT_EXTRA_INTERNAL_ID, -1)

                viewModel.addPokemon(pokemonData)

                showMessage(
                    "$name " + resources.getString(R.string.message_has_been_added),
                    MessageType.Success
                )
                for (listener in recyclerViewChangedListeners)
                    listener.addPokemon(pokemonData)
            }
        } else {
            showMessage(getString(R.string.no_pokemon_added), MessageType.Info)
        }

    }

    private fun closeDrawer() {
        if (drawerLayout.isDrawerOpen(drawerLayout)) {
            drawerLayout.closeDrawers()
        }
    }

    private fun onRandomPokemonClicked() {
        val pokemon = viewModel.getRandomPokemon(getCurrentTabIndex())
        if (pokemon == null)
            showMessage(getString(R.string.error_random_pokemon), MessageType.Error)
        else
            showMessage(pokemon.name, MessageType.Info)
    }

    private fun startAddNewPokemonActivity() {
        val intent = Intent(applicationContext, AddNewPokemonActivity::class.java)
        intent.putExtra(
            AddNewPokemonActivity.INTENT_EXTRA_TAB_INDEX,
            view_pager.currentItem
        )
        startActivityForResult(intent, App.REQUEST_ADD_POKEMON)
    }

}
