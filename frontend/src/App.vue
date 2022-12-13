<script lang="ts">
import {defineComponent} from "vue"

import Home from "./routes/Home.vue"
import Dependencies from "./routes/Dependencies.vue"
import Mappings from "./routes/Mappings.vue"
import OpenSourceLicenses from "./routes/OpenSourceLicenses.vue"
import NotFound from "./routes/NotFound.vue"

import Footer from "./components/Footer.vue"
import Navbar from "./components/Navbar.vue"
import Alerts from "./components/Alerts.vue"

const routes: { [route: string]: any; } = {
    "/": Home,
    "/dependencies": Dependencies,
    "/mappings": Mappings,
    "/oss": OpenSourceLicenses,
}

export default defineComponent({
    data() {
        return {
            current: window.location.pathname as string,
        }
    },
    components: {Alerts, Footer, Navbar},
    computed: {
        currentView() {
            return routes[this.current || "/"] || NotFound
        },
        theme(): string {
            return localStorage.getItem("theme") ?? ""
        },
    },
    mounted() {
        let theme = localStorage.getItem("theme")
        if (theme) {
            document.documentElement.setAttribute("data-theme", theme)
        } else {
            localStorage.setItem("theme", "cupcake")
            document.documentElement.setAttribute("data-theme", "cupcake")
        }
    },
})

</script>

<template>
    <meta name="theme-color" :key="theme" :content="theme === 'cupcake' ? '#efeae6' : '#242933'">
    <div class="w-screen overflow-x-hidden">
        <Navbar class="top-0 fixed z-10" :class="`navbar-${current}`"/>

        <div class="w-screen min-h-screen flex flex-col justify-between bg-base-200">
            <component :is="currentView"/>
            <Footer/>
        </div>

        <Alerts/>
    </div>
</template>

<style>
@import url('https://fonts.googleapis.com/css2?family=Inter:ital,wght@0,100;0,200;0,300;0,400;0,500;0,600;0,700;0,800;0,900;1,100;1,200;1,300;1,400;1,500;1,600;1,700;1,800;1,900&display=swap');

body {
    @apply bg-base-200
}

.select {
    outline: 0 !important;
    border: 0 !important;
}
</style>
