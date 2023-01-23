<template>
    <div class="pt-20"/>
    <div v-if="entries.length !== 0">
        <div class="max-w-[calc(62rem+var(--sidebar-width))] mx-auto">
            <div class="grid-setup">
                <div class="col-[1] px-5 pb-6 sm:pb-0 sm:pr-0 sm:w-[var(--sidebar-width)] sm:min-w-[var(--sidebar-width)] sm:absolute">
                    <div class="p-5 card bg-base-100 dark:bg-base-dark-200 shadow-xl rounded-lg">
                        <SubHeader :add-padding="false">Table of Contents</SubHeader>
                        <ol class="list-decimal pl-6 mt-2">
                            <li v-for="entry in entries">
                                <a :href="'#oss-' + entries.indexOf(entry)">{{ entry.name }}</a>
                            </li>
                        </ol>
                    </div>
                </div>

                <div class="col-[2/span_2] min-w-0">
                    <DependencyBlock v-for="entry in entries" :title="entry.name" :id="'oss-' + entries.indexOf(entry)">
                            <span class="whitespace-pre-line">
                                {{ entry.license }}
                            </span>
                        <div class="divider mt-0 mb-0"/>
                        <a class="btn btn-ghost btn-sm normal-case" :href="entry.link">More about {{ entry.name }}</a>
                    </DependencyBlock>
                </div>
            </div>
        </div>
    </div>

    <div v-else class="text-center h-[calc(100vh-56px-24px-5rem)] items-center justify-center grid">
        <div class="flex gap-4 items-center justify-center animate-pulse animate-bounce">
            <svg xmlns="http://www.w3.org/2000/svg" class="animate-spin" width="24" height="24" viewBox="0 0 24 24" stroke-width="2" stroke="currentColor"
                 fill="none" stroke-linecap="round" stroke-linejoin="round">
                <path stroke="none" d="M0 0h24v24H0z" fill="none"></path>
                <path d="M10 17a3 3 0 1 1 -1.543 -2.623l2.087 -3.754a3 3 0 0 1 1.456 -5.623a3 3 0 0 1 1.457 5.623l2.087 3.754a3 3 0 1 1 -1.538 2.8l-.006 -.177h-4z"></path>
                <path d="M17 17v.01"></path>
                <path d="M7 17v.01"></path>
                <path d="M12 8v.01"></path>
            </svg>
            <p class="font-medium text-xl">Loading...</p>
        </div>
    </div>
</template>

<script lang="ts">
import DependencyBlock from "../components/dependencies/DependencyBlock.vue"
import {reqOss} from "../app/backend"
import Block from "../components/Block.vue"
import SubHeader from "../components/dependencies/SubHeader.vue"
import {defineComponent} from "vue"
import {addAlert} from "../app/alerts"

interface OssEntry {
    name: string,
    link: string,
    license: string,
}

export default defineComponent({
    name: "Dependencies",
    components: {SubHeader, Block, DependencyBlock},
    data() {
        return {
            entries: [] as OssEntry[],
        }
    },
    mounted() {
        reqOss().then(value => {
            this.entries = value.data
        }).catch(reason => {
            addAlert({
                type: "error",
                message: `Failed to fetch entries: ${reason.message}`,
            })
        })
    },
})
</script>

<style scoped>
div {
    --sidebar-width: 18rem;
}

@media (min-width: 640px) {
    .grid-setup {
        grid-template-columns: auto 0 minmax(0, calc(100% - var(--sidebar-width)));
        @apply grid grid-flow-col;
    }
}
</style>