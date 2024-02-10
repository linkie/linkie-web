<template>
    <div class="w-full flex flex-col gap-4">
        <generator-description v-if="canChoosePlugin()">
            {{ $t("generator.build.system.description.forge") }}
        </generator-description>
        <GeneratorOptions :size="2" v-if="canChoosePlugin()">
            <template #slot-0>
                <GeneratorOptionEntry :selected="gradleSystem === 'fg'" @click="gradleSystem = 'fg'">
                    <span class="flex flex-col text-left">
                        <span class="font-semibold">ForgeGradle</span>
                    </span>
                    <span class="flex flex-col text-left">
                        <span class="text-sm">✔ Used by the majority of Forge mods</span>
                        <span class="text-sm">✔ Only supports MCP / Mojang Mappings</span>
                        <span class="text-sm">✔ Officially supported by Forge</span>
                    </span>
                </GeneratorOptionEntry>
            </template>
            <template #slot-1>
                <GeneratorOptionEntry :selected="gradleSystem === 'loom'" @click="gradleSystem = 'loom'">
                    <span class="flex flex-col text-left">
                        <span class="font-semibold">Architectury Loom</span>
                    </span>
                    <span class="flex flex-col text-left">
                        <span class="text-sm">✔ Built on Fabric Loom to support both Forge and Fabric</span>
                        <span class="text-sm">✔ Supports MCP / Mojang / Yarn Mappings</span>
                        <span class="text-sm">✘ Unofficial alternative, unsupported by Forge</span>
                    </span>
                </GeneratorOptionEntry>
            </template>
        </GeneratorOptions>
        <generator-description v-if="canChooseMappings()">
            {{ $t("generator.build.system.description.mappings") }}
        </generator-description>
        <GeneratorOptions :size="3" v-if="canChooseMappings()">
            <template #slot-0>
                <GeneratorOptionEntry :selected="mappings === 'mojmap'" @click="mappings = 'mojmap'">
                    <span class="flex flex-col text-left">
                        <span class="font-semibold">Official Mojang</span>
                    </span>
                    <span class="flex flex-col text-left">
                        <span class="text-sm">✔ Published officially by Mojang</span>
                        <span class="text-sm">✔ All classes, methods and fields are mapped</span>
                        <span class="text-sm">✔ Supported natively by both Fabric and Forge</span>
                        <span class="text-sm">✘ Parameters not provided by Mojang (optionally via community-sourced Parchment)</span>
                    </span>
                </GeneratorOptionEntry>
            </template>
            <template #slot-1>
                <GeneratorOptionEntry :selected="mappings === 'yarn'" @click="mappings = 'yarn'">
                    <span class="flex flex-col text-left">
                        <span class="font-semibold">Yarn</span>
                          <span class="text-sm">from FabricMC</span>
                    </span>
                    <span class="flex flex-col text-left">
                        <span class="text-sm">✔ Free and Open Source</span>
                        <span class="text-sm">✔ Subjectively Superior Names</span>
                        <span class="text-sm">✘ Not supported by Forge</span>
                    </span>
                </GeneratorOptionEntry>
            </template>
            <template #slot-2 v-if="template === 'quilt' || (isTemplateMultiPlatform() && !!modLoaders?.includes('quilt')) || mappings === 'quilt'">
                <GeneratorOptionEntry :selected="mappings === 'quilt'" @click="mappings = 'quilt'">
                    <span class="flex flex-col text-left">
                        <span class="font-semibold">Quilt Mappings</span>
                        <span class="text-sm">from QuiltMC</span>
                    </span>
                    <span class="flex flex-col text-left">
                        <span class="text-sm">✔ Free and Open Source</span>
                        <span class="text-sm">✔ Subjectively Superior Names</span>
                        <span class="text-sm">✘ Not supported by Forge</span>
                    </span>
                </GeneratorOptionEntry>
            </template>
        </GeneratorOptions>
        <transition-fade-in>
            <generator-description v-if="canChooseMappings() && mappings === 'mojmap'">
                {{ $t("generator.build.system.description.mappings.parchment") }}
            </generator-description>
            <div class="flex gap-3 items-center" v-if="canChooseMappings() && mappings === 'mojmap'">
                <input type="checkbox" id="parchment-toggle" v-model="parchment"/>
                <label for="parchment-toggle" class="select-none"> {{ $t("generator.build.system.description.mappings.parchment.toggle") }} </label>
            </div>
        </transition-fade-in>
        <generator-description v-if="canChooseSplitEnv()">
            {{ $t("generator.build.system.description.env.split") }}
        </generator-description>
        <div class="flex gap-3 items-center" v-if="canChooseSplitEnv()">
            <input type="checkbox" id="split-env-toggle" v-model="splitEnv"/>
            <label for="split-env-toggle" class="select-none">Enable Split Client and Common Environments</label>
        </div>
        <generator-description>
            {{ $t("generator.build.system.description.gradle.language") }}
        </generator-description>
        <GeneratorOptions :size="2">
            <template #slot-0>
                <GeneratorOptionEntry :selected="gradleLanguage === 'groovy'" @click="selectGradleLanguage('groovy')">
                    <span class="flex flex-col text-left">
                        <span class="font-semibold">Groovy</span>
                    </span>
                </GeneratorOptionEntry>
            </template>
            <template #slot-1>
                <GeneratorOptionEntry :selected="gradleLanguage === 'kts'" @click="selectGradleLanguage('kts')">
                    <span class="flex flex-col text-left">
                        <span class="font-semibold">Kotlin (KTS)</span>
                    </span>
                </GeneratorOptionEntry>
            </template>
        </GeneratorOptions>
    </div>
</template>

<script lang="ts">
import {mapWritableState} from "pinia"
import {isTemplateMultiPlatform, useGeneratorStore} from "../../app/generator-store"
import GeneratorOptions from "./GeneratorOptions.vue"
import GeneratorOptionEntry from "./GeneratorOptionEntry.vue"
import {defineComponent} from "vue"
import GeneratorDescription from "./GeneratorDescription.vue"
import TransitionFadeIn from "../TransitionFadeIn.vue"

export default defineComponent({
    name: "GeneratorBuildSystem",
    components: {TransitionFadeIn, GeneratorDescription, GeneratorOptionEntry, GeneratorOptions},
    computed: {
        ...mapWritableState(useGeneratorStore, ["template", "gradleSystem", "mappings", "parchment", "modLoaders", "gradleLanguage", "splitEnv"]),
    },
    methods: {
        canChoosePlugin() {
            return this.template === "forge"
        },
        canChooseSplitEnv() {
            return this.template === "fabric"
        },
        canChooseMappings() {
            return (this.template === "forge" && this.gradleSystem === "loom") || (this.template !== "forge" && this.template !== "multiloader")
        },
        selectGradleLanguage(language: ReturnType<typeof useGeneratorStore>["gradleLanguage"]) {
            if (this.gradleLanguage === language) {
                this.gradleLanguage = null
            } else {
                this.gradleLanguage = language
            }
        },
        isTemplateMultiPlatform,
    },
})
</script>

<style scoped>

</style>