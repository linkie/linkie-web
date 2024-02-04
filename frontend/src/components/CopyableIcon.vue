<template>
    <IconCopy class="min-w-[20px] w-[20px] ml-[1px] mr-[-.3rem]" :strokeWidth="strokeWidth ?? '2'"/>
</template>

<script lang="ts">
import {defineComponent} from "vue"
import {IconCopy} from "@tabler/icons-vue"

export default defineComponent({
    name: "CopyableIcon",
    components: {
        IconCopy,
    },
    props: {
        strokeWidth: {
            type: String,
        },
    },
})
</script>

<style scoped>
</style>