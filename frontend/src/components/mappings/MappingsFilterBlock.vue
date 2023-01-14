<template>
    <div class="flex flex-col">
        <SubHeader :add-padding="false" class="pb-1">Namespace</SubHeader>

        <div v-for="[group, nses] in Object.entries(namespacesGrouped)" class="pb-1">
            <div v-if="group !== 'Others' || expandNamespaces">
                <p class="text-sm font-bold">{{ group }}</p>
                <div v-for="ns in nses" :class="[
                    namespace === ns.id ? 'opacity-100 font-bold' : 'opacity-60 hover:font-normal',
                    'cursor-pointer px-2 py-1 capitalize rounded transition-all hover:opacity-100 hover:bg-neutral hover:text-white']"
                     @click="namespace = ns.id">
                    {{ localizeNamespace(ns) ?? "" }}
                </div>
            </div>
        </div>

        <div class="px-2 py-1 justify-center cursor-pointer flex opacity-60 hover:opacity-100 transition-all rounded hover:bg-neutral hover:text-white"
             @click="expandNamespaces=!expandNamespaces">
            <svg xmlns="http://www.w3.org/2000/svg" :class="[expandNamespaces ? 'rotate-180' : '']" width="24" height="24" viewBox="0 0 24 24"
                 stroke-width="2" stroke="currentColor" fill="none" stroke-linecap="round" stroke-linejoin="round">
                <path stroke="none" d="M0 0h24v24H0z" fill="none"></path>
                <polyline points="6 9 12 15 18 9"></polyline>
            </svg>
            <span v-if="!expandNamespaces">
                Show More...
            </span>
            <span v-else>
                Show Less...
            </span>
        </div>

        <div class="divider mt-0 mb-0"/>
        <SubHeader :add-padding="false" class="pb-1">Version</SubHeader>
        <div class="flex flex-col flex-nowrap justify-center h-full whitespace-nowrap pb-2">
            <div>
                <span class="pr-2">Enable snapshots</span>
                <input type="checkbox" class="checkbox checkbox-primary h-4" :checked="allowSnapshots" aria-label="Enable Snapshots"
                       @input="allowSnapshots = ($event.target as any)?.checked ?? allowSnapshots"/>
            </div>
        </div>

        <div class="bg-base-300 rounded-lg">
            <div class="p-3 h-40 overflow-x-clip gradient-mask-b-80 overflow-y-scroll">
                <p v-for="v in applicableVersions"
                   :class="[version === v.version && v.hasTranslation ? 'opacity-100 font-bold' : 'opacity-60 hover:font-normal',
                    v.hasTranslation ? 'transition-all hover:opacity-100 hover:bg-neutral hover:text-white rounded-md cursor-pointer' : 'cursor-not-allowed line-through',
                    'px-2 py-1']"
                   @click="version = v.hasTranslation ? v.version : version">
                    {{ v.hasTranslation ? v.version : v.version + " (no translation)" }}
                </p>
            </div>
        </div>

        <div class="divider mt-0 mb-0"/>
        <div class="mt-2">
            <SubHeader :add-padding="false" class="pb-2">Translation</SubHeader>
            <p class="text-sm font-bold">No Translation</p>
            <div :class="[
                translateAs === undefined ? 'opacity-100 font-bold' : 'opacity-60 hover:font-normal',
                'cursor-pointer px-2 py-1 capitalize rounded transition-all hover:opacity-100 hover:bg-neutral hover:text-white']"
                 @click="translateAs = undefined">
                N/A
            </div>
            <div v-for="[group, nses] in Object.entries(namespacesGrouped)" class="pb-1">
                <p class="text-sm font-bold">{{ group }}</p>
                <div v-for="ns in nses">
                    <div v-if="ns?.id !== namespace" :class="[
                        translateAs === ns.id ? 'opacity-100 font-bold' : 'opacity-60 hover:font-normal',
                        'cursor-pointer px-2 py-1 capitalize rounded transition-all hover:opacity-100 hover:bg-neutral hover:text-white']"
                         @click="translateAs = ns.id">
                        {{ localizeNamespace(ns) ?? "" }}
                    </div>
                </div>
            </div>
        </div>
    </div>
