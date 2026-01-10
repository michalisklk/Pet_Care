// Notification Service - SMS Monitor (UI)

// API endpoints + polling
const API_LIST = "/api/v1/monitor/sms-events?limit=50";
const API_CLEAR = "/api/v1/monitor/sms-events/clear";
const POLL_MS = 2000;

// DOM στοιχεία
const body = document.getElementById("eventsBody");
const badge = document.getElementById("countBadge");
const errorBox = document.getElementById("errorBox");
const baseUrlEl = document.getElementById("baseUrl");
const lastUpdatedEl = document.getElementById("lastUpdated");
const btnRefresh = document.getElementById("btnRefresh");
const btnClear = document.getElementById("btnClear");

let timerId = null;

// base URL του service (π.χ. http://localhost:8081)
baseUrlEl.textContent = window.location.origin;

// κουμπί refresh
btnRefresh.addEventListener("click", loadEvents);

// κουμπί clear (σβήνει τα events)
btnClear.addEventListener("click", async () => {
    if (!confirm("Σίγουρα θες να καθαρίσεις τα events;")) return;
    try {
        await fetch(API_CLEAR, { method: "POST" });
        await loadEvents();
    } catch (e) {
        showError(true);
        console.error(e);
    }
});

function showError(on) {
    errorBox.classList.toggle("pc-hidden", !on);
}

function setBadge(count) {
    badge.textContent = String(count);
}

function setLastUpdatedNow() {
    lastUpdatedEl.textContent = new Date().toLocaleString("el-GR");
}

// μορφοποίηση χρόνου από ISO
function fmtAt(iso) {
    if (!iso) return "";
    const d = new Date(iso);
    return isNaN(d.getTime()) ? String(iso) : d.toLocaleString("el-GR");
}

// empty κατάσταση πίνακα
function emptyRow() {
    body.innerHTML = "";
    const tr = document.createElement("tr");
    const td = document.createElement("td");
    td.colSpan = 4;
    td.className = "pc-monitor__empty";
    td.textContent = "Waiting for events… (κάνε ένα appointment)";
    tr.appendChild(td);
    body.appendChild(tr);
}

// status pill (SENT/FAILED)
function pill(sent) {
    const span = document.createElement("span");
    span.className = "pc-pill " + (sent ? "pc-pill--success" : "pc-pill--error");
    span.textContent = sent ? "SENT" : "FAILED";
    return span;
}

// φόρτωση events από API και ενημέρωση πίνακα
async function loadEvents() {
    try {
        showError(false);

        const r = await fetch(API_LIST, { cache: "no-store" });
        if (!r.ok) throw new Error("HTTP " + r.status);

        const data = await r.json();

        setBadge(Array.isArray(data) ? data.length : 0);
        setLastUpdatedNow();

        if (!Array.isArray(data) || data.length === 0) {
            emptyRow();
            return;
        }

        body.innerHTML = "";

        for (const e of data) {
            const tr = document.createElement("tr");

            const tdAt = document.createElement("td");
            tdAt.className = "pc-mono";
            tdAt.textContent = fmtAt(e.at);

            const tdTo = document.createElement("td");
            tdTo.className = "pc-mono";
            tdTo.textContent = e.toE164 ?? "";

            const tdContent = document.createElement("td");
            tdContent.textContent = e.content ?? "";

            const tdStatus = document.createElement("td");
            tdStatus.className = "pc-monitor__status-cell";
            tdStatus.appendChild(pill(!!e.sent));

            tr.appendChild(tdAt);
            tr.appendChild(tdTo);
            tr.appendChild(tdContent);
            tr.appendChild(tdStatus);

            body.appendChild(tr);
        }
    } catch (e) {
        showError(true);
        console.error(e);
    }
}

// polling κάθε POLL_MS (σταματάει όταν φύγεις από το tab)
function startPolling() {
    if (timerId) clearInterval(timerId);
    timerId = setInterval(loadEvents, POLL_MS);
}

document.addEventListener("visibilitychange", () => {
    if (document.visibilityState === "visible") {
        loadEvents();
        startPolling();
    } else {
        if (timerId) clearInterval(timerId);
        timerId = null;
    }
});

// αρχικό load
loadEvents();
startPolling();
