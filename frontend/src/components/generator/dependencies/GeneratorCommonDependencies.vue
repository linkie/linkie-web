<template>
    <div class="w-full flex flex-col gap-4">
        <span>
            Lastly, if you want to add any additional dependencies, you can do so here.
        </span>
        <GeneratorOptions :size="freeDependencies.length">
            <template v-for="i in freeDependencies.length" #[`slot-${i-1}`]>
                <GeneratorOptionEntry :selected="!!dependencies?.includes(freeDependencies[i - 1].id)" @click="selectDependency(freeDependencies[i - 1].id)">
                    <span class="flex flex-col text-left">
                        <span class="font-semibold">{{ freeDependencies[i - 1].name}}</span>
                        <span class="flex gap-x-1.5">
                            <span v-for="loader of freeDependencies[i - 1].modLoaders" class="bg-base-500 dark:bg-base-dark-500 rounded-md text-[.75rem] px-[.438rem] inline-flex items-center justify-center h-4">{{ loader }}
                            </span>
                        </span>
                    </span>
                    <span class="flex flex-col text-left" v-if="!!freeDependencies[i - 1].descriptions">
                        <span v-for="desc of freeDependencies[i - 1].descriptions" class="text-sm"> {{ desc }}</span>
                    </span>
                </GeneratorOptionEntry>
            </template>
        </GeneratorOptions>
    </div>
</template>

<script lang="ts">
import {mapWritableState} from "pinia"
import {freeDependencies, useGeneratorStore} from "../../../app/generator-store"
import GeneratorOptions from "../GeneratorOptions.vue"
import GeneratorOptionEntry from "../GeneratorOptionEntry.vue"
import {defineComponent} from "vue"

export default defineComponent({
    name: "GeneratorCommonDependencies",
    components: {GeneratorOptionEntry, GeneratorOptions},
    computed: {
        ...mapWritableState(useGeneratorStore, ["dependencies"]),
        freeDependencies() {
            return freeDependencies()
        },
    },
    methods: {
        selectDependency(dependency: string) {
            if (!this.dependencies) return
            if (!this.dependencies.includes(dependency)) {
                this.dependencies.push(dependency)
            } else {
                this.dependencies = this.dependencies.filter(it => it !== dependency)
            }
        },
    },
})
</script>

<style scoped>

</style>