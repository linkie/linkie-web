<template>
    <div class="text-base whitespace-nowrap flex py-1">
        <span class="pr-2">{{ title }}</span>
        <div class="px-2 rounded bg-base-l2 overflow-x-auto overflow-y-clip epic-scroller" v-if="code">
            <pre><code class="break-all whitespace-pre text-xs"><slot/></code></pre>
        </div>
        <span v-else><slot/></span>
    </div>
</template>

<script setup lang="ts">
withDefaults(defineProps<{
    title?: string,
    code: boolean
}>(), {
    code: true,
})
</script>

<style scoped>
</style>