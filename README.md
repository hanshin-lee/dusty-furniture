# Dusty

A marketplace for vintage furniture and antiques, built with Kotlin Multiplatform and Compose Multiplatform. One shared codebase targets Android, iOS, and Web (WebAssembly).

## Tech Stack

- **Kotlin Multiplatform** — shared business logic across all platforms
- **Compose Multiplatform** — shared UI with platform-native rendering
- **Supabase** — auth, database (Postgrest), storage, and realtime
- **Koin** — dependency injection
- **Ktor** — HTTP client (CIO on Android, Darwin on iOS, Js on Web)
- **Coil 3** — image loading

## Project Structure

```
dusty/
├── shared/             # Shared KMP module (commonMain, androidMain, iosMain, wasmJsMain)
│   └── src/commonMain/kotlin/com/dusty/
│       ├── data/       # Models, repositories, Supabase client
│       ├── domain/     # Repository interfaces
│       ├── presentation/  # Screens, ViewModels, components, navigation, theme
│       ├── di/         # Koin modules
│       └── util/       # Constants, Resource sealed class
├── androidApp/         # Android app entry point
├── iosApp/             # iOS app (SwiftUI wrapper)
├── webApp/             # Web app (WASM/JS entry point)
└── supabase/           # Database migrations and seed data
```

## Getting Started

### Prerequisites

- JDK 17+
- Android Studio (for Android development)
- Xcode (for iOS development)

### Build & Run

```bash
# Android
./gradlew :androidApp:installDebug

# Web (dev server with hot reload)
./gradlew :webApp:wasmJsBrowserDevelopmentRun

# Web (production build)
./gradlew :webApp:wasmJsBrowserDistribution

# iOS framework (simulator)
./gradlew :shared:linkDebugFrameworkIosSimulatorArm64

# Full build (all targets)
./gradlew build
```

For iOS, open `iosApp/` in Xcode after building the shared framework.

## Deployment

Web deployment is automated via GitHub Actions. Pushing to `main` triggers a build and deploy to Cloudflare Pages.
