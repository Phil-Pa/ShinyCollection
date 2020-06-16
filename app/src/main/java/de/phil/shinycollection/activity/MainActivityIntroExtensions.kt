package de.phil.shinycollection.activity

import android.annotation.SuppressLint
import android.view.Gravity
import android.view.MenuItem
import android.view.View
import androidx.drawerlayout.widget.DrawerLayout
import com.getkeepsafe.taptargetview.TapTarget
import com.getkeepsafe.taptargetview.TapTargetSequence
import de.phil.shinycollection.R
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
            getString(R.string.guide_shiny_list_title),
            getString(R.string.guide_shiny_list_description)
        ) // All options below are optional
            .setDefaultTapTargetValues()
            .targetRadius(40),

            TapTarget.forView(
                tabs.tabs.getTabAt(1)!!.view,
                getString(R.string.guide_breeding_title),
                getString(R.string.guide_breeding_description)) // All options below are optional
                .setDefaultTapTargetValues()
                .targetRadius(40),

            TapTarget.forToolbarMenuItem(
                toolbar,
                menuItemAdd.itemId,
                getString(R.string.guide_add_pokemon_title),
                getString(R.string.guide_add_pokemon_description)
            ) // All options below are optional
                .setDefaultTapTargetValues()
                .targetRadius(30),

            TapTarget.forToolbarMenuItem(
                toolbar,
                menuItemRandom.itemId,
                getString(R.string.guide_random_pokemon_title),
                getString(R.string.guide_random_pokemon_description)
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
                    getString(R.string.guide_pokemon_edition_title),
                    getString(R.string.guide_pokemon_edition_description)
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

    drawer_layout.addDrawerListener(object : DrawerLayout.DrawerListener {
        override fun onDrawerStateChanged(newState: Int) {

        }

        override fun onDrawerSlide(drawerView: View, slideOffset: Float) {

        }

        override fun onDrawerClosed(drawerView: View) {

        }

        override fun onDrawerOpened(drawerView: View) {
            tapGuide.start()
            drawer_layout.removeDrawerListener(this)
        }
    })

}