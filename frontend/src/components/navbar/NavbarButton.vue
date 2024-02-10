<template>
    <router-link :class="[
            'px-4 py-3 rounded-lg transition-all duration-100 normal-case flex items-center',
            bold ? 'text-xl font-bold' : 'font-medium',
            hoverDim ? 'hover:opacity-70' : '',
    ]" :to="{path: (href ?? ''), query: {}}">
        <slot/>
    </router-link>
</template>

<script lang="ts">
import {defineComponent} from "vue"

export default defineComponent({
    name: "NavbarButton",
    props: {
        href: String,
        bold: {
            type: Boolean,
            default: false,
        },
        hoverDim: {
            type: Boolean,
            default: true,
        },
    },
})
</script>

<style scoped>
</style>