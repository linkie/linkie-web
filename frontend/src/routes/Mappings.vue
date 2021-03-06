<template>
    <div class="max-w-[calc(62rem+var(--sidebar-width))] mx-auto">
        <div v-if="mappingsData.namespaces.length !== 0" class="grid-setup">
            <div class="col-[1] px-5 pt-6 sm:pr-0 sm:w-[var(--sidebar-width)] sm:min-w-[var(--sidebar-width)] sm:absolute">
                <div class="p-5 card bg-base-100 shadow-xl rounded-lg">
                    <MappingsFilterBlock :data="mappingsData"/>
                </div>
            </div>
            <div class="col-[2/span_2] min-w-0">
                <MappingsSearchBlock/>
                <MappingsEntryBlock v-for="entry in infoData.entries" :namespace="mappingsData.namespaces.find(value => value.id === infoData.namespace)"
                                    :translated-to-namespace="infoData.translateAs ? mappingsData.namespaces.find(value => value.id === infoData.translateAs) : undefined"
                                    :entry="entry"/>
            </div>
        </div>
    </div>
</template>

<script lang="ts">
import {defineComponent} from "vue"
import {addAlert} from "../app/alerts"
import {reqNamespaces, reqSearch} from "../app/backend"
import {useMappingsStore} from "../app/mappings-store"
import {mapState} from "pinia"
import MappingsSearchBlock from "../components/mappings/MappingsSearchBlock.vue"
import MappingsEntryBlock from "../components/mappings/MappingsEntryBlock.vue"
import MappingsFilterBlock from "../components/mappings/MappingsFilterBlock.vue"
import {VersionEntry} from "./Dependencies.vue"
import axios from "axios"

export interface Namespace {
    id: string,
    versions: VersionEntry[],
    supportsAT: boolean,
    supportsAW: boolean,
    supportsMixin: boolean,
    supportsFieldDescription: boolean,
}

export interface MappingsData {
    namespaces: Namespace[],
    entries: string[],
}

export const mappingTypes = ["class", "field", "method"] as const
export type MappingType = typeof mappingTypes[number]

export interface MappingEntry {
    obf?: string,
    intermediary: string,
    named?: string,
    descObf?: string,
    descIntermediary?: string,
    descNamed?: string,
    ownerObf?: string,
    ownerIntermediary?: string,
    ownerNamed?: string,
    ownerObfClient?: string,
    obfClient?: string,
    descObfClient?: string,
    ownerObfServer?: string,
    obfServer?: string,
    descObfServer?: string,
    type: MappingType,
    translatedTo?: MappingEntry,
}

interface InfoData {
    namespace?: string,
    version?: string,
    allowClasses?: boolean,
    allowFields?: boolean,
    allowMethods?: boolean,
    translateAs?: string,
    query?: string,
    entries: MappingEntry[],
    fuzzy: boolean,
}

