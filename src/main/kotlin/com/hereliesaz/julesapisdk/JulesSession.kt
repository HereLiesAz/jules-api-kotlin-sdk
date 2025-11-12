package com.hereliesaz.julesapisdk

class JulesSession(
    private val client: JulesClient,
    val session: Session
) {
    suspend fun sendMessage(prompt: String): SdkResult<MessageResponse> {
        return client.sendMessage(session.name, prompt)
    }
}
