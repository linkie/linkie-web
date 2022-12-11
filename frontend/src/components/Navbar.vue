<template>
    <div class="navbar w-full">
        <div class="max-w-[78rem] mx-auto w-full flex justify-between min-h-16">
            <div class="flex">
                <NavbarButton href="/" :bold="true">Linkie</NavbarButton>
                <NavbarButton href="/dependencies">Dependencies</NavbarButton>
                <NavbarButton href="/mappings">Mappings</NavbarButton>
            </div>

            <NavbarButton @click="setDarkMode(!(($event.target as any)?.checked ?? darkMode()))" :key="componentKey">
                <svg v-if="darkMode()" xmlns="http://www.w3.org/2000/svg" class="icon icon-tabler" width="24" height="24" viewBox="0 0 24 24"
                     stroke-width="2"
                     stroke="currentColor" fill="none" stroke-linecap="round" stroke-linejoin="round">
                    <path stroke="none" d="M0 0h24v24H0z" fill="none"></path>
                    <path d="M7.962 3.949a8.97 8.97 0 0 1 4.038 -.957v.008h.393a7.478 7.478 0 0 0 -2.07 3.308m-.141 3.84c.186 .823 .514 1.626 .989 2.373a7.49 7.49 0 0 0 4.586 3.268m3.893 -.11c.223 -.067 .444 -.144 .663 -.233a9.088 9.088 0 0 1 -.274 .597m-1.695 2.337a9 9 0 0 1 -12.71 -12.749"></path>
                    <path d="M3 3l18 18"></path>
                </svg>
                <svg v-else xmlns="http://www.w3.org/2000/svg" class="icon icon-tabler" width="24" height="24" viewBox="0 0 24 24" stroke-width="2"
                     stroke="currentColor" fill="none" stroke-linecap="round" stroke-linejoin="round">
                    <path stroke="none" d="M0 0h24v24H0z" fill="none"></path>
                    <path d="M12 3c.132 0 .263 0 .393 0a7.5 7.5 0 0 0 7.92 12.446a9 9 0 1 1 -8.313 -12.454z"></path>
                </svg>
            </NavbarButton>
        </div>
    </div>
</template>

<script lang="ts">
import {defineComponent} from "vue"
import NavbarButton from "./NavbarButton.vue"

export default defineComponent({
    name: "Navbar",
    components: {NavbarButton},
    data() {
        return {
            componentKey: 0,
        }
    },
    methods: {
        darkMode(): boolean {
            return localStorage.getItem("theme") === "dark"
        },
        setDarkMode(darkMode: boolean) {
            localStorage.setItem("theme", darkMode ? "dark" : "cupcake")
            document.documentElement.setAttribute("data-theme", localStorage.getItem("theme")!)
            this.componentKey++
        },
    },
})
</script>

<style scoped>
</style>