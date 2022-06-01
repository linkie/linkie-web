<template>
    <div class="text-base whitespace-nowrap flex py-1">
        <span class="pr-2">{{ title }}</span>
        <div class="px-2 rounded bg-base-300 overflow-x-auto" v-if="code">
            <pre><code class="break-all whitespace-pre text-xs">{{ content }}</code></pre>
        </div>
        <span v-else>{{ content }}</span>
    </div>
</template>

<script lang="ts">

import {defineComponent} from "vue"

export default defineComponent({
    name: "EntryDetails",
    props: {
        title: String,
        content: String,
        code: {
            type: Boolean,
            default: () => true,
        },
    },
})
</script>

<style scoped>
</style>