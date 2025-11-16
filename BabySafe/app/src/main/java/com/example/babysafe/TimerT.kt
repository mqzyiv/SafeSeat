package com.example.babysafe

import java.util.TimerTask

class TimerT: TimerTask {
    private lateinit var activity : MainActivity
    constructor( activity : MainActivity ) {
        this.activity = activity
    }
    override fun run() {
        activity.update()
    }
}