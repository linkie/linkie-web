<template>
    <div class="whitespace-nowrap [&_>*]:hover:underline [&_>*]:cursor-pointer" @click="copyAs($i18n, copy)">
        <span><slot/></span>
        <CopyableIcon class="inline-block pl-0.5" :strokeWidth="strokeWidth"/>
    </div>
</template>

<script setup lang="ts">
import {copyAs} from "../app/copy"
import CopyableIcon from "./CopyableIcon.vue"

defineProps<{
    copy?: string,
    strokeWidth?: string,
}>()
</script>

<style scoped>
</style>