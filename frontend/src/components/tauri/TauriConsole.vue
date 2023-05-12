<template>
    <div class="fixed left-0 h-full w-full bg-base-dark-300 z-40 transition-all duration-300 flex flex-col text-base-dark-content"
         :class="[...(consoleEnabled ? ['opacity-100 top-0'] : ['opacity-0 top-[-100%]'])]">
        <span class="text-lg px-4 pt-3 pb-2.5 font-semibold">Console</span>
        <div class="overflow-auto mb-8 flex flex-col bg-base-dark-400">
            <div v-for="s in Object.keys(consoleMessages)" class="px-3 whitespace-pre text-sm flex gap-2">
                <code class="shrink-0 w-8 text-right font-semibold">{{ (Number.parseInt(s) + 1) }}</code>
                <code>
                    {{ consoleMessages[s as any as number] }}
                </code>
            </div>
        </div>
    </div>
</template>

<script lang="ts">
import {defineComponent} from "vue"
import {mapState} from "pinia"
import {useTauriStore} from "../../app/tauri/tauri"

export default defineComponent({
    name: "TauriConsole",
    computed: {
        ...mapState(useTauriStore, ["consoleMessages"]),
    },
    props: {
        consoleEnabled: {
            type: Boolean,
        },
    },
})
</script>

<style>
</style>