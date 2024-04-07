<template>
    <div class="sm:w-[var(--sidebar-width)] sm:min-w-[var(--sidebar-width)]">
        <slot/>
    </div>
</template>

<script setup lang="ts">
</script>

<style scoped>
div {
    --sidebar-width: 18rem;
}
</style>