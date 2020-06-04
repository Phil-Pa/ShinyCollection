package de.phil.solidsabissupershinysammlung.activity

import android.app.Activity
import android.app.ActivityOptions
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.util.Pair
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
import de.phil.solidsabissupershinysammlung.model.HuntMethod
import de.phil.solidsabissupershinysammlung.model.PokemonData
import de.phil.solidsabissupershinysammlung.model.PokemonEdition
import de.phil.solidsabissupershinysammlung.model.PokemonSortMethod
import de.phil.solidsabissupershinysammlung.utils.MessageType
import de.phil.solidsabissupershinysammlung.viewmodel.MainViewModel
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    private fun applyPokemonEdition(pokemonEdition: PokemonEdition) {
        val updateData = viewModel.getStatisticsData()

        updateShinyStatistics(
            updateData.totalNumberOfShiny,
            updateData.totalNumberOfEggShiny,
            updateData.totalNumberOfSosShiny,
            updateData.averageSos,
            updateData.totalEggs,
            updateData.averageEggs
        )

        with (imageViewPokemonEdition) {
            when (pokemonEdition) {
                PokemonEdition.XY -> setImageResource(R.drawable.cover_xy)
                PokemonEdition.ORAS -> setImageResource(R.drawable.cover_oras)
                    PokemonEdition.SM -> setImageResource(R.drawable.cover_sm)
                PokemonEdition.USUM -> setImageResource(R.drawable.cover_usum)
                PokemonEdition.SWSH -> setImageResource(R.drawable.cover_swsh)
                PokemonEdition.GO -> setImageResource(R.drawable.cover_go)
                PokemonEdition.LETSGO -> setImageResource(R.drawable.cover_letsgo)
            }
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
            customView.findViewById(R.id.dialog_edition_xy),
            customView.findViewById(R.id.dialog_edition_oras),
            customView.findViewById(R.id.dialog_edition_sm),
            customView.findViewById(R.id.dialog_edition_usum),
            customView.findViewById(R.id.dialog_edition_swsh),
            customView.findViewById(R.id.dialog_edition_go),
            customView.findViewById(R.id.dialog_edition_letsgo)
        )
        val dialog = builder.create()
        for ((index, view) in imageViews.withIndex()) {
            view.setOnClickListener {
                val updatedPokemonEdition = PokemonEdition.fromInt(index)!!
                viewModel.setPokemonEdition(updatedPokemonEdition)
                dialog.dismiss()
            }
        }
        dialog.show()
    }

    private fun showConfirmDeleteDialog() {

        vibrate(200)

        val builder = AlertDialog.Builder(this)
        builder.setTitle(getString(R.string.dialog_watch_out))
        builder.setMessage("Möchtest du ${selectedPokemon!!.name} wirklich löschen?")
        builder.setNegativeButton(R.string.sort_dialog_negative_button,
            DialogInterface.OnClickListener { _, _ -> return@OnClickListener })
        builder.setPositiveButton(
            R.string.sort_dialog_positive_button
        ) { _, _ ->
            recyclerViewChangedListeners.forEach { it.deletePokemon(selectedPokemon!!) }
            viewModel.deletePokemon(selectedPokemon!!)
        }

        val dialog = builder.create()
        dialog.show()
    }

    private fun showDialog(action: (PokemonSortMethod) -> Unit) {
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
            if (getCurrentTabIndex() == App.TAB_INDEX_SHINY_LIST)
                return

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
                                if (selectedPokemon!!.encounterNeeded > 0 && selectedPokemon?.tabIndex != App.TAB_INDEX_SHINY_LIST) {
                                    selectedPokemon!!.encounterNeeded--
                                    recyclerViewChangedListeners.forEach {
                                        it.updatePokemonEncounter(selectedPokemon!!) }
                                    viewModel.updatePokemon(data)
                                }
                            }
                        }
                        R.id.move_to_shiny_list -> {
                            if (selectedPokemon != null && selectedPokemon?.tabIndex != App.TAB_INDEX_SHINY_LIST) {
                                recyclerViewChangedListeners.forEach {
                                    it.deletePokemon(selectedPokemon!!) }
                                viewModel.deletePokemon(selectedPokemon!!)

                                selectedPokemon!!.tabIndex = App.TAB_INDEX_SHINY_LIST

                                viewModel.addPokemon(selectedPokemon!!)
                                recyclerViewChangedListeners.forEach {
                                    it.addPokemon(selectedPokemon!!) }

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
    private lateinit var imageViewSignaturePokemon: ImageView
    private lateinit var textViewTotalEggs: TextView
    private lateinit var textViewTotalEggShinys: TextView
    private lateinit var textViewTotalSosShinys: TextView
    private lateinit var textViewAverageSosShinys: TextView
    private lateinit var textViewAverageEggs: TextView
    private lateinit var textViewTotalShinys: TextView

    interface OnListChangedListener {
        fun sort(pokemonSortMethod: PokemonSortMethod)
        fun addPokemon(pokemonData: PokemonData)
        fun updatePokemonEncounter(pokemonData: PokemonData)
        fun deletePokemon(pokemonData: PokemonData)
        fun deleteAllPokemon(tabIndex: Int)
        fun refreshRecyclerView()
        fun reload()
    }

    lateinit var viewModel: MainViewModel
    private val recyclerViewChangedListeners = mutableListOf<OnListChangedListener>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProviders.of(this).get(MainViewModel::class.java)
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
        viewModel.getPokemonEditionLiveData().observe(this, Observer {
            recyclerViewChangedListeners.forEach { listener -> listener.reload() }
            applyPokemonEdition(it)
        })

        initPreferences()
        viewModel.currentTheme = initTheme()
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)
        initTabs()
        initNavigationDrawer()
        initNavigationViewViews()
    }

    private fun initPreferences() {

        val prefs = PreferenceManager.getDefaultSharedPreferences(this)

        if (!prefs.contains(App.PREFERENCES_SORT_METHOD))
            viewModel.setSortMethod(PokemonSortMethod.InternalId)
        if (!prefs.contains(App.PREFERENCES_CURRENT_THEME))
            prefs.edit().putString(App.PREFERENCES_CURRENT_THEME, "Sabi").apply()
    }

    private fun initDarkMode() {
        val prefs = PreferenceManager.getDefaultSharedPreferences(this)

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
                R.id.view_statistics -> {
                    val intent = Intent(this, StatisticsActivity::class.java)
                    startActivity(intent, ActivityOptions.makeSceneTransitionAnimation(this,
                        Pair(textViewTotalShinys, "transitionNameNumShinys"),
                        Pair(textViewTotalEggShinys, "transitionNameNumEggShinys"),
                        Pair(textViewTotalSosShinys, "transitionNameNumSosShinys"),
                        Pair(textViewAverageSosShinys, "transitionNameAvgSosShinys"),
                        Pair(textViewTotalEggs, "transitionNameNumEggs"),
                        Pair(textViewAverageEggs, "transitionNameAvgEggs"),
                        Pair(imageViewSignaturePokemon, "transitionNameImageView")
                        ).toBundle())
                }
                R.id.settings -> {
                    startActivity(Intent(applicationContext, SettingsActivity::class.java))
                }
                R.id.importData -> {
                    val data = getClipboardStringData()
                    viewModel.import(data) { importSuccessful ->

                        if (!importSuccessful) {
                            showMessage(getString(R.string.import_error), MessageType.Error)
                            toolbar.title = getString(R.string.app_name)
                        }
                        else {
                            finish()
                            startActivity(intent)
                            showMessage(getString(R.string.import_success), MessageType.Success)
                        }
                    }
                }
                R.id.exportData -> {
                    viewModel.export { data ->
                        if (data == null) {
                            showMessage(getString(R.string.export_error), MessageType.Info)
                        }
                        else {
                            copyToClipboard(data)
                        }
                    }

                }
                R.id.changeEdition -> {
                    changeEdition()
                }
                R.id.sortData -> {
                    showDialog { sortMethod ->
                        recyclerViewChangedListeners.forEach { listener -> listener.sort(sortMethod) }
                        viewModel.setSortMethod(sortMethod)
                    }
                }
            }
            true
        }
    }

    private fun initNavigationViewViews() {
        val headerView = navigationView.getHeaderView(0)
        imageViewPokemonEdition = headerView.findViewById(R.id.imageView_pokemon_edition)
        imageViewSignaturePokemon = headerView.findViewById(R.id.imageView)
        textViewTotalShinys = headerView.findViewById(R.id.textView_number_shinys)
        textViewTotalEggShinys = headerView.findViewById(R.id.textView_number_shinys_eggs)
        textViewTotalSosShinys = headerView.findViewById(R.id.textView_number_shinys_sos)
        textViewAverageSosShinys = headerView.findViewById(R.id.textView_average_shinys_sos)
        textViewTotalEggs = headerView.findViewById(R.id.textView_all_eggs)
        textViewAverageEggs = headerView.findViewById(R.id.textView_average_eggs)

        val prefs = PreferenceManager.getDefaultSharedPreferences(this)
        when (prefs.getString(App.PREFERENCES_CURRENT_THEME, null)) {
            "Sabi" -> imageViewSignaturePokemon.setImageResource(R.drawable.leufeo)
            "Torben" -> imageViewSignaturePokemon.setImageResource(R.drawable.wuffels)
            "Johannes" -> imageViewSignaturePokemon.setImageResource(R.drawable.scytherold)
            "Phil" -> imageViewSignaturePokemon.setImageResource(R.drawable.leufeo)
        }
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

        // if theme has been changes in the settings activity
        val prefs = PreferenceManager.getDefaultSharedPreferences(this)
        if (prefs.getString(App.PREFERENCES_CURRENT_THEME, null) != viewModel.currentTheme)
            recreate()

        initDarkMode()
        recyclerViewChangedListeners.forEach { it.reload() }
        applyPokemonEdition(viewModel.getPokemonEdition())

        if (viewModel.shouldAutoSort())
            recyclerViewChangedListeners.forEach { it.sort(viewModel.getSortMethod()) }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        return when (item.itemId) {
            android.R.id.home -> {

                if (drawerLayout.isDrawerOpen(drawerLayout)) {
                    drawerLayout.closeDrawers()
                }

                true
            }
            R.id.random_pokemon -> {
                val pokemon = viewModel.getRandomPokemon(getCurrentTabIndex())
                if (pokemon == null)
                    showMessage(getString(R.string.error_random_pokemon), MessageType.Error)
                else
                    showMessage(pokemon.name, MessageType.Info)
                true
            }
            R.id.add_pokemon -> {
                val intent = Intent(applicationContext, AddNewPokemonActivity::class.java)
                intent.putExtra(AddNewPokemonActivity.INTENT_EXTRA_TAB_INDEX, view_pager.currentItem)
                startActivityForResult(intent, App.REQUEST_ADD_POKEMON)
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
                val name = data.getStringExtra(AddNewPokemonActivity.INTENT_EXTRA_NAME) ?: throw Exception()
                val encounters = data.getIntExtra(AddNewPokemonActivity.INTENT_EXTRA_ENCOUNTERS, 0)
                val id = data.getIntExtra(AddNewPokemonActivity.INTENT_EXTRA_POKEDEX_ID, 0)
                val generation = data.getIntExtra(AddNewPokemonActivity.INTENT_EXTRA_GENERATION, 0)
                val tabIndex = data.getIntExtra(AddNewPokemonActivity.INTENT_EXTRA_TAB_INDEX, 0)
                val pokemonEdition = data.getIntExtra(AddNewPokemonActivity.INTENT_EXTRA_POKEMON_EDITION, 0)

                val pokemonData = PokemonData(name, id, generation, encounters, HuntMethod.fromInt(huntMethod)!!, PokemonEdition.fromInt(pokemonEdition)!!, tabIndex)
                pokemonData.internalId = data.getIntExtra(AddNewPokemonActivity.INTENT_EXTRA_INTERNAL_ID, -1)

                viewModel.addPokemon(pokemonData)

                showMessage("$name " + resources.getString(R.string.message_has_been_added), MessageType.Success)
                for (listener in recyclerViewChangedListeners)
                    listener.addPokemon(pokemonData)
            }
        } else {
            showMessage(getString(R.string.no_pokemon_added), MessageType.Info)
        }

    }
}
