<template>
    <div class="p-5 bg-base-l1 shadow-sm rounded-lg">
        <slot></slot>
    </div>
</template>

<script lang="ts">
import {defineComponent} from "vue"

export default defineComponent({
    name: "Block",
})
</script>

<style scoped>
</style>