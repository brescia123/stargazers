package it.gbresciani.stargazers.ui

import io.reactivex.Observable
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.observers.DisposableObserver
import io.reactivex.subjects.BehaviorSubject
import io.reactivex.subjects.PublishSubject
import it.gbresciani.stargazers.interactors.ViewState
import kotlin.reflect.KFunction1
import kotlin.reflect.KFunction2

typealias Binder<T> = () -> Observable<T>

abstract class MVIPresenter<V : View, VS : ViewState> : Presenter<V>() {

    class Relay<in V: View, T: Any>(val pair: Pair<PublishSubject<T>, KFunction1<V, Observable<T>>>)

    private lateinit var producer: Observable<VS>

    abstract fun bindIntents(): Observable<VS>
    abstract val viewStateRender: KFunction2<V, VS, Boolean>

    private val viewStateSubject: BehaviorSubject<VS> = BehaviorSubject.create<VS>()

    private var firstRun = true
    private val intentPublishSubjects = listOf<PublishSubject<*>>()

    private val intentsRelays = mutableListOf<Relay<V, *>>()


    private val intentsCompositeDisposable: CompositeDisposable = CompositeDisposable()
    private var viewStateDisposable: Disposable? = null


    override fun onAttach(v: V) {
        super.onAttach(v)

        if (firstRun) {
            producer = bindIntents()
            bindViewState(producer)
        }

        // Subscribe to all subjects
        intentsRelays.forEach { intentsCompositeDisposable.add(bind(v, it)) }

        // Subscribe to viewState observable
        viewStateSubject.subscribe({ viewStateRender.invoke(v, it) })

        firstRun = false
    }

    override fun onDetach() {
        super.onDetach()
        intentsCompositeDisposable.clear()
        viewStateDisposable?.dispose()
    }

    /**
     * Generates a Relay and adds it to the list
     */
    protected fun <T : Any> intent(binder: KFunction1<V, Observable<T>>): Observable<T> = PublishSubject.create<T>()
            .apply { intentsRelays.add(Relay<V, T>(this to binder)) }


    /**
     * Subscribe a DisposableObserver using the given subject to the Observable produced by the given Binder
     */
    private fun <T : Any> bind(view: V, relay: Relay<V, T>): Disposable {
        val (subject, binder) = relay.pair
        return binder(view).subscribeWith(object : DisposableObserver<T>() {
            override fun onComplete() = subject.onComplete()
            override fun onError(e: Throwable?) = subject.onError(e)
            override fun onNext(t: T) = subject.onNext(t)
        })
    }

    private fun bindViewState(producer: Observable<VS>) {
        producer.subscribeWith(object : DisposableObserver<VS>() {
            override fun onComplete() = viewStateSubject.onComplete()
            override fun onError(e: Throwable?) = viewStateSubject.onError(e)
            override fun onNext(t: VS) = viewStateSubject.onNext(t)
        })
    }

}
