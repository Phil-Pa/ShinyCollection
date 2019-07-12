package de.phil.solidsabissupershinysammlung.presenter

import de.phil.solidsabissupershinysammlung.view.MainView
import org.junit.Test
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.Mockito.mock

class PresenterTest {

    @Mock
    val mainView: MainView = mock(MainView::class.java)

    @Test(expected = IllegalStateException::class)
    fun testTabIndexOutOfRange() {

        var tabIndex = 3

        `when`(mainView.startAddNewPokemonActivity(tabIndex)).then {}
        `when`(mainView.getCurrentTabIndex()).thenReturn(tabIndex)


        val mainPresenter = MainPresenter(mainView)
        mainPresenter.startAddNewPokemonActivity()

        tabIndex = 7
        `when`(mainView.getCurrentTabIndex()).thenReturn(tabIndex)
        mainPresenter.startAddNewPokemonActivity()

    }


}