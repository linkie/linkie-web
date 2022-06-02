<template>
    <div class="max-w-4xl mx-auto">
        <div v-if="Object.keys(searchData.versions).length !== 0">
            <DependencyFilterBlock :searchData="searchData"/>

            <Block>
                <SubHeader :add-padding="false">Table of Contents</SubHeader>
                <ol class="list-decimal pl-6 mt-2">
                    <li v-for="entry in dependencyBlocks">
                        <a :href="'#dep-' + dependencyBlocks.indexOf(entry)">{{ entry[0] }}</a>
                    </li>
                </ol>
            </Block>

            <DependencyBlock v-for="[blockName, block] in dependencyBlocks" :title="blockName"
                             :id="'dep-' + dependencyBlocks.findIndex(entry => entry[0] === blockName)">
                <div v-if="block.mavens.length > 0">
                    <div v-for="maven in block.mavens">
                        <SubHeader v-if="block.mavens.indexOf(maven) === 0 && !maven.subtitle">Maven Repository</SubHeader>

                        <CodeBlock :title="maven.subtitle ?? ''">
                            {{ formatMaven(maven.url) }}
                        </CodeBlock>
                    </div>
                </div>

                <CodeBlock v-for="dependency in block.dependencies" :title="dependency.name">
                    {{ formatDependency(dependency.type, dependency.notation) }}
                </CodeBlock>
            </DependencyBlock>
        </div>

        <div v-else>
            <Block>
                <progress class="progress progress-accent w-80 mx-auto"></progress>
            </Block>
        </div>
    </div>
</template>

<script lang="ts">
import DependencyBlock from "../components/dependencies/DependencyBlock.vue"
import DependencyFilterBlock from "../components/dependencies/DependencyFilterBlock.vue"
import CodeBlock from "../components/dependencies/CodeBlock.vue"
import SubHeader from "../components/dependencies/SubHeader.vue"
import {DependencyType, formatDependency, formatMaven, tab} from "../app/dep-format"
import {reqVersions, reqVersionsFor} from "../app/backend"
import Block from "../components/Block.vue"
import {defineComponent} from "vue"
import {useDependencySearchStore} from "../app/dependency-store"
import {mapState} from "pinia"
import {addAlert} from "../app/alerts"

export interface VersionEntry {
    version: string,
    stable: boolean,
}

export interface DependencyEntry {
    name: string,
    type: DependencyType,
    notation: string,
    version: string,
}

export interface MavenInfo {
    subtitle?: string,
    url: string,
}

export interface DependencyBlock {
    mavens: MavenInfo[],
    dependencies: DependencyEntry[],
}

export type DependencyBlocks = { [name: string]: DependencyBlock }

export interface DependencySearchData {
    versions: { [loader: string]: VersionEntry[]; }
}

export interface DependencyInfoData {
    loader?: string,
    versions: { [version: string]: DependencyBlocks; }
}

export default defineComponent({
    name: "Dependencies",
    components: {Block, CodeBlock, DependencyBlock, DependencyFilterBlock, SubHeader},
    data() {
        return {
            searchData: {
                versions: {},
            } as DependencySearchData,
            infoData: {
                versions: {},
            } as DependencyInfoData,
            formatMaven,
            formatDependency,
            tab,
            reqVersionsPromise: undefined as Promise<any> | undefined,
        }
    },
    computed: {
        dependencyBlocks(): [string, DependencyBlock][] {
            return Object.entries(this.infoData.versions[useDependencySearchStore().version ?? ""] ?? {})
        },
        ...mapState(useDependencySearchStore, ["loader", "version", "allowSnapshots"]),
    },
    watch: {
        loader: {
            handler() {
                this.updateDependencyData()
                this.ensureDependencyData()
                this.updateDependencyInfo()
            },
            immediate: true,
        },
        version: {
            handler() {
                this.updateDependencyData()
                this.ensureDependencyData()
            },
            immediate: true,
        },
        allowSnapshots: {
            handler() {
                this.ensureDependencyData()
            },
            immediate: true,
        },
    },
    mounted() {
        this.updateDependencyData()
        this.ensureDependencyData()
        this.updateDependencyInfo()
    },
    methods: {
        updateDependencyData() {
            if (Object.keys(this.searchData.versions).length == 0 && !this.reqVersionsPromise) {
                this.reqVersionsPromise = reqVersions().then(value => {
                    this.searchData.versions = value.data
                    this.ensureDependencyData()
                }).catch(reason => {
                    addAlert({
                        type: "error",
                        message: `Failed to fetch versions: ${reason.message}`,
                    })
                }).finally(() => {
                    this.reqVersionsPromise = undefined
                })
            }
        },
        ensureDependencyData() {
            let {loader, version, allowSnapshots} = useDependencySearchStore()
            if (!loader) {
                loader = Object.keys(this.searchData.versions).at(0)
                useDependencySearchStore().loader = loader
            }
            let applicable_versions = this.searchData.versions[loader!!]
            if (applicable_versions) {
                if (!allowSnapshots) {
                    applicable_versions = applicable_versions.filter(entry => entry.stable)
                }
                if (!version || !applicable_versions.find(entry => entry.version === version)) {
                    version = applicable_versions.find(entry => entry.stable)?.version
                    useDependencySearchStore().version = version
                }
            }
        },
        updateDependencyInfo() {
            let {loader} = useDependencySearchStore()
            if (loader) {
                if (this.infoData.loader !== loader) {
                    reqVersionsFor(loader).then(value => {
                        this.infoData.versions = value.data
                    }).catch(reason => {
                        addAlert({
                            type: "error",
                            message: `Failed to fetch dependencies: ${reason.message}`,
                        })
                    })
                }
            } else {
                this.infoData.versions = {}
            }
            this.infoData.loader = loader
        },
    },
})
</script>

<style scoped>
</style>