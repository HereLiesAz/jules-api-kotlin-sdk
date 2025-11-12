package com.hereliesaz.julesapisdk

import kotlinx.serialization.json.Json
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

class SerializationTest {

    private val json = Json { ignoreUnknownKeys = true }

    @Test
    fun `can deserialize agentMessaged activity`() {
        val activityJson = """
            {
                "id": "1",
                "name": "activity1",
                "createTime": "2025-01-01T00:00:00Z",
                "updateTime": "2025-01-01T00:00:00Z",
                "prompt": "User prompt",
                "state": "COMPLETED",
                "agentMessaged": {
                    "agentMessage": "Hello from the agent"
                }
            }
        """.trimIndent()
        val activity = json.decodeFromString<Activity>(activityJson)
        assertTrue(activity is Activity.AgentMessagedActivity)
        assertEquals("Hello from the agent", (activity as Activity.AgentMessagedActivity).agentMessaged.agentMessage)
    }

    @Test
    fun `can deserialize planGenerated activity`() {
        val activityJson = """
            {
                "id": "2",
                "name": "activity2",
                "createTime": "2025-01-01T00:00:00Z",
                "updateTime": "2025-01-01T00:00:00Z",
                "prompt": "User prompt",
                "state": "COMPLETED",
                "planGenerated": {
                    "plan": {
                        "id": "plan1",
                        "steps": [
                            { "id": "step1", "title": "Step 1", "description": "First step", "index": 0 },
                            { "id": "step2", "title": "Step 2", "description": "Second step", "index": 1 }
                        ]
                    }
                }
            }
        """.trimIndent()
        val activity = json.decodeFromString<Activity>(activityJson)
        assertTrue(activity is Activity.PlanGeneratedActivity)
        assertEquals(2, (activity as Activity.PlanGeneratedActivity).planGenerated.plan.steps?.size)
    }

    @Test
    fun `can deserialize changeSet artifact`() {
        val artifactJson = """
            {
                "changeSet": {
                    "source": "file.kt",
                    "gitPatch": {
                        "unidiffPatch": "@@ -1,1 +1,1 @@"
                    }
                }
            }
        """.trimIndent()
        val artifact = json.decodeFromString<Artifact>(artifactJson)
        assertTrue(artifact is Artifact.ChangeSetArtifact)
        assertEquals("file.kt", (artifact as Artifact.ChangeSetArtifact).changeSet.source)
    }
}
