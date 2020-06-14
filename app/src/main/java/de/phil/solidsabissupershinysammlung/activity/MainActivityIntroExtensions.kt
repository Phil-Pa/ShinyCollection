package de.phil.solidsabissupershinysammlung.activity

import android.annotation.SuppressLint
import android.view.Gravity
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.drawerlayout.widget.DrawerLayout
import com.getkeepsafe.taptargetview.TapTarget
import com.getkeepsafe.taptargetview.TapTargetSequence
import de.phil.solidsabissupershinysammlung.R
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_main.view.*
import kotlinx.android.synthetic.main.activity_main_header_navigation_drawer.*

private fun TapTarget.setDefaultTapTargetValues(): TapTarget {
    this
        .targetCircleColor(android.R.color.white)
        .outerCircleColor(R.color.color_purple_sabi)
        .outerCircleAlpha(0.80f)
        .textColor(android.R.color.white)
        .dimColor(android.R.color.black)
        .cancelable(false)
        .transparentTarget(true)
    return this
}

internal fun MainActivity.showGuide(
    menuItemAdd: MenuItem,
    menuItemRandom: MenuItem
) {

    TapTargetSequence(this)
        .targets(
            TapTarget.forView(
            tabs.tabs.getTabAt(0)!!.view,
            "Die Shiny Liste",
            "In der Shiny Liste wirst du alle deine gefangenen Shiny Pokemon wiederfinden. Du kannst die Begegnungen nicht erhöhen oder verringern."
        ) // All options below are optional
            .setDefaultTapTargetValues()
            .targetRadius(40),

            TapTarget.forView(
                tabs.tabs.getTabAt(1)!!.view,
                "Züchten",
                "Rechts neben der Shiny Liste befinden sich deine Shiny Wunschlisten. Hier fügst du Pokemon hinzu, die du Shiny-Hunten möchtest. Diese weiteren Listen ermöglichen dir es, deine Wunschpokemon je nach Shiny-Hunt Methode einzuordnen. Zum Beispiel in \"Züchten\" trägst du alle Pokemon ein, die du durch Zucht als Shiny bekommen möchtest. Analog funktionieren die anderen Listen rechts nebenan."
            ) // All options below are optional
                .setDefaultTapTargetValues()
                .targetRadius(40),

            TapTarget.forToolbarMenuItem(
                toolbar,
                menuItemAdd.itemId,
                "Pokemon hinzufügen",
                "Füge hier ein Pokemon zu der Liste hinzu, in der du gerade bist. Probiere es doch gleich aus!"
            ) // All options below are optional
                .setDefaultTapTargetValues()
                .targetRadius(30),

            TapTarget.forToolbarMenuItem(
                toolbar,
                menuItemRandom.itemId,
                "Zufälliges Pokemon auswählen",
                "Hiermit kannst du ein zufälliges Pokemon aus der aktuellen Liste für dich auswählen lassen! Erhöhe dann die Anzahl seiner Begegnungen durch Klicken auf den Pokemoneintrag und sobald du das Shiny gefunden hast, halte lange auf den Eintrag gedrückt und verschiebe es in die Shiny Liste!"
            ) // All options below are optional
                .setDefaultTapTargetValues()
                .targetRadius(30))
        .listener(object : TapTargetSequence.Listener {
            override fun onSequenceCanceled(lastTarget: TapTarget?) {

            }

            override fun onSequenceFinish() {
                // introduce nav drawer after list introduction
                showNavigationDrawerGuide()
            }

            override fun onSequenceStep(lastTarget: TapTarget?, targetClicked: Boolean) {

            }

        })
        .start()
}

@SuppressLint("RtlHardcoded")
internal fun MainActivity.showNavigationDrawerGuide() {

    drawer_layout.openDrawer(Gravity.LEFT)

    val tapGuide = TapTargetSequence(this)
        .target(
            TapTarget
                .forView(imageView_pokemon_edition,
                    "Pokemon Edition",
                    "Hier siehst du die aktuell ausgewählte Pokemon Edition. Wenn du Pokemon der Shiny Liste oder einer Wunschliste hinzufügst, werden sie in die hier angezeigte Edition gespeichert. So kannst du über mehrere Pokemon Spiele hinweg deine Shiny Pokemon verwalten."
                )
                .setDefaultTapTargetValues()
                .cancelable(true)
                .targetRadius(200)
        ).listener(object : TapTargetSequence.Listener {
            override fun onSequenceCanceled(lastTarget: TapTarget?) {

            }

            override fun onSequenceFinish() {
                drawer_layout.closeDrawers()
            }

            override fun onSequenceStep(lastTarget: TapTarget?, targetClicked: Boolean) {

            }

        })

    val tempMainActivity = this

    drawer_layout.addDrawerListener(object : DrawerLayout.DrawerListener {
        override fun onDrawerStateChanged(newState: Int) {

        }

        override fun onDrawerSlide(drawerView: View, slideOffset: Float) {

        }

        override fun onDrawerClosed(drawerView: View) {
            Toast.makeText(tempMainActivity, "Viel Spaß noch mit der App!", Toast.LENGTH_SHORT).show()
        }

        override fun onDrawerOpened(drawerView: View) {
            tapGuide.start()
            drawer_layout.removeDrawerListener(this)
        }
    })

}