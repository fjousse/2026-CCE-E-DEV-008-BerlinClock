import { act, cleanup, fireEvent, render, screen } from "@testing-library/react";
import { afterEach, beforeEach, describe, expect, it, vi } from "vitest";
import App from "./App";

const rows = {
  secondsLamp: "Y",
  fiveHourRow: "ROOO",
  singleHourRow: "RRRO",
  fiveMinuteRow: "YYRYYRYYRYO",
  singleMinuteRow: "OOOO",
};

const fetchMock = vi.fn(async (_url: RequestInfo | URL, _options?: RequestInit) => ({
  ok: true,
  json: async () => rows,
}));

function requestedTimes(): Array<string | null> {
  return fetchMock.mock.calls.map(([url]) =>
    new URL(String(url), "http://localhost").searchParams.get("time"),
  );
}

async function finishConversion() {
  await act(async () => {
    await Promise.resolve();
  });
}

beforeEach(() => {
  vi.useFakeTimers();
  vi.setSystemTime(new Date(2026, 0, 7, 8, 35, 54));
  fetchMock.mockClear();
  vi.stubGlobal("fetch", fetchMock);
});

afterEach(() => {
  cleanup();
  vi.unstubAllGlobals();
  vi.useRealTimers();
});

describe("Berlin Clock interactions", () => {
  it("converts a manually selected time and stops current-time refresh", async () => {
    render(<App />);
    await finishConversion();

    fireEvent.change(screen.getByLabelText("Manual time"), { target: { value: "23:12:47" } });
    fireEvent.click(screen.getByRole("button", { name: "Show" }));
    await finishConversion();

    expect(requestedTimes()).toEqual(["08:35:54", "23:12:47"]);
    expect(screen.getByRole("heading", { level: 2 }).textContent).toBe("23:12:47");
    expect(screen.getByText("YYRYYRYYRYO")).toBeTruthy();

    await act(async () => {
      vi.advanceTimersByTime(3000);
    });
    expect(requestedTimes()).toEqual(["08:35:54", "23:12:47"]);
  });

  it("returns to the browser's current local time", async () => {
    render(<App />);
    await finishConversion();
    fireEvent.change(screen.getByLabelText("Manual time"), { target: { value: "23:12:47" } });
    fireEvent.click(screen.getByRole("button", { name: "Show" }));
    await finishConversion();

    vi.setSystemTime(new Date(2026, 0, 7, 8, 35, 55));
    fireEvent.click(screen.getByRole("button", { name: "Current time" }));
    await finishConversion();

    expect(requestedTimes()).toEqual(["08:35:54", "23:12:47", "08:35:55"]);
    expect(screen.getByRole("heading", { level: 2 }).textContent).toBe("08:35:55");
  });

  it("refreshes each second only while automatic refresh is enabled", async () => {
    render(<App />);
    await finishConversion();

    await act(async () => {
      vi.advanceTimersByTime(1000);
    });
    await finishConversion();
    expect(requestedTimes()).toEqual(["08:35:54", "08:35:55"]);

    fireEvent.click(screen.getByRole("checkbox", { name: "Refresh automatically every second" }));
    await act(async () => {
      vi.advanceTimersByTime(1000);
    });
    expect(requestedTimes()).toEqual(["08:35:54", "08:35:55"]);

    fireEvent.click(screen.getByRole("checkbox", { name: "Refresh automatically every second" }));
    await act(async () => {
      vi.advanceTimersByTime(1000);
    });
    expect(requestedTimes()).toEqual(["08:35:54", "08:35:55", "08:35:57"]);
  });
});
