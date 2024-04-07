<template>
    <a :class="['mx-1 p-1 select-none cursor-pointer text-center rounded-full bg-base-200 dark:bg-base-dark-100 shadow transition-all',
    enabled ? 'bg-neutral dark:bg-base-dark-300 text-white font-medium' : 'hover:brightness-90']">
        <div class="flex mx-2 items-center">
            <IconFilter v-if="enabled" :size="20"/>
            <IconFilterOff v-else :size="20"/>
            <span class="w-20">{{ text }}</span>
        </div>
    </a>
</template>

<script setup lang="ts">
import {IconFilter, IconFilterOff} from "@tabler/icons-vue"

defineProps<{
    enabled: boolean,
    text: string,
}>()
</script>

<style scoped>
</style>