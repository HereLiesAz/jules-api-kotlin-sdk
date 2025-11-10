package com.jules.sdk

import kotlinx.serialization.Serializable

/**
 * Represents the context of a GitHub repository.
 *
 * @property startingBranch The starting branch of the repository.
 */
@Serializable
data class GithubRepoContext(
    val startingBranch: String
)

/**
 * Represents the source context for a session.
 *
 * @property source The source of the context.
 * @property githubRepoContext The GitHub repository context, if applicable.
 */
@Serializable
data class SourceContext(
    val source: String,
    val githubRepoContext: GithubRepoContext? = null
)

/**
 * The automation mode for a session.
 */
@Serializable
enum class AutomationMode {
    AUTOMATION_MODE_UNSPECIFIED,
    AUTO_CREATE_PR
}

/**
 * The state of a session.
 */
@Serializable
enum class SessionState {
    STATE_UNSPECIFIED,
    QUEUED,
    PLANNING,
    AWAITING_PLAN_APPROVAL,
    AWAITING_USER_FEEDBACK,
    IN_PROGRESS,
    PAUSED,
    FAILED,
    COMPLETED
}

/**
 * A request to create a new session.
 *
 * @property prompt The initial prompt for the session.
 * @property sourceContext The source context for the session.
 * @property title An optional title for the session.
 * @property requirePlanApproval Whether the session requires plan approval.
 * @property automationMode The automation mode for the session.
 */
@Serializable
data class CreateSessionRequest(
    val prompt: String,
    val sourceContext: SourceContext,
    val title: String? = null,
    val requirePlanApproval: Boolean? = null,
    val automationMode: AutomationMode? = null
)

/**
 * A pull request created by a session.
 *
 * @property url The URL of the pull request.
 * @property title The title of the pull request.
 * @property description The description of the pull request.
 */
@Serializable
data class PullRequest(
    val url: String,
    val title: String,
    val description: String
)

/**
 * The output of a session.
 *
 * @property pullRequest The pull request created by the session, if any.
 */
@Serializable
data class SessionOutput(
    val pullRequest: PullRequest? = null
)

/**
 * A session with the Jules AI.
 *
 * @property name The name of the session.
 * @property id The unique ID of the session.
 * @property createTime The time the session was created.
 * @property updateTime The time the session was last updated.
 * @property state The current state of the session.
 * @property url The URL of the session.
 * @property prompt The initial prompt for the session.
 * @property sourceContext The source context for the session.
 * @property title An optional title for the session.
 * @property requirePlanApproval Whether the session requires plan approval.
 * @property automationMode The automation mode for the session.
 * @property outputs The outputs of the session.
 */
@Serializable
data class Session(
    val name: String,
    val id: String,
    val createTime: String,
    val updateTime: String,
    val state: SessionState,
    val url: String,
    val prompt: String,
    val sourceContext: SourceContext,
    val title: String? = null,
    val requirePlanApproval: Boolean? = null,
    val automationMode: AutomationMode? = null,
    val outputs: List<SessionOutput>? = null
)

/**
 * A response containing a list of sessions.
 *
 * @property sessions The list of sessions.
 * @property nextPageToken A token for pagination.
 */
@Serializable
data class ListSessionsResponse(
    val sessions: List<Session>? = null,
    val nextPageToken: String? = null
)

/**
 * An artifact produced by an activity.
 *
 * @property type The type of the artifact.
 * @property content The content of the artifact.
 */
@Serializable
data class Artifact(
    val type: String,
    val content: String
)

/**
 * An activity within a session.
 *
 * @property id The unique ID of the activity.
 * @property createTime The time the activity was created.
 * @property updateTime The time the activity was last updated.
 * @property prompt The prompt that triggered the activity.
 * @property state The current state of the activity.
 * @property artifacts The artifacts produced by the activity.
 */
@Serializable
data class Activity(
    val id: String,
    val createTime: String,
    val updateTime: String,
    val prompt: String,
    val state: String,
    val artifacts: List<Artifact>? = null
)

/**
 * A response containing a list of activities.
 *
 * @property activities The list of activities.
 * @property nextPageToken A token for pagination.
 */
@Serializable
data class ListActivitiesResponse(
    val activities: List<Activity>? = null,
    val nextPageToken: String? = null
)

/**
 * A source for the Jules AI.
 *
 * @property name The name of the source.
 * @property id The unique ID of the source.
 * @property createTime The time the source was created.
 * @property updateTime The time the source was last updated.
 * @property url The URL of the source.
 * @property type The type of the source.
 */
@Serializable
data class Source(
    val name: String,
    val id: String,
    val createTime: String,
    val updateTime: String,
    val url: String,
    val type: String
)

/**
 * A response containing a list of sources.
 *
 * @property sources The list of sources.
 * @property nextPageToken A token for pagination.
 */
@Serializable
data class ListSourcesResponse(
    val sources: List<Source>? = null,
    val nextPageToken: String? = null
)

/**
 * A detail of a Google API error.
 *
 * @property type The type of the error.
 * @property message The error message.
 */
@Serializable
data class ErrorDetail(
    val type: String,
    val message: String
)

/**
 * A Google API error.
 *
 * @property code The HTTP status code.
 * @property message The error message.
 * @property status The error status.
 * @property details The details of the error.
 */
@Serializable
data class GoogleApiError(
    val code: Int,
    val message: String,
    val status: String,
    val details: List<ErrorDetail>? = null
)
