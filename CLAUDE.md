# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

Dusty is a Kotlin Multiplatform (KMP) marketplace for vintage furniture and antiques. It targets Android, iOS, and Web (WASM) from a single shared codebase using Compose Multiplatform and Supabase as the backend.

## Build & Run Commands

```bash
# Android
./gradlew :androidApp:installDebug

# Web (dev server with hot reload)
./gradlew :webApp:wasmJsBrowserDevelopmentRun

# Web (production build)
./gradlew :webApp:wasmJsBrowserDistribution

# iOS framework (simulator)
./gradlew :shared:linkDebugFrameworkIosSimulatorArm64

# iOS framework (device)
./gradlew :shared:linkDebugFrameworkIosArm64

# Full build (all targets)
./gradlew build
```

Web deployment is automated via GitHub Actions → Cloudflare Pages on push to `main`.

## Architecture

Clean Architecture with Repository Pattern across three layers:

- **domain/** — Repository interfaces (contracts only)
- **data/** — Repository implementations, Kotlin Serializable models, Supabase client
- **presentation/** — Compose screens, ViewModels (StateFlow), reusable components, navigation, theme

Data flow: `Screen → ViewModel (StateFlow) → Repository Interface → Repository Impl → SupabaseClient`

All async results are wrapped in `Resource<T>` (sealed class: Loading, Success, Error).

## Dependency Injection (Koin)

- `AppModule.kt` — registers SupabaseClient (singleton), all repositories (singleton), and ViewModels (factory)
- `PlatformModule.kt` — expect/actual pattern providing platform-specific Ktor HTTP engines:
  - Android: CIO, iOS: Darwin, Web: Js
- Initialized in `App.kt` via `KoinApplication { modules(appModule, platformModule) }`
- ViewModels that need navigation args use `params.get()` (e.g., `ListingDetailViewModel`)

## Navigation

Type-safe navigation using Kotlin Serializable route objects in `presentation/routes/Routes.kt`. The nav graph in `DustyNavGraph.kt` has 4 bottom tabs (Home, Browse, Cart, Profile) with nested destinations. Bottom bar is conditionally hidden on detail/checkout screens.

## Supabase Backend

- Credentials in `shared/.../util/Constants.kt`
- Client configured in `SupabaseProvider.kt` with Auth, Postgrest, Storage, and Realtime plugins
- Database schema in `supabase/migrations/00001_initial_schema.sql`
- Seed data in `supabase/seed.sql` (10 categories)
- RLS enabled on all tables — users must authenticate for cart/order operations
- Full-text search on listings via generated `fts` tsvector column
- Trigger auto-creates profile row on user signup

## Key Conventions

- Shared code lives in `shared/src/commonMain/`; platform-specific code uses expect/actual in `androidMain/`, `iosMain/`, `wasmJsMain/`
- Models use `@Serializable` with `@SerialName` for snake_case DB column mapping
- iOS framework is built as static (`isStatic = true`, name: `shared`)
- Web WASM output filename is `dusty.js`, rendered into a `<canvas id="ComposeTarget">`
- Version catalog in `gradle/libs.versions.toml` — all dependency versions managed there
