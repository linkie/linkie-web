<template>
    <a :class="['mx-1 p-1 select-none cursor-pointer text-center rounded-full bg-base-200 shadow transition-all',
    enabled ? 'bg-neutral text-white font-medium' : 'hover:brightness-90']">
        <div class="flex mx-2">
            <svg xmlns="http://www.w3.org/2000/svg" width="24" height="24" viewBox="0 0 24 24" stroke-width="2" stroke="currentColor" fill="none"
                 stroke-linecap="round" stroke-linejoin="round">
                <path stroke="none" d="M0 0h24v24H0z" fill="none"></path>
                <line v-if="!enabled" x1="3" y1="3" x2="21" y2="21"></line>
                <path d="M5.5 5h13a1 1 0 0 1 .5 1.5l-5 5.5l0 7l-4 -3l0 -4l-5 -5.5a1 1 0 0 1 .5 -1.5"></path>
            </svg>
            <span class="w-20">{{ text }}</span>
        </div>
    </a>
</template>

<script lang="ts">
import {defineComponent} from "vue"

export default defineComponent({
    name: "MappingsFilterPill",
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