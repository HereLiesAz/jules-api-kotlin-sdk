package com.jules.sdk

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

// ================================================================================================
// GitHub / Source Schemas
// ================================================================================================

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
 * @property source The name of the source (e.g., "sources/github/owner/repo").
 * @property githubRepoContext The GitHub repository context, if applicable.
 */
@Serializable
data class SourceContext(
    val source: String,
    val githubRepoContext: GithubRepoContext? = null
)

// ================================================================================================
// Session Schemas
// ================================================================================================

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
 * @property name The resource name of the session.
 * @property id The unique ID of the session.
 * @property createTime The time the session was created.
 * @property updateTime The time the session was last updated.
 * @property state The current state of the session.
 * @property url The URL of the session.
 * @property prompt The initial prompt for the session.
 * @property sourceContext The source context for the session.
 * @property title An optional title for the session.
 * @property requirePlanApproval Whether the session requires plan approval.
 *_
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

// ================================================================================================
// Activity & Artifact Schemas (CORRECTED)
// ================================================================================================

/**
 * Represents a Git patch artifact.
 *
 * @property unidiffPatch The patch in unified diff format.
 * @property baseCommitId The commit ID the patch is based on.
 * @property suggestedCommitMessage A commit message for the patch.
 */
@Serializable
data class GitPatch(
    val unidiffPatch: String,
    val baseCommitId: String? = null,
    val suggestedCommitMessage: String? = null
)

/**
* Represents a change set artifact.
*
* @property source The source the change set applies to.
* @property gitPatch The Git patch details.
*/
@Serializable
data class ChangeSet(
    val source: String,
    val gitPatch: GitPatch
)

/**
 * Represents a media artifact.
 *
 * @property mimeType The MIME type of the media.
 * @property content The base64-encoded content.
 */
@Serializable
data class Media(
    val mimeType: String,
    val content: String
)

/**
 * Represents a bash command output artifact.
 *
 * @property command The command that was run.
 * @property output The stdout/stderr output.
 * @property exitCode The exit code of the command.
 */
@Serializable
data class BashOutput(
    val command: String,
    val output: String,
    val exitCode: Int
)

/**
 * An artifact produced by an activity.
 * This uses the "optional fields" pattern. Only one of the fields will be present.
 *
 * @property changeSet A code change set.
 * @property media A media file.
 * @property bashOutput The output of a bash command.
 */
@Serializable
data class Artifact(
    // Original (incorrect) fields - a "type" string is not what the API returns
    // val type: String,
    // val content: String

    // Corrected (optional fields)
    val changeSet: ChangeSet? = null,
    val media: Media? = null,
    val bashOutput: BashOutput? = null
)

/**
 * Represents a message from the agent.
 *
 * @property agentMessage The content of the agent's message.
 */
@Serializable
data class AgentMessaged(
    val agentMessage: String
)

/**
 * Represents a message from the user.
 *
 * @property userMessage The content of the user's message.
 */
@Serializable
data class UserMessaged(
    val userMessage: String
)

/**
 * Represents a step in a plan.
 *
 * @property id The unique ID of the step.
 * @property title The title of the step.
 * @property description The description of the step.
 * @property index The order index of the step.
 */
@Serializable
data class PlanStep(
    val id: String,
    val title: String,
    val description: String,
    val index: Int
)

/**
 * Represents a plan.
 *
 * @property id The unique ID of the plan.
 * @property steps The list of steps in the plan.
 */
@Serializable
data class Plan(
    val id: String,
    val steps: List<PlanStep>? = null
)

/**
 * Represents a plan generation activity.
 *
 * @property plan The plan that was generated.
 */
@Serializable
data class PlanGenerated(
    val plan: Plan
)

/**
 * Represents a plan approval activity.
 *
 * @property planId The ID of the plan that was approved.
 */
@Serializable
data class PlanApproved(
    val planId: String
)

/**
 * Represents a progress update activity.
 *
 * @property title The title of the progress update.
 * @property description A description of the progress.
 */
@Serializable
data class ProgressUpdated(
    val title: String,
    val description: String? = null
)

/**
 * Represents a session completion activity.
 *
 * @property output The output of the session.
 */
@Serializable
data class SessionCompleted(
    val output: SessionOutput
)

/**
 * Represents a session failure activity.
 *
 * @property reason The reason for the failure.
 */
@Serializable
data class SessionFailed(
    val reason: String
)

/**
 * An activity within a session.
 * This uses the "optional fields" pattern. Only one of the content fields will be present.
 *
 * @property id The unique ID of the activity.
 * @property name The resource name of the activity.
 * @property description An optional description of the activity.
 * @property createTime The time the activity was created.
 * @property updateTime The time the activity was last updated.
 * @property prompt The prompt that triggered the activity (legacy, use userMessaged).
 * @property state The current state of the activity.
 * @property artifacts The artifacts produced by the activity.
 * @property originator The originator of the activity (user or agent).
 *
 * @property agentMessaged Content if the agent sent a message.
 * @property userMessaged Content if the user sent a message.
 * @property planGenerated Content if a plan was generated.
 * @property planApproved Content if a plan was approved.
 * @property progressUpdated Content if progress was updated.
 * @property sessionCompleted Content if the session completed.
 * @property sessionFailed Content if the session failed.
 */
@Serializable
data class Activity(
    val id: String,
    val name: String,
    val description: String? = null, // Made optional, as per TS SDK fix
    val createTime: String,
    val updateTime: String,
    val prompt: String, // This is often present but redundant
    val state: String,
    val artifacts: List<Artifact>? = null,
    val originator: String? = null, // 'user' or 'agent'

    // Content fields (only one will be populated)
    val agentMessaged: AgentMessaged? = null,
    val userMessaged: UserMessaged? = null,
    val planGenerated: PlanGenerated? = null,
    val planApproved: PlanApproved? = null,
    val progressUpdated: ProgressUpdated? = null,
    val sessionCompleted: SessionCompleted? = null,
    val sessionFailed: SessionFailed? = null
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

// ================================================================================================
// Source Schemas (Continued)
// ================================================================================================

/**
 * A source for the Jules AI.
 *
 * @property name The name of the source.
 * @property id The unique ID of the source.
 * @property createTime The time the source was created.
 * @property updateTime The time the source was last updated.
 * @property url The URL of the source.
 * @property type The type of the source.
 * @property githubRepo The GitHub repo details, if this is a GitHub source.
 */
@Serializable
data class Source(
    val name: String,
    val id: String,
    val createTime: String,
    val updateTime: String,
    val url: String,
    val type: String,
    val githubRepo: GithubRepoContext? = null // Add this based on TS SDK
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

// ================================================================================================
// Error Schemas
// ================================================================================================

/**
 * A detail of a Google API error.
 *
 * @property type The type of the error.
 * @property message The error message.
 */
@Serializable
data class ErrorDetail(
    @SerialName("@type") val type: String,
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

/**
 * The response from sending a message.
 *
 * @property message A confirmation message.
 */
@Serializable
data class MessageResponse(
    val message: String
)
