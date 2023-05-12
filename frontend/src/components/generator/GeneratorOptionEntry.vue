<template>
    <button :aria-selected="selected"
            class="py-3 px-4 flex-1 flex flex-col gap-2 text-left rounded-lg bg-base-400 dark:bg-base-dark-200 bg-opacity-50 hover:bg-opacity-100
                dark:hover:bg-base-dark-400 dark:focus:bg-base-dark-400 transition-all duration-150 border-none select-none">
        <slot/>
    </button>
</template>

<script lang="ts">
import {defineComponent} from "vue"

export default defineComponent({
    name: "GeneratorOptionEntry",
    props: {
        selected: {
            type: Boolean,
            required: true,
        },
    },
})
</script>

<style scoped>
button[aria-selected=true], button:focus {
    @apply ring ring-offset-2;
    @apply focus:ring-base-dark-700/40 dark:focus:ring-base-dark-900/50;
    @apply outline-0;
}

button[aria-selected=true] {
    @apply ring-base-dark-800 focus:ring-base-dark-100;
    @apply dark:ring-base-dark-900 dark:focus:ring-base-dark-content;
}
</style>