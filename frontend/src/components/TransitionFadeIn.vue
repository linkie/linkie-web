<template>
    <transition-group
            enter-active-class="duration-200 ease-out"
            enter-from-class="transform opacity-0"
            enter-to-class="opacity-100"
            leave-active-class="duration-200 ease-in"
            leave-from-class="opacity-100"
            leave-to-class="transform opacity-0">
        <slot/>
    </transition-group>
</template>

<script lang="ts">
import {defineComponent} from "vue"

export default defineComponent({
    name: "TransitionFadeIn",
})
</script>

<style scoped>
</style>