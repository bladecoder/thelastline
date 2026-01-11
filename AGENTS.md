# AGENTS

Repo overview: Gradle multi-module (core, lwjgl3, android) built with Java 11/libGDX.
Cursor/Copilot rules: none present as of Jan 2026.

Build desktop run: `./gradlew lwjgl3:run` (assets auto-synced).
Package desktop jar/image: `./gradlew lwjgl3:dist` or `lwjgl3:jpackageImage`.
Android debug build: `./gradlew android:assembleDebug`; release use `assembleRelease`.
Full verification: `./gradlew clean build` (runs compile + unit tests).
Single module tests: `./gradlew core:test` (no Android unit tests configured).
Single test method: `./gradlew test --tests com.example.ClassTest.method`.

Java style: follow existing libGDX code—4-space indents, K&R braces, one statement per line.
Keep imports explicit, alphabetized, and grouped (java.*, third-party, project).
Prefer `final` fields/locals when values do not change; avoid raw types.
Use descriptive camelCase for fields/methods, PascalCase for classes, UPPER_SNAKE for constants.
Favor EngineLogger/Gdx logging utilities; never swallow exceptions silently.
Validate inputs early and return fast; use guard clauses as seen in state handlers.
UI/layout code should respect DPIUtils spacing helpers rather than hardcoded values.
Game loops should stay deterministic: accumulate delta, avoid blocking calls, reuse Timer tasks judiciously.
