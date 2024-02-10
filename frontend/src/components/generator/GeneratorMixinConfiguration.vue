<template>
    <div class="w-full flex flex-col gap-4">
        <div>
            <generator-description>
                {{ $t("generator.mixins.config.description.1") }}
            </generator-description>
            <generator-description v-if="hasFabric()">
                {{ "\n" + $t("generator.mixins.config.description.fabric") }}
            </generator-description>
            <br><br>
            <generator-description>
                {{ $t("generator.mixins.config.description.2") }}
            </generator-description>
        </div>
        <GeneratorOptions :size="2">
            <template #slot-0>
                <GeneratorOptionEntry :selected="mixin === true" @click="selectMixin(true)">
                     <span class="flex flex-col text-left">
                        <span class="font-semibold">Enable Mixins</span>
                    </span>
                </GeneratorOptionEntry>
            </template>
            <template #slot-1>
                <GeneratorOptionEntry :selected="mixin === false" @click="selectMixin(false)">
                     <span class="flex flex-col text-left">
                        <span class="font-semibold">Disable Mixins</span>
                    </span>
                </GeneratorOptionEntry>
            </template>
        </GeneratorOptions>
    </div>
</template>

<script lang="ts">
import {mapWritableState} from "pinia"
import {useGeneratorStore} from "../../app/generator-store"
import GeneratorOptions from "./GeneratorOptions.vue"
import GeneratorOptionEntry from "./GeneratorOptionEntry.vue"
import {defineComponent} from "vue"
import GeneratorDescription from "./GeneratorDescription.vue"

export default defineComponent({
    name: "GeneratorMixinConfiguration",
    components: {GeneratorDescription, GeneratorOptionEntry, GeneratorOptions},
    computed: {
        ...mapWritableState(useGeneratorStore, ["template", "mixin", "modLoaders"]),
    },
    methods: {
        hasFabric(): boolean {
            return this.template === "fabric" || (this.template === "architectury" || this.template === "multiloader" && this.modLoaders.includes("fabric"))
        },
        selectMixin(mixin: boolean) {
            if (this.mixin === mixin) {
                this.mixin = null
            } else {
                this.mixin = mixin
            }
        },
    },
})
</script>

<style scoped>
</style>