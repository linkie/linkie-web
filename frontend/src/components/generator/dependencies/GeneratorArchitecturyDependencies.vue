<template>
    <div class="w-full flex flex-col gap-4">
        <span>
            We are now at the last step of the setup process. Let's choose the main library we want to use.
            <br>
            This is the library we use to register our blocks, items, entities, etc.
            <br>
            Traditionally, <span class="underline underline-offset-2 decoration-2 font-semibold">Architectury API</span> is the default for Architectury projects,
            as it packs most of the hooks and events you need to get started.
            <br>
            However, <span class="underline underline-offset-2 decoration-2 font-semibold">Botarium</span> is an alternative that contains a rudimentary transfer api for item, fluid, energy containers,
            but does not offer as many hooks and events as Architectury API.
            <br>
            <br>
            But of course, you can always go without one and experiment on your own.
        </span>
        <GeneratorOptions :size="3">
            <template #slot-0>
                <GeneratorOptionEntry :selected="!!dependencies?.includes('architectury-api')" @click="selectArchitecturyApi()">
                    <span class="flex flex-col text-left">
                        <span class="font-semibold">Architectury API</span>
                        <span class="text-sm">from Architectury</span>
                    </span>
                    <span class="flex flex-col text-left">
                        <span class="text-sm">✔ Includes common hooks, such as registration, mod <span class="whitespace-nowrap">inter-compatibility</span> interfaces</span>
                        <span class="text-sm">✔ Includes 80+ event hooks</span>
                        <span class="text-sm">✔ Includes networking abstraction</span>
                    </span>
                </GeneratorOptionEntry>
            </template>
            <template #slot-1>
                <GeneratorOptionEntry :selected="!!dependencies?.includes('botarium')" @click="selectBotarium()">
                    <span class="flex flex-col text-left">
                        <span class="font-semibold">Botarium</span>
                        <span class="text-sm">from Terrarium</span>
                    </span>
                    <span class="flex flex-col text-left">
                        <span class="text-sm">✔ Includes common hooks, such as registration, mod <span class="whitespace-nowrap">inter-compatibility</span> interfaces</span>
                        <span class="text-sm">✔ Support for transfer api for item, fluid, energy containers</span>
                    </span>
                </GeneratorOptionEntry>
            </template>
            <template #slot-2>
                <GeneratorOptionEntry :selected="!dependencies?.includes('architectury-api') && !dependencies?.includes('botarium')" @click="unselect()">
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
    name: "GeneratorArchitecturyDependencies",
    components: {GeneratorOptionEntry, GeneratorOptions},
    computed: {
        ...mapWritableState(useGeneratorStore, ["dependencies"]),
    },
    methods: {
        selectArchitecturyApi() {
            if (!this.dependencies) return
            this.unselect()
            this.dependencies.push("architectury-api")
        },
        selectBotarium() {
            if (!this.dependencies) return
            this.unselect()
            this.dependencies.push("botarium")
        },
        unselect() {
            if (!this.dependencies) return
            this.dependencies = this.dependencies.filter(it => it !== "botarium" && it !== "architectury-api")
        },
    },
})
</script>

<style scoped>

</style>