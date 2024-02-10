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
                                :namespace="namespaceObj"
                                :translated-to-namespace="infoData.translateAs ? mappingsData.namespaces.find(value => value.id === infoData.translateAs) : (infoData.translateAsVersion ? namespaceObj : undefined)"
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
import {ensureMappingsData, Namespace, updateMappingsData, updateMappingsInfo, useMappingsDataStore} from "../app/mappings-data"
import MappingsSearchPlaceholder from "../components/mappings/MappingsSearchPlaceholder.vue"
import PageWidthLimiter from "../components/PageWidthLimiter.vue"
import PageSidebar from "../components/PageSidebar.vue"
import PageContent from "../components/PageContent.vue"
import LoadingSection from "../components/LoadingSection.vue"
import Block from "../components/Block.vue"
import {fullPath} from "../app/backend"

export default defineComponent({
    name: "Mappings",
    components: {
        Block,
        LoadingSection,
        PageContent, PageSidebar, PageWidthLimiter, MappingsSearchPlaceholder, MappingsFilterBlock, MappingsSearchBlock, MappingsEntryBlock,
    },
    computed: {
        namespaceObj(): Namespace {
            return this.mappingsData.namespaces.find(value => value.id === this.infoData.namespace)!!
        },
        ...mapState(useMappingsDataStore, ["mappingsData", "infoData", "searchController"]),
        ...mapState(useMappingsStore, ["namespace", "version", "allowSnapshots", "searchText", "allowClasses", "allowMethods", "allowFields", "translateAs", "translateAsVersion"]),
    },
    watch: {
        namespace: {
            handler() {
                updateMappingsData(fullPath())
                ensureMappingsData(fullPath())
                updateMappingsInfo(fullPath())
            },
            immediate: true,
        },
        version: {
            handler() {
                updateMappingsData(fullPath())
                ensureMappingsData(fullPath())
                updateMappingsInfo(fullPath())
            },
            immediate: true,
        },
        allowSnapshots: {
            handler() {
                ensureMappingsData(fullPath())
            },
            immediate: true,
        },
        searchText: {
            handler() {
                updateMappingsInfo(fullPath())
            },
            immediate: true,
        },
        allowClasses: {
            handler() {
                updateMappingsInfo(fullPath())
            },
            immediate: true,
        },
        allowMethods: {
            handler() {
                updateMappingsInfo(fullPath())
            },
            immediate: true,
        },
        allowFields: {
            handler() {
                updateMappingsInfo(fullPath())
            },
            immediate: true,
        },
        translateAs: {
            handler() {
                ensureMappingsData(fullPath())
                updateMappingsInfo(fullPath())
            },
            immediate: true,
        },
        translateAsVersion: {
            handler() {
                ensureMappingsData(fullPath())
                updateMappingsInfo(fullPath())
            },
            immediate: true,
        },
    },
    mounted() {
        updateMappingsData(fullPath(), this.$route.query)
    },
})
</script>

<style scoped>
</style>