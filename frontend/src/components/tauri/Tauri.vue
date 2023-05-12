<template>
    <TauriConsole :console-enabled="consoleEnabled"/>
    <TauriFooter :console-enabled="consoleEnabled" :set-console-enabled="enabled => consoleEnabled = enabled"/>
</template>

<script lang="ts">
import {defineComponent} from "vue"
import TauriFooter from "./TauriFooter.vue"
import TauriConsole from "./TauriConsole.vue"

export default defineComponent({
    name: "Tauri",
    data() {
        return {
            consoleEnabled: false,
        }
    },
    components: {TauriConsole, TauriFooter},
})
</script>

<style>
</style>