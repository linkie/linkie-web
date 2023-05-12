<template>
    <div class="fixed z-50 bottom-0 bg-base-400 text-base-content dark:bg-base-dark-300 dark:text-base-dark-content text-sm w-full px-1 py-0.5 flex justify-between shadow-lg drop-shadow-lg">
        <div class="gap-1 flex">
            <span>Linkie: Tauri (Experimental)</span>
            <button @click="setConsoleEnabled(!consoleEnabled)"
                class="bg-base-600 dark:bg-base-dark-600 rounded px-1">
                Console
            </button>
        </div>
        <span>
            using {{ currentBackendServer() }}
        </span>
    </div>
</template>

<script lang="ts">
import {defineComponent, PropType} from "vue"
import {currentBackendServer} from "../../app/backend"

export default defineComponent({
    name: "TauriFooter",
    data() {
        return {
            currentBackendServer,
        }
    },
    props: {
        consoleEnabled: {
            type: Boolean,
        },
        setConsoleEnabled: {
            type: Object as PropType<(enabled: boolean) => {}>,
        },
    },
})
</script>

<style>
</style>