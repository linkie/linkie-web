<template>
    <div class="navbar bg-base-100 shadow-xl z-10">
        <div class="flex-1">
            <a class="btn btn-ghost normal-case text-xl" href="/">Linkie</a>
        </div>

        <div class="flex-none">
            <ul class="menu menu-horizontal p-0">
                <li><a href="/dependencies">Dependencies</a></li>
                <li><a href="/mappings">Mappings</a></li>

                <li>
                    <label class="cursor-pointer">
                        Dark Mode
                        <input type="checkbox" class="toggle" :checked="darkMode" @click="setDarkMode(($event.target as any)?.checked ?? darkMode)"/>
                    </label>
                </li>
            </ul>
        </div>
    </div>
</template>

<script lang="ts">
import {defineComponent} from "vue"

export default defineComponent({
    name: "Navbar",
    computed: {
        darkMode(): boolean {
            return localStorage.getItem("theme") === "dark"
        },
    },
    methods: {
        setDarkMode(darkMode: boolean) {
            localStorage.setItem("theme", darkMode ? "dark" : "cupcake");
            document.documentElement.setAttribute("data-theme", localStorage.getItem("theme")!);
        },
    },
})
</script>

<style scoped>
</style>