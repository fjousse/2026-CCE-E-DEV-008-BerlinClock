# Berlin Clock

Spring Boot converts a local time of day into Berlin Clock rows. React displays the lamps and lets you choose between the browser's current time and a manual time.

## Prerequisites

- JDK 25
- Maven 3.9 or newer
- Node.js 24 and npm 11

Verified with Java 25.0.3, Maven 3.9.16, Node.js 24.19.0, and npm 11.17.0.

## Build and test

Run these commands from the repository root:

```sh
cd backend
mvn verify
cd ../frontend
npm ci
npm test
npm run build
```

`mvn verify` runs the backend tests and builds the backend. `npm test` runs the frontend interaction tests; `npm run build` checks TypeScript and builds the frontend.

## Run

Start each server in a separate terminal from the repository root:

```sh
cd backend
mvn spring-boot:run
```

```sh
cd frontend
npm ci
npm run dev
```

Open the URL printed by Vite, normally `http://localhost:5173`. The frontend forwards `/api` requests to the backend on `http://localhost:8080`. Stop either server with `Ctrl+C`.

## Use the clock

- **Current time** is selected on startup. The browser sends its local time to the backend every second.
- Clear **Refresh automatically every second** to pause current-time updates.
- Enter a time including seconds and select **Show** to use manual time. This stops current-time updates.
- Select **Current time** to return to the browser's time. Refresh resumes if its checkbox is selected.
- In the separate **Berlin Clock to digital time** section, enter all 24 lamps and select **Convert**. The backend returns the digital time or a validation error.

## API

Convert a local time of day with `GET /api/berlin-clock?time=HH:mm:ss`:

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

`R` is a lit red lamp, `Y` is a lit yellow lamp, and `O` is an off lamp. The API treats `time` as a local time of day without timezone conversion. Missing or invalid times return HTTP `400` with a validation detail.

Convert a 24-character Berlin Clock representation back to digital time with `GET /api/digital-time?berlinClock=...`:

```sh
curl 'http://localhost:8080/api/digital-time?berlinClock=ORROOROOOYYRYYRYOOOOYYOO'
```

The response is `{"time":"11:37:01"}`. The seconds lamp reveals only parity, so reverse conversion uses `00` for a lit `Y` lamp and `01` for an unlit `O` lamp. Missing, malformed, or inconsistent lamp representations return HTTP `400` with a validation detail.
