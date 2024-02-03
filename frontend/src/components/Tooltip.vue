<template>
    <div class="bg-base-content dark:bg-black/80 dark:backdrop-blur-md rounded-lg drop-shadow-lg p-3
                text-base-200 dark:text-base-dark-content text-xs sm:text-sm
                max-sm:hidden
                invisible peer-hover:visible
                absolute peer-hover:opacity-100 opacity-0 transition-all duration-200
                scale-75 peer-hover:scale-100 whitespace-nowrap origin-top-left
                top-[1.75rem] z-10">
        <slot/>
    </div>
</template>

<script lang="ts">
import {defineComponent} from "vue"

export default defineComponent({
    name: "Tooltip",
})
</script>

<style scoped>
</style>