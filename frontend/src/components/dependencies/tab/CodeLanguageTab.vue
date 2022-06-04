<template>
    <button class="tab capitalize pl-2" :class="codeLanguage === language ? 'tab-active' : ''"
            @click="codeLanguage = language">
        {{ language.toLowerCase() }}
    </button>
</template>

<script lang="ts">
import {defineComponent} from "vue"
import {mapActions, mapWritableState} from "pinia"
import {usePreferenceStore} from "../../../app/preference-store"

export default defineComponent({
    name: "CodeLanguageTab",
    computed: {
        ...mapWritableState(usePreferenceStore, ["codeLanguage"]),
    },
    methods: {
        ...mapActions(usePreferenceStore, ["setCodeLanguage"]),
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