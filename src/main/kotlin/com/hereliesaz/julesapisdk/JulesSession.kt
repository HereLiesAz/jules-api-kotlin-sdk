package com.hereliesaz.julesapisdk

class JulesSession(
    private val client: JulesClient,
    private val session: Session
) {
    suspend fun sendMessage(prompt: String): MessageResponse {
        return client.sendMessage(session.id, prompt)
    }
}
