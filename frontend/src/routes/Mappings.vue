<template>
    <PageWidthLimiter v-if="mappingsData.namespaces.length !== 0">
        <PageSidebar>
            <Block>
                <MappingsFilterBlock :data="mappingsData"/>
            </Block>
        </PageSidebar>
        <PageContent class="flex flex-col gap-y-5">
            <MappingsSearchBlock/>
            <MappingsEntryBlock v-for="entry in infoData.entries"
                                :namespace="mappingsData.namespaces.find(value => value.id === infoData.namespace)!!"
                                :translated-to-namespace="infoData.translateAs ? mappingsData.namespaces.find(value => value.id === infoData.translateAs) : undefined"
                                :entry="entry" :version="version" :query="searchText"/>
            <MappingsSearchPlaceholder v-if="infoData.entries.length == 0"
                                       :searching="!!searchController"
                                       :has-query="!!infoData.query"/>
        </PageContent>
    </PageWidthLimiter>

    <LoadingSection v-else class="h-[calc(100vh-56px-24px-5rem)]"/>
</template>

<script lang="ts">
import {defineComponent} from "vue"
import {useMappingsStore} from "../app/mappings-store"
import {mapState} from "pinia"
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

export default defineComponent({
    name: "Mappings",
    components: {
        Block,
        LoadingSection,
        PageContent, PageSidebar, PageWidthLimiter, MappingsSearchPlaceholder, MappingsFilterBlock, MappingsSearchBlock, MappingsEntryBlock,
    },
    computed: {
        ...mapState(useMappingsDataStore, ["mappingsData", "infoData", "searchController"]),
        ...mapState(useMappingsStore, ["namespace", "version", "allowSnapshots", "searchText", "allowClasses", "allowMethods", "allowFields", "translateAs"]),
    },
    watch: {
        namespace: {
            handler() {
                updateMappingsData()
                ensureMappingsData()
                updateMappingsInfo()
            },
            immediate: true,
        },
        version: {
            handler() {
                updateMappingsData()
                ensureMappingsData()
                updateMappingsInfo()
            },
            immediate: true,
        },
        allowSnapshots: {
            handler() {
                ensureMappingsData()
            },
            immediate: true,
        },
        searchText: {
            handler() {
                updateMappingsInfo()
            },
            immediate: true,
        },
        allowClasses: {
            handler() {
                updateMappingsInfo()
            },
            immediate: true,
        },
        allowMethods: {
            handler() {
                updateMappingsInfo()
            },
            immediate: true,
        },
        allowFields: {
            handler() {
                updateMappingsInfo()
            },
            immediate: true,
        },
        translateAs: {
            handler() {
                ensureMappingsData()
                updateMappingsInfo()
            },
            immediate: true,
        },
    },
    mounted() {
        updateMappingsData(true)
    },
})
</script>

<style scoped>
</style>