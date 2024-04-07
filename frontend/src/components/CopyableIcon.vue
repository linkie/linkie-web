<template>
    <IconCopy class="min-w-[20px] w-[20px] ml-[1px] mr-[-.3rem]" :strokeWidth="strokeWidth ?? '2'"/>
</template>

<script setup lang="ts">
import {IconCopy} from "@tabler/icons-vue"

defineProps<{
    strokeWidth?: string,
}>()
</script>

<style scoped>
</style>