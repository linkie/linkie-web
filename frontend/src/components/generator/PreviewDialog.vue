<template>
    <dialog ref="dialog" class="p-0 rounded-lg drop-shadow-lg shadow-2xl
            w-screen h-screen
            sm:w-[calc(100vw-var(--x-margin)*2)] sm:h-[calc(100vh-var(--y-margin)*2)]">
        <div class="w-full h-full grid title-grid text-base-content dark:text-base-dark-content">
            <div class="bg-base-400 dark:bg-base-dark-200 flex flex-initial" v-if="files">
                <span class="text-lg px-4 py-3 font-semibold flex-1">Preview Files</span>
                <button @click="closeDialog()"
                        class="font-medium bg-base-800 dark:bg-base-dark-600 bg-opacity-0 dark:bg-opacity-0 hover:bg-opacity-50 dark:hover:bg-opacity-50 px-3 mx-2 my-2 rounded-md transition-[background-color]">
                    Close
                </button>
            </div>
            <div class="sm:rounded grid grid-setup bg-base-400 dark:bg-base-dark-200 flex-grow-0" v-if="files">
                <div class="flex flex-col gap-y-0.5 overflow-auto">
                    <button v-for="file of Object.keys(files)"
                            @click="selectedFile = file"
                            class="text-sm text-left break-all mx-2 p-1 rounded bg-base-800 dark:bg-base-dark-700 bg-opacity-0 dark:bg-opacity-0 hover:bg-opacity-50 dark:hover:bg-opacity-50 transition-[background-color]">
                        {{ file }}
                    </button>
                </div>
                <div class="bg-[#2d2d2d] dark:bg-[#1d1d1d] p-2 rounded-tl-lg overflow-auto">
                    <pre class="whitespace-pre preview-code dark:bg-[#1d1d1d]" v-if="selectedFile" :class="`language-` + extension()"
                         ref="source">{{ files[selectedFile] }}</pre>
                </div>
            </div>
        </div>
    </dialog>
</template>

<script lang="ts">
import {useGeneratorStore} from "../../app/generator-store"
import {mapWritableState} from "pinia"
import {generate} from "../../app/generator/generator"
import {defineComponent, PropType} from "vue"
import Prism from "prismjs"

type Awaited<T> = T extends PromiseLike<infer U> ? U : T

export default defineComponent({
    name: "PreviewDialog",
    data() {
        return {
            selectedFile: null as string | null,
        }
    },
    computed: {
        ...mapWritableState(useGeneratorStore, ["generating"]),
    },
    props: {
        files: {
            type: Object as PropType<Awaited<ReturnType<typeof generate>>>,
        },
        close: {
            type: Function as PropType<() => void>,
            required: true,
        },
    },
    methods: {
        extension() {
            if (!this.files) return ""
            if (this.selectedFile!!.includes(".")) {
                let extension = this.selectedFile!!.substring(this.selectedFile!!.lastIndexOf(".") + 1)
                if (extension === "bat") {
                    return "batch"
                }
                return extension
            }
            if (this.files[this.selectedFile!!].startsWith("#!/bin/bash") || this.files[this.selectedFile!!].startsWith("#!/bin/sh")) {
                return "bash"
            }
            return ""
        },
        closeDialog() {
            this.close()
        },
        highlight() {
            let source = this.$refs.source
            if (source) {
                console.log("Highlighting")
                Prism.highlightElement(source as HTMLElement)
            }
        },
    },
    watch: {
        selectedFile() {
            this.$nextTick(() => {
                this.highlight()
            })
        },
        files() {
            this.$nextTick(() => {
                if (!!this.files) {
                    (this.$refs.dialog as HTMLDialogElement).showModal()
                    this.highlight()
                } else {
                    (this.$refs.dialog as HTMLDialogElement).close()
                }
            })
        },
    },
})
</script>

<style scoped>
* {
    --x-margin: 3rem;
    --y-margin: 5rem;
}

.title-grid {
    grid-template-rows: auto minmax(0, 1fr);
}

.grid-setup {
    @media (min-width: 640px) {
        grid-template-columns: 1fr 2fr;
    }
    @media not (min-width: 640px) {
        grid-template-rows: 1fr 2fr;
    }
}

.preview-code {
    margin: 0 !important;
    padding: 0 !important;
    @apply text-sm !important;
}
</style>