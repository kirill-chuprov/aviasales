package com.aviasales.task.utils.common

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.Fragment
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.jakewharton.rxrelay2.PublishRelay
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable

abstract class BaseFragment<DB : ViewDataBinding> : Fragment() {

    protected var viewSubscriptions: Disposable? = null
    protected var compositeDisposable: CompositeDisposable? = null

    protected var viewBinding: DB? = null
        private set

    abstract fun resLayoutId(): Int

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        compositeDisposable = CompositeDisposable()
        viewBinding = DataBindingUtil.inflate(
            LayoutInflater.from(context),
            resLayoutId(),
            container,
            false
        )
        return viewBinding!!.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        compositeDisposable?.dispose()
        viewSubscriptions?.dispose()
        viewBinding = null
    }

}

interface BaseView<State> {

    fun initIntents()

    fun handleStates()

    fun render(state: State)
}


abstract class BaseViewModel<State> : ViewModel() {

    private val states = MutableLiveData<State>()
    private val viewIntentsConsumer: PublishRelay<Any> = PublishRelay.create()
    private var intentsDisposable: Disposable? = null

    protected abstract fun initState(): State

    private fun handleIntents() {
        intentsDisposable = Observable.merge(vmIntents(), viewIntents(viewIntentsConsumer))
            .scan(initState()) { previousState, stateChanges ->
                reduceState(previousState, stateChanges)
            }
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe { state -> states.value = state }
    }

    protected open fun vmIntents(): Observable<Any> = Observable.never()

    protected abstract fun viewIntents(intentStream: Observable<*>): Observable<Any>

    protected abstract fun reduceState(previousState: State, stateChange: Any): State

    fun viewIntentsConsumer() = viewIntentsConsumer.also {
        if (intentsDisposable == null)
            handleIntents()
    }

    fun stateReceived(): LiveData<State> = states

    override fun onCleared() {
        intentsDisposable?.dispose()
    }
}