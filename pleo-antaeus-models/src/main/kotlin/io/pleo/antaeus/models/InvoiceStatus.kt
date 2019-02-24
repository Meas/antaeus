package io.pleo.antaeus.models

enum class InvoiceStatus {
    PENDING,
    PAID,
    ERROR_NOT_ENOUGH_FUNDS,
    ERROR_CUSTOMER_NOT_FOUND,
    ERROR_CURRENCY_MISMATCH,
    ERROR_NETWORK_EXCEPTION,
    ERROR_SOLVED,
    ERROR_FATAL
}