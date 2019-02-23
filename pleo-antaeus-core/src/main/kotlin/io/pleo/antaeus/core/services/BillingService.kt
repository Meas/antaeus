package io.pleo.antaeus.core.services

import io.pleo.antaeus.core.external.PaymentProvider
import io.pleo.antaeus.models.Currency
import io.pleo.antaeus.models.Invoice
import io.pleo.antaeus.models.InvoiceStatus
import io.pleo.antaeus.models.Timezones
import java.time.DayOfWeek
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
            if(this.isFirstHourOfFirstDay(currentTimeForCurrency))
                invoiceService.fetchPendingForCurrency(it)!!.map{invoice ->
                    this.chargeInvoice(invoice)
                }
        }
        /*invoiceService.fetchPendingForCurrency(Currency.DKK)!!.map{
            println(it)
        }*/
    }


    private fun chargeInvoice(invoice: Invoice) {
        println(invoice)
    }

    private fun checkErrorInvoices() {
        invoiceService.fetchForStatus(InvoiceStatus.ERROR)!!.map{
            chargeInvoice(it)
        }
    }

    //might abstract these 2 to CurrencyService
    private fun getCurrentTimeForCurrency(currency: Currency) : GregorianCalendar {
        return GregorianCalendar(TimeZone.getTimeZone(getTimezoneForCurrency(currency)))
    }

    private fun getTimezoneForCurrency(currency: Currency): String {
        return Timezones.valueOf(currency.toString()).value
    }

    private fun isFirstHourOfFirstDay(time: GregorianCalendar): Boolean {
        return time.get(GregorianCalendar.HOUR_OF_DAY) == 0
                /*&& time.get(GregorianCalendar.DAY_OF_MONTH) == 1*/
    }
}