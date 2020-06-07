package de.phil.solidsabissupershinysammlung.activity

import android.app.ActivityOptions
import android.content.Context
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
import androidx.appcompat.widget.AppCompatButton
import androidx.appcompat.widget.AppCompatEditText
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatSpinner
import androidx.drawerlayout.widget.DrawerLayout
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.preference.PreferenceManager
import androidx.viewpager.widget.ViewPager
import com.commit451.modalbottomsheetdialogfragment.ModalBottomSheetDialogFragment
import com.commit451.modalbottomsheetdialogfragment.Option
import com.google.android.material.navigation.NavigationView
import com.google.android.material.tabs.TabLayout
import de.phil.solidsabissupershinysammlung.R
import de.phil.solidsabissupershinysammlung.adapter.SectionsPagerAdapter
import de.phil.solidsabissupershinysammlung.core.App
import de.phil.solidsabissupershinysammlung.model.*
import de.phil.solidsabissupershinysammlung.utils.MessageType
import de.phil.solidsabissupershinysammlung.viewmodel.MainViewModel
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity(), IPokemonListActivity, ModalBottomSheetDialogFragment.Listener {

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

    override fun onListEntryClick(pokemonData: PokemonData) {
        if (getCurrentTabIndex() == App.TAB_INDEX_SHINY_LIST)
            return

        pokemonData.encounterNeeded++
        recyclerViewChangedListeners.forEach { it.updatePokemonEncounter(pokemonData) }
        viewModel.updatePokemon(pokemonData)

        if (viewModel.shouldAutoSort())
            recyclerViewChangedListeners.forEach { it.sort(viewModel.getSortMethod()) }
    }

    private var selectedPokemon: PokemonData? = null

    override fun onListEntryLongClick(pokemonData: PokemonData) {

        selectedPokemon = pokemonData

        val dialog = ModalBottomSheetDialogFragment.Builder()
            .add(R.menu.menu_actions)
            .build()

        dialog.show(supportFragmentManager, "options")

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

        imageViewPokemonEdition.setOnClickListener { changeEdition() }

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

        if (menu == null)
            return true

        Handler().post {
            val menuItemAdd = menu.findItem(R.id.add_pokemon)
            val menuItemRandom = menu.findItem(R.id.random_pokemon)

//            showGuide(menuItemAdd, menuItemRandom)

            if (!viewModel.isGuideShown()) {
                showGuide(menuItemAdd, menuItemRandom)
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
                intent.putExtra(INTENT_EXTRA_TAB_INDEX, view_pager.currentItem)
                startActivity(intent)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun addRecyclerViewChangedListener(listener: OnListChangedListener) {
        recyclerViewChangedListeners.add(listener)
    }

    override fun getAllPokemonDataFromTabIndex(mTabIndex: Int): List<PokemonData> {
        return viewModel.getAllPokemonDataFromTabIndex(mTabIndex)
    }

    override fun getSortMethod(): PokemonSortMethod {
        return viewModel.getSortMethod()
    }

    override fun getContext(): Context {
        return this
    }

    override fun showSmallIcons(): Boolean {
        return false
    }

    override fun onModalOptionSelected(tag: String?, option: Option) {

        when (option.id) {
            R.id.delete_entry -> {
                if (selectedPokemon != null) {
                    showConfirmDeleteDialog()
                }
            }
            R.id.decrease_encounter -> {
                if (selectedPokemon != null) {
                    if (selectedPokemon!!.encounterNeeded > 0 && selectedPokemon!!.tabIndex != App.TAB_INDEX_SHINY_LIST) {
                        selectedPokemon!!.encounterNeeded--
                        recyclerViewChangedListeners.forEach {
                            it.updatePokemonEncounter(selectedPokemon!!) }
                        viewModel.updatePokemon(selectedPokemon!!)
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
            R.id.edit_pokemon_data -> {

                if (selectedPokemon != null) {

                    val pokemon = selectedPokemon!!

                    val customView = layoutInflater.inflate(R.layout.dialog_edit_pokemon, drawerLayout, false)

                    val spinnerEditions = customView.findViewById<AppCompatSpinner>(R.id.edit_pokemon_spinner_pokemon_editions)
                    val spinnerMethods = customView.findViewById<AppCompatSpinner>(R.id.edit_pokemon_spinner_hunt_methods)
                    val spinnerTabTitles = customView.findViewById<AppCompatSpinner>(R.id.edit_pokemon_spinner_tab_titles)
                    val editTextEncounter = customView.findViewById<AppCompatEditText>(R.id.edit_pokemon_edittext_eggsNeeded)
                    val saveButton = customView.findViewById<AppCompatButton>(R.id.edit_pokemon_button_save)

                    spinnerEditions.setSelection(pokemon.pokemonEdition.ordinal)
                    spinnerMethods.setSelection(pokemon.huntMethod.ordinal)
                    spinnerTabTitles.setSelection(pokemon.tabIndex)
                    editTextEncounter.setText(pokemon.encounterNeeded.toString())

                    val dialog = AlertDialog.Builder(this)
                        .setTitle("Daten von ${pokemon.name} bearbeiten")
                        .setView(customView)
                        .create()

                    dialog.show()

                    saveButton.setOnClickListener {

                        val pokemonEdition = PokemonEdition.fromInt(spinnerEditions.selectedItemPosition)
                        val huntMethod = HuntMethod.fromInt(spinnerMethods.selectedItemPosition)
                        val tabIndex = spinnerTabTitles.selectedItemPosition
                        val encounters = editTextEncounter.text.toString().toInt()

                        if (pokemonEdition == null || huntMethod == null) {
                            showMessage("Unerwarteter Fehler", MessageType.Error)
                            dialog.dismiss()
                            return@setOnClickListener
                        }

                        pokemon.pokemonEdition = pokemonEdition
                        pokemon.huntMethod = huntMethod
                        pokemon.tabIndex = tabIndex
                        pokemon.encounterNeeded = encounters

                        viewModel.updatePokemon(pokemon)

                        recyclerViewChangedListeners.forEach {
                            it.reload()
                        }

                        showMessage("Änderungen wurden gespeichert", MessageType.Info)
                        dialog.dismiss()
                    }
                }
            }
        }
    }
}