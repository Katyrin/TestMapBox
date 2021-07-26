package com.katyrin.testmapbox

import io.reactivex.rxjava3.android.plugins.RxAndroidPlugins
import io.reactivex.rxjava3.core.Scheduler
import io.reactivex.rxjava3.plugins.RxJavaPlugins
import io.reactivex.rxjava3.schedulers.Schedulers
import org.junit.rules.TestRule
import org.junit.runner.Description
import org.junit.runners.model.Statement

class RxRule(val scheduler: Scheduler = Schedulers.trampoline()) : TestRule {

    private val uncaughtExceptions = mutableListOf<Throwable>()

    override fun apply(base: Statement, d: Description): Statement =
        object : Statement() {
            override fun evaluate() {
                RxJavaPlugins.setErrorHandler { uncaughtExceptions.add(it) }
                RxJavaPlugins.setIoSchedulerHandler { scheduler }
                RxJavaPlugins.setComputationSchedulerHandler { scheduler }
                RxJavaPlugins.setNewThreadSchedulerHandler { scheduler }
                RxJavaPlugins.setSingleSchedulerHandler { scheduler }
                RxAndroidPlugins.setInitMainThreadSchedulerHandler { scheduler }
                RxAndroidPlugins.setMainThreadSchedulerHandler { scheduler }

                try {
                    base.evaluate()
                    assertNoErrors()
                } finally {
                    RxJavaPlugins.reset()
                    RxAndroidPlugins.reset()
                }
            }
        }

    private fun assertNoErrors() {
        if (uncaughtExceptions.isNotEmpty()) {
            throw AssertionError("Expected no errors but found $uncaughtExceptions").apply {
                uncaughtExceptions.forEach(::addSuppressed)
            }
        }
    }
}