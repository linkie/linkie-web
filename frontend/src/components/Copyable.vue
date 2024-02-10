<template>
    <div class="whitespace-nowrap [&_>*]:hover:underline [&_>*]:cursor-pointer" @click="copyAs($i18n, copy)">
        <span><slot/></span>
        <CopyableIcon class="inline-block pl-0.5" :strokeWidth="strokeWidth"/>
    </div>
</template>

<script lang="ts">
import {defineComponent} from "vue"
import {copyAs} from "../app/copy"
import CopyableIcon from "./CopyableIcon.vue"

export default defineComponent({
    name: "Copyable",
    components: {CopyableIcon},
    data() {
        return {
            copyAs,
        }
    },
    props: {
        copy: {
            type: String,
        },
        strokeWidth: {
            type: String,
        },
    },
})
</script>

<style scoped>
</style>