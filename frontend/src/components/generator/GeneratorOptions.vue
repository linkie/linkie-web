<template>
    <div class="grid sm:grid-cols-2 gap-4 md:grid-cols-3" :class="size >= 4 ? 'lg:grid-cols-4' : ''">
        <slot v-for="i in size" :name="`slot-${i - 1}`"></slot>
    </div>
</template>

<script lang="ts">
import {defineComponent} from "vue"

export default defineComponent({
    name: "GeneratorOptions",
    props: {
        size: {
            type: Number,
            required: true,
        },
    },
})
</script>

<style scoped>
</style>