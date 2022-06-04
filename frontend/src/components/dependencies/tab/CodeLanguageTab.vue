<template>
    <button class="tab capitalize pl-2" :class="codeLanguage === language ? 'tab-active' : ''"
            @click="codeLanguage = language">
        {{ language.toLowerCase() }}
    </button>
</template>

<script lang="ts">
import {defineComponent} from "vue"
import {mapWritableState} from "pinia"
import {usePreferenceStore} from "../../../app/preference-store"

export default defineComponent({
    name: "CodeLanguageTab",
    computed: {
        ...mapWritableState(usePreferenceStore, ["codeLanguage"]),
    },
    props: {
        language: {
            type: String,
        },
    },
})
</script>

<style scoped>

</style>