</template>

<script lang="ts">
import {defineComponent, PropType} from "vue"
import {mapWritableState} from "pinia"
import {useMappingsStore} from "../../app/mappings-store"
import SubHeader from "../dependencies/SubHeader.vue"
import {namespaceGroups, namespaceLocalizations} from "../../app/backend"
import {MappingsData, Namespace} from "../../app/mappings-data"

interface VersionPossible {
    version: string
    hasTranslation: boolean
}

export default defineComponent({
    name: "MappingsFilterBlock",
    components: {SubHeader},
    data() {
        return {
            expandNamespaces: false,
        }
    },
    methods: {
        localizeNamespace(namespace?: Namespace | string): string | undefined {
            if (typeof namespace === "string") {
                return namespaceLocalizations[namespace.toLowerCase()] || namespace.toLowerCase()
            } else if (namespace) {
                let id = namespace.id
                return namespaceLocalizations[id.toLowerCase()] || id.toLowerCase()
            } else {
                return undefined
            }
        },
        delocalizeNamespace(string: string): Namespace {
            let id = Object.entries(namespaceLocalizations)
                    .find(([id, name]) => name === string)
                    ?.[0] ?? this.namespace
            return this.namespaces.find(ns => ns.id === id) ?? (this.namespace ?? this.namespaces[0])
        },
    },
    computed: {
        ...mapWritableState(useMappingsStore, ["namespace", "version", "allowSnapshots", "translateAs"]),
        namespaces(): Namespace[] {
            return this.data.namespaces
        },
        namespacesGrouped(): { [group: string]: Namespace[] } {
            let groups = {} as { [group: string]: Namespace[] }
            for (let ns in this.namespaces) {
                let groupsApplicable = namespaceGroups[this.namespaces[ns].id] ?? "Others"
                if (typeof groupsApplicable === "string") {
                    groupsApplicable = [groupsApplicable]
                }
                for (let group of groupsApplicable) {
                    if (!groups[group]) {
                        groups[group] = []
                    }
                    groups[group].push(this.namespaces[ns])
                }
            }
            for (let groupsKey in groups) {
                //sort
                groups[groupsKey].sort((a, b) => (this.localizeNamespace(a) ?? "").localeCompare(this.localizeNamespace(b) ?? ""))
            }
            return groups
        },
        firstNamespace(): Namespace | undefined {
            return this.namespaces[0]
        },
        applicableVersions(): VersionPossible[] {
            let {namespace, allowSnapshots, translateAs} = useMappingsStore()
            if (!namespace) return []
            let namespaceObj = this.data.namespaces.find(value => value.id === namespace)
            if (!namespaceObj) return []
            let versions = namespaceObj.versions
            if (versions && !allowSnapshots) {
                versions = versions.filter(entry => entry.stable)
            }
            if (versions && translateAs) {
                let translateAsObj = this.data.namespaces.find(value => value.id === translateAs)
                let retain = translateAsObj?.versions?.map(entry => entry.version) ?? []
                return versions.map(entry => {
                    return {
                        version: entry.version,
                        hasTranslation: retain.includes(entry.version),
                    }
                })
            } else {
                return versions.map(entry => {
                    return {
                        version: entry.version,
                        hasTranslation: true,
                    }
                })
            }
        },
    },
    props: {
        data: {
            type: Object as PropType<MappingsData>,
            required: true,
        },
    },
    mounted() {
        const urlParams = new URLSearchParams(window.location.search)
        if (this.namespaces.map(namespace => namespace.id).includes(urlParams.get("namespace") ?? "")) {
            useMappingsStore().namespace = (urlParams.get("namespace") ?? "") as string
            useMappingsStore().translateAs = undefined

            if (this.applicableVersions.map(version => version.version).includes(urlParams.get("version") ?? "")) {
                useMappingsStore().version = (urlParams.get("version") ?? "") as string
            }

            history.pushState({}, "", "/mappings")
        }
    },
})
</script>

<style scoped>
.checkbox {
    vertical-align: -0.1rem;
}
</style>