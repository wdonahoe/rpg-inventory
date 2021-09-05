package com.github.wdonahoe.rpginventory.util

import com.github.wdonahoe.rpginventory.model.QuantityAndUnit

object UnitUtil {

    private val separator = "\\s+".toRegex()
    private val altSeparator = "[a-zA-Z]".toRegex()

    fun parseQuantityAndUnit(value: String) : QuantityAndUnit? {
        var split = value.split(separator)
        if (split.none()) {
            return null
        }

        if (split.size == 1) {
            var lastNumericIndex = 0
            for (iter in value.withIndex()) {
                if (iter.value.isDigit()) {
                    lastNumericIndex = iter.index
                }
            }

            split = listOf(
                value.take(lastNumericIndex + 1),
                value.drop(lastNumericIndex + 1)
            )
        }

        val quantity = split[0].toDoubleOrNull() ?: return null

        return QuantityAndUnit(
            quantity,
            if (split.size > 1) {
                split.drop(1).joinToString(" ")
            } else {
                null
            }
        )
    }
}