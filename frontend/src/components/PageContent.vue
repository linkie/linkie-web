<template>
    <div class="flex-1 min-w-0">
        <slot/>
    </div>
</template>

<script lang="ts">
import {defineComponent} from "vue"

export default defineComponent({
    name: "PageContent",
})
</script>

<style scoped>

</style>