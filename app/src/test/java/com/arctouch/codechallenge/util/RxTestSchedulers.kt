package com.arctouch.codechallenge.util

import com.arctouch.codechallenge.util.rx.RxSchedulers
import io.reactivex.Scheduler
import io.reactivex.schedulers.TestScheduler

class RxTestSchedulers(private val testScheduler: TestScheduler) : RxSchedulers {

    override fun io(): Scheduler = testScheduler

    override fun ui(): Scheduler = testScheduler

}