package it.gbresciani.stargazers.ui

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.app.LoaderManager
import android.support.v4.content.Loader

/** Base Activity ensuring that the given Presenter is detached form the the Activity lifecycle */
abstract class PresenterFragment<V : View, P : Presenter<V>> : Fragment() {

    /** Factory function that has to be implemented by subclasses to provide a Presenter */
    abstract val presenterGenerator: () -> P

    protected var presenter: P? = null
    private val loaderCallbacks: LoaderManager.LoaderCallbacks<P> = object : LoaderManager.LoaderCallbacks<P> {
        override fun onCreateLoader(id: Int, args: Bundle?): Loader<P> =
                PresenterLoader(context, presenterGenerator)

        override fun onLoadFinished(loader: Loader<P>, data: P) {
            presenter = data
        }

        override fun onLoaderReset(loader: Loader<P>) {
            presenter = null
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        loaderManager.initLoader(101, null, loaderCallbacks)
    }

    override fun onStart() {
        super.onStart()
        presenter?.onAttach(this as V)
    }

    override fun onStop() {
        presenter?.onDetach()
        super.onStop()
    }
}