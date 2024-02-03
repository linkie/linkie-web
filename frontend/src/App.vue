<script lang="ts">
import {defineComponent} from "vue"

import Home from "./routes/Home.vue"
import Dependencies from "./routes/Dependencies.vue"
import Generator from "./routes/Generator.vue"
import Mappings from "./routes/Mappings.vue"
import OpenSourceLicenses from "./routes/OpenSourceLicenses.vue"

import NotFound from "./routes/NotFound.vue"
import Footer from "./components/Footer.vue"
import Navbar from "./components/Navbar.vue"
import Alerts from "./components/Alerts.vue"
import {useI18nStore} from "./app/i18n-store"
import {isTauri, tauriInit} from "./app/tauri/tauri"
import Tauri from "./components/tauri/Tauri.vue"
import SourcesStatus from "./routes/SourcesStatus.vue";

const routes: { [route: string]: any; } = {
    "/": Home,
    "/dependencies": Dependencies,
    "/generator": Generator,
    "/mappings": Mappings,
    "/oss": OpenSourceLicenses,
    "/status/sources": SourcesStatus,
}

export default defineComponent({
    data() {
        return {
            current: window.location.pathname as string,
            isTauri,
        }
    },
    components: {Tauri, Alerts, Footer, Navbar},
    computed: {
        currentView() {
            return routes[this.current || "/"] || NotFound
        },
        theme(): string {
            return localStorage.getItem("theme") ?? ""
        },
        locale(): string {
            return this.$i18n.locale
        },
    },
    mounted() {
        this.$i18n.locale = this.$i18n.availableLocales.find((locale: string) => locale === useI18nStore().locale) ?? "en_US"

        let theme = localStorage.getItem("theme")
        if (theme) {
            if (theme === "cupcake") {
                document.documentElement.removeAttribute("data-theme")
                document.documentElement.classList.remove("dark")
                document.documentElement.style.setProperty("--color-scheme", "light")
            } else {
                document.documentElement.setAttribute("data-theme", "dark")
                document.documentElement.classList.add("dark")
                document.documentElement.style.setProperty("--color-scheme", "dark")
            }
        } else {
            localStorage.setItem("theme", "cupcake")
            document.documentElement.removeAttribute("data-theme")
            document.documentElement.classList.remove("dark")
            document.documentElement.style.setProperty("--color-scheme", "light")
        }
        
        if (isTauri()) {
            tauriInit()
        }
    },
})

</script>

<template>
    <meta name="theme-color" :key="theme" :content="theme === 'cupcake' ? '#efeae6' : '#242933'">
    <div class="overflow-x-hidden text-base-content dark:text-base-dark-content" :key="locale">
        <Navbar class="top-0 fixed z-10" :class="`navbar-${current}`"/>

        <div class="min-h-[100vh] flex flex-col justify-between bg-base-floor">
            <div v-if="current !== '/'" class="pt-[4.5rem]"/>
            <div class="grow shrink-0">
                <component :is="currentView"/>
            </div>
            <Footer v-if="!isTauri()"/>
            <div v-else class="h-10"/>
        </div>

        <Alerts/>
    </div>
    <Tauri v-if="isTauri()"/>
</template>

<style>
@import url('https://fonts.googleapis.com/css2?family=Inter:ital,wght@0,100;0,200;0,300;0,400;0,500;0,600;0,700;0,800;0,900;1,100;1,200;1,300;1,400;1,500;1,600;1,700;1,800;1,900&display=swap');
@import url('https://fonts.googleapis.com/css2?family=JetBrains+Mono:ital,wght@0,100..800;1,100..800&display=swap');

body {
    @apply bg-base-100 dark:bg-base-dark-100;
}

.select {
    outline: 0 !important;
    border: 0 !important;
}

:root > * {
    color-scheme: var(--color-scheme) !important;
}

:root {
    color-scheme: var(--color-scheme) !important;
}

</style>
