## Synopsis

The purpose of this project is to create a chat room web application using the Java API for Websockets.

## Code Example

The Java server will be written using the Java API for WebSockets, and the client will be written in JavaScript, which will utilise the jQuery library for DOM manipulation.

## Motivation

I have created this project to help solidify my understanding of the Java API for WebSockets after watching the Pluralsight course "Introduction to the Java API for WebSockets" @ https://app.pluralsight.com/library/courses/java-api-websockets-introduction/table-of-contents.

## Getting started
This Java EE project was created using Adam Bien's minimalist maven archetype, which can be found at:
- http://www.adam-bien.com/roller/abien/entry/essential_javaee_7_pom_xml

# HTML websocket clients
_Access the Echo endpoint_ @
* http://localhost:8080/websockets-chat-app/echo.html

_Access the Chat webapp_ @ 
* http://localhost:8080/websockets-chat-app/

# Sonar Report
The Sonar report for this project can be found @
* https://sonarcloud.io/dashboard?id=com.pluralsight.websockets%3Awebsockets-chat-app

Sonar maven command
```
mvn clean org.jacoco:jacoco-maven-plugin:prepare-agent package sonar:sonar \
    -Dsonar.host.url=https://sonarcloud.io \
    -Dsonar.organization=bazzani-github \
    -Dsonar.login=b5c80850abb54d57f065ca7e5f7159d8daaaedf6
```
