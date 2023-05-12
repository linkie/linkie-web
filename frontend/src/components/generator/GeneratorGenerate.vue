<template>
    <PreviewDialog :files="files ?? undefined" :close="() => files = null"/>
    <div class="w-full flex gap-3 overflow-x-auto">
        <button @click="preview()"
                class="bg-base-400 dark:bg-base-dark-200 bg-opacity-50 hover:bg-opacity-100 dark:hover:bg-base-dark-400 transition-all rounded-lg p-8 ml-auto text-3xl select-none">
            Preview
        </button>
        <button :disabled="!!generating" @click="generating = true; generate()"
                class="bg-base-400 dark:bg-base-dark-200 bg-opacity-50 hover:bg-opacity-100 dark:hover:bg-base-dark-400 transition-all rounded-lg p-8 mr-auto font-semibold text-3xl
                disabled:bg-base-500 disabled:dark:bg-base-dark-200 disabled:dark:bg-opacity-80 disabled:text-base-dark-800 disabled:dark:text-base-dark-900 select-none">
            Generate!
        </button>
    </div>
</template>

<script lang="ts">

import {useGeneratorStore} from "../../app/generator-store"
import {mapWritableState} from "pinia"
import {generate} from "../../app/generator/generator"
import {defineComponent} from "vue"
import PreviewDialog from "./PreviewDialog.vue"

type Awaited<T> = T extends PromiseLike<infer U> ? U : T
export default defineComponent({
    name: "GeneratorGenerate",
    components: {PreviewDialog},
    data() {
        return {
            files: null as Awaited<ReturnType<typeof generate>> | null,
        }
    },
    computed: {
        ...mapWritableState(useGeneratorStore, ["generating"]),
    },
    methods: {
        generate,
        preview() {
            generate().then(files => {
                this.files = files
            })
        },
    },
})
</script>

<style scoped>
</style>