<script lang="ts">
import {defineComponent} from "vue"

import Footer from "./components/Footer.vue"
import Navbar from "./components/Navbar.vue"
import Alerts from "./components/Alerts.vue"
import {useI18nStore} from "./app/i18n-store"
import {isTauri, tauriInit} from "./app/tauri/tauri"
import Tauri from "./components/tauri/Tauri.vue"
import {useI18n} from "vue-i18n"

export default defineComponent({
    data() {
        return {
            isTauri,
            allowTransition: false,
        }
    },
    components: {Tauri, Alerts, Footer, Navbar},
    computed: {
        current(): string {
            return this.$route.path
        },
        theme(): string {
            return localStorage.getItem("theme") ?? ""
        },
        locale(): string {
            return this.$i18n.locale
        },
        fontLink(): string | undefined {
            switch (this.locale) {
                case "zh_TW":
                    return "https://fonts.googleapis.com/css2?family=Noto+Sans+TC:wght@100..900"
                case "zh_CN":
                    return "https://fonts.googleapis.com/css2?family=Noto+Sans+SC:wght@100..900"
                default:
                    return undefined
            }
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

        setTimeout(() => {
            this.allowTransition = true
        }, 1000)
    },
    setup() {
        const { t } = useI18n()
        return { t }
    }
})

</script>

<template>
    <link rel="stylesheet" :href="fontLink" v-if="!!fontLink">
    <meta name="theme-color" :key="theme" :content="theme === 'cupcake' ? '#efeae6' : '#242933'">
    <div class="overflow-x-hidden text-base-content dark:text-base-dark-content" :key="locale">
        <Navbar class="top-0 fixed z-10" :class="`navbar-${current}`"/>

        <div class="min-h-[100vh] flex flex-col justify-between bg-base-floor">
            <transition :enter-active-class="`duration-500 ${allowTransition ? 'transition-all' : 'transition-none'}`"
                        enter-from-class="!mt-0"
                        :leave-active-class="`duration-500 ${allowTransition ? 'transition-all' : 'transition-none'}`"
                        leave-to-class="!mt-0">
                <div v-if="current !== '/'" class="mt-[4.5rem]"/>
            </transition>
            <div class="grow shrink-0">
                <router-view v-slot="{ Component, route }">
                    <transition enter-active-class="transition-opacity duration-250 delay-250 ease-linear"
                                enter-from-class="opacity-50"
                                enter-to-class="opacity-100"
                                leave-active-class="transition-opacity duration-250 ease-linear"
                                leave-from-class="opacity-100"
                                leave-to-class="opacity-50">
                        <component :is="Component"/>
                    </transition>
                </router-view>
            </div>
            <Footer v-if="!isTauri()"/>
            <div v-else class="h-10"/>
        </div>

        <Alerts/>
    </div>
    <Tauri v-if="isTauri()"/>
</template>

<style>
@import url('https://fonts.googleapis.com/css2?family=Inter:ital,wght@0,100;0,200;0,300;0,400;0,500;0,600;0,700;0,800;0,900;1,100;1,200;1,300;1,400;1,500;1,600;1,700;1,800;1,900');
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
