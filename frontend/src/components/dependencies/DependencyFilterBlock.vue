<template>
    <div class="px-5 pt-6">
        <div class="card bg-base-100 shadow-xl rounded-lg overflow-x-auto">
            <div class="flex">
                <div class="px-4 whitespace-nowrap">
                    Loader
                    <select class="select capitalize font-light rounded-none w-52"
                            @change="loader = (($event.target as any)?.value?.toLowerCase() ?? loader)" :value="loader ?? ''">
                        <option disabled selected>Select mod loader</option>
                        <option v-for="loader in loaders">
                            {{ loader }}
                        </option>
                    </select>
                </div>

                <div class="px-4 whitespace-nowrap">
                    Version
                    <select class="select capitalize font-light rounded-none w-52"
                            @change="version = (($event.target as any)?.value ?? version)" :value="version ?? ''">
                        <option disabled selected>Select version</option>
                        <option v-for="v in applicableVersions">
                            {{ v }}
                        </option>
                    </select>
                </div>

                <div class="px-4 grow">
                    <div class="flex flex-col flex-nowrap justify-center h-full whitespace-nowrap">
                        <div>
                            <span class="pr-2">Enable snapshots</span>
                            <input type="checkbox" class="checkbox checkbox-primary h-4" :checked="allowSnapshots" aria-label="Enable Snapshots"
                                   @input="allowSnapshots = (($event.target as any)?.checked ?? allowSnapshots)"/>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    </div>
</template>

<script lang="ts">
import {DependencySearchData} from "../../routes/Dependencies.vue"
import {defineComponent, PropType} from "vue"
import {useDependencySearchStore} from "../../app/dependency-store"
import {mapWritableState} from "pinia"

export default defineComponent({
    name: "DependencyFilterBlock",
    computed: {
        ...mapWritableState(useDependencySearchStore, ["loader", "version", "allowSnapshots"]),
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
</style>