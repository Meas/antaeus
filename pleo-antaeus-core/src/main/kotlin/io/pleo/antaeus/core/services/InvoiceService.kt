/*
    Implements endpoints related to invoices.
 */

package io.pleo.antaeus.core.services

import io.pleo.antaeus.core.exceptions.InvoiceNotFoundException
import io.pleo.antaeus.data.AntaeusDal
import io.pleo.antaeus.models.Currency
import io.pleo.antaeus.models.Invoice
import io.pleo.antaeus.models.InvoiceStatus

class InvoiceService(private val dal: AntaeusDal) {
    fun fetchAll(): List<Invoice> {
       return dal.fetchInvoices()
    }

    fun fetch(id: Int): Invoice {
        return dal.fetchInvoice(id) ?: throw InvoiceNotFoundException(id)
    }

    fun fetchPendingForCurrency(currency: Currency): List<Invoice>? {
        return dal.fetchPendingInvoiceForCurrency(currency)
    }

    fun fetchForStatus(status: InvoiceStatus): List<Invoice>? {
        return dal.fetchInvoiceForStatus(status)
    }

    fun fetchForStatuses(statuses: List<InvoiceStatus>): List<Invoice>? {
        return dal.fetchInvoiceForStatuses(statuses)
    }

    fun updateStatus(id: Int, status: InvoiceStatus): Invoice? {
        return dal.updateInvoiceStatus(id, status)
    }
}
