<template>
    <div v-if="Object.keys(searchData.versions).length !== 0">
        <div class="max-w-[calc(62rem+var(--sidebar-width))] mx-auto">
            <div class="grid-setup">
                <div class="col-[1] px-5 sm:pr-0 sm:w-[var(--sidebar-width)] sm:min-w-[var(--sidebar-width)] sm:absolute">
                    <div class="p-5 card bg-base-100 shadow-xl rounded-lg">
                        <DependencyFilterBlock :searchData="searchData"/>
                    </div>

                    <div class="p-5 mt-6 card bg-base-100 shadow-xl rounded-lg">
                        <SubHeader :add-padding="false">Table of Contents</SubHeader>
                        <ol class="list-decimal pl-6 mt-2">
                            <li v-for="entry in dependencyBlocks">
                                <a :href="'#dep-' + dependencyBlocks.indexOf(entry)">{{ entry[0] }}</a>
                            </li>
                        </ol>
                    </div>
                </div>

                <div class="col-[2/span_2] min-w-0">
                    <DependencyBlock v-for="[blockName, block] in dependencyBlocks" :title="blockName"
                                     :id="'dep-' + dependencyBlocks.findIndex(entry => entry[0] === blockName)">
                        <div v-if="block.mavens.length > 0">
                            <div v-for="maven in block.mavens">
                                <SubHeader v-if="block.mavens.indexOf(maven) === 0 && !maven.subtitle">Maven Repository</SubHeader>

                                <CodeBlock :title="maven.subtitle ?? ''">
                                    <span class="hover:underline cursor-pointer"
                                          @click="copyAs(formatMaven(maven.url))">
                                        {{ formatMaven(maven.url) }}
                                    </span>
                                </CodeBlock>
                            </div>
                        </div>

                        <div v-for="dependency in block.dependencies">
                            <p class="mt-1">
                                {{ dependency.name }}
                                <span class="hover:underline cursor-pointer font-bold" @click="copyAs(dependency.version)">{{ dependency.version }}</span>
                            </p>
                            <CodeBlock :title="undefined">dependencies {<br>
                                <span>{{ "    " }}</span>
                                <span class="hover:underline cursor-pointer" @click="copyAs(formatDependency(dependency.type, dependency.notation, false))">
                                {{ formatDependency(dependency.type, dependency.notation, false) }}
                            </span>
                                <br>}
                            </CodeBlock>
                        </div>
                    </DependencyBlock>
                </div>
            </div>
        </div>
    </div>

    <div v-else class="max-w-4xl mx-auto">
        <Block>
            <progress class="progress progress-accent w-80 mx-auto"></progress>
        </Block>
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

export default defineComponent({
    name: "Dependencies",
    components: {Block, CodeBlock, DependencyBlock, DependencyFilterBlock, SubHeader},
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