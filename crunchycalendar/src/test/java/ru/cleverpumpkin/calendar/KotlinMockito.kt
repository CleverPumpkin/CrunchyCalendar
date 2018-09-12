package ru.cleverpumpkin.calendar

import org.mockito.Mockito

/**
 * A workaround for Mockito.
 * It helps to mock a not-nullable Kotlin objects
 */
@Suppress("UNCHECKED_CAST")
fun <T> anyNotNullObject(): T {
    Mockito.any<T>()
    return null as T
}