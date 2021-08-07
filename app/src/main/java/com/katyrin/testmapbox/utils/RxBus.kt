package com.katyrin.testmapbox.utils

import io.reactivex.rxjava3.disposables.Disposable
import io.reactivex.rxjava3.subjects.PublishSubject
import io.reactivex.rxjava3.subjects.Subject

class RxBus {

    private val bus: Subject<Unit> = PublishSubject.create()

    fun publish(): Unit = bus.onNext(Unit)

    fun subscribe(block: () -> Unit): Disposable =
        bus.subscribe { block() }
}