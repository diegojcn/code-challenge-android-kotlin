package com.arctouch.codechallenge.util.rx

import io.reactivex.Scheduler

interface RxSchedulers {

    fun io(): Scheduler

    fun ui(): Scheduler
}