<template>
    <div class="pb-6">
        <div class="p-5 card bg-base-100 dark:bg-base-dark-200 shadow-lg rounded-lg">
            <slot></slot>
        </div>
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