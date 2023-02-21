<template>
    <div class="pt-20"/>
    <div v-if="mappingsData.namespaces.length !== 0" class="max-w-[80rem] mx-auto w-full px-5 sm:flex sm:gap-5">
        <div class="sm:w-[var(--sidebar-width)] sm:min-w-[var(--sidebar-width)]">
            <div class="p-5 card bg-base-100 dark:bg-base-dark-200 shadow-xl rounded-lg mb-10">
                <MappingsFilterBlock :data="mappingsData"/>
            </div>
        </div>
        <div class="flex-1 min-w-0">
            <MappingsSearchBlock/>
            <div v-if="infoData.entries.length > 0">
                <MappingsEntryBlock v-for="entry in infoData.entries"
                                    :namespace="mappingsData.namespaces.find(value => value.id === infoData.namespace)"
                                    :translated-to-namespace="infoData.translateAs ? mappingsData.namespaces.find(value => value.id === infoData.translateAs) : undefined"
                                    :entry="entry" :version="version" :query="searchText"/>
            </div>
            <MappingsSearchPlaceholder v-else
                                       :searching="!!searchController"
                                       :has-query="!!infoData.query"/>
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
import {defineComponent} from "vue"
import {useMappingsStore} from "../app/mappings-store"
import {mapState} from "pinia"
import MappingsSearchBlock from "../components/mappings/MappingsSearchBlock.vue"
import MappingsEntryBlock from "../components/mappings/MappingsEntryBlock.vue"
import MappingsFilterBlock from "../components/mappings/MappingsFilterBlock.vue"
import {ensureMappingsData, updateMappingsData, updateMappingsInfo, useMappingsDataStore} from "../app/mappings-data"
import MappingsSearchPlaceholder from "../components/mappings/MappingsSearchPlaceholder.vue"

export default defineComponent({
    name: "Mappings",
    components: {MappingsSearchPlaceholder, MappingsFilterBlock, MappingsSearchBlock, MappingsEntryBlock},
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
        updateMappingsData()
        ensureMappingsData()
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

/* Hide scrollbar for Chrome, Safari and Opera */
.no-scrollbar::-webkit-scrollbar {
    display: none;
}

/* Hide scrollbar for IE, Edge and Firefox */
.no-scrollbar {
    -ms-overflow-style: none; /* IE and Edge */
    scrollbar-width: none; /* Firefox */
}
</style>