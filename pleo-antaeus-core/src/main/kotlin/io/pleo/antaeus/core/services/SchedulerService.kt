package io.pleo.antaeus.core.services

import java.util.*
import kotlin.concurrent.scheduleAtFixedRate

class SchedulerService(
    private val billingService: BillingService
) {
    fun init() {
        val timer = Timer("schedule", true)

        // try every hour and on startup
        timer.scheduleAtFixedRate(1000, 3600000) {
            billingService.checkBeforeCharge()
        }
    }
}