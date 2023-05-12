<template>
    <PageWidthLimiter v-if="Object.keys(searchData.versions).length !== 0">
        <PageSidebar class="flex flex-col gap-y-5">
            <Block>
                <DependencyFilterBlock :searchData="searchData"/>
            </Block>

            <Block>
                <SubHeader>{{ $t("dependencies.toc") }}</SubHeader>
                <ol class="list-decimal pl-6 mt-2">
                    <li v-for="entry in dependencyBlocks">
                        <a :href="'#dep-' + dependencyBlocks.indexOf(entry)">{{ entry[0] }}</a>
                    </li>
                </ol>
            </Block>
        </PageSidebar>

        <PageContent class="flex flex-col gap-y-5">
            <Block v-for="[blockName, block] in dependencyBlocks"
                   :id="'dep-' + dependencyBlocks.findIndex(entry => entry[0] === blockName)">
                <div class="text-2xl font-extrabold mb-2">{{ blockName }}</div>
                <div v-if="block.mavens.length > 0" class="flex flex-col gap-1">
                    <div v-for="maven in block.mavens">
                        <div class="font-bold" v-if="block.mavens.indexOf(maven) === 0 && !maven.subtitle"> {{ $t("dependencies.maven.repo") }}</div>

                        <CodeBlock :title="maven.subtitle ?? ''">
                            <Copyable :copy="formatMaven(maven.url)" stroke-width="1">
                                {{ formatMaven(maven.url) }}
                            </Copyable>
                        </CodeBlock>
                    </div>
                </div>

                <div class="flex flex-col gap-0.5">
                    <div v-for="dependency in block.dependencies">
                        <p class="mt-1 flex gap-x-1">
                            {{ dependency.name }}
                            <Copyable class="font-bold" :copy="dependency.version">{{ dependency.version }}</Copyable>
                        </p>
                        <CodeBlock :title="undefined">dependencies {<br>
                            <Copyable :copy="formatDependency(dependency.type, dependency.notation, false)" stroke-width="1" class="pl-8">
                                {{ formatDependency(dependency.type, dependency.notation, false) }}
                            </Copyable>
                            }
                        </CodeBlock>
                    </div>
                </div>
            </Block>
        </PageContent>
    </PageWidthLimiter>

    <LoadingSection v-else class="h-[calc(100vh-56px-24px-5rem)]"/>
</template>

<script lang="ts">
import DependencyFilterBlock from "../components/dependencies/DependencyFilterBlock.vue"
import CodeBlock from "../components/dependencies/CodeBlock.vue"
import SubHeader from "../components/dependencies/SubHeader.vue"
import {formatDependency, formatMaven} from "../app/dep-format"
import Block from "../components/Block.vue"
import {defineComponent} from "vue"
import {useDependencySearchStore} from "../app/dependency-store"
import {mapState} from "pinia"
import {DependencyBlockData, ensureDependencyData, updateDependencyData, useDependenciesDataStore} from "../app/dependencies-data"
import Copyable from "../components/Copyable.vue"
import Header from "../components/dependencies/Header.vue"
import PageWidthLimiter from "../components/PageWidthLimiter.vue"
import PageSidebar from "../components/PageSidebar.vue"
import PageContent from "../components/PageContent.vue"
import LoadingSection from "../components/LoadingSection.vue"

export default defineComponent({
    name: "Dependencies",
    components: {LoadingSection, PageContent, PageSidebar, PageWidthLimiter, Header, Copyable, Block, CodeBlock, DependencyFilterBlock, SubHeader},
    data() {
        return {
            formatMaven,
            formatDependency,
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
        updateDependencyData(true)
    },
})
</script>

<style scoped>
</style>