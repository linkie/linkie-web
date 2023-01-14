<template>
    <div class="whitespace-nowrap flex copy items-center" @click="copyAs(copy)">
        <span><slot/></span>
        <svg xmlns="http://www.w3.org/2000/svg" :class="['min-w-[20px] w-[20px] ml-[1px] mr-[-.3rem]', `stroke-${strokeWidth ?? '2'}`]" width="24" height="24" viewBox="0 0 24 24" stroke-width="2"
             stroke="currentColor" fill="none" stroke-linecap="round" stroke-linejoin="round">
            <path stroke="none" d="M0 0h24v24H0z" fill="none"></path>
            <rect x="8" y="8" width="12" height="12" rx="2"></rect>
            <path d="M16 8v-2a2 2 0 0 0 -2 -2h-8a2 2 0 0 0 -2 2v8a2 2 0 0 0 2 2h2"></path>
        </svg>
    </div>
</template>

<script lang="ts">
import {defineComponent} from "vue"
import {copyAs} from "../app/copy"

export default defineComponent({
    name: "Copyable",
    data() {
        return {
            copyAs,
        }
    },
    props: {
        copy: {
            type: String,
            required: true,
        },
        strokeWidth: {
            type: String,
        }
    },
})
</script>

<style scoped>
.copy > * {
    @apply hover:underline cursor-pointer;
}
</style>