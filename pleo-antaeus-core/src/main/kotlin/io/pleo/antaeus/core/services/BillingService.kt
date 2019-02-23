package io.pleo.antaeus.core.services

import io.pleo.antaeus.core.external.PaymentProvider
import io.pleo.antaeus.models.Currency
import io.pleo.antaeus.models.InvoiceStatus
import io.pleo.antaeus.models.Timezones
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*

class BillingService(
    private val paymentProvider: PaymentProvider,
    private val invoiceService: InvoiceService
) {
    fun getItem(inputCurrency: String): Any {
        return object {
            val currency: Currency = Currency.valueOf(inputCurrency)
        }
    }
    fun checkBeforeCharge() {
        this.checkPendingInvoices()
        this.checkErrorInvoices()
        /*val dateFormat = DateTimeFormatter.ofPattern("yyyy-MM-dd")
        dateFormat.timeZone = TimeZone.getTimeZone("GMT")
        val current = LocalDateTime.now()
        println("Date is "+current.format(dateFormat))*/


        /*val today: Calendar= Calendar.getInstance()*/
        /*val firstDayOfMonth = calendar.getActualMinimum(Calendar.DATE)
        calendar.set(Calendar.DATE, firstDayOfMonth)*/

        /*println("Current Date and Time is: $calendar")*/
    }

    private fun checkPendingInvoices() {
        Currency.values().forEach {
            val currentTimeForCurrency = this.getCurrentTimeForCurrency(it)
            println(currentTimeForCurrency)
        }
        invoiceService.fetchInvoiceWithStatus(InvoiceStatus.PAID)!!.map{
            println(it)
        }
        /*invoiceService.fetchPendingForCurrency(Currency.DKK)!!.map{
            println(it)
        }*/
    }

    private fun checkErrorInvoices() {
        return
    }

    private fun getCurrentTimeForCurrency(currency: Currency) : GregorianCalendar {
        return GregorianCalendar(TimeZone.getTimeZone(getTimezoneForCurrency(currency)))
    }

    private fun getTimezoneForCurrency(currency: Currency): String {
        return Timezones.valueOf(currency.toString()).value
    }
}