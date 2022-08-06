<template>
    <div class="flex flex-col">
        <SubHeader :add-padding="false" class="pb-1">Loader</SubHeader>

        <div v-for="l in loaders" :class="[
            loader === l ? 'opacity-100 font-bold' : 'opacity-50 font-normal',
            'cursor-pointer p-2 capitalize rounded transition-all hover:opacity-100 hover:bg-neutral hover:text-white']" @click="loader = l">
            {{ l }}
        </div>

        <div v-if="loader === 'forge'">
            <div class="divider mt-0 mb-0"/>
            <SubHeader :add-padding="false" class="pb-1">Build System</SubHeader>

            <div :class="[
            !forgeGradle ? 'opacity-100 font-bold' : 'opacity-50 font-normal',
            'cursor-pointer p-2 capitalize rounded transition-all hover:opacity-100 hover:bg-neutral hover:text-white']" @click="forgeGradle = false">
                Architectury Loom
            </div>

            <div :class="[
            forgeGradle ? 'opacity-100 font-bold' : 'opacity-50 font-normal',
            'cursor-pointer p-2 capitalize rounded transition-all hover:opacity-100 hover:bg-neutral hover:text-white']" @click="forgeGradle = true">
                ForgeGradle
            </div>
        </div>

        <div class="divider mt-0 mb-0"/>
        <SubHeader :add-padding="false" class="pb-1">Version</SubHeader>
        <div class="flex flex-col flex-nowrap justify-center h-full whitespace-nowrap pb-2" v-if="loader === 'fabric'">
            <div>
                <span class="pr-2">Enable snapshots</span>
                <input type="checkbox" class="checkbox checkbox-primary h-4" :checked="allowSnapshots" aria-label="Enable Snapshots"
                       @input="allowSnapshots = (($event.target as any)?.checked ?? allowSnapshots)"/>
            </div>
        </div>

        <div class="bg-base-300 rounded-lg">
            <div class="px-1 py-2 h-52 overflow-x-clip gradient-mask-b-80 overflow-y-scroll">
                <p v-for="v in applicableVersions"
                   :class="[version === v ? 'opacity-100 font-bold' : 'opacity-50 font-normal',
                    'transition-all hover:opacity-100 hover:bg-neutral hover:text-white px-2 py-1 rounded-md cursor-pointer']"
                   @click="version = v">
                    {{ v }}
                </p>
            </div>
        </div>
    </div>
</template>

<script lang="ts">
import {DependencySearchData} from "../../routes/Dependencies.vue"
import {defineComponent, PropType} from "vue"
import {useDependencySearchStore} from "../../app/dependency-store"
import {mapWritableState} from "pinia"
import SubHeader from "./SubHeader.vue"

export default defineComponent({
    name: "DependencyFilterBlock",
    components: {SubHeader},
    computed: {
        ...mapWritableState(useDependencySearchStore, ["loader", "version", "allowSnapshots", "forgeGradle"]),
        loaders(): string[] {
            return Object.keys(this.searchData.versions)
        },
        applicableVersions(): string[] {
            let {loader, allowSnapshots} = useDependencySearchStore()
            if (!loader) return []
            let versions = this.searchData.versions[loader] ?? []
            if (versions && !allowSnapshots) {
                versions = versions.filter(entry => entry.stable)
            }
            return versions.map(entry => entry.version)
        },
    },
    props: {
        searchData: {
            type: Object as PropType<DependencySearchData>,
            required: true,
        },
    },
})
</script>

<style scoped>
.checkbox {
    vertical-align: -0.1rem;
}

.select-label {
    display: flex !important;
}
</style>