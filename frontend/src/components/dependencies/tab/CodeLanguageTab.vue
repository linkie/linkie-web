<template>
    <button class="h-8 text-sm leading-8 select-none pr-4 capitalize pl-2 transition-opacity"
            :class="codeLanguage === language ? 'cursor-default' : 'cursor-pointer opacity-50 hover:opacity-100'"
            @click="codeLanguage = language">
        {{ language.toLowerCase() }}
    </button>
</template>

<script lang="ts">
import {defineComponent, PropType} from "vue"
import {mapWritableState} from "pinia"
import {CodeLanguage, usePreferenceStore} from "../../../app/preference-store"

export default defineComponent({
    name: "CodeLanguageTab",
    computed: {
        ...mapWritableState(usePreferenceStore, ["codeLanguage"]),
    },
    props: {
        language: {
            type: String as PropType<CodeLanguage>,
            required: true,
        },
    },
})
</script>

<style scoped>

</style>