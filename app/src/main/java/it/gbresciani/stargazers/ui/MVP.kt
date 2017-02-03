package it.gbresciani.stargazers.ui

import java.lang.ref.WeakReference

/**
 * Base Interface for all the presenters.
 *
 * @param T The type of view (which extends [View]) the presenter is controlling.
 */
interface PresenterApi<in T : View> {
    fun onAttach(v: T)
    fun onDetach()
}

/** Base Class for all the presenters. It takes care of implementing the attach/detach view mechanism. */
abstract class Presenter<T : View> : PresenterApi<T> {
    /** holds the view reference when it is attached. if the Presenter is not attached ot any View it is null. */
    private var viewRef: WeakReference<T>? = null

    /** convenient method to get the View */
    fun view(): T? = viewRef?.get()

    /**
     * This method should be called every time a View is attached to the Presenter (e.g. configuration changes)
     * or it is re-created. Here the presenter should render the view.
     * (typically within onStart())
     *
     * @param v the [View] to be attached
     */
    override fun onAttach(v: T) {
        this.viewRef = WeakReference(v)
    }

    /**
     * This method should be called every time the View is detached from the Presenter (e.g. configuration changes).
     * (typically within onStop())
     */
    override fun onDetach() {
        viewRef?.clear()
        viewRef = null
    }
}

/** Base Interface for all the components (Activities, Fragments..) that act as views in MVP. */
interface View