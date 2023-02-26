<template>
    <div class="font-extrabold text-xl">
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