export default defineComponent({
    name: "Mappings",
    components: {MappingsFilterBlock, MappingsSearchBlock, MappingsEntryBlock},
    data() {
        return {
            mappingsData: {
                namespaces: [],
                entries: [],
            } as MappingsData,
            infoData: {
                entries: [],
                fuzzy: false,
            } as InfoData,
            reqNamespacesPromise: undefined as Promise<any> | undefined,
            searchController: undefined as AbortController | undefined,
        }
    },
    computed: {
        ...mapState(useMappingsStore, ["namespace", "version", "allowSnapshots", "searchText", "allowClasses", "allowMethods", "allowFields", "translateAs"]),
    },
    watch: {
        namespace: {
            handler() {
                this.updateMappingsData()
                this.ensureMappingsData()
                this.updateMappingsInfo()
            },
            immediate: true,
        },
        version: {
            handler() {
                this.updateMappingsData()
                this.ensureMappingsData()
                this.updateMappingsInfo()
            },
            immediate: true,
        },
        allowSnapshots: {
            handler() {
                this.ensureMappingsData()
            },
            immediate: true,
        },
        searchText: {
            handler() {
                this.updateMappingsInfo()
            },
            immediate: true,
        },
        allowClasses: {
            handler() {
                this.updateMappingsInfo()
            },
            immediate: true,
        },
        allowMethods: {
            handler() {
                this.updateMappingsInfo()
            },
            immediate: true,
        },
        allowFields: {
            handler() {
                this.updateMappingsInfo()
            },
            immediate: true,
        },
        translateAs: {
            handler() {
                this.ensureMappingsData()
                this.updateMappingsInfo()
            },
            immediate: true,
        },
    },
    mounted() {
        this.updateMappingsData()
        this.ensureMappingsData()
    },
    methods: {
        updateMappingsData() {
            if (Object.keys(this.mappingsData.namespaces).length == 0 && !this.reqNamespacesPromise) {
                this.reqNamespacesPromise = reqNamespaces().then(value => {
                    this.mappingsData.namespaces = value.data
                    this.ensureMappingsData()
                }).catch(reason => {
                    addAlert({
                        type: "error",
                        message: `Failed to fetch namespaces: ${reason.message}`,
                    })
                }).finally(() => {
                    this.reqNamespacesPromise = undefined
                })
            }
        },
        ensureMappingsData() {
            let {namespace, version, allowSnapshots, translateAs} = useMappingsStore()
            if (!namespace) {
                namespace = this.mappingsData.namespaces[0]?.id
                useMappingsStore().namespace = namespace
            }
            let namespaceObj = this.mappingsData.namespaces.find(value => value.id === namespace)
            let applicable_versions = namespaceObj?.versions
            if (applicable_versions) {
                if (!allowSnapshots) {
                    applicable_versions = applicable_versions.filter(entry => entry.stable)
                }
                if (translateAs) {
                    let translateAsObj = this.mappingsData.namespaces.find(value => value.id === translateAs)
                    let retain = translateAsObj?.versions?.map(entry => entry.version) ?? []
                    applicable_versions = applicable_versions.filter(value => retain.includes(value.version))
                }
                if (!version || !applicable_versions.find(entry => entry.version === version)) {
                    version = applicable_versions.find(_ => true)?.version
                    useMappingsStore().version = version
                }
            }
        },
        updateMappingsInfo() {
            let {namespace, version, searchText, allowClasses, allowFields, allowMethods, translateAs} = useMappingsStore()
            if (namespace && version && searchText && (allowClasses || allowMethods || allowFields)) {
                if (this.infoData.namespace !== namespace || this.infoData.version !== version || this.infoData.query !== searchText
                    || this.infoData.allowClasses !== allowClasses || this.infoData.allowFields !== allowFields || this.infoData.allowMethods !== allowMethods
                    || this.infoData.translateAs !== translateAs) {
                    if (this.searchController) {
                        this.searchController.abort()
                    }
                    this.searchController = new AbortController()
                    reqSearch(namespace, version, searchText, allowClasses, allowFields, allowMethods, translateAs, this.searchController).then(value => {
                        if (value.data.error) {
                            if (value.data.error !== "No results found!") {
                                addAlert({
                                    type: "error",
                                    message: `Failed to search: ${value.data.error}`,
                                })
                            }
                            this.infoData.entries = []
                            this.infoData.fuzzy = false
                            return
                        }
                        this.infoData.fuzzy = value.data.fuzzy
                        this.infoData.entries = (value.data.entries as any[]).map(this.mapEntryToMappingEntry)
                    }).catch(reason => {
                        if (!axios.isCancel(reason)) {
                            addAlert({
                                type: "error",
                                message: `Failed to search: ${reason.message}`,
                            })
                            this.infoData.entries = []
                            this.infoData.fuzzy = false
                        }
                    })
                }
            } else {
                this.infoData.entries = []
                this.infoData.fuzzy = false
            }
            this.setInfoDataToCurrent()
        },
        setInfoDataToCurrent() {
            let {namespace, version, searchText, allowClasses, allowFields, allowMethods, translateAs} = useMappingsStore()
            this.infoData.namespace = namespace
            this.infoData.version = version
            this.infoData.query = searchText
            this.infoData.allowClasses = allowClasses
            this.infoData.allowMethods = allowMethods
            this.infoData.allowFields = allowFields
            this.infoData.translateAs = translateAs
        },
        mapEntryToMappingEntry(obj: any): MappingEntry {
            let type: string
            if (obj.t === "c") type = "class"
            else if (obj.t === "f") type = "field"
            else if (obj.t === "m") type = "method"
            else type = obj.t
            let translatedTo: undefined | MappingEntry
            if (obj.l) translatedTo = this.mapEntryToMappingEntry(obj.l)
            else translatedTo = undefined
            return {
                obf: obj.o,
                intermediary: obj.i,
                named: obj.n,
                descObf: obj.d,
                descIntermediary: obj.e,
                descNamed: obj.f,
                ownerObf: obj.a,
                ownerIntermediary: obj.b,
                ownerNamed: obj.c,
                ownerObfClient: obj.g,
                obfClient: obj.h,
                descObfClient: obj.j,
                ownerObfServer: obj.k,
                obfServer: obj.l,
                descObfServer: obj.m,
                type,
                translatedTo,
            } as MappingEntry
        },
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