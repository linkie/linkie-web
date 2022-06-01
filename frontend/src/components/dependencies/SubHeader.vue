<template>
    <div class="text-base-content font-extrabold text-xl" :class="addPadding ? 'py-2' : ''">
        <slot/>
    </div>
</template>

<script lang="ts">
import {defineComponent} from "vue"

export default defineComponent({
    name: "SubHeader",
    props: {
        addPadding: {
            type: Boolean,
            default: () => true,
        },
    },
})
</script>

<style scoped>
</style>