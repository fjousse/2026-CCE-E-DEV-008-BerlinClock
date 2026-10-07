# Berlin Clock

This repository contains a Spring Boot backend and a React frontend for the Berlin Clock kata. The project is being built in small, test-driven steps. At this stage, both applications start, but conversion and API endpoints have not been added yet.

## Prerequisites

- JDK 25
- Maven 3.9 or newer
- Node.js 24 and npm 11

The commands below were verified with Java 25.0.3, Maven 3.9.16, Node.js 24.19.0, and npm 11.17.0.

## Backend

From the repository root:

```sh
cd backend
mvn verify
mvn spring-boot:run
```

Spring Boot starts on `http://localhost:8080`. There are no HTTP endpoints yet. `mvn verify` builds the backend and runs its tests; no tests have been added at this stage.

## Frontend

In a separate terminal, from the repository root:

```sh
cd frontend
npm ci
npm run build
npm run dev
```

Vite prints the local URL, normally `http://localhost:5173`. The page currently shows a placeholder heading. The frontend does not call the backend yet.

The backend and frontend can be started independently. Stop either development server with `Ctrl+C`.
