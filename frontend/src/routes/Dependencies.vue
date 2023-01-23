<template>
    <div class="pt-20"/>
    <div v-if="Object.keys(searchData.versions).length !== 0">
        <div class="max-w-[calc(62rem+var(--sidebar-width))] mx-auto">
            <div class="grid-setup">
                <div class="col-[1] px-5 sm:pr-0 sm:w-[var(--sidebar-width)] sm:min-w-[var(--sidebar-width)] sm:absolute">
                    <div class="p-5 card bg-base-100 dark:bg-base-dark-200 shadow-xl rounded-lg">
                        <DependencyFilterBlock :searchData="searchData"/>
                    </div>

                    <div class="p-5 mt-6 card bg-base-100 dark:bg-base-dark-200 shadow-xl rounded-lg">
                        <SubHeader :add-padding="false"> {{ $t("dependencies.toc") }}</SubHeader>
                        <ol class="list-decimal pl-6 mt-2">
                            <li v-for="entry in dependencyBlocks">
                                <a :href="'#dep-' + dependencyBlocks.indexOf(entry)">{{ entry[0] }}</a>
                            </li>
                        </ol>
                    </div>
                </div>

                <div class="col-[2/span_2] min-w-0 pt-6 sm:pt-0">
                    <DependencyBlock v-for="[blockName, block] in dependencyBlocks" :title="blockName"
                                     :id="'dep-' + dependencyBlocks.findIndex(entry => entry[0] === blockName)">
                        <div v-if="block.mavens.length > 0">
                            <div v-for="maven in block.mavens">
                                <SubHeader v-if="block.mavens.indexOf(maven) === 0 && !maven.subtitle"> {{ $t("dependencies.maven.repo") }} </SubHeader>

                                <CodeBlock :title="maven.subtitle ?? ''">
                                    <Copyable :copy="formatMaven(maven.url)" stroke-width="1">
                                        {{ formatMaven(maven.url) }}
                                    </Copyable>
                                </CodeBlock>
                            </div>
                        </div>

                        <div v-for="dependency in block.dependencies">
                            <p class="mt-1 flex gap-x-1">
                                {{ dependency.name }}
                                <Copyable class="font-bold" :copy="dependency.version">{{ dependency.version }}</Copyable>
                            </p>
                            <CodeBlock :title="undefined">dependencies {<br>
                                <Copyable :copy="formatDependency(dependency.type, dependency.notation, false)" stroke-width="1" class="pl-8">
                                        {{ formatDependency(dependency.type, dependency.notation, false) }}
                                </Copyable>}
                            </CodeBlock>
                        </div>
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
import DependencyFilterBlock from "../components/dependencies/DependencyFilterBlock.vue"
import CodeBlock from "../components/dependencies/CodeBlock.vue"
import SubHeader from "../components/dependencies/SubHeader.vue"
import {formatBlock, formatDependency, formatMaven, tab} from "../app/dep-format"
import Block from "../components/Block.vue"
import {defineComponent} from "vue"
import {useDependencySearchStore} from "../app/dependency-store"
import {mapState} from "pinia"
import {copyAs} from "../app/copy"
import {DependencyBlockData, ensureDependencyData, updateDependencyData, useDependenciesDataStore} from "../app/dependencies-data"
import Copyable from "../components/Copyable.vue"

export default defineComponent({
    name: "Dependencies",
    components: {Copyable, Block, CodeBlock, DependencyBlock, DependencyFilterBlock, SubHeader},
    data() {
        return {
            formatMaven,
            formatDependency,
            formatBlock,
            tab,
            copyAs,
        }
    },
    computed: {
        dependencyBlocks(): [string, DependencyBlockData][] {
            let {loader, version} = useDependencySearchStore()
            let {searchData} = useDependenciesDataStore()
            return Object.entries(searchData.versions[loader ?? ""]
                    ?.find(entry => entry.version === version)
                    ?.blocks ?? {})
        },
        ...mapState(useDependenciesDataStore, ["searchData"]),
        ...mapState(useDependencySearchStore, ["loader", "version", "allowSnapshots"]),
    },
    watch: {
        loader: {
            handler() {
                updateDependencyData()
                ensureDependencyData()
            },
            immediate: true,
        },
        version: {
            handler() {
                ensureDependencyData()
            },
            immediate: true,
        },
        allowSnapshots: {
            handler() {
                ensureDependencyData()
            },
            immediate: true,
        },
    },
    mounted() {
        updateDependencyData()
        ensureDependencyData()
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