package com.hereliesaz.julesapisdk

import kotlinx.serialization.json.Json
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class SchemasTest {

    @Test
    fun `ErrorDetail deserializes @type field`() {
        val json = """
            {
                "@type": "some_type",
                "message": "some_message"
            }
        """.trimIndent()
        val errorDetail = Json.decodeFromString<ErrorDetail>(json)
        assertEquals("some_type", errorDetail.type)
        assertEquals("some_message", errorDetail.message)
    }
}
