<!--
SPDX-FileCopyrightText: 2023 Deutsche Telekom AG

SPDX-License-Identifier: CC0-1.0    
-->
# Welcome to the LMOS Sample Agents Repository

## About

This repository contains sample agents for the LMOS (Language Model Orchestration System) framework. These agents demonstrate how to integrate and utilize various language models, such as GPT-4o, within a Spring Boot application. The provided agents include examples for news, product search, technical specifications, and weather information. Each agent is designed to handle specific tasks and can be customized to fit different use cases.

## Agents

The following agents are available in this repository:

- [News Agent](./news-agent) (`news-agent`): Retrieves the latest news headlines from a given URL.
- [Weather Agent](./weather-agent) ('weather-agent`): Retrieves the current weather forecast for a given location.
- [Technical Specifications Agent](./techspec-agent) (`techspec-agent`): Defines technical specifications from a requirements description.
- [Product Search Agent](./productsearch-agent) (`productsearch-agent`): Searches for products that match given technical specifications.

## How to Run

#### 1. Set your OpenAI API Key

The sample agents are set up to use the OpenAI API to access the GPT-4o-mini language model. To use the agents, you need to obtain an API key from OpenAI. You can sign up for an API key at https://platform.openai.com/signup.

Then simply export the API key as an environment variable.

```bash
export OPENAI_API_KEY=[YOUR_OPENAI_API_KEY]
```

#### 2. Start the Application

Choose one of the agents and start it like a normal Spring Boot application.
This requires the port 8080 to be available.

```bash
  ./gradlew :<[agent-name]>:bootRun
```
e.g.
```bash
  ./gradlew :news-agent:bootRun
```
for the news agent.

#### 3. Access the Agent

The sample agents are built with [Arc](https://lmos-ai.github.io/arc/) and can be accessed via a Graphiql interface.
Open http://localhost:8080/graphiql?path=/graphql

Example request for the news agent:

```graphql
subscription {
    agent(
        request: {
            conversationContext: {
                conversationId: "1"
            }
            systemContext: [],
            userContext: {
                userId: "1234",
                profile: []
            },
            messages: [
                {
                    role: "user",
                    content: "What are the headlines at https://www.theregister.com/ today?",
                    format: "text",
                }
            ]
        }
    ) {
        messages {
            content
        }
    }
}
```

## Code of Conduct

This project has adopted the [Contributor Covenant](https://www.contributor-covenant.org/) in version 2.1 as our code of conduct. Please see the details in our [CODE_OF_CONDUCT.md](CODE_OF_CONDUCT.md). All contributors must abide by the code of conduct.

By participating in this project, you agree to abide by its [Code of Conduct](./CODE_OF_CONDUCT.md) at all times.

## Licensing

This project follows the [REUSE standard for software licensing](https://reuse.software/).    
Each file contains copyright and license information, and license texts can be found in the [./LICENSES](./LICENSES) folder. For more information visit https://reuse.software/.    
You can find a guide for developers at https://telekom.github.io/reuse-template/.
