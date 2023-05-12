<template>
    <div class="text-lg font-bold">
        <slot/>
    </div>
</template>

<script lang="ts">
import {defineComponent} from "vue"

export default defineComponent({
    name: "SubHeader",
})
</script>

<style scoped>
</style>