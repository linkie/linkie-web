<template>
    <div class="navbar bg-base-100 shadow-xl z-10">
        <div class="flex-1">
            <a class="btn btn-ghost normal-case text-xl" href="/">Linkie</a>
        </div>

        <div class="flex-none">
            <ul class="menu menu-horizontal p-0">
                <li class="block sm:hidden">
                    <div class="dropdown dropdown-end">
                        <label tabindex="0">
                            <svg xmlns="http://www.w3.org/2000/svg" class="icon icon-tabler icon-tabler-menu-2" width="24" height="24" viewBox="0 0 24 24" stroke-width="2" stroke="currentColor" fill="none" stroke-linecap="round" stroke-linejoin="round">
                                <path stroke="none" d="M0 0h24v24H0z" fill="none"></path>
                                <line x1="4" y1="6" x2="20" y2="6"></line>
                                <line x1="4" y1="12" x2="20" y2="12"></line>
                                <line x1="4" y1="18" x2="20" y2="18"></line>
                            </svg>
                        </label>
                        <ul tabindex="0" class="menu dropdown-content p-2 shadow-2xl bg-base-100 rounded-box whitespace-nowrap mt-4 top-12">
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
                </li>
                <li class="hidden sm:block"><a href="/dependencies">Dependencies</a></li>
                <li class="hidden sm:block"><a href="/mappings">Mappings</a></li>

                <li class="hidden sm:block">
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