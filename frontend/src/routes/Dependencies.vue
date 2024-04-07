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
                <div class="text-2xl font-bold mb-2">{{ blockName }}</div>
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

<script setup lang="ts">
import DependencyFilterBlock from "../components/dependencies/DependencyFilterBlock.vue"
import CodeBlock from "../components/dependencies/CodeBlock.vue"
import SubHeader from "../components/dependencies/SubHeader.vue"
import {formatDependency, formatMaven} from "../app/dep-format"
import Block from "../components/Block.vue"
import {computed, onMounted, watch} from "vue"
import {useDependencySearchStore} from "../app/dependency-store"
import {storeToRefs} from "pinia"
import {ensureDependencyData, updateDependencyData, useDependenciesDataStore} from "../app/dependencies-data"
import Copyable from "../components/Copyable.vue"
import PageWidthLimiter from "../components/PageWidthLimiter.vue"
import PageSidebar from "../components/PageSidebar.vue"
import PageContent from "../components/PageContent.vue"
import LoadingSection from "../components/LoadingSection.vue"
import {fullPath} from "../app/backend"
import {useRoute} from "vue-router"

const { searchData } = storeToRefs(useDependenciesDataStore())
const { loader, version, allowSnapshots } = storeToRefs(useDependencySearchStore())

const route = useRoute()

const dependencyBlocks = computed(() => {
    let {loader, version} = useDependencySearchStore()
    let {searchData} = useDependenciesDataStore()
    return Object.entries(searchData.versions[loader ?? ""]
    ?.find(entry => entry.version === version)
        ?.blocks ?? {})
})

watch(loader, () => {
    updateDependencyData(fullPath(route))
    ensureDependencyData(fullPath(route))
}, {
    immediate: true,
})

watch(version, () => {
    ensureDependencyData(fullPath(route))
}, {
    immediate: true,
})

watch(allowSnapshots, () => {
    ensureDependencyData(fullPath(route))
}, {
    immediate: true,
})

onMounted(() => {
    updateDependencyData(fullPath(route), useRoute().query)
})
</script>

<style scoped>
</style>