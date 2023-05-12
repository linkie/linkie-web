<template>
    <div class="w-full flex flex-col gap-4">
        <span>
            Since you have selected a template that supports multiple mod loaders, you need to select the ones to support.
            <br>
            <span class="underline underline-offset-2 decoration-2 font-semibold">Fabric</span> and <span
                class="underline underline-offset-2 decoration-2 font-semibold">Forge</span> has been
            selected by default for you, but feel free to change them as you see fit.
        </span>
        <GeneratorOptions :size="3">
            <template #slot-0>
                <GeneratorOptionEntry :selected="modLoaders.includes('fabric')" @click="selectModLoader('fabric')">
                    <span class="flex flex-col text-left">
                        <span class="font-semibold">Fabric</span>
                    </span>
                </GeneratorOptionEntry>
            </template>
            <template #slot-1>
                <GeneratorOptionEntry :selected="modLoaders.includes('forge')" @click="selectModLoader('forge')">
                    <span class="flex flex-col text-left">
                        <span class="font-semibold">Forge</span>
                    </span>
                </GeneratorOptionEntry>
            </template>
            <template #slot-2>
                <GeneratorOptionEntry :selected="modLoaders.includes('quilt')" @click="selectModLoader('quilt')">
                    <span class="flex flex-col text-left">
                        <span class="font-semibold">Quilt</span>
                    </span>
                </GeneratorOptionEntry>
            </template>
        </GeneratorOptions>
        <transition-fade-in>
            <span v-if="canChooseFabricLike()">
                Having chosen both Fabric and Quilt, you may choose to enable the <span class="underline underline-offset-2 decoration-2 font-semibold">Fabric-Like Intermediary</span> subproject, used to call Fabric APIs.
                This can be used to call code that is not specific to neither Fabric nor Quilt, such as hooks that will apply to both Fabric and Quilt.
            </span>
            <div class="flex gap-3 items-center" v-if="canChooseFabricLike()">
                <input type="checkbox" id="fabric-like-toggle" v-model="fabricLike"/>
                <label for="fabric-like-toggle" class="select-none">Enable Fabric-Like Intermediary</label>
            </div>
        </transition-fade-in>
    </div>
</template>

<script lang="ts">
import {mapWritableState} from "pinia"
import {useGeneratorStore} from "../../app/generator-store"
import GeneratorOptions from "./GeneratorOptions.vue"
import GeneratorOptionEntry from "./GeneratorOptionEntry.vue"
import {defineComponent} from "vue"
import TransitionFadeIn from "../TransitionFadeIn.vue"

export default defineComponent({
    name: "GeneratorModLoaders",
    components: {TransitionFadeIn, GeneratorOptionEntry, GeneratorOptions},
    computed: {
        ...mapWritableState(useGeneratorStore, ["template", "modLoaders", "fabricLike"]),
    },
    methods: {
        canChooseFabricLike() {
            return this.template === "architectury" && this.modLoaders.includes("fabric") && this.modLoaders.includes("quilt")
        },
        selectModLoader(loader: ReturnType<typeof useGeneratorStore>["modLoaders"][number]) {
            if (this.modLoaders.includes(loader)) {
                this.modLoaders = this.modLoaders.filter(it => it !== loader)
            } else {
                this.modLoaders.push(loader)
            }
        },
    },
})
</script>

<style scoped>

</style>