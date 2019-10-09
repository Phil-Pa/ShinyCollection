package de.phil.solidsabissupershinysammlung.activity

import android.content.*
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Typeface
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.view.ActionMode
import androidx.appcompat.widget.AppCompatSpinner
import androidx.drawerlayout.widget.DrawerLayout
import androidx.lifecycle.ViewModelProviders
import androidx.viewpager.widget.ViewPager
import com.getkeepsafe.taptargetview.TapTarget
import com.getkeepsafe.taptargetview.TapTargetSequence
import com.google.android.material.navigation.NavigationView
import com.google.android.material.tabs.TabLayout
import de.phil.solidsabissupershinysammlung.R
import de.phil.solidsabissupershinysammlung.adapter.SectionsPagerAdapter
import de.phil.solidsabissupershinysammlung.core.App
import de.phil.solidsabissupershinysammlung.model.PokemonData
import de.phil.solidsabissupershinysammlung.model.PokemonSortMethod
import de.phil.solidsabissupershinysammlung.presenter.MainPresenter
import de.phil.solidsabissupershinysammlung.presenter.MainViewPresenter
import de.phil.solidsabissupershinysammlung.view.MainView
import de.phil.solidsabissupershinysammlung.viewmodel.MainViewModel
import kotlinx.android.synthetic.main.activity_main.*
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream

class MainActivity : AppCompatActivity(), MainView {

