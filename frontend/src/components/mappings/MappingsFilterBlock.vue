<template>
    <div class="flex flex-col">
        <SubHeader :add-padding="false" class="pb-2">Namespace</SubHeader>
        <select class="select select-sm capitalize font-light p-0"
                @change="namespace = ($event.target as any)?.value?.toLowerCase() ?? namespace" :value="namespace ?? ''">
            <option disabled selected>Select namespace</option>
            <option v-for="namespace in namespaces">
                {{ namespace.id }}
            </option>
        </select>
        
        <div class="divider mt-0 mb-0"></div>
        <SubHeader :add-padding="false" class="pb-2">Version</SubHeader>
        <div class="flex flex-col flex-nowrap justify-center h-full whitespace-nowrap pb-2">
            <div>
                <span class="pr-2">Enable snapshots</span>
                <input type="checkbox" class="checkbox checkbox-primary h-4" :checked="allowSnapshots" aria-label="Enable Snapshots"
                       @input="allowSnapshots = ($event.target as any)?.checked ?? allowSnapshots"/>
            </div>
        </div>
        
        <select class="select select-sm capitalize font-light p-0"
                @change="version = ($event.target as any)?.value ?? version" :value="version ?? ''">
            <option disabled selected>Select version</option>
            <option v-for="v in applicableVersions">
                {{ v }}
            </option>
        </select>

        <div class="divider mt-0 mb-0"></div>
        <SubHeader :add-padding="false" class="pb-2">Search Type</SubHeader>
        <div class="flex flex-col flex-nowrap justify-center h-full whitespace-nowrap pb-2">
            <div>
                <input type="checkbox" class="checkbox checkbox-primary h-4" :checked="allowClasses" aria-label="Allow Classes"
                       @input="allowClasses = ($event.target as any)?.checked ?? allowClasses"/>
                <span class="pl-2">Classes</span>
            </div>
            <div>
                <input type="checkbox" class="checkbox checkbox-primary h-4" :checked="allowMethods" aria-label="Allow Methods"
                       @input="allowMethods = ($event.target as any)?.checked ?? allowMethods"/>
                <span class="pl-2">Methods</span>
            </div>
            <div>
                <input type="checkbox" class="checkbox checkbox-primary h-4" :checked="allowFields" aria-label="Allow Fields"
                       @input="allowFields = ($event.target as any)?.checked ?? allowFields"/>
                <span class="pl-2">Fields</span>
            </div>
        </div>

        <div class="divider mt-0 mb-0"></div>
        <SubHeader :add-padding="false" class="pb-2">Translate To</SubHeader>
        <select class="select select-sm capitalize font-light p-0"
                @change="translateAs = ($event.target as any)?.value === 'Do not translate' ? undefined : ($event.target as any)?.value?.toLowerCase()" :value="translateAs ?? 'Do not translate'">
            <option disabled selected>Select namespace</option>
            <option>Do not translate</option>
            <option v-for="ns in namespaces" :disabled="ns.id === namespace">
                {{ ns.id }}
            </option>
        </select>
    </div>
</template>

<script lang="ts">
import {defineComponent, PropType} from "vue"
import {mapWritableState} from "pinia"
import {useMappingsStore} from "../../app/mappings-store"
import {MappingsData, Namespace} from "../../routes/Mappings.vue"
import {useDependencySearchStore} from "../../app/dependency-store"
import SubHeader from "../dependencies/SubHeader.vue"

export default defineComponent({
    name: "MappingsFilterBlock",
    components: {SubHeader},
    computed: {
        ...mapWritableState(useMappingsStore, ["namespace", "version", "allowSnapshots", "allowClasses", "allowFields", "allowMethods", "translateAs"]),
        namespaces(): Namespace[] {
            return this.data.namespaces
        },
        applicableVersions(): string[] {
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
                versions = versions.filter(value => retain.includes(value.version))
            }
            return versions.map(entry => entry.version)
        },
    },
    props: {
        data: {
            type: Object as PropType<MappingsData>,
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