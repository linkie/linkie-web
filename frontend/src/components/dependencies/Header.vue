<template>
    <div class="pb-2 text-base-content font-extrabold text-3xl">
        <slot/>
    </div>
    <div class="divider mt-0 mb-0"></div>
</template>

<script lang="ts">
import {defineComponent} from "vue"

export default defineComponent({
    name: "Header",
})
</script>

<style scoped>
</style>