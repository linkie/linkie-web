<template>
    <div :class="[`bg-base-content dark:bg-black/80 dark:backdrop-blur-md rounded-lg drop-shadow-lg p-3
                text-base-200 dark:text-base-dark-content text-xs sm:text-sm
                max-sm:hidden
                invisible peer-hover:visible hover:visible
                absolute peer-hover:opacity-100 hover:opacity-100 opacity-0 transition-all duration-200 delay-100
                scale-75 peer-hover:scale-100 hover:scale-100 whitespace-nowrap
                top-[1.75rem] z-10 select-none cursor-default`,
                placement === 'right' ? `origin-top-left` : `origin-top-right right-0`]">
        <slot/>
    </div>
</template>

<script setup lang="ts">
withDefaults(defineProps<{
    placement?: string
}>(), {
    placement: "right",
})
</script>

<style scoped>
</style>