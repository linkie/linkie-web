# Linkie Web
View mod dependencies; Search up mappings; All in one place!

## About the Project

#### backend

This subfolder contains the backend JVM server, connecting linkie-core.

This project uses Ktor as the HTTP server.

#### frontend

This subfolder contains the frontend Vue website. The frontend runs independently from the backend and is entirely client sided and static. The official instance is in fact ran from GitHub Pages.

#### frontend/src-tauri

This subfolder contains the experimental tauri application for a local Linkie experience.

The application fires a JRE process of the backend and connects to it, the frontend is the Vue website.

Due to the nature of the project, with a JRE VM, it uses quite a lot of memory. There are goals to turn the underlying backend into a Kotlin/Native project in the future.

## Running the Project

#### backend

Compiling the backend:

```bash
cd backend
./gradlew build
```

The compiled artifact is at `./backend/build/libs/linkie-web-backend.jar`, run it using JRE8+.

#### frontend

Testing the frontend:

```bash
npm install
npm run dev
```

Compiling the frontend: (Resultant HTML at `/dist`)

```bash
npm install
npm run build
```

#### frontend/tauri

First, we need to build a custom JRE to bundle along: (Use Java 17)

```bash
cd backend
./gradlew customJre
```

The custom jre is in `build/libs/`.
Now, also compile the `backend`. (Instructions above)

Build the tauri project: (Requires Rust)

```bash
npm install
npm run tauri build
```

Compiled file is at `/frontend/src-tauri/target/release/`.<br>
Bundled installer/app is at `/frontend/src-tauri/target/release/bundle`.
