<template>
    <div class="w-full flex flex-col gap-4">
        <generator-description>
            {{ $t("generator.versions.description") }}
        </generator-description>
        <div class="w-full sm:w-[60%] grid sm:input-grid grid-cols-1 sm:grid-cols-2 gap-x-3 gap-y-1.5 items-center min-w-10 max-w-full">
            <label for="mc-version"> {{ $t("generator.versions.version") }} </label>
            <select id="mc-version" class="select-field">
                <option>Select a version</option>
            </select>
        </div>
    </div>
</template>

<script lang="ts">
import {mapWritableState} from "pinia"
import {useGeneratorStore} from "../../app/generator-store"
import GeneratorOptions from "./GeneratorOptions.vue"
import GeneratorOptionEntry from "./GeneratorOptionEntry.vue"
import {defineComponent} from "vue"
import GeneratorDescription from "./GeneratorDescription.vue"

export default defineComponent({
    name: "GeneratorVersions",
    components: {GeneratorDescription, GeneratorOptionEntry, GeneratorOptions},
    computed: {
        ...mapWritableState(useGeneratorStore, ["template", "gradleSystem", "mappings", "modLoaders", "gradleLanguage", "splitEnv"]),
    },
    methods: {},
})
</script>

<style scoped>
.input-grid {
    grid-template-columns: auto 1fr;
}

.select-field {
    @apply h-9 bg-base-400 bg-opacity-50 hover:bg-opacity-100 transition-all rounded outline-0 border-0 text-base-dark-300;
    @apply dark:bg-base-dark-300 dark:hover:bg-base-dark-400 dark:text-base-dark-content;
}

.select-field:focus {
    @apply ring-2 ring-base-dark-300 ring-offset-1;
    @apply dark:ring-base-dark-content;
}
</style>