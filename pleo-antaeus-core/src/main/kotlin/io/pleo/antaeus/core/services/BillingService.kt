package io.pleo.antaeus.core.services

import io.pleo.antaeus.core.exceptions.CurrencyMismatchException
import io.pleo.antaeus.core.exceptions.CustomerNotFoundException
import io.pleo.antaeus.core.exceptions.NetworkException
import io.pleo.antaeus.core.external.PaymentProvider
import io.pleo.antaeus.models.Currency
import io.pleo.antaeus.models.Invoice
import io.pleo.antaeus.models.InvoiceStatus
import io.pleo.antaeus.models.Timezones
import mu.KotlinLogging
import java.util.*

class BillingService(
    private val paymentProvider: PaymentProvider,
    private val invoiceService: InvoiceService
) {

    private val logger = KotlinLogging.logger {}
    private val retryableErrorStatuses: List<InvoiceStatus> = listOf(
        InvoiceStatus.ERROR_NETWORK_EXCEPTION,
        InvoiceStatus.ERROR_NOT_ENOUGH_FUNDS,
        InvoiceStatus.ERROR_FATAL,
        //when ERROR_CURRENCY_MISMATCH and ERROR_CUSTOMER_NOT_FOUND are fixed somewhere else
        InvoiceStatus.ERROR_SOLVED

    )

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
            if(this.isFirstHourOfFirstDay(currentTimeForCurrency))
                invoiceService.fetchPendingForCurrency(it)!!.map{invoice ->
                    this.chargeInvoice(invoice)
                }
        }
    }


    private fun chargeInvoice(invoice: Invoice) {
        try {
            if (paymentProvider.charge(invoice))
                println(invoiceService.updateStatus(invoice.id, InvoiceStatus.PAID))
            else
                println(invoiceService.updateStatus(invoice.id, InvoiceStatus.ERROR_NOT_ENOUGH_FUNDS))
        } catch (e: CustomerNotFoundException) {
            invoiceService.updateStatus(invoice.id, InvoiceStatus.ERROR_CUSTOMER_NOT_FOUND)
        } catch(e: CurrencyMismatchException) {
            invoiceService.updateStatus(invoice.id, InvoiceStatus.ERROR_CURRENCY_MISMATCH)
        } catch(e: NetworkException) {
            invoiceService.updateStatus(invoice.id, InvoiceStatus.ERROR_NETWORK_EXCEPTION)
        } catch(e: Exception) {
            println(invoiceService.updateStatus(invoice.id, InvoiceStatus.ERROR_FATAL))
            logger.error(e) { "Internal server error for invoice $invoice" }
        }
    }

    private fun checkErrorInvoices() {
        invoiceService.fetchForStatuses(retryableErrorStatuses)!!.map{
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