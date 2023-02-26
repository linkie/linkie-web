<template>
    <div class="sm:w-[var(--sidebar-width)] sm:min-w-[var(--sidebar-width)]">
        <slot/>
    </div>
</template>

<script lang="ts">
import {defineComponent} from "vue"

export default defineComponent({
    name: "PageSidebar",
})
</script>

<style scoped>
div {
    --sidebar-width: 18rem;
}
</style>