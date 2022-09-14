package com.jacoco.aggregate.reports.utilities

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

internal class SplitUtilsTest {

    @Test
    fun `deve formatar um texto`() {

        val input: String = "Hello     World"
        val result = StringUtils.split(input)

        assertEquals(2, result.size())
    }
}