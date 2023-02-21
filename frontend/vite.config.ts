import {defineConfig} from "vite"
import vue from "@vitejs/plugin-vue"
import mkcert from "vite-plugin-mkcert"
import {VitePWA} from "vite-plugin-pwa"
import prismjsPlugin from "vite-plugin-prismjs"

// https://vitejs.dev/config/
export default defineConfig({
    server: {
        https: true,
    },
    plugins: [
        vue(),
        mkcert(),
        VitePWA({
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
            workbox: {
                cleanupOutdatedCaches: false,
                sourcemap: true,
            },
        }),
        prismjsPlugin({
            languages: ["java", "groovy", "kotlin", "gradle"],
            css: true,
            theme: "tomorrow",
        }),
    ],
})
