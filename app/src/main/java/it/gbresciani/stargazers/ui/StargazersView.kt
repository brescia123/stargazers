package it.gbresciani.stargazers.ui

import io.reactivex.Observable
import it.gbresciani.stargazers.interactors.StargazersViewState

interface StargazersView: View {
    fun searchIntent(): Observable<Pair<String, String>>
    fun loadNextPageIntent(): Observable<Boolean>
    fun render(viewState: StargazersViewState): Boolean
}


