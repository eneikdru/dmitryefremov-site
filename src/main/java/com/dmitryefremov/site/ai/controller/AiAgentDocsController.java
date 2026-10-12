package com.dmitryefremov.site.ai.controller;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.LinkedHashMap;
import java.util.Map;

@RestController
public class AiAgentDocsController {

    @GetMapping(value = "/llms.txt", produces = MediaType.TEXT_PLAIN_VALUE)
    public ResponseEntity<String> getLlmsTxt() {
        String content = """
                # Dmitry Efremov Personal Site & Service API for AI Agents

                > Machine-readable documentation and discovery file for AI agents (ChatGPT, Claude, Custom Assistants).

                ## Overview
                This API allows external AI agents to discover service capabilities, place orders for professional services (MVP development, architectural audit, automation), and send direct messages on behalf of users.

                ## Available Service Endpoints

                ### 1. Place Service Order
                - **Endpoint**: `POST /api/v1/ai/orders`
                - **Content-Type**: `application/json`
                - **Payload Schema**:
                  - `userName` (string, required): Name of the user placing the order
                  - `userEmail` (string, required, valid email): Contact email address
                  - `agentIdentifier` (string, optional): ID/name of calling AI agent
                  - `serviceType` (string, required): Type of service requested (e.g. "MVP", "AUDIT", "AUTOMATION")
                  - `requirements` (string, optional): Detailed requirements or scope description
                - **Response**: 201 Created with JSON acknowledgement including order ID, status ("NEW"), and confirmation message.

                ### 2. Send Direct Message
                - **Endpoint**: `POST /api/v1/ai/messages`
                - **Content-Type**: `application/json`
                - **Payload Schema**:
                  - `senderName` (string, required): Name of sender
                  - `senderEmail` (string, required, valid email): Sender contact email
                  - `agentIdentifier` (string, optional): ID/name of calling AI agent
                  - `content` (string, required): Message text content
                - **Response**: 201 Created with JSON acknowledgement including message ID, status ("NEW"), and confirmation message.

                ### 3. Read Articles & Podcasts
                - **Articles Endpoint**: `GET /api/v1/articles`
                - **Podcast Episodes Endpoint**: `GET /api/v1/podcasts/episodes`

                ## Machine Readable OpenAPI Spec
                - **OpenAPI Schema Endpoint**: `GET /api/v1/ai/docs`
                """;
        return ResponseEntity.ok(content);
    }

    @GetMapping(value = "/api/v1/ai/docs", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Map<String, Object>> getOpenApiSpec() {
        Map<String, Object> spec = new LinkedHashMap<>();
        spec.put("openapi", "3.0.3");

        Map<String, Object> info = new LinkedHashMap<>();
        info.put("title", "AI Agent API Specification");
        info.put("version", "1.0.0");
        info.put("description", "Public machine-readable API for AI agents to submit service orders and direct messages.");
        spec.put("info", info);

        Map<String, Object> paths = new LinkedHashMap<>();

        // /api/v1/ai/orders
        Map<String, Object> ordersPath = new LinkedHashMap<>();
        Map<String, Object> postOrder = new LinkedHashMap<>();
        postOrder.put("summary", "Submit a service order on behalf of a user");
        postOrder.put("operationId", "createServiceOrder");
        paths.put("/api/v1/ai/orders", ordersPath);
        ordersPath.put("post", postOrder);

        // /api/v1/ai/messages
        Map<String, Object> messagesPath = new LinkedHashMap<>();
        Map<String, Object> postMessage = new LinkedHashMap<>();
        postMessage.put("summary", "Send a direct message to the author");
        postMessage.put("operationId", "sendDirectMessage");
        paths.put("/api/v1/ai/messages", messagesPath);
        messagesPath.put("post", postMessage);

        spec.put("paths", paths);

        return ResponseEntity.ok(spec);
    }
}
