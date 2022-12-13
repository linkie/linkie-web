<template>
    <div class="pt-20"/>
    <div class="sm:max-w-[calc(62rem+var(--sidebar-width))] sm:mx-auto">
        <div v-if="mappingsData.namespaces.length !== 0" class="grid-setup">
            <div class="col-[1] px-5 sm:pr-0 sm:w-[var(--sidebar-width)] sm:min-w-[var(--sidebar-width)]">
                <div class="p-5 card bg-base-100 shadow-xl rounded-lg mb-10">
                    <MappingsFilterBlock :data="mappingsData"/>
                </div>
            </div>
            <div class="col-[2/span_2] min-w-0 max-w-[62rem]">
                <MappingsSearchBlock/>
                <div v-if="infoData.entries.length > 0">
                    <MappingsEntryBlock v-for="entry in infoData.entries" :namespace="mappingsData.namespaces.find(value => value.id === infoData.namespace)"
                                        :translated-to-namespace="infoData.translateAs ? mappingsData.namespaces.find(value => value.id === infoData.translateAs) : undefined"
                                        :entry="entry" :version="version"/>
                </div>
                <div v-else-if="searchController" class="m-10 flex flex-col items-center">
                    <svg xmlns="http://www.w3.org/2000/svg" class="icon icon-tabler icon-tabler-search m-4" width="24" height="24" viewBox="0 0 24 24" stroke-width="2" stroke="currentColor" fill="none" stroke-linecap="round" stroke-linejoin="round">
                        <path stroke="none" d="M0 0h24v24H0z" fill="none"></path>
                        <circle cx="10" cy="10" r="7"></circle>
                        <line x1="21" y1="21" x2="15" y2="15"></line>
                    </svg>
                    <p class="font-bold">Searching Results</p>
                    <p>Feeling Lucky?</p>
                </div>
                <div v-else class="m-10 flex flex-col items-center">
                    <svg xmlns="http://www.w3.org/2000/svg" class="icon icon-tabler icon-tabler-ban m-4" width="24" height="24" viewBox="0 0 24 24"
                         stroke-width="2" stroke="currentColor" fill="none" stroke-linecap="round" stroke-linejoin="round">
                        <path stroke="none" d="M0 0h24v24H0z" fill="none"></path>
                        <circle cx="12" cy="12" r="9"></circle>
                        <line x1="5.7" y1="5.7" x2="18.3" y2="18.3"></line>
                    </svg>
                    <p class="font-bold">No Results</p>
                    <p v-if="!infoData.query">Try searching something?</p>
                    <p v-else>Did you make a mistake in your search?</p>
                </div>
            </div>
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

export default defineComponent({
    name: "Mappings",
    components: {MappingsFilterBlock, MappingsSearchBlock, MappingsEntryBlock},
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