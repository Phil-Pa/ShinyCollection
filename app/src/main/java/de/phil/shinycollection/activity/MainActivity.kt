package de.phil.shinycollection.activity

import android.app.ActivityOptions
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Pair
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.ImageView
import android.widget.PopupMenu
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.appcompat.widget.AppCompatButton
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatSpinner
import androidx.drawerlayout.widget.DrawerLayout
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.preference.PreferenceManager
import com.google.android.material.navigation.NavigationView
import com.google.android.material.tabs.TabLayout
import com.google.android.material.textfield.TextInputEditText
import de.phil.shinycollection.R
import de.phil.shinycollection.ShinyPokemonApplication
import de.phil.shinycollection.adapter.SectionsPagerAdapter
import de.phil.shinycollection.databinding.ActivityMainBinding
import de.phil.shinycollection.model.*
import de.phil.shinycollection.utils.MessageType
import de.phil.shinycollection.viewmodel.MainViewModel
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity(), IPokemonListActivity {

    private fun applyPokemonEditionOnUi(pokemonEdition: PokemonEdition) {
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
            .setTitle(getString(R.string.dialog_change_edition_title))
            .setMessage(getString(R.string.dialog_change_edition_message))
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

    private fun showConfirmDeleteDialog(selectedPokemon: PokemonData) {
        vibrate(200)

        val builder = AlertDialog.Builder(this)
        builder.setTitle(getString(R.string.dialog_watch_out))
        builder.setMessage("${selectedPokemon.name} " + getString(R.string.confirm_delete_dialog_message))
        builder.setNegativeButton(R.string.sort_dialog_negative_button, DialogInterface.OnClickListener { _, _ -> return@OnClickListener })
        builder.setPositiveButton(R.string.sort_dialog_positive_button) { _, _ ->
            recyclerViewChangedListeners[getCurrentTabIndex()].deletePokemon(selectedPokemon)
            viewModel.deletePokemon(selectedPokemon)
        }

        val dialog = builder.create()
        dialog.show()
    }

    private fun showSortDialog(sortPokemonList: (PokemonSortMethod) -> Unit) {
        val builder = AlertDialog.Builder(this)
        builder.setTitle(resources.getString(R.string.sort_dialog_title))
        builder.setMessage(R.string.dialog_sort_message)

        val customView = layoutInflater.inflate(R.layout.dialog_sort, drawerLayout, false)
        builder.setView(customView)
        builder.setNegativeButton(R.string.sort_dialog_negative_button, DialogInterface.OnClickListener { _, _ -> return@OnClickListener })
        builder.setPositiveButton(R.string.sort_dialog_positive_button) { _, _ ->
            val spinner = customView.findViewById<AppCompatSpinner>(R.id.dialog_sort_spinner)

            // 0 = name, 1 = encounter, 2 = pokedexId, 3 = original
            val sortMethod = when (spinner.selectedItemPosition) {
                0 -> PokemonSortMethod.Name
                1 -> PokemonSortMethod.Encounter
                2 -> PokemonSortMethod.PokedexId
                3 -> PokemonSortMethod.InternalId
                else -> PokemonSortMethod.InternalId
            }

            sortPokemonList(sortMethod)
        }

        val dialog = builder.create()
        dialog.show()
    }

    private fun getCurrentTabIndex() = view_pager.currentItem

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
        if (getCurrentTabIndex() == ShinyPokemonApplication.TAB_INDEX_SHINY_LIST)
            return

        viewModel.increasePokemonEncounter(pokemonData)
        recyclerViewChangedListeners[getCurrentTabIndex()].updatePokemonEncounter(pokemonData)

        if (viewModel.shouldAutoSort())
            recyclerViewChangedListeners[getCurrentTabIndex()].sort(viewModel.getSortMethod())
    }

    override fun onListEntryLongClick(view: View) {
        // passed through the interface from fragment
        val pokemonData = view.tag as PokemonData
        showLongClickPokemonPopup(view, pokemonData)
    }

    private fun showLongClickPokemonPopup(view: View, pokemonData: PokemonData) {
        val popup = PopupMenu(this, view)
        popup.menuInflater.inflate(R.menu.menu_actions, popup.menu)
        popup.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {
                R.id.delete_entry -> {
                    showConfirmDeleteDialog(pokemonData)
                    if (viewModel.shouldAutoSort())
                        recyclerViewChangedListeners[getCurrentTabIndex()].sort(viewModel.getSortMethod())
                    return@setOnMenuItemClickListener true
                }
                R.id.decrease_encounter -> {
                    decreasePokemonEncounter(pokemonData)
                    return@setOnMenuItemClickListener true
                }
                R.id.move_to_shiny_list -> {
                    movePokemonToShinyList(pokemonData)
                    if (viewModel.shouldAutoSort()) {
                        recyclerViewChangedListeners[getCurrentTabIndex()].sort(viewModel.getSortMethod())
                        recyclerViewChangedListeners[ShinyPokemonApplication.TAB_INDEX_SHINY_LIST].sort(
                            viewModel.getSortMethod()
                        )
                    }
                    return@setOnMenuItemClickListener true
                }
                R.id.edit_pokemon_data -> {
                    showEditPokemonDialog(pokemonData)
                    return@setOnMenuItemClickListener true
                }
                else -> false
            }
        }
        popup.show()
    }

    private fun showEditPokemonDialog(pokemonData: PokemonData) {
        val customView = layoutInflater.inflate(R.layout.dialog_edit_pokemon, drawerLayout, false)

        val spinnerEditions = customView.findViewById<AppCompatSpinner>(R.id.edit_pokemon_spinner_pokemon_editions)
        val spinnerMethods = customView.findViewById<AppCompatSpinner>(R.id.edit_pokemon_spinner_hunt_methods)
        val spinnerTabTitles = customView.findViewById<AppCompatSpinner>(R.id.edit_pokemon_spinner_tab_titles)
        val editTextEncounter = customView.findViewById<TextInputEditText>(R.id.edit_pokemon_edittext_eggsNeeded)
        val saveButton = customView.findViewById<AppCompatButton>(R.id.edit_pokemon_button_save)

        spinnerEditions.setSelection(pokemonData.pokemonEdition.ordinal)
        spinnerMethods.setSelection(pokemonData.huntMethod.ordinal)
        spinnerTabTitles.setSelection(pokemonData.tabIndex)
        editTextEncounter.setText(pokemonData.encounterNeeded.toString())

        val dialog = AlertDialog.Builder(this)
            .setTitle(pokemonData.name + " " + getString(R.string.dialog_edit_pokemon_data_title))
            .setView(customView)
            .create()

        saveButton.setOnClickListener {

            val pokemonEdition = PokemonEdition.fromInt(spinnerEditions.selectedItemPosition)
            val huntMethod = HuntMethod.fromInt(spinnerMethods.selectedItemPosition)
            val tabIndex = spinnerTabTitles.selectedItemPosition
            val encountersString = editTextEncounter.text.toString()

            if (encountersString.isEmpty()) {
                showMessage(getString(R.string.dialog_message_enter_encounter), MessageType.Info)
                return@setOnClickListener
            }

            val encounters = encountersString.toInt()

            if (pokemonEdition == null || huntMethod == null) {
                showMessage(getString(R.string.dialog_message_unknown_error), MessageType.Error)
                dialog.dismiss()
                return@setOnClickListener
            }

            pokemonData.pokemonEdition = pokemonEdition
            pokemonData.huntMethod = huntMethod
            pokemonData.tabIndex = tabIndex
            pokemonData.encounterNeeded = encounters

            viewModel.updatePokemon(pokemonData)
            recyclerViewChangedListeners[getCurrentTabIndex()].reload()

            if (pokemonData.tabIndex != getCurrentTabIndex())
                recyclerViewChangedListeners[pokemonData.tabIndex].reload()

            showMessage(getString(R.string.dialog_message_saved_changes), MessageType.Info)
            dialog.dismiss()
        }

        dialog.show()
    }

    private fun movePokemonToShinyList(selectedPokemon: PokemonData) {
        if (!selectedPokemon.isInShinyList()) {
            recyclerViewChangedListeners[getCurrentTabIndex()].deletePokemon(selectedPokemon)

            selectedPokemon.tabIndex = ShinyPokemonApplication.TAB_INDEX_SHINY_LIST
            viewModel.updatePokemon(selectedPokemon)

            recyclerViewChangedListeners[ShinyPokemonApplication.TAB_INDEX_SHINY_LIST].addPokemon(selectedPokemon)
        }
    }

    private fun decreasePokemonEncounter(selectedPokemon: PokemonData) {
        if (selectedPokemon.encounterNeeded > 0 && !selectedPokemon.isInShinyList()) {
            selectedPokemon.encounterNeeded--
            recyclerViewChangedListeners[getCurrentTabIndex()].updatePokemonEncounter(selectedPokemon)
            viewModel.updatePokemon(selectedPokemon)
        }
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

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        viewModel = ViewModelProvider(this).get(MainViewModel::class.java)
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
            applyPokemonEditionOnUi(it)
        })

        initPreferences()
        viewModel.currentTheme = initTheme()

        setContentView(binding.root)
        setSupportActionBar(toolbar)
        initTabs()
        initNavigationDrawer()
        initNavigationViewViews()
    }

    private fun initPreferences() {

        val prefs = PreferenceManager.getDefaultSharedPreferences(this)

        if (!prefs.contains(ShinyPokemonApplication.PREFERENCES_SORT_METHOD))
            viewModel.setSortMethod(PokemonSortMethod.InternalId)
        if (!prefs.contains(ShinyPokemonApplication.PREFERENCES_CURRENT_THEME))
            prefs.edit().putString(ShinyPokemonApplication.PREFERENCES_CURRENT_THEME, getString(R.string.theme_orange)).apply()
    }

    private fun initDarkMode() {
        val prefs = PreferenceManager.getDefaultSharedPreferences(this)

        if (prefs.getBoolean(ShinyPokemonApplication.PREFERENCES_USE_DARK_MODE, false)) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            toolbar.popupTheme = android.R.style.ThemeOverlay_Material_Dark
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
            toolbar.popupTheme = android.R.style.ThemeOverlay_Material_Light
        }
    }

    private fun initTabs() {
        val sectionsPagerAdapter = SectionsPagerAdapter(applicationContext, supportFragmentManager)
        binding.viewPager.adapter = sectionsPagerAdapter
        binding.viewPager.offscreenPageLimit = ShinyPokemonApplication.NUM_TAB_VIEWS
        val tabs: TabLayout = binding.tabs
        tabs.setupWithViewPager(view_pager)
    }

    private fun initNavigationDrawer() {
        drawerLayout = binding.drawerLayout
        navigationView = binding.navView
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
                    showYesNoDialog(getString(R.string.dialog_message_compress_data)) { shouldCompressData ->
                        viewModel.export(shouldCompressData) { data ->
                            if (data == null) {
                                showMessage(getString(R.string.export_error), MessageType.Info)
                            } else {
                                copyToClipboard(data)
                            }
                        }
                    }
                }
                R.id.changeEdition -> {
                    changeEdition()
                }
                R.id.sortData -> {
                    showSortDialog { sortMethod ->
                        recyclerViewChangedListeners.forEach { listener -> listener.sort(sortMethod) }
                        viewModel.setSortMethod(sortMethod)
                    }
                }
            }
            true
        }
    }

    private fun initNavigationViewViews() {
        val headerView = binding.navView.getHeaderView(0)
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
        when (prefs.getString(ShinyPokemonApplication.PREFERENCES_CURRENT_THEME, null)) {
            getString(R.string.theme_orange) -> imageViewSignaturePokemon.setImageResource(R.drawable.leufeo)
            getString(R.string.theme_purple) -> imageViewSignaturePokemon.setImageResource(R.drawable.wuffels)
            getString(R.string.theme_blue) -> imageViewSignaturePokemon.setImageResource(R.drawable.scytherold)
            getString(R.string.theme_red) -> imageViewSignaturePokemon.setImageResource(R.drawable.leufeo)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)

        if (menu == null)
            return true

        if (!viewModel.isGuideShown()) {
            Handler(Looper.myLooper()!!).post {
                val menuItemAdd = menu.findItem(R.id.add_pokemon)
                val menuItemRandom = menu.findItem(R.id.random_pokemon)

                showGuide(menuItemAdd, menuItemRandom)
                viewModel.setGuideShown()
            }
        }

        return true
    }

    @RequiresApi(Build.VERSION_CODES.P)
    override fun onResume() {
        super.onResume()

        // if theme has been changed in the settings activity
        val prefs = PreferenceManager.getDefaultSharedPreferences(this)
        val themeChanged = prefs.getString(ShinyPokemonApplication.PREFERENCES_CURRENT_THEME, null) != viewModel.currentTheme
        if (themeChanged)
            recreate()

        initDarkMode()
        recyclerViewChangedListeners.forEach { it.reload() }
        applyPokemonEditionOnUi(viewModel.getPokemonEdition())

        if (viewModel.shouldAutoSort())
            recyclerViewChangedListeners.forEach { it.sort(viewModel.getSortMethod()) }

        checkForNewAppVersion()
    }

    @RequiresApi(Build.VERSION_CODES.P)
    private fun checkForNewAppVersion() {
        if (!viewModel.shouldAskForUpdate())
            return

        if (isNetworkAvailable()) {
            val versionCode = packageManager.getPackageInfo(packageName, 0).longVersionCode
            viewModel.ifNewVersionAvailable(versionCode) {
                showYesNoDialog("New Update available! Want to download?") { answer ->
                    if (answer) {
                        val url = "https://github.com/Phil-Pa/ShinyCollection/releases"
                        val intent = Intent(Intent.ACTION_VIEW)
                        intent.data = Uri.parse(url)
                        startActivity(intent)
                    }
                }
            }
        }
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
                intent.putExtra(INTENT_EXTRA_POKEMON_EDITION, viewModel.getPokemonEdition().value)
                startActivity(intent)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun addRecyclerViewChangedListener(listener: OnListChangedListener) {
        recyclerViewChangedListeners.add(listener)
    }

    override fun getAllPokemonDataFromTabIndex(tabIndex: Int): List<PokemonData> {
        return viewModel.getAllPokemonDataFromTabIndex(tabIndex)
    }

    override fun getSortMethod(): PokemonSortMethod {
        return viewModel.getSortMethod()
    }

    override fun getContext(): Context {
        return this
    }
}