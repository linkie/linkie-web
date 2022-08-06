<template>
    <div v-if="Object.keys(searchData.versions).length !== 0">
        <div class="max-w-[calc(62rem+var(--sidebar-width))] mx-auto">
            <div class="grid-setup">
                <div class="col-[1] px-5 pt-6 sm:pr-0 sm:w-[var(--sidebar-width)] sm:min-w-[var(--sidebar-width)] sm:absolute">
                    <div class="p-5 card bg-base-100 shadow-xl rounded-lg">
                        <DependencyFilterBlock :searchData="searchData"/>
                    </div>

                    <div class="p-5 mt-6 card bg-base-100 shadow-xl rounded-lg">
                        <SubHeader :add-padding="false">Table of Contents</SubHeader>
                        <ol class="list-decimal pl-6 mt-2">
                            <li v-for="entry in dependencyBlocks">
                                <a :href="'#dep-' + dependencyBlocks.indexOf(entry)">{{ entry[0] }}</a>
                            </li>
                        </ol>
                    </div>
                </div>

                <div class="col-[2/span_2] min-w-0">
                    <DependencyBlock v-for="[blockName, block] in dependencyBlocks" :title="blockName"
                                     :id="'dep-' + dependencyBlocks.findIndex(entry => entry[0] === blockName)">
                        <div v-if="block.mavens.length > 0">
                            <div v-for="maven in block.mavens">
                                <SubHeader v-if="block.mavens.indexOf(maven) === 0 && !maven.subtitle">Maven Repository</SubHeader>

                                <CodeBlock :title="maven.subtitle ?? ''">
                                    <span class="hover:underline cursor-pointer"
                                          @click="copyAs(formatMaven(maven.url))">
                                        {{ formatMaven(maven.url) }}
                                    </span>
                                </CodeBlock>
                            </div>
                        </div>

                        <div v-for="dependency in block.dependencies">
                            <p class="mt-1">
                                {{ dependency.name }}
                                <span class="hover:underline cursor-pointer" @click="copyAs(dependency.version)">{{ dependency.version }}</span>
                            </p>
                            <CodeBlock :title="null">dependencies {<br>
                                <span>{{ "    " }}</span>
                                <span class="hover:underline cursor-pointer" @click="copyAs(formatDependency(dependency.type, dependency.notation, false))">
                                {{ formatDependency(dependency.type, dependency.notation, false) }}
                            </span>
                                <br>}
                            </CodeBlock>
                        </div>
                    </DependencyBlock>
                </div>
            </div>
        </div>
    </div>

    <div v-else class="max-w-4xl mx-auto">
        <Block>
            <progress class="progress progress-accent w-80 mx-auto"></progress>
        </Block>
    </div>
</template>

<script lang="ts">
import DependencyBlock from "../components/dependencies/DependencyBlock.vue"
import DependencyFilterBlock from "../components/dependencies/DependencyFilterBlock.vue"
import CodeBlock from "../components/dependencies/CodeBlock.vue"
import SubHeader from "../components/dependencies/SubHeader.vue"
import {DependencyType, formatBlock, formatDependency, formatMaven, tab} from "../app/dep-format"
import {reqVersions} from "../app/backend"
import Block from "../components/Block.vue"
import {defineComponent} from "vue"
import {useDependencySearchStore} from "../app/dependency-store"
import {mapState} from "pinia"
import {addAlert} from "../app/alerts"
import {copyAs} from "../app/copy";

export interface VersionEntry {
    version: string,
    stable: boolean,
    blocks: DependencyBlocks,
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

export default defineComponent({
    name: "Dependencies",
    components: {Block, CodeBlock, DependencyBlock, DependencyFilterBlock, SubHeader},
    data() {
        return {
            searchData: {
                versions: {},
            } as DependencySearchData,
            formatMaven,
            formatDependency,
            formatBlock,
            tab,
            reqVersionsPromise: undefined as Promise<any> | undefined,
            copyAs,
        }
    },
    computed: {
        dependencyBlocks(): [string, DependencyBlock][] {
            let {loader, version} = useDependencySearchStore()
            return Object.entries(this.searchData.versions[loader ?? ""]
                    ?.find(entry => entry.version === version)
                    ?.blocks ?? {})
        },
        ...mapState(useDependencySearchStore, ["loader", "version", "allowSnapshots"]),
    },
    watch: {
        loader: {
            handler() {
                this.updateDependencyData()
                this.ensureDependencyData()
            },
            immediate: true,
        },
        version: {
            handler() {
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
                loader = Object.keys(this.searchData.versions)[0]
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