<template>
    <div class="max-w-[80rem] mx-auto w-full px-5 sm:flex sm:gap-6">
        <slot/>
    </div>
</template>

<script lang="ts">
import {defineComponent} from "vue"

export default defineComponent({
    name: "PageWidthLimiter",
})
</script>

<style scoped>

</style>