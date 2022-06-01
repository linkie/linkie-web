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
                        <input type="checkbox" class="toggle" :checked="darkMode" data-toggle-theme="dark,cupcake" data-act-class="ACTIVECLASS"/>
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
})
</script>

<style scoped>
</style>