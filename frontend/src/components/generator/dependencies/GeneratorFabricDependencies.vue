<template>
    <div class="w-full flex flex-col gap-4">
        <span>
            We are now at the last step of the setup process. Let's choose the main library we want to use.
            <br>
            This is the library we use to register our blocks, items, entities, etc.
            <br>
            <br>
            But of course, you can always go without one and experiment on your own.
        </span>
        <GeneratorOptions :size="2">
            <template #slot-0>
                <GeneratorOptionEntry :selected="!!dependencies?.includes('fabric-api')" @click="selectFabricApi(true)">
                    <span class="flex flex-col text-left">
                        <span class="font-semibold">Fabric API</span>
                        <span class="text-sm">from FabricMC</span>
                    </span>
                    <span class="flex flex-col text-left">
                        <span class="text-sm">✔ Used by the majority of Fabric mods</span>
                        <span class="text-sm">✔ Includes common hooks, such as registration, events, mod <span class="whitespace-nowrap">inter-compatibility</span> interfaces</span>
                    </span>
                </GeneratorOptionEntry>
            </template>
            <template #slot-1>
                <GeneratorOptionEntry :selected="!dependencies?.includes('fabric-api')" @click="selectFabricApi(false)">
                    <span class="flex flex-col text-left">
                        <span class="font-semibold">No Library</span>
                    </span>
                    <span class="flex flex-col text-left">
                        <span class="text-sm">✔ No extra dependencies for users to install</span>
                    </span>
                </GeneratorOptionEntry>
            </template>
        </GeneratorOptions>
    </div>
</template>

<script lang="ts">
import {mapWritableState} from "pinia"
import {useGeneratorStore} from "../../../app/generator-store"
import GeneratorOptions from "../GeneratorOptions.vue"
import GeneratorOptionEntry from "../GeneratorOptionEntry.vue"
import {defineComponent} from "vue"

export default defineComponent({
    name: "GeneratorFabricDependencies",
    components: {GeneratorOptionEntry, GeneratorOptions},
    computed: {
        ...mapWritableState(useGeneratorStore, ["dependencies"]),
    },
    methods: {
        selectFabricApi(api: boolean) {
            if (!this.dependencies) return
            if (api) {
                this.dependencies.push("fabric-api")
            } else {
                this.dependencies = this.dependencies.filter(it => it !== "fabric-api")
            }
        },
    },
})
</script>

<style scoped>

</style>