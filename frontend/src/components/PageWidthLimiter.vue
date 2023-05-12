<template>
    <div class="max-w-[80rem] mx-auto w-full px-5 flex gap-5 flex-col sm:flex-row">
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