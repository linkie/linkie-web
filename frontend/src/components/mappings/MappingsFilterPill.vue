<template>
    <a :class="['mx-1 p-1 select-none cursor-pointer text-center rounded-full bg-base-200 dark:bg-base-dark-100 shadow transition-all',
    enabled ? 'bg-neutral dark:bg-base-dark-300 text-white font-medium' : 'hover:brightness-90']">
        <div class="flex mx-2 items-center">
            <IconFilter v-if="enabled" size="20"/>
            <IconFilterOff v-else size="20"/>
            <span class="w-20">{{ text }}</span>
        </div>
    </a>
</template>

<script lang="ts">
import {defineComponent} from "vue"
import {IconFilter, IconFilterOff} from "@tabler/icons-vue"

export default defineComponent({
    name: "MappingsFilterPill",
    components: {
        IconFilter,
        IconFilterOff,
    },
    props: {
        enabled: {
            type: Boolean,
        },
        text: {
            type: String,
        },
    },
})
</script>

<style scoped>
</style>