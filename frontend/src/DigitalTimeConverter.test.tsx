import { cleanup, fireEvent, render, screen } from "@testing-library/react";
import { afterEach, beforeEach, describe, expect, it, vi } from "vitest";
import DigitalTimeConverter from "./DigitalTimeConverter";

const fetchMock = vi.fn();
const validClock = "ORROOROOOYYRYYRYOOOOYYOO";
const invalidClock = "ORROOROOOYYRYYRYOOOOYOOY";
const validationMessage = "Berlin Clock must be a valid 24-character R/Y/O representation";

function submit(representation: string) {
  fireEvent.change(screen.getByLabelText("Berlin Clock lamps"), {
    target: { value: representation },
  });
  fireEvent.click(screen.getByRole("button", { name: "Convert" }));
}

beforeEach(() => {
  fetchMock.mockReset();
  vi.stubGlobal("fetch", fetchMock);
});

afterEach(() => {
  cleanup();
  vi.unstubAllGlobals();
});

describe("reverse conversion", () => {
  it("shows the digital time returned by the backend", async () => {
    fetchMock.mockResolvedValue({ ok: true, json: async () => ({ time: "11:37:01" }) });
    render(<DigitalTimeConverter />);

    expect(fetchMock).not.toHaveBeenCalled();
    submit(validClock);

    expect(await screen.findByText("11:37:01")).toBeTruthy();
    expect(fetchMock).toHaveBeenCalledWith(
      `/api/digital-time?berlinClock=${validClock}`,
      { signal: expect.any(AbortSignal) },
    );
  });

  it("shows the backend validation error for an inconsistent lamp row", async () => {
    fetchMock.mockResolvedValue({
      ok: false,
      json: async () => ({ detail: validationMessage }),
    });
    render(<DigitalTimeConverter />);
    submit(invalidClock);

    expect((await screen.findByRole("alert")).textContent).toBe(validationMessage);
    expect(fetchMock).toHaveBeenCalledWith(
      `/api/digital-time?berlinClock=${invalidClock}`,
      { signal: expect.any(AbortSignal) },
    );
  });

  it("can retry the same representation after an error", async () => {
    fetchMock
      .mockResolvedValueOnce({ ok: false, json: async () => ({ detail: validationMessage }) })
      .mockResolvedValueOnce({ ok: true, json: async () => ({ time: "11:37:01" }) });
    render(<DigitalTimeConverter />);
    submit(validClock);
    await screen.findByRole("alert");

    fireEvent.click(screen.getByRole("button", { name: "Convert" }));

    expect(await screen.findByText("11:37:01")).toBeTruthy();
    expect(screen.queryByRole("alert")).toBeNull();
    expect(fetchMock).toHaveBeenCalledTimes(2);
  });
});
