<template>
    <router-link :class="[
            'px-4 py-3 rounded-lg transition-all duration-100 normal-case flex items-center',
            bold ? 'text-xl font-bold' : 'font-medium',
            hoverDim ? 'hover:opacity-70' : '',
    ]" :to="{path: href, query: {}}">
        <slot/>
    </router-link>
</template>

<script setup lang="ts">
withDefaults(defineProps<{
    href?: string,
    bold?: boolean,
    hoverDim?: boolean,
}>(), {
    href: '',
    bold: false,
    hoverDim: true,
})
</script>

<style scoped>
</style>