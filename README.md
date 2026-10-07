# Berlin Clock

This repository contains a Spring Boot backend and a React frontend for the Berlin Clock kata. The backend converts a supplied digital time into five Berlin Clock rows. The frontend shows those rows as text for either the browser's current local time or a manually selected time.

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

Spring Boot starts on `http://localhost:8080`. `mvn verify` builds the backend and runs its tests.

Convert a local time of day using `GET /api/berlin-clock?time=HH:mm:ss`:

```sh
curl 'http://localhost:8080/api/berlin-clock?time=23:12:47'
```

The response contains five named lamp rows:

```json
{
  "secondsLamp": "O",
  "fiveHourRow": "RRRR",
  "singleHourRow": "RRRO",
  "fiveMinuteRow": "YYOOOOOOOOO",
  "singleMinuteRow": "YYOO"
}
```

`R` is a lit red lamp, `Y` is a lit yellow lamp, and `O` is an off lamp. Missing or invalid times return HTTP `400` with a validation detail. The API treats `time` as a local time of day, without timezone conversion.

## Frontend

In a separate terminal, from the repository root:

```sh
cd frontend
npm ci
npm run build
npm run dev
```

Vite prints the local URL, normally `http://localhost:5173`. The page opens in **Current Time** mode and converts the browser's local time every second. Clear **Automatic refresh** to pause those updates. Enter a time including seconds and select **Convert manual time** to switch to manual mode; this stops current-time updates. Select **Use current time** to return to Current Time mode, which resumes automatic refresh if the checkbox is selected. Both servers must be running: the Vite development server forwards `/api` requests to the backend on port `8080`.

The backend and frontend can be started independently. Stop either development server with `Ctrl+C`.
