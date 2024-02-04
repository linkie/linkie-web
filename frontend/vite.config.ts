import {defineConfig} from "vite"
import vue from "@vitejs/plugin-vue"
import {VitePWA} from "vite-plugin-pwa"
import {obfuscator} from "rollup-obfuscator"
import prismjsPlugin from "vite-plugin-prismjs"

// https://vitejs.dev/config/
export default defineConfig({
    server: {
        https: false,
        port: 8080,
    },
    plugins: [
        vue(),
        VitePWA({
            registerType: "autoUpdate",
            includeAssets: [
                "favicon.ico",
                "favicon-32x32.png",
                "favicon-16x16.png",
                "android-chrome-192x192.png",
                "android-chrome-512x512.png",
                "apple-touch-icon.png",
                "safari-pinned-tab.svg",
                "robots.txt",
            ],
            manifest: {
                name: "Linkie",
                short_name: "Linkie",
                icons: [
                    {
                        src: "/android-chrome-192x192.png",
                        sizes: "192x192",
                        type: "image/png",
                    },
                    {
                        src: "/android-chrome-512x512.png",
                        sizes: "512x512",
                        type: "image/png",
                    },
                ],
                theme_color: "#333333",
                background_color: "#333333",
                display: "standalone",
            },
            selfDestroying: true,
            workbox: {
                cleanupOutdatedCaches: true,
                sourcemap: true,
                skipWaiting: true,
            },
            devOptions: {
                enabled: true,
            }
        }),
        prismjsPlugin({
            languages: ["java", "groovy", "kotlin", "gradle", "json", "properties", "kt", "kts", "batch", "bash"],
            css: true,
            theme: "tomorrow",
        }),
        obfuscator({
            global: true,
            exclude: ["node_modules/**/*"],
            compact: true,
            controlFlowFlattening: false,
            deadCodeInjection: false,
            debugProtection: false,
            debugProtectionInterval: 0,
            disableConsoleOutput: true,
            identifierNamesGenerator: "hexadecimal",
            log: false,
            numbersToExpressions: false,
            renameGlobals: false,
            selfDefending: true,
            simplify: true,
            splitStrings: false,
            stringArray: true,
            stringArrayCallsTransform: false,
            stringArrayEncoding: [],
            stringArrayIndexShift: true,
            stringArrayRotate: true,
            stringArrayShuffle: true,
            stringArrayWrappersCount: 1,
            stringArrayWrappersChainedCalls: true,
            stringArrayWrappersParametersMaxCount: 2,
            stringArrayWrappersType: "variable",
            stringArrayThreshold: 0.75,
            unicodeEscapeSequence: false,
        }),
    ],
})
