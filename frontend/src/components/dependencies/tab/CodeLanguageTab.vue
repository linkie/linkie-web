<template>
    <button class="tab capitalize pl-2" :class="codeLanguage === language ? 'tab-active' : ''"
            @click="setCodeLanguage(language)">
        {{ language.toLowerCase() }}
    </button>
</template>

<script lang="ts">
import {defineComponent} from "vue"
import {mapActions, mapState} from "pinia"
import {usePreferenceStore} from "../../../app/preference-store"

export default defineComponent({
    name: "CodeLanguageTab",
    computed: {
        ...mapState(usePreferenceStore, ["codeLanguage"]),
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