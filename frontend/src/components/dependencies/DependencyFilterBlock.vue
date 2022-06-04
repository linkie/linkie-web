<template>
    <div class="flex flex-col">
        <SubHeader :add-padding="false" class="pb-2">Loader</SubHeader>
        <select class="select select-sm font-light p-0"
                @change="loader = (($event.target as any)?.value?.toLowerCase() ?? loader)" :value="loader ?? ''">
            <option disabled selected>Select mod loader</option>
            <option v-for="loader in loaders">
                {{ loader }}
            </option>
        </select>

        <div class="divider mt-0 mb-0"/>
        <SubHeader :add-padding="false" class="pb-2">Build System</SubHeader>
        <select class="select select-sm font-light p-0"
                @change="forgeGradle = (($event.target as any)?.value === 'ForgeGradle')" :value="forgeGradle ? 'ForgeGradle' : 'Architectury Loom'">
            <option disabled selected>Select build system</option>
            <option>Architectury Loom</option>
            <option>ForgeGradle</option>
        </select>

        <div class="divider mt-0 mb-0"/>
        <SubHeader :add-padding="false" class="pb-2">Version</SubHeader>
        <div class="flex flex-col flex-nowrap justify-center h-full whitespace-nowrap pb-2">
            <div>
                <span class="pr-2">Enable snapshots</span>
                <input type="checkbox" class="checkbox checkbox-primary h-4" :checked="allowSnapshots" aria-label="Enable Snapshots"
                       @input="allowSnapshots = (($event.target as any)?.checked ?? allowSnapshots)"/>
            </div>
        </div>
        
        <select class="select select-sm font-light"
                @change="version = (($event.target as any)?.value ?? version)" :value="version ?? ''">
            <option disabled selected>Select version</option>
            <option v-for="v in applicableVersions">
                {{ v }}
            </option>
        </select>
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