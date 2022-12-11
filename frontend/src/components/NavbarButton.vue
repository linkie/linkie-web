<template>
    <a :class="[
            'px-4 py-3 rounded-lg hover:opacity-70 transition-all normal-case flex items-center',
            bold ? 'text-xl font-bold' : 'font-medium',
    ]" :href="href">
        <slot/>
    </a>

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
    },
})
</script>

<style scoped>
</style>