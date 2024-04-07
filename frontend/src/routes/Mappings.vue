<template>
    <PageWidthLimiter v-if="mappingsData.namespaces.length !== 0">
        <PageSidebar>
            <Block>
                <MappingsFilterBlock :data="mappingsData"/>
            </Block>
        </PageSidebar>
        <PageContent class="flex flex-col gap-y-5" v-if="(mode ?? 'mappings') === 'mappings'">
            <MappingsSearchBlock/>
            <MappingsEntryBlock v-for="entry in infoData.entries"
                                :namespace="namespaceObj"
                                :translated-to-namespace="infoData.translateAs ? mappingsData.namespaces.find(value => value.id === infoData.translateAs) : (infoData.translateAsVersion ? namespaceObj : undefined)"
                                :entry="entry" :version="version" :query="searchText"/>
            <MappingsSearchPlaceholder v-if="infoData.entries.length == 0"
                                       :searching="!!searchController"
                                       :has-query="!!infoData.query"/>
        </PageContent>
        <PageContent class="flex flex-col gap-y-5" v-else-if="mode === 'stacktrace'">
            <MappingsStacktraceBlock/>
        </PageContent>
    </PageWidthLimiter>

    <LoadingSection v-else class="h-[calc(100vh-56px-24px-5rem)]"/>
</template>

<script setup lang="ts">
import {computed, onMounted, watch} from "vue"
import {useMappingsStore} from "../app/mappings-store"
import {storeToRefs} from "pinia"
import MappingsSearchBlock from "../components/mappings/MappingsSearchBlock.vue"
import MappingsEntryBlock from "../components/mappings/MappingsEntryBlock.vue"
import MappingsFilterBlock from "../components/mappings/MappingsFilterBlock.vue"
import {ensureMappingsData, updateMappingsData, updateMappingsInfo, useMappingsDataStore} from "../app/mappings-data"
import MappingsSearchPlaceholder from "../components/mappings/MappingsSearchPlaceholder.vue"
import PageWidthLimiter from "../components/PageWidthLimiter.vue"
import PageSidebar from "../components/PageSidebar.vue"
import PageContent from "../components/PageContent.vue"
import LoadingSection from "../components/LoadingSection.vue"
import Block from "../components/Block.vue"
import {fullPath} from "../app/backend"
import {useRoute} from "vue-router"
import MappingsStacktraceBlock from "../components/mappings/MappingsStacktraceBlock.vue"

const { mappingsData, infoData, searchController } = storeToRefs(useMappingsDataStore())
const { mode, namespace, version, allowSnapshots, searchText, allowClasses, allowMethods, allowFields, translateAs, translateAsVersion } = storeToRefs(useMappingsStore())

const namespaceObj = computed(() => mappingsData.value.namespaces.find(value => value.id === useMappingsStore().namespace)!!)

const route = useRoute()

watch([namespace, version], () => {
    updateMappingsData(fullPath(route))
    ensureMappingsData(fullPath(route))
    updateMappingsInfo(fullPath(route))
}, { immediate: true })

watch(allowSnapshots, () => {
    ensureMappingsData(fullPath(route))
}, { immediate: true })

watch([searchText, allowClasses, allowMethods, allowFields], () => {
    updateMappingsInfo(fullPath(route))
}, { immediate: true })

watch([translateAs, translateAsVersion], () => {
    ensureMappingsData(fullPath(route))
    updateMappingsInfo(fullPath(route))
}, { immediate: true })

onMounted(() => {
    updateMappingsData(fullPath(route), route.query)
})
</script>

<style scoped>
</style>