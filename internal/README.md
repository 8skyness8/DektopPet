# DesktopPet Internal

This is the active, offline Windows production implementation. The repository-root Java application is frozen as a reference, and `internal-poc/` remains feasibility evidence only.

Launch `DesktopPet_Internal.hta` directly. No installation, administrator rights, network access, registry modification, execution-policy change, imported executable, or third-party runtime is used. Runtime files are created only below `data/session_*`; preferences are stored in `data/settings.ini`.

## Architecture

The HTA presentation calls separate JScript modules for bridge lifecycle, terrain normalization, pure pet physics, and orchestration. One hidden Windows PowerShell process compiles the user32 P/Invoke helper once, configures color-key transparency/tool-window styling, and refreshes visible top-level-window geometry approximately every 400 ms. It atomically replaces a TSV snapshot through `MoveFileEx`; the HTA never reads a `WshScriptExec` status, exit code, or stream.

Each launch uses a unique session directory. The HTA refreshes `client.heartbeat`; PowerShell writes `bridge.heartbeat` and `status.txt`, observes `stop.request`, and self-terminates after 15 seconds without a fresh client heartbeat. Session evidence is intentionally retained for troubleshooting and can be deleted while DesktopPet is not running.

`status.txt` reports `starting`, `native_ready`, and `running<TAB><hwnd>` during a successful startup. Embedded C# compiler failures are preserved as `error<TAB>ADD_TYPE_COMPILE<TAB><message>`, and later bridge failures use `error<TAB>BRIDGE_RUNTIME<TAB><message>`. If HTA cannot marshal the discarded `Exec` return object, its exception is retained separately in `launch_error.txt` while file-based startup monitoring continues.

See `docs/MANUAL_SMOKE_TEST.md` for required Windows acceptance and `docs/INTERNAL_ROADMAP.md` for deliberately deferred work.
