
# Jules API  |  Google for Developers

*   On this page
*   [Introduction](#introduction)
*   [Authentication](#authentication)
    *   [Generate Your API Key](#generate-your-api-key)
    *   [Use Your API Key](#use-your-api-key)
*   [API concepts](#api-concepts)
*   [Quickstart: Your first API call](#quickstart:-your-first-api-call)
    *   [Step 1: List your available sources](#step-1:-list-your-available-sources)
    *   [Step 2: Create a new session](#step-2:-create-a-new-session)
    *   [Step 3: Listing sessions](#step-3:-listing-sessions)
    *   [Step 4: Approve plan](#step-4:-approve-plan)
    *   [Step 5: Activities and interacting with the agent](#step-5:-activities-and-interacting-with-the-agent)
*   [Full API reference](#full-api-reference)

*   [Home](https://developers.google.com/)
*   [Products](https://developers.google.com/products)
*   [Jules API](https://developers.google.com/jules/api)

Was this helpful?

# Jules API bookmark\_borderbookmark Stay organized with collections Save and categorize content based on your preferences.

content\_copy

*   On this page
*   [Introduction](#introduction)
*   [Authentication](#authentication)
    *   [Generate Your API Key](#generate-your-api-key)
    *   [Use Your API Key](#use-your-api-key)
*   [API concepts](#api-concepts)
*   [Quickstart: Your first API call](#quickstart:-your-first-api-call)
    *   [Step 1: List your available sources](#step-1:-list-your-available-sources)
    *   [Step 2: Create a new session](#step-2:-create-a-new-session)
    *   [Step 3: Listing sessions](#step-3:-listing-sessions)
    *   [Step 4: Approve plan](#step-4:-approve-plan)
    *   [Step 5: Activities and interacting with the agent](#step-5:-activities-and-interacting-with-the-agent)
*   [Full API reference](#full-api-reference)

## Introduction

The Jules API lets you programmatically access Jules's capabilities to automate and enhance your software development lifecycle. You can use the API to create custom workflows, automate tasks like bug fixing and code reviews, and embed Jules's intelligence directly into the tools you use every day, such as Slack, Linear, and GitHub.

**Note:** The Jules API is in an alpha release, which means it is experimental. Be aware that we may change specifications, API keys, and definitions as we work toward stabilization. In the future, we plan to maintain at least one stable and one experimental version.

## Authentication

To get started with the Jules API, you'll need an API key.

### Generate Your API Key

In the Jules web app, go to the **[Settings](https://jules.google.com/settings#api)** page to create a new API key. You can have at most 3 API keys at a time.

![Jules API Key creation interface](/static/jules/assets/jules-api-key-settings.png)

### Use Your API Key

To authenticate your requests, pass the API key in the `X-Goog-Api-Key` header of your API calls.

**Important:** Keep your API keys secure. Don't share them or embed them in public code. For your protection, any API keys found to be publicly exposed will be [automatically disabled](https://cloud.google.com/resource-manager/docs/organization-policy/restricting-service-accounts#disable-exposed-keys) to prevent abuse.

## API concepts

The Jules API is built around a few core resources. Understanding these will help you use the API effectively.

**Source**

An input source for the agent (e.g., a GitHub repository). Before using a source using the API, you must first [install the Jules GitHub app](https://jules.google/docs) through the Jules web app.

**Session**

A continuous unit of work within a specific context, similar to a chat session. A session is initiated with a prompt and a source.

**Activity**

A single unit of work within a Session. A Session contains multiple activities from both the user and the agent, such as generating a plan, sending a message, or updating progress.

## Quickstart: Your first API call

We'll walk through creating your first session with the Jules API using curl.

### Step 1: List your available sources

First, you need to find the name of the source you want to work with (e.g., your GitHub repo). This command will return a list of all sources you have connected to Jules.

```
curl 'https://jules.googleapis.com/v1alpha/sources' \
    -H 'X-Goog-Api-Key: YOUR_API_KEY'
```

![](https://storage.googleapis.com/pieces-web-extensions-cdn/pieces.png)Copy And Save![](https://storage.googleapis.com/pieces-web-extensions-cdn/link.png)Share![](https://storage.googleapis.com/pieces-web-extensions-cdn/copilot.png)Ask Copilot![](https://storage.googleapis.com/pieces-web-extensions-cdn/settings.png)

The response will look something like this:

```
{
  "sources": [
    {
      "name": "sources/github/bobalover/boba",
      "id": "github/bobalover/boba",
      "githubRepo": {
        "owner": "bobalover",
        "repo": "boba"
      }
    }
  ],
  "nextPageToken": "github/bobalover/boba-web"
}
```

![](https://storage.googleapis.com/pieces-web-extensions-cdn/pieces.png)Copy And Save![](https://storage.googleapis.com/pieces-web-extensions-cdn/link.png)Share![](https://storage.googleapis.com/pieces-web-extensions-cdn/copilot.png)Ask Copilot![](https://storage.googleapis.com/pieces-web-extensions-cdn/settings.png)

### Step 2: Create a new session

Now, create a new session. You'll need the source name from the previous step. This request tells Jules to create a boba app in the specified repository.

```
curl 'https://jules.googleapis.com/v1alpha/sessions' \
    -X POST \
    -H "Content-Type: application/json" \
    -H 'X-Goog-Api-Key: YOUR_API_KEY' \
    -d '{
      "prompt": "Create a boba app!",
      "sourceContext": {
        "source": "sources/github/bobalover/boba",
        "githubRepoContext": {
          "startingBranch": "main"
        }
      },
      "automationMode": "AUTO_CREATE_PR",
      "title": "Boba App"
    }'
```

![](https://storage.googleapis.com/pieces-web-extensions-cdn/pieces.png)Copy And Save![](https://storage.googleapis.com/pieces-web-extensions-cdn/link.png)Share![](https://storage.googleapis.com/pieces-web-extensions-cdn/copilot.png)Ask Copilot![](https://storage.googleapis.com/pieces-web-extensions-cdn/settings.png)

The `automationMode` field is optional. By default, no PR will be automatically created.

The immediate response will look something like this:

```
{
        "name": "sessions/31415926535897932384",
        "id": "31415926535897932384",
        "title": "Boba App",
        "sourceContext": {
          "source": "sources/github/bobalover/boba",
          "githubRepoContext": {
            "startingBranch": "main"
          }
        },
        "prompt": "Create a boba app!"
      }
```

![](https://storage.googleapis.com/pieces-web-extensions-cdn/pieces.png)Copy And Save![](https://storage.googleapis.com/pieces-web-extensions-cdn/link.png)Share![](https://storage.googleapis.com/pieces-web-extensions-cdn/copilot.png)Ask Copilot![](https://storage.googleapis.com/pieces-web-extensions-cdn/settings.png)

You can poll the latest session information using GetSession or ListSessions. For example, if a PR was automatically created, you can see the PR in the session output.

```
{
  "name": "sessions/31415926535897932384",
  "id": "31415926535897932384",
  "title": "Boba App",
  "sourceContext": {
    "source": "sources/github/bobalover/boba",
    "githubRepoContext": {
      "startingBranch": "main"
    }
  },
  "prompt": "Create a boba app!",
  "outputs": [
    {
      "pullRequest": {
        "url": "https://github.com/bobalover/boba/pull/35",
        "title": "Create a boba app",
        "description": "This change adds the initial implementation of a boba app."
      }
    }
  ]
}
    
```

![](https://storage.googleapis.com/pieces-web-extensions-cdn/pieces.png)Copy And Save![](https://storage.googleapis.com/pieces-web-extensions-cdn/link.png)Share![](https://storage.googleapis.com/pieces-web-extensions-cdn/copilot.png)Ask Copilot![](https://storage.googleapis.com/pieces-web-extensions-cdn/settings.png)

By default, sessions created through the API will have their plans automatically approved. If you want to create a session that requires explicit plan approval, set the `requirePlanApproval` field to `true`.

### Step 3: Listing sessions

You can list your sessions as follows.

```
curl 'https://jules.googleapis.com/v1alpha/sessions?pageSize=5' \
    -H 'X-Goog-Api-Key: YOUR_API_KEY'
```

![](https://storage.googleapis.com/pieces-web-extensions-cdn/pieces.png)Copy And Save![](https://storage.googleapis.com/pieces-web-extensions-cdn/link.png)Share![](https://storage.googleapis.com/pieces-web-extensions-cdn/copilot.png)Ask Copilot![](https://storage.googleapis.com/pieces-web-extensions-cdn/settings.png)

### Step 4: Approve plan

If your session requires explicit plan approval, you can approve the latest plan as follows:

```
curl 'https://jules.googleapis.com/v1alpha/sessions/SESSION_ID:approvePlan' \
    -X POST \
    -H "Content-Type: application/json" \
    -H 'X-Goog-Api-Key: YOUR_API_KEY'
```

![](https://storage.googleapis.com/pieces-web-extensions-cdn/pieces.png)Copy And Save![](https://storage.googleapis.com/pieces-web-extensions-cdn/link.png)Share![](https://storage.googleapis.com/pieces-web-extensions-cdn/copilot.png)Ask Copilot![](https://storage.googleapis.com/pieces-web-extensions-cdn/settings.png)

### Step 5: Activities and interacting with the agent

To list activities in a session:

```
curl 'https://jules.googleapis.com/v1alpha/sessions/SESSION_ID/activities?pageSize=30' \
    -H 'X-Goog-Api-Key: YOUR_API_KEY'
```

![](https://storage.googleapis.com/pieces-web-extensions-cdn/pieces.png)Copy And Save![](https://storage.googleapis.com/pieces-web-extensions-cdn/link.png)Share![](https://storage.googleapis.com/pieces-web-extensions-cdn/copilot.png)Ask Copilot![](https://storage.googleapis.com/pieces-web-extensions-cdn/settings.png)

To send a message to the agent:

```
curl 'https://jules.googleapis.com/v1alpha/sessions/SESSION_ID:sendMessage' \
    -X POST \
    -H "Content-Type: application/json" \
    -H 'X-Goog-Api-Key: YOUR_API_KEY' \
    -d '{
      "prompt": "Can you make the app corgi themed?"
    }'
```

![](https://storage.googleapis.com/pieces-web-extensions-cdn/pieces.png)Copy And Save![](https://storage.googleapis.com/pieces-web-extensions-cdn/link.png)Share![](https://storage.googleapis.com/pieces-web-extensions-cdn/copilot.png)Ask Copilot![](https://storage.googleapis.com/pieces-web-extensions-cdn/settings.png)

The response will be empty because the agent will send its response in the next activity. To see the agent's response, list the activities again.

Here is an example of a ListActivities response.

```
{
  "activities": [
    {
      "name": "sessions/14550388554331055113/activities/02200cce44f746308651037e4a18caed",
      "createTime": "2025-10-03T05:43:42.801654Z",
      "originator": "agent",
      "planGenerated": {
        "plan": {
          "id": "5103d604240042cd9f59a4cb2355643a",
          "steps": [
            {
              "id": "705a61fc8ec24a98abc9296a3956fb6b",
              "title": "Setup the environment. I will install the dependencies to run the app."
            },
            {
              "id": "bb5276efad354794a4527e9ad7c0cd42",
              "title": "Modify `src/App.js`. I will replace the existing React boilerplate with a simple Boba-themed component. This will include a title and a list of boba options.",
              "index": 1
            },
            {
              "id": "377c9a1c91764dc794a618a06772e3d8",
              "title": "Modify `src/App.css`. I will update the CSS to provide a fresh, modern look for the Boba app.",
              "index": 2
            },
            {
              "id": "335802b585b449aeabb855c722cd9c40",
              "title": "Frontend Verification. I will use the `frontend_verification_instructions` tool to get instructions on how to write a Playwright script to verify the frontend application and generate a screenshot of the changes.",
              "index": 3
            },
            {
              "id": "3e4cc97c7b2448668d1ac75b8c7b7d69",
              "title": "Submit the changes. Once the app is looking good and verified, I will submit my work.",
              "index": 4
            }
          ]
        }
      },
      "id": "02200cce44f746308651037e4a18caed"
    },
    {
      "name": "sessions/14550388554331055113/activities/2918fac8bc54450a9cbda423b7688413",
      "createTime": "2025-10-03T05:43:44.954030Z",
      "originator": "user",
      "planApproved": {
        "planId": "5103d604240042cd9f59a4cb2355643a"
      },
      "id": "2918fac8bc54450a9cbda423b7688413"
    },
    {
      "name": "sessions/14550388554331055113/activities/5b3acd1b3ca2439f9cbaefaccf7f709a",
      "createTime": "2025-10-03T05:44:16.700231Z",
      "originator": "agent",
      "progressUpdated": {
        "title": "Ran bash command",
        "description": "Command: \nnpm install\nOutput: added 1326 packages, and audited 1327 packages in 25s\n\n268 packages are looking for fundingExit Code: 0"
      },
      "artifacts": [
        {
          "bashOutput": {
            "command": "\nnpm install",
            "output": "added 1326 packages, and audited 1327 packages in 25s\n\n268 packages are looking for funding"
          }
        }
      ],
      "id": "5b3acd1b3ca2439f9cbaefaccf7f709a"
    },
    {
      "name": "sessions/14550388554331055113/activities/1ad545d812614a5b84c23a33f6120063",
      "createTime": "2025-10-03T05:44:19.502115Z",
      "originator": "agent",
      "progressUpdated": {
        "title": "I have installed the dependencies using `npm install`."
      },
      "artifacts": [
        {
          "changeSet": {
            "source": "sources/github/bobalover/boba",
            "gitPatch": {
              "baseCommitId": "36ead0a4caefc451b9652ed926a15af9570f4f35"
            }
          }
        }
      ],
      "id": "1ad545d812614a5b84c23a33f6120063"
    },
    {
      "name": "sessions/14550388554331055113/activities/3a2b46329f894ebea1faf6b8fb956428",
      "createTime": "2025-10-03T05:44:29.265425Z",
      "originator": "agent",
      "progressUpdated": {
        "title": "I have replaced the boilerplate in `src/App.js` with a Boba-themed component."
      },
      "artifacts": [
        {
          "changeSet": {
            "source": "sources/github/bobalover/boba",
            "gitPatch": {
              "unidiffPatch": "",
              "baseCommitId": "36ead0a4caefc451b9652ed926a15af9570f4f35"
            }
          }
        }
      ],
      "id": "3a2b46329f894ebea1faf6b8fb956428"
    },
    {
      "name": "sessions/14550388554331055113/activities/4e8dd66f15e247e3ae5e48eeff89ef47",
      "createTime": "2025-10-03T05:44:37.721013Z",
      "originator": "agent",
      "progressUpdated": {
        "title": "I have updated the CSS in `src/App.css` to give the app a fresh, modern look."
      },
      "artifacts": [
        {
          "changeSet": {
            "source": "sources/github/bobalover/boba",
            "gitPatch": {
              "unidiffPatch": ""
            }
          }
        }
      ],
      "id": "4e8dd66f15e247e3ae5e48eeff89ef47"
    },
    {
      "name": "sessions/14550388554331055113/activities/100901155a4141d3b37e8e8d2950f3b7",
      "createTime": "2025-10-03T05:44:51.048996Z",
      "originator": "agent",
      "progressUpdated": {
        "title": "Ran bash command",
        "description": "Command: \nOutput: Command failed due to an internal error.\nExit Code: 1"
      },
      "artifacts": [
        {
          "bashOutput": {
            "output": "Command failed due to an internal error.",
            "exitCode": 1
          }
        }
      ],
      "id": "100901155a4141d3b37e8e8d2950f3b7"
    },
    {
      "name": "sessions/14550388554331055113/activities/a76b35353eda42d09b1c37aedaa56047",
      "createTime": "2025-10-03T05:47:49.628363Z",
      "originator": "agent",
      "progressUpdated": {
        "title": "Frontend verification",
        "description": "Agent provided UI verification."
      },
      "artifacts": [
        {
          "media": {
            "data": "",
            "mimeType": "image/png"
          }
        }
      ],
      "id": "a76b35353eda42d09b1c37aedaa56047"
    },
    {
      "name": "sessions/14550388554331055113/activities/db089c7052024cbeb9e37b8c584bc964",
      "createTime": "2025-10-03T05:47:53.669642Z",
      "originator": "agent",
      "progressUpdated": {
        "title": "I have successfully verified the frontend changes by building the app, runnin...",
        "description": "I have successfully verified the frontend changes by building the app, running a Playwright script, and inspecting the resulting screenshot."
      },
      "artifacts": [
        {
          "changeSet": {
            "source": "sources/github/bobalover/boba",
            "gitPatch": {
              "unidiffPatch": "",
              "baseCommitId": "36ead0a4caefc451b9652ed926a15af9570f4f35"
            }
          }
        }
      ],
      "id": "db089c7052024cbeb9e37b8c584bc964"
    },
    {
      "name": "sessions/14550388554331055113/activities/890e16e30dbb4bf99a92613bdccec212",
      "createTime": "2025-10-03T05:48:14.434427Z",
      "originator": "agent",
      "progressUpdated": {
        "title": "Code reviewed",
        "description": "The user wants to create a \"boba app\". This is a very open-ended request, but the agent's implementation suggests the goal is to create a simple, static web page about boba."
      },
      "id": "890e16e30dbb4bf99a92613bdccec212"
    },
    {
      "name": "sessions/14550388554331055113/activities/022837dbc0e940eabcc1bc53608e15fc",
      "createTime": "2025-10-03T05:48:35.523200Z",
      "originator": "agent",
      "sessionCompleted": {},
      "artifacts": [
        {
          "changeSet": {
            "source": "sources/github/bobalover/boba",
            "gitPatch": {
              "unidiffPatch": "",
              "baseCommitId": "36ead0a4caefc451b9652ed926a15af9570f4f35",
              "suggestedCommitMessage": "feat: Create simple Boba App\n\nThis commit transforms the default Create React App boilerplate into a simple, visually appealing Boba-themed application."
            }
          }
        }
      ],
      "id": "022837dbc0e940eabcc1bc53608e15fc"
    }
  ]
}
```

![](https://storage.googleapis.com/pieces-web-extensions-cdn/pieces.png)Copy And Save![](https://storage.googleapis.com/pieces-web-extensions-cdn/link.png)Share![](https://storage.googleapis.com/pieces-web-extensions-cdn/copilot.png)Ask Copilot![](https://storage.googleapis.com/pieces-web-extensions-cdn/settings.png)

## Full API reference

View the full [API reference](/jules/api/reference/rest) documentation for the Jules API.

* * *

Was this helpful?

Except as otherwise noted, the content of this page is licensed under the [Creative Commons Attribution 4.0 License](https://creativecommons.org/licenses/by/4.0/), and code samples are licensed under the [Apache 2.0 License](https://www.apache.org/licenses/LICENSE-2.0). For details, see the [Google Developers Site Policies](https://developers.google.com/site-policies). Java is a registered trademark of Oracle and/or its affiliates.

Last updated 2025-11-10 UTC.

# Jules API bookmark\_borderbookmark Stay organized with collections Save and categorize content based on your preferences.

*   On this page
*   [Service: jules.googleapis.com](#service:-jules.googleapis.com)
    *   [Service endpoint](#service-endpoint)
*   [REST Resource: v1alpha.sessions](#rest-resource:-v1alpha.sessions)
*   [REST Resource: v1alpha.sessions.activities](#rest-resource:-v1alpha.sessions.activities)
*   [REST Resource: v1alpha.sources](#rest-resource:-v1alpha.sources)



Programmatically create and access your asynchronous coding tasks.

*   [REST Resource: v1alpha.sessions](#v1alpha.sessions)
*   [REST Resource: v1alpha.sessions.activities](#v1alpha.sessions.activities)
*   [REST Resource: v1alpha.sources](#v1alpha.sources)

## Service: jules.googleapis.com

To call this service, we recommend that you use the Google-provided [client libraries](https://cloud.google.com/apis/docs/client-libraries-explained). If your application needs to use your own libraries to call this service, use the following information when you make the API requests.

### Service endpoint

A [service endpoint](https://cloud.google.com/apis/design/glossary#api_service_endpoint) is a base URL that specifies the network address of an API service. One service might have multiple service endpoints. This service has the following service endpoint and all URIs below are relative to this service endpoint:

*   `https://jules.googleapis.com`

## REST Resource: [v1alpha.sessions](/jules/api/reference/rest/v1alpha/sessions)



Methods

`[approvePlan](/jules/api/reference/rest/v1alpha/sessions/approvePlan)`

`POST /v1alpha/{session=sessions/*}:approvePlan`  
Approves a plan in a session.

`[create](/jules/api/reference/rest/v1alpha/sessions/create)`

`POST /v1alpha/sessions`  
Creates a new session.

`[get](/jules/api/reference/rest/v1alpha/sessions/get)`

`GET /v1alpha/{name=sessions/*}`  
Gets a single session.

`[list](/jules/api/reference/rest/v1alpha/sessions/list)`

`GET /v1alpha/sessions`  
Lists all sessions.

`[sendMessage](/jules/api/reference/rest/v1alpha/sessions/sendMessage)`

`POST /v1alpha/{session=sessions/*}:sendMessage`  
Sends a message from the user to a session.

## REST Resource: [v1alpha.sessions.activities](/jules/api/reference/rest/v1alpha/sessions.activities)



Methods

`[get](/jules/api/reference/rest/v1alpha/sessions.activities/get)`

`GET /v1alpha/{name=sessions/*/activities/*}`  
Gets a single activity.

`[list](/jules/api/reference/rest/v1alpha/sessions.activities/list)`

`GET /v1alpha/{parent=sessions/*}/activities`  
Lists activities for a session.

## REST Resource: [v1alpha.sources](/jules/api/reference/rest/v1alpha/sources)



Methods

`[get](/jules/api/reference/rest/v1alpha/sources/get)`

`GET /v1alpha/{name=sources/**}`  
Gets a single source.

`[list](/jules/api/reference/rest/v1alpha/sources/list)`

`GET /v1alpha/sources`  
Lists sources.


# REST Resource: sessions bookmark\_borderbookmark Stay organized with collections Save and categorize content based on your preferences.

content\_copy

*   On this page
*   [Resource: Session](#resource:-session)
*   [SourceContext](#sourcecontext)
*   [GitHubRepoContext](#githubrepocontext)
*   [AutomationMode](#automationmode)
*   [State](#state)
*   [SessionOutput](#sessionoutput)
*   [PullRequest](#pullrequest)
*   [Methods](#methods)
    *   [approvePlan](#approveplan)
    *   [create](#create)
    *   [get](#get)
    *   [list](#list)
    *   [sendMessage](#sendmessage)



*   [Resource: Session](#Session)
    *   [JSON representation](#Session.SCHEMA_REPRESENTATION)
*   [SourceContext](#SourceContext)
    *   [JSON representation](#SourceContext.SCHEMA_REPRESENTATION)
*   [GitHubRepoContext](#GitHubRepoContext)
    *   [JSON representation](#GitHubRepoContext.SCHEMA_REPRESENTATION)
*   [AutomationMode](#AutomationMode)
*   [State](#State)
*   [SessionOutput](#SessionOutput)
    *   [JSON representation](#SessionOutput.SCHEMA_REPRESENTATION)
*   [PullRequest](#PullRequest)
    *   [JSON representation](#PullRequest.SCHEMA_REPRESENTATION)
*   [Methods](#METHODS_SUMMARY)

## Resource: Session

A session is a contiguous amount of work within the same context.

JSON representation

{
"name": string,
"id": string,
"prompt": string,
"sourceContext": {
object (`[SourceContext](/jules/api/reference/rest/v1alpha/sessions#SourceContext)`)
},
"title": string,
"requirePlanApproval": boolean,
"automationMode": enum (`[AutomationMode](/jules/api/reference/rest/v1alpha/sessions#AutomationMode)`),
"createTime": string,
"updateTime": string,
"state": enum (`[State](/jules/api/reference/rest/v1alpha/sessions#State)`),
"url": string,
"outputs": \[
{
object (`[SessionOutput](/jules/api/reference/rest/v1alpha/sessions#SessionOutput)`)
}
\]
}

![](https://storage.googleapis.com/pieces-web-extensions-cdn/pieces.png)Copy And Save![](https://storage.googleapis.com/pieces-web-extensions-cdn/link.png)Share![](https://storage.googleapis.com/pieces-web-extensions-cdn/copilot.png)Ask Copilot![](https://storage.googleapis.com/pieces-web-extensions-cdn/settings.png)



Fields

`name`

`string`

Output only. Identifier. The full resource name (e.g., "sessions/{session}").

`id`

`string`

Output only. The id of the session. This is the same as the "{session}" part of the resource name (e.g., "sessions/{session}").

`prompt`

`string`

Required. The prompt to start the session with.

`sourceContext`

``object (`[SourceContext](/jules/api/reference/rest/v1alpha/sessions#SourceContext)`)``

Required. The source to use in this session, with additional context.

`title`

`string`

Optional. If not provided, the system will generate one.

`requirePlanApproval`

`boolean`

Optional. Input only. If true, plans the agent generates will require explicit plan approval before the agent starts working. If not set, plans will be auto-approved.

`automationMode`

``enum (`[AutomationMode](/jules/api/reference/rest/v1alpha/sessions#AutomationMode)`)``

Optional. Input only. The automation mode of the session. If not set, the default automation mode will be used.

`createTime`

``string (`[Timestamp](https://protobuf.dev/reference/protobuf/google.protobuf/#timestamp)` format)``

Output only. The time the session was created.

Uses RFC 3339, where generated output will always be Z-normalized and use 0, 3, 6 or 9 fractional digits. Offsets other than "Z" are also accepted. Examples: `"2014-10-02T15:01:23Z"`, `"2014-10-02T15:01:23.045123456Z"` or `"2014-10-02T15:01:23+05:30"`.

`updateTime`

``string (`[Timestamp](https://protobuf.dev/reference/protobuf/google.protobuf/#timestamp)` format)``

Output only. The time the session was last updated.

Uses RFC 3339, where generated output will always be Z-normalized and use 0, 3, 6 or 9 fractional digits. Offsets other than "Z" are also accepted. Examples: `"2014-10-02T15:01:23Z"`, `"2014-10-02T15:01:23.045123456Z"` or `"2014-10-02T15:01:23+05:30"`.

`state`

``enum (`[State](/jules/api/reference/rest/v1alpha/sessions#State)`)``

Output only. The state of the session.

`url`

`string`

Output only. The URL of the session to view the session in the Jules web app.

`outputs[]`

``object (`[SessionOutput](/jules/api/reference/rest/v1alpha/sessions#SessionOutput)`)``

Output only. The outputs of the session, if any.

## SourceContext

Context for how to use a source in a session.

JSON representation

{
"source": string,

// Union field `context` can be only one of the following:
"githubRepoContext": {
object (`[GitHubRepoContext](/jules/api/reference/rest/v1alpha/sessions#GitHubRepoContext)`)
}
// End of list of possible types for union field `context`.
}

![](https://storage.googleapis.com/pieces-web-extensions-cdn/pieces.png)Copy And Save![](https://storage.googleapis.com/pieces-web-extensions-cdn/link.png)Share![](https://storage.googleapis.com/pieces-web-extensions-cdn/copilot.png)Ask Copilot![](https://storage.googleapis.com/pieces-web-extensions-cdn/settings.png)



Fields

`source`

`string`

Required. The name of the source this context is for. To get the list of sources, use the ListSources API. Format: sources/{source}

Union field `context`. The context for how to use the source in a session. `context` can be only one of the following:

`githubRepoContext`

``object (`[GitHubRepoContext](/jules/api/reference/rest/v1alpha/sessions#GitHubRepoContext)`)``

Context to use a GitHubRepo in a session.

## GitHubRepoContext

Context to use a GitHubRepo in a session.

JSON representation

{
"startingBranch": string
}

![](https://storage.googleapis.com/pieces-web-extensions-cdn/pieces.png)Copy And Save![](https://storage.googleapis.com/pieces-web-extensions-cdn/link.png)Share![](https://storage.googleapis.com/pieces-web-extensions-cdn/copilot.png)Ask Copilot![](https://storage.googleapis.com/pieces-web-extensions-cdn/settings.png)



Fields

`startingBranch`

`string`

Required. The name of the branch to start the session from.

## AutomationMode

The automation mode of the session.



Enums

`AUTOMATION_MODE_UNSPECIFIED`

The automation mode is unspecified. Default to no automation.

`AUTO_CREATE_PR`

Whenever a final code patch is generated in the session, automatically create a branch and a pull request for it, if applicable.

## State

State of a session.



Enums

`STATE_UNSPECIFIED`

The state is unspecified.

`QUEUED`

The session is queued.

`PLANNING`

The agent is planning.

`AWAITING_PLAN_APPROVAL`

The agent is waiting for plan approval.

`AWAITING_USER_FEEDBACK`

The agent is waiting for user feedback.

`IN_PROGRESS`

The session is in progress.

`PAUSED`

The session is paused.

`FAILED`

The session has failed.

`COMPLETED`

The session has completed.

## SessionOutput

An output of a session.

JSON representation

{

// Union field `output` can be only one of the following:
"pullRequest": {
object (`[PullRequest](/jules/api/reference/rest/v1alpha/sessions#PullRequest)`)
}
// End of list of possible types for union field `output`.
}

![](https://storage.googleapis.com/pieces-web-extensions-cdn/pieces.png)Copy And Save![](https://storage.googleapis.com/pieces-web-extensions-cdn/link.png)Share![](https://storage.googleapis.com/pieces-web-extensions-cdn/copilot.png)Ask Copilot![](https://storage.googleapis.com/pieces-web-extensions-cdn/settings.png)



Fields

Union field `output`. An output of the session. `output` can be only one of the following:

`pullRequest`

``object (`[PullRequest](/jules/api/reference/rest/v1alpha/sessions#PullRequest)`)``

A pull request created by the session, if applicable.

## PullRequest

A pull request.

JSON representation

{
"url": string,
"title": string,
"description": string
}

![](https://storage.googleapis.com/pieces-web-extensions-cdn/pieces.png)Copy And Save![](https://storage.googleapis.com/pieces-web-extensions-cdn/link.png)Share![](https://storage.googleapis.com/pieces-web-extensions-cdn/copilot.png)Ask Copilot![](https://storage.googleapis.com/pieces-web-extensions-cdn/settings.png)



Fields

`url`

`string`

The URL of the pull request.

`title`

`string`

The title of the pull request.

`description`

`string`

The description of the pull request.



## Methods

### `[approvePlan](/jules/api/reference/rest/v1alpha/sessions/approvePlan)`

Approves a plan in a session.

### `[create](/jules/api/reference/rest/v1alpha/sessions/create)`

Creates a new session.

### `[get](/jules/api/reference/rest/v1alpha/sessions/get)`

Gets a single session.

### `[list](/jules/api/reference/rest/v1alpha/sessions/list)`

Lists all sessions.

### `[sendMessage](/jules/api/reference/rest/v1alpha/sessions/sendMessage)`

Sends a message from the user to a session.


Method: sessions.approvePlan

bookmark_border
Approves a plan in a session.

HTTP request
POST https://jules.googleapis.com/v1alpha/{session=sessions/*}:approvePlan

The URL uses gRPC Transcoding syntax.

Path parameters
Parameters
session
string

Required. The resource name of the session to approve the plan in. Format: sessions/{session} It takes the form sessions/{session}.

Request body
The request body must be empty.

Response body
If successful, the response body is empty.

# Method: sessions.create bookmark\_borderbookmark Stay organized with collections Save and categorize content based on your preferences.

*   On this page
*   [HTTP request](#http-request)
*   [Request body](#request-body)
*   [Response body](#response-body)



*   [HTTP request](#body.HTTP_TEMPLATE)
*   [Request body](#body.request_body)
*   [Response body](#body.response_body)

Creates a new session.

### HTTP request

`POST https://jules.googleapis.com/v1alpha/sessions`

The URL uses [gRPC Transcoding](https://google.aip.dev/127) syntax.

### Request body

The request body contains an instance of `[Session](/jules/api/reference/rest/v1alpha/sessions#Session)`.

### Response body

If successful, the response body contains a newly created instance of `[Session](/jules/api/reference/rest/v1alpha/sessions#Session)`.

Method: sessions.get

bookmark_border
Gets a single session.

HTTP request
GET https://jules.googleapis.com/v1alpha/{name=sessions/*}

The URL uses gRPC Transcoding syntax.

Path parameters
Parameters
name
string

Required. The resource name of the session to retrieve. Format: sessions/{session} It takes the form sessions/{session}.

Request body
The request body must be empty.

Response body
If successful, the response body contains an instance of Session.

# Method: sessions.list bookmark\_borderbookmark Stay organized with collections Save and categorize content based on your preferences.

*   On this page
*   [HTTP request](#http-request)
*   [Query parameters](#query-parameters)
*   [Request body](#request-body)
*   [Response body](#response-body)



*   [HTTP request](#body.HTTP_TEMPLATE)
*   [Query parameters](#body.QUERY_PARAMETERS)
*   [Request body](#body.request_body)
*   [Response body](#body.response_body)
    *   [JSON representation](#body.ListSessionsResponse.SCHEMA_REPRESENTATION)

Lists all sessions.

### HTTP request

`GET https://jules.googleapis.com/v1alpha/sessions`

The URL uses [gRPC Transcoding](https://google.aip.dev/127) syntax.

### Query parameters



Parameters

`pageSize`

`integer`

Optional. The number of sessions to return. Must be between 1 and 100, inclusive. If unset, defaults to 30. If set to greater than 100, it will be coerced to 100.

`pageToken`

`string`

Optional. A page token, received from a previous `sessions.list` call.

### Request body

The request body must be empty.

### Response body

Response message for sessions.list.

If successful, the response body contains data with the following structure:

JSON representation

{
"sessions": \[
{
object (`[Session](/jules/api/reference/rest/v1alpha/sessions#Session)`)
}
\],
"nextPageToken": string
}

![](https://storage.googleapis.com/pieces-web-extensions-cdn/pieces.png)Copy And Save![](https://storage.googleapis.com/pieces-web-extensions-cdn/link.png)Share![](https://storage.googleapis.com/pieces-web-extensions-cdn/copilot.png)Ask Copilot![](https://storage.googleapis.com/pieces-web-extensions-cdn/settings.png)



Fields

`sessions[]`

``object (`[Session](/jules/api/reference/rest/v1alpha/sessions#Session)`)``

The sessions from the specified request.

`nextPageToken`

`string`

A token, which can be sent as `pageToken` to retrieve the next page. If this field is omitted, there are no subsequent pages.


Method: sessions.sendMessage

bookmark_border
Sends a message from the user to a session.

HTTP request
POST https://jules.googleapis.com/v1alpha/{session=sessions/*}:sendMessage

The URL uses gRPC Transcoding syntax.

Path parameters
Parameters
session
string

Required. The resource name of the session to post the message to. Format: sessions/{session} It takes the form sessions/{session}.

Request body
The request body contains data with the following structure:

JSON representation

{
"prompt": string
}
Fields
prompt
string

Required. The user prompt to send to the session.

Response body
If successful, the response body is empty.

# REST Resource: sessions.activities bookmark\_borderbookmark Stay organized with collections Save and categorize content based on your preferences.

*   On this page
*   [Resource: Activity](#resource:-activity)
*   [AgentMessaged](#agentmessaged)
*   [UserMessaged](#usermessaged)
*   [PlanGenerated](#plangenerated)
*   [Plan](#plan)
*   [PlanStep](#planstep)
*   [PlanApproved](#planapproved)
*   [ProgressUpdated](#progressupdated)
*   [SessionCompleted](#sessioncompleted)
*   [SessionFailed](#sessionfailed)
*   [Artifact](#artifact)
*   [ChangeSet](#changeset)
*   [GitPatch](#gitpatch)
*   [Media](#media)
*   [BashOutput](#bashoutput)
*   [Methods](#methods)
    *   [get](#get)
    *   [list](#list)



*   [Resource: Activity](#Activity)
    *   [JSON representation](#Activity.SCHEMA_REPRESENTATION)
*   [AgentMessaged](#AgentMessaged)
    *   [JSON representation](#AgentMessaged.SCHEMA_REPRESENTATION)
*   [UserMessaged](#UserMessaged)
    *   [JSON representation](#UserMessaged.SCHEMA_REPRESENTATION)
*   [PlanGenerated](#PlanGenerated)
    *   [JSON representation](#PlanGenerated.SCHEMA_REPRESENTATION)
*   [Plan](#Plan)
    *   [JSON representation](#Plan.SCHEMA_REPRESENTATION)
*   [PlanStep](#PlanStep)
    *   [JSON representation](#PlanStep.SCHEMA_REPRESENTATION)
*   [PlanApproved](#PlanApproved)
    *   [JSON representation](#PlanApproved.SCHEMA_REPRESENTATION)
*   [ProgressUpdated](#ProgressUpdated)
    *   [JSON representation](#ProgressUpdated.SCHEMA_REPRESENTATION)
*   [SessionCompleted](#SessionCompleted)
*   [SessionFailed](#SessionFailed)
    *   [JSON representation](#SessionFailed.SCHEMA_REPRESENTATION)
*   [Artifact](#Artifact)
    *   [JSON representation](#Artifact.SCHEMA_REPRESENTATION)
*   [ChangeSet](#ChangeSet)
    *   [JSON representation](#ChangeSet.SCHEMA_REPRESENTATION)
*   [GitPatch](#GitPatch)
    *   [JSON representation](#GitPatch.SCHEMA_REPRESENTATION)
*   [Media](#Media)
    *   [JSON representation](#Media.SCHEMA_REPRESENTATION)
*   [BashOutput](#BashOutput)
    *   [JSON representation](#BashOutput.SCHEMA_REPRESENTATION)
*   [Methods](#METHODS_SUMMARY)

## Resource: Activity

An activity is a single unit of work within a session.

JSON representation

{
"name": string,
"id": string,
"description": string,
"createTime": string,
"originator": string,
"artifacts": \[
{
object (`[Artifact](/jules/api/reference/rest/v1alpha/sessions.activities#Artifact)`)
}
\],

// Union field `activity` can be only one of the following:
"agentMessaged": {
object (`[AgentMessaged](/jules/api/reference/rest/v1alpha/sessions.activities#AgentMessaged)`)
},
"userMessaged": {
object (`[UserMessaged](/jules/api/reference/rest/v1alpha/sessions.activities#UserMessaged)`)
},
"planGenerated": {
object (`[PlanGenerated](/jules/api/reference/rest/v1alpha/sessions.activities#PlanGenerated)`)
},
"planApproved": {
object (`[PlanApproved](/jules/api/reference/rest/v1alpha/sessions.activities#PlanApproved)`)
},
"progressUpdated": {
object (`[ProgressUpdated](/jules/api/reference/rest/v1alpha/sessions.activities#ProgressUpdated)`)
},
"sessionCompleted": {
object (`[SessionCompleted](/jules/api/reference/rest/v1alpha/sessions.activities#SessionCompleted)`)
},
"sessionFailed": {
object (`[SessionFailed](/jules/api/reference/rest/v1alpha/sessions.activities#SessionFailed)`)
}
// End of list of possible types for union field `activity`.
}

![](https://storage.googleapis.com/pieces-web-extensions-cdn/pieces.png)Copy And Save![](https://storage.googleapis.com/pieces-web-extensions-cdn/link.png)Share![](https://storage.googleapis.com/pieces-web-extensions-cdn/copilot.png)Ask Copilot![](https://storage.googleapis.com/pieces-web-extensions-cdn/settings.png)



Fields

`name`

`string`

Identifier. The full resource name (e.g., "sessions/{session}/activities/{activity}").

`id`

`string`

Output only. The id of the activity. This is the same as the "{activity}" part of the resource name (e.g., "sessions/{session}/activities/{activity}").

`description`

`string`

Output only. A description of this activity.

`createTime`

``string (`[Timestamp](https://protobuf.dev/reference/protobuf/google.protobuf/#timestamp)` format)``

Output only. The time at which this activity was created.

Uses RFC 3339, where generated output will always be Z-normalized and use 0, 3, 6 or 9 fractional digits. Offsets other than "Z" are also accepted. Examples: `"2014-10-02T15:01:23Z"`, `"2014-10-02T15:01:23.045123456Z"` or `"2014-10-02T15:01:23+05:30"`.

`originator`

`string`

The entity that this activity originated from (e.g. "user", "agent", "system").

`artifacts[]`

``object (`[Artifact](/jules/api/reference/rest/v1alpha/sessions.activities#Artifact)`)``

Output only. The artifacts produced by this activity.

Union field `activity`. The activity content. `activity` can be only one of the following:

`agentMessaged`

``object (`[AgentMessaged](/jules/api/reference/rest/v1alpha/sessions.activities#AgentMessaged)`)``

The agent posted a message.

`userMessaged`

``object (`[UserMessaged](/jules/api/reference/rest/v1alpha/sessions.activities#UserMessaged)`)``

The user posted a message.

`planGenerated`

``object (`[PlanGenerated](/jules/api/reference/rest/v1alpha/sessions.activities#PlanGenerated)`)``

A plan was generated.

`planApproved`

``object (`[PlanApproved](/jules/api/reference/rest/v1alpha/sessions.activities#PlanApproved)`)``

A plan was approved.

`progressUpdated`

``object (`[ProgressUpdated](/jules/api/reference/rest/v1alpha/sessions.activities#ProgressUpdated)`)``

There was a progress update.

`sessionCompleted`

``object (`[SessionCompleted](/jules/api/reference/rest/v1alpha/sessions.activities#SessionCompleted)`)``

The session was completed.

`sessionFailed`

``object (`[SessionFailed](/jules/api/reference/rest/v1alpha/sessions.activities#SessionFailed)`)``

The session failed.

## AgentMessaged

The agent posted a message.

JSON representation

{
"agentMessage": string
}

![](https://storage.googleapis.com/pieces-web-extensions-cdn/pieces.png)Copy And Save![](https://storage.googleapis.com/pieces-web-extensions-cdn/link.png)Share![](https://storage.googleapis.com/pieces-web-extensions-cdn/copilot.png)Ask Copilot![](https://storage.googleapis.com/pieces-web-extensions-cdn/settings.png)



Fields

`agentMessage`

`string`

The message the agent posted.

## UserMessaged

The user posted a message.

JSON representation

{
"userMessage": string
}

![](https://storage.googleapis.com/pieces-web-extensions-cdn/pieces.png)Copy And Save![](https://storage.googleapis.com/pieces-web-extensions-cdn/link.png)Share![](https://storage.googleapis.com/pieces-web-extensions-cdn/copilot.png)Ask Copilot![](https://storage.googleapis.com/pieces-web-extensions-cdn/settings.png)



Fields

`userMessage`

`string`

The message the user posted.

## PlanGenerated

A plan was generated.

JSON representation

{
"plan": {
object (`[Plan](/jules/api/reference/rest/v1alpha/sessions.activities#Plan)`)
}
}

![](https://storage.googleapis.com/pieces-web-extensions-cdn/pieces.png)Copy And Save![](https://storage.googleapis.com/pieces-web-extensions-cdn/link.png)Share![](https://storage.googleapis.com/pieces-web-extensions-cdn/copilot.png)Ask Copilot![](https://storage.googleapis.com/pieces-web-extensions-cdn/settings.png)



Fields

`plan`

``object (`[Plan](/jules/api/reference/rest/v1alpha/sessions.activities#Plan)`)``

The plan that was generated.

## Plan

A plan is a sequence of steps that the agent will take to complete the task.

JSON representation

{
"id": string,
"steps": \[
{
object (`[PlanStep](/jules/api/reference/rest/v1alpha/sessions.activities#PlanStep)`)
}
\],
"createTime": string
}

![](https://storage.googleapis.com/pieces-web-extensions-cdn/pieces.png)Copy And Save![](https://storage.googleapis.com/pieces-web-extensions-cdn/link.png)Share![](https://storage.googleapis.com/pieces-web-extensions-cdn/copilot.png)Ask Copilot![](https://storage.googleapis.com/pieces-web-extensions-cdn/settings.png)



Fields

`id`

`string`

Output only. ID for this plan; unique within a session.

`steps[]`

``object (`[PlanStep](/jules/api/reference/rest/v1alpha/sessions.activities#PlanStep)`)``

Output only. The steps in the plan.

`createTime`

``string (`[Timestamp](https://protobuf.dev/reference/protobuf/google.protobuf/#timestamp)` format)``

Output only. Time when the plan was created.

Uses RFC 3339, where generated output will always be Z-normalized and use 0, 3, 6 or 9 fractional digits. Offsets other than "Z" are also accepted. Examples: `"2014-10-02T15:01:23Z"`, `"2014-10-02T15:01:23.045123456Z"` or `"2014-10-02T15:01:23+05:30"`.

## PlanStep

A step in a plan.

JSON representation

{
"id": string,
"title": string,
"description": string,
"index": integer
}

![](https://storage.googleapis.com/pieces-web-extensions-cdn/pieces.png)Copy And Save![](https://storage.googleapis.com/pieces-web-extensions-cdn/link.png)Share![](https://storage.googleapis.com/pieces-web-extensions-cdn/copilot.png)Ask Copilot![](https://storage.googleapis.com/pieces-web-extensions-cdn/settings.png)



Fields

`id`

`string`

Output only. ID for this step; unique within a plan.

`title`

`string`

Output only. The title of the step.

`description`

`string`

Output only. The description of the step.

`index`

`integer`

Output only. 0-based index into the plan.steps.

## PlanApproved

A plan was approved.

JSON representation

{
"planId": string
}

![](https://storage.googleapis.com/pieces-web-extensions-cdn/pieces.png)Copy And Save![](https://storage.googleapis.com/pieces-web-extensions-cdn/link.png)Share![](https://storage.googleapis.com/pieces-web-extensions-cdn/copilot.png)Ask Copilot![](https://storage.googleapis.com/pieces-web-extensions-cdn/settings.png)



Fields

`planId`

`string`

The ID of the plan that was approved.

## ProgressUpdated

There was a progress update.

JSON representation

{
"title": string,
"description": string
}

![](https://storage.googleapis.com/pieces-web-extensions-cdn/pieces.png)Copy And Save![](https://storage.googleapis.com/pieces-web-extensions-cdn/link.png)Share![](https://storage.googleapis.com/pieces-web-extensions-cdn/copilot.png)Ask Copilot![](https://storage.googleapis.com/pieces-web-extensions-cdn/settings.png)



Fields

`title`

`string`

The title of the progress update.

`description`

`string`

The description of the progress update.

## SessionCompleted

This type has no fields.

The session was completed.

## SessionFailed

The session failed.

JSON representation

{
"reason": string
}

![](https://storage.googleapis.com/pieces-web-extensions-cdn/pieces.png)Copy And Save![](https://storage.googleapis.com/pieces-web-extensions-cdn/link.png)Share![](https://storage.googleapis.com/pieces-web-extensions-cdn/copilot.png)Ask Copilot![](https://storage.googleapis.com/pieces-web-extensions-cdn/settings.png)



Fields

`reason`

`string`

The reason the session failed.

## Artifact

An artifact is a single unit of data produced by an activity step.

JSON representation

{

// Union field `content` can be only one of the following:
"changeSet": {
object (`[ChangeSet](/jules/api/reference/rest/v1alpha/sessions.activities#ChangeSet)`)
},
"media": {
object (`[Media](/jules/api/reference/rest/v1alpha/sessions.activities#Media)`)
},
"bashOutput": {
object (`[BashOutput](/jules/api/reference/rest/v1alpha/sessions.activities#BashOutput)`)
}
// End of list of possible types for union field `content`.
}

![](https://storage.googleapis.com/pieces-web-extensions-cdn/pieces.png)Copy And Save![](https://storage.googleapis.com/pieces-web-extensions-cdn/link.png)Share![](https://storage.googleapis.com/pieces-web-extensions-cdn/copilot.png)Ask Copilot![](https://storage.googleapis.com/pieces-web-extensions-cdn/settings.png)



Fields

Union field `content`. The artifact content. `content` can be only one of the following:

`changeSet`

``object (`[ChangeSet](/jules/api/reference/rest/v1alpha/sessions.activities#ChangeSet)`)``

A change set was produced (e.g. code changes).

`media`

``object (`[Media](/jules/api/reference/rest/v1alpha/sessions.activities#Media)`)``

A media file was produced (e.g. image, video).

`bashOutput`

``object (`[BashOutput](/jules/api/reference/rest/v1alpha/sessions.activities#BashOutput)`)``

A bash output was produced.

## ChangeSet

A set of changes to be applied to a source.

JSON representation

{
"source": string,

// Union field `changes` can be only one of the following:
"gitPatch": {
object (`[GitPatch](/jules/api/reference/rest/v1alpha/sessions.activities#GitPatch)`)
}
// End of list of possible types for union field `changes`.
}

![](https://storage.googleapis.com/pieces-web-extensions-cdn/pieces.png)Copy And Save![](https://storage.googleapis.com/pieces-web-extensions-cdn/link.png)Share![](https://storage.googleapis.com/pieces-web-extensions-cdn/copilot.png)Ask Copilot![](https://storage.googleapis.com/pieces-web-extensions-cdn/settings.png)



Fields

`source`

`string`

The name of the source this change set applies to. Format: sources/{source}

Union field `changes`. The changes to be applied to the source. `changes` can be only one of the following:

`gitPatch`

``object (`[GitPatch](/jules/api/reference/rest/v1alpha/sessions.activities#GitPatch)`)``

A patch in Git format.

## GitPatch

A patch in Git format.

JSON representation

{
"unidiffPatch": string,
"baseCommitId": string,
"suggestedCommitMessage": string
}

![](https://storage.googleapis.com/pieces-web-extensions-cdn/pieces.png)Copy And Save![](https://storage.googleapis.com/pieces-web-extensions-cdn/link.png)Share![](https://storage.googleapis.com/pieces-web-extensions-cdn/copilot.png)Ask Copilot![](https://storage.googleapis.com/pieces-web-extensions-cdn/settings.png)



Fields

`unidiffPatch`

`string`

The patch in unidiff format.

`baseCommitId`

`string`

The base commit id of the patch. This is the id of the commit that the patch should be applied to.

`suggestedCommitMessage`

`string`

A suggested commit message for the patch, if one is generated.

## Media

A media output.

JSON representation

{
"data": string,
"mimeType": string
}

![](https://storage.googleapis.com/pieces-web-extensions-cdn/pieces.png)Copy And Save![](https://storage.googleapis.com/pieces-web-extensions-cdn/link.png)Share![](https://storage.googleapis.com/pieces-web-extensions-cdn/copilot.png)Ask Copilot![](https://storage.googleapis.com/pieces-web-extensions-cdn/settings.png)



Fields

`data`

`string ([bytes](https://developers.google.com/discovery/v1/type-format) format)`

The media data.

A base64-encoded string.

`mimeType`

`string`

The media mime type.

## BashOutput

A bash output.

JSON representation

{
"command": string,
"output": string,
"exitCode": integer
}

![](https://storage.googleapis.com/pieces-web-extensions-cdn/pieces.png)Copy And Save![](https://storage.googleapis.com/pieces-web-extensions-cdn/link.png)Share![](https://storage.googleapis.com/pieces-web-extensions-cdn/copilot.png)Ask Copilot![](https://storage.googleapis.com/pieces-web-extensions-cdn/settings.png)



Fields

`command`

`string`

The bash command.

`output`

`string`

The bash output. Includes both stdout and stderr.

`exitCode`

`integer`

The bash exit code.



## Methods

### `[get](/jules/api/reference/rest/v1alpha/sessions.activities/get)`

Gets a single activity.

### `[list](/jules/api/reference/rest/v1alpha/sessions.activities/list)`

Lists activities for a session.

Method: sessions.activities.get

bookmark_border
Gets a single activity.

HTTP request
GET https://jules.googleapis.com/v1alpha/{name=sessions/*/activities/*}

The URL uses gRPC Transcoding syntax.

Path parameters
Parameters
name
string

Required. The resource name of the activity to retrieve. Format: sessions/{session}/activities/{activity} It takes the form sessions/{session}/activities/{activities}.

Request body
The request body must be empty.

Response body
If successful, the response body contains an instance of Activity.

Method: sessions.activities.list

bookmark_border
Lists activities for a session.

HTTP request
GET https://jules.googleapis.com/v1alpha/{parent=sessions/*}/activities

The URL uses gRPC Transcoding syntax.

Path parameters
Parameters
parent
string

Required. The parent session, which owns this collection of activities. Format: sessions/{session} It takes the form sessions/{session}.

Query parameters
Parameters
pageSize
integer

Optional. The number of activities to return. Must be between 1 and 100, inclusive. If unset, defaults to 50. If set to greater than 100, it will be coerced to 100.

pageToken
string

Optional. A page token, received from a previous activities.list call.

Request body
The request body must be empty.

Response body
Response message for the activities.list RPC.

If successful, the response body contains data with the following structure:

JSON representation

{
"activities": [
{
object (Activity)
}
],
"nextPageToken": string
}
Fields
activities[]
object (Activity)

The activities from the specified session.

nextPageToken
string

A token, which can be sent as pageToken to retrieve the next page. If this field is omitted, there are no subsequent pages.

# REST Resource: sources bookmark\_borderbookmark Stay organized with collections Save and categorize content based on your preferences.

*   On this page
*   [Resource: Source](#resource:-source)
*   [GitHubRepo](#githubrepo)
*   [GitHubBranch](#githubbranch)
*   [Methods](#methods)
    *   [get](#get)
    *   [list](#list)



*   [Resource: Source](#Source)
    *   [JSON representation](#Source.SCHEMA_REPRESENTATION)
*   [GitHubRepo](#GitHubRepo)
    *   [JSON representation](#GitHubRepo.SCHEMA_REPRESENTATION)
*   [GitHubBranch](#GitHubBranch)
    *   [JSON representation](#GitHubBranch.SCHEMA_REPRESENTATION)
*   [Methods](#METHODS_SUMMARY)

## Resource: Source

An input source of data for a session.

JSON representation

{
"name": string,
"id": string,

// Union field `source` can be only one of the following:
"githubRepo": {
object (`[GitHubRepo](/jules/api/reference/rest/v1alpha/sources#GitHubRepo)`)
}
// End of list of possible types for union field `source`.
}

![](https://storage.googleapis.com/pieces-web-extensions-cdn/pieces.png)Copy And Save![](https://storage.googleapis.com/pieces-web-extensions-cdn/link.png)Share![](https://storage.googleapis.com/pieces-web-extensions-cdn/copilot.png)Ask Copilot![](https://storage.googleapis.com/pieces-web-extensions-cdn/settings.png)



Fields

`name`

`string`

Identifier. The full resource name (e.g., "sources/{source}").

`id`

`string`

Output only. The id of the source. This is the same as the "{source}" part of the resource name (e.g., "sources/{source}").

Union field `source`. The input data source. `source` can be only one of the following:

`githubRepo`

``object (`[GitHubRepo](/jules/api/reference/rest/v1alpha/sources#GitHubRepo)`)``

A GitHub repo.

## GitHubRepo

A GitHub repo.

JSON representation

{
"owner": string,
"repo": string,
"isPrivate": boolean,
"defaultBranch": {
object (`[GitHubBranch](/jules/api/reference/rest/v1alpha/sources#GitHubBranch)`)
},
"branches": \[
{
object (`[GitHubBranch](/jules/api/reference/rest/v1alpha/sources#GitHubBranch)`)
}
\]
}

![](https://storage.googleapis.com/pieces-web-extensions-cdn/pieces.png)Copy And Save![](https://storage.googleapis.com/pieces-web-extensions-cdn/link.png)Share![](https://storage.googleapis.com/pieces-web-extensions-cdn/copilot.png)Ask Copilot![](https://storage.googleapis.com/pieces-web-extensions-cdn/settings.png)



Fields

`owner`

`string`

The owner of the repo; the `<owner>` in `https://github.com/<owner>/<repo>`.

`repo`

`string`

The name of the repo; the `<repo>` in `https://github.com/<owner>/<repo>`.

`isPrivate`

`boolean`

Whether this repo is private.

`defaultBranch`

``object (`[GitHubBranch](/jules/api/reference/rest/v1alpha/sources#GitHubBranch)`)``

The default branch for this repo.

`branches[]`

``object (`[GitHubBranch](/jules/api/reference/rest/v1alpha/sources#GitHubBranch)`)``

The list of active branches for this repo.

## GitHubBranch

A GitHub branch.

JSON representation

{
"displayName": string
}

![](https://storage.googleapis.com/pieces-web-extensions-cdn/pieces.png)Copy And Save![](https://storage.googleapis.com/pieces-web-extensions-cdn/link.png)Share![](https://storage.googleapis.com/pieces-web-extensions-cdn/copilot.png)Ask Copilot![](https://storage.googleapis.com/pieces-web-extensions-cdn/settings.png)



Fields

`displayName`

`string`

The name of the GitHub branch.



## Methods

### `[get](/jules/api/reference/rest/v1alpha/sources/get)`

Gets a single source.

### `[list](/jules/api/reference/rest/v1alpha/sources/list)`

Lists sources.

# Method: sources.get bookmark\_borderbookmark Stay organized with collections Save and categorize content based on your preferences.

*   On this page
*   [HTTP request](#http-request)
*   [Path parameters](#path-parameters)
*   [Request body](#request-body)
*   [Response body](#response-body)



*   [HTTP request](#body.HTTP_TEMPLATE)
*   [Path parameters](#body.PATH_PARAMETERS)
*   [Request body](#body.request_body)
*   [Response body](#body.response_body)

Gets a single source.

### HTTP request

`GET https://jules.googleapis.com/v1alpha/{name=sources/**}`

The URL uses [gRPC Transcoding](https://google.aip.dev/127) syntax.

### Path parameters



Parameters

`name`

`string`

Required. The resource name of the source to retrieve. Format: sources/{source} It takes the form `sources/{+source}`.

### Request body

The request body must be empty.

### Response body

If successful, the response body contains an instance of `[Source](/jules/api/reference/rest/v1alpha/sources#Source)`.

# Method: sources.list bookmark\_borderbookmark Stay organized with collections Save and categorize content based on your preferences.

*   On this page
*   [HTTP request](#http-request)
*   [Query parameters](#query-parameters)
*   [Request body](#request-body)
*   [Response body](#response-body)



*   [HTTP request](#body.HTTP_TEMPLATE)
*   [Query parameters](#body.QUERY_PARAMETERS)
*   [Request body](#body.request_body)
*   [Response body](#body.response_body)
    *   [JSON representation](#body.ListSourcesResponse.SCHEMA_REPRESENTATION)

Lists sources.

### HTTP request

`GET https://jules.googleapis.com/v1alpha/sources`

The URL uses [gRPC Transcoding](https://google.aip.dev/127) syntax.

### Query parameters



Parameters

`filter`

`string`

Optional. The filter expression for listing sources, based on AIP-160. If not set, all sources will be returned. Currently only supports filtering by name, which can be used to filter by a single source or multiple sources separated by OR.

Example filters: - 'name=sources/source1 OR name=sources/source2'

`pageSize`

`integer`

Optional. The number of sources to return. Must be between 1 and 100, inclusive. If unset, defaults to 30. If set to greater than 100, it will be coerced to 100.

`pageToken`

`string`

Optional. A page token, received from a previous `sources.list` call.

### Request body

The request body must be empty.

### Response body

Response message for the sources.list RPC.

If successful, the response body contains data with the following structure:

JSON representation

{
"sources": \[
{
object (`[Source](/jules/api/reference/rest/v1alpha/sources#Source)`)
}
\],
"nextPageToken": string
}

![](https://storage.googleapis.com/pieces-web-extensions-cdn/pieces.png)Copy And Save![](https://storage.googleapis.com/pieces-web-extensions-cdn/link.png)Share![](https://storage.googleapis.com/pieces-web-extensions-cdn/copilot.png)Ask Copilot![](https://storage.googleapis.com/pieces-web-extensions-cdn/settings.png)



Fields

`sources[]`

``object (`[Source](/jules/api/reference/rest/v1alpha/sources#Source)`)``

The sources from the specified request.

`nextPageToken`

`string`

A token, which can be sent as `pageToken` to retrieve the next page. If this field is omitted, there are no subsequent pages.