    override fun saveBitmap(bitmapFileName: String, bitmap: Bitmap) {

        val contextWrapper = ContextWrapper(this)

        val directory = contextWrapper.getDir("images", Context.MODE_PRIVATE)
        val path = File(directory, bitmapFileName)

        val fos = FileOutputStream(path)
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos)
        fos.close()

    }

    override fun loadSavedBitmap(bitmapFileName: String): Bitmap? {

        val contextWrapper = ContextWrapper(this)

        val directory = contextWrapper.getDir("images", Context.MODE_PRIVATE)

        val file = File(directory, bitmapFileName)

        return try {
            BitmapFactory.decodeStream(FileInputStream(file))
        } catch (e: Exception) {
            null
        }
    }

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
        var result: String? = null

        var finished = false

        runOnUiThread {
            val clipboard = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager

            // 0 -> text, 1 -> uri, 2 -> intent

            result = clipboard.primaryClip?.getItemAt(0)?.text?.toString()
            finished = true
        }

        while (true) {
            if (finished)
                return result
        }
    }

    override fun copyToClipboard(data: String) {
        runOnUiThread {
            val clipboard = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
            val clip = ClipData.newPlainText("label", data)
            clipboard.setPrimaryClip(clip)
            showMessage("Copied data to clipboard")
        }
    }

    override fun getCurrentTabIndex(): Int {
        return view_pager.currentItem
    }

    override fun updateShinyStatistics(
        numberOfShinys: Int,
        numberOfEggShinys: Int,
        numberOfSosShinys: Int,
        averageSosCount: Float,
        totalEggsCount: Int,
        averageEggsCount: Float
    ) {
        textViewTotalShinys.text = (resources.getString(R.string.num_shinys) + ": $numberOfShinys")
        textViewTotalEggShinys.text = (resources.getString(R.string.num_shinys_eggs) + ": $numberOfEggShinys")
        textViewTotalSosShinys.text = (resources.getString(R.string.num_shinys_sos) + ": $numberOfSosShinys")
        textViewTotalEggs.text = (resources.getString(R.string.num_eggs) + ": $totalEggsCount")
        textViewAverageEggs.text = (resources.getString(R.string.avg_eggs) + ": $averageEggsCount")
        textViewAverageSosShinys.text = (resources.getString(R.string.avg_shinys_sos) + ": $averageSosCount")
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

    private val presenter = MainPresenter(this)
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var navigationView: NavigationView

    private lateinit var textViewTotalEggs: TextView
    private lateinit var textViewTotalEggShinys: TextView
    private lateinit var textViewTotalSosShinys: TextView
    private lateinit var textViewAverageSosShinys: TextView
    private lateinit var textViewAverageEggs: TextView
    private lateinit var textViewTotalShinys: TextView

    private lateinit var menuItemRandom: View
    private lateinit var menuItemAdd: View

    companion object {
        private const val TAG = "MainActivity"
    }

    private lateinit var viewModel: MainViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.i(TAG, "App started")
        App.init(applicationContext, this)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)
        initTabs()
        initNavigationDrawer()
        initNavigationViewViews()
        presenter.setNavigationViewData()

        viewModel = ViewModelProviders.of(this).get(MainViewModel::class.java)

    }

    private fun showGuide() {
        TapTargetSequence(this)
            .targets(
                TapTarget.forView(
                    menuItemRandom,
                    "Zufälliges Pokemon",
                    "Hiermit kannst du ein Pokemon aus der Liste zufällig auswählen lassen, in der du dich gerade befindest."
                )
                    .outerCircleColor(R.color.colorAccent)
                    .outerCircleAlpha(0.96f)
                    .targetCircleColor(android.R.color.white)
                    .titleTextSize(20)
                    .titleTextColor(android.R.color.white)
                    .descriptionTextSize(16)
                    .descriptionTextColor(android.R.color.white)
                    .textColor(android.R.color.white)
                    .textTypeface(Typeface.SANS_SERIF)
                    .dimColor(android.R.color.black)
                    .drawShadow(true)
                    .cancelable(false)
                    .tintTarget(true)
                    .transparentTarget(false)
                    //.icon()
                    .targetRadius(60),
                TapTarget.forView(
                    menuItemAdd,
                    "Pokemon hinzufügen",
                    "Hier kannst du ein Pokemon zu der Liste hinzufügen, in der du dich gerade befindest."
                )
                    .outerCircleColor(R.color.colorAccent)
                    .outerCircleAlpha(0.96f)
                    .targetCircleColor(android.R.color.white)
                    .titleTextSize(20)
                    .titleTextColor(android.R.color.white)
                    .descriptionTextSize(16)
                    .descriptionTextColor(android.R.color.white)
                    .textColor(android.R.color.white)
                    .textTypeface(Typeface.SANS_SERIF)
                    .dimColor(android.R.color.black)
                    .drawShadow(true)
                    .cancelable(false)
                    .tintTarget(true)
                    .transparentTarget(false)
                    //.icon()
                    .targetRadius(60)
            )
            .listener(object : TapTargetSequence.Listener {
                override fun onSequenceStep(lastTarget: TapTarget?, targetClicked: Boolean) {

                }

                override fun onSequenceFinish() {
                    // Yay
                }

                override fun onSequenceCanceled(lastTarget: TapTarget) {
                    // Boo
                }
            }).start()
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
                    val data = getClipboardStringData()

                    val success = viewModel.import(data)
                    if (!success)
                        showMessage("Could not import data")
                }
                R.id.exportData -> {
                    val data = viewModel.export()

                    if (data == null)
                        showMessage("there is no data to export")
                    else
                        copyToClipboard(data)
                }
                R.id.sortData -> {
                    // TODO: handle callbacks
                    showDialog {sortMethod ->
                        App.setSortMethod(sortMethod)
                        for (i in 0 until App.NUM_TAB_VIEWS) {
                            App.dataChangedListeners[i].notifySortPokemon(sortMethod)
                        }
                    }
                }
            }
            true
        }
    }

    private fun initNavigationViewViews() {
        val headerView = navigationView.getHeaderView(0)
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
            menuItemAdd = findViewById(R.id.add_pokemon)
            menuItemRandom = findViewById(R.id.random_pokemon)

            // app first start has already
            if (!App.config.guideShown) {
                showGuide()
                App.config.guideShown = true
            }
        }

        return true
    }

    override fun onResume() {
        super.onResume()
        presenter.setNavigationViewData()

        App.performAutoSort()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        return when (item.itemId) {
            R.id.random_pokemon -> {
//                presenter.showRandomPokemon()
                val pokemon = viewModel.getRandomPokemon()
                if (pokemon == null)
                    showMessage("No pokemon saved")
                else
                    showMessage(pokemon.name)
                true
            }
            R.id.add_pokemon -> {
//                presenter.startAddNewPokemonActivity()
                // TODO: copy the code in these methods right here
                startAddNewPokemonActivity(getCurrentTabIndex())
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}
