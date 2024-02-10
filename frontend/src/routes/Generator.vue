<template>
    <PageWidthLimiter>
        <div class="flex flex-col gap-6">
            <NewsProjectGenerator class="mt-2"/>
            <span class="font-semibold text-xl mb-[-.5rem]">Create a new project...</span>
            <GeneratorStepper :steps="steps()" :step="step">
                <template v-for="i in steps()" #[`title-${i-1}`]>
                    <div class="flex h-full items-center font-medium text-md sm:text-lg" :ref="`title-${i-1}`">{{ stepEntries[i - 1].title }}</div>
                </template>
                <template v-for="i in steps()" #[`content-${i-1}`]>
                    <component :is="stepEntries[i - 1].content()"/>
                </template>
            </GeneratorStepper>
        </div>
    </PageWidthLimiter>
</template>

<script lang="ts">
import {Component, defineComponent, h} from "vue"
import PageWidthLimiter from "../components/PageWidthLimiter.vue"
import Block from "../components/Block.vue"
import GeneratorStepper from "../components/generator/GeneratorStepper.vue"
import GeneratorTemplateBase from "../components/generator/GeneratorTemplateBase.vue"
import {useGeneratorStore} from "../app/generator-store"
import {VNode} from "@vue/runtime-core"
import {mapWritableState} from "pinia"
import GeneratorBuildSystem from "../components/generator/GeneratorBuildSystem.vue"
import GeneratorModLoaders from "../components/generator/GeneratorModLoaders.vue"
import GeneratorModConfiguration from "../components/generator/GeneratorModConfiguration.vue"
import GeneratorMixinConfiguration from "../components/generator/GeneratorMixinConfiguration.vue"
import GeneratorDummyDependencies from "../components/generator/dependencies/GeneratorDummyDependencies.vue"
import GeneratorFabricDependencies from "../components/generator/dependencies/GeneratorFabricDependencies.vue"
import GeneratorCommonDependencies from "../components/generator/dependencies/GeneratorCommonDependencies.vue"
import GeneratorArchitecturyDependencies from "../components/generator/dependencies/GeneratorArchitecturyDependencies.vue"
import GeneratorGenerate from "../components/generator/GeneratorGenerate.vue"
import NewsProjectGenerator from "../components/NewsProjectGenerator.vue"
import GeneratorVersions from "../components/generator/GeneratorVersions.vue"

interface StepEntry {
    title: string,
    content: () => VNode,
}

export default defineComponent({
    name: "Generator",
    components: {NewsProjectGenerator, GeneratorTemplateBase, GeneratorStepper, Block, PageWidthLimiter},
    data() {
        return {
            selected: null as string | null,
        }
    },
    watch: {
        step: {
            handler(newValue, oldValue) {
                this.$nextTick(() => {
                    const destValue = newValue > oldValue ? oldValue + 1 : newValue
                    const element = this.$refs[`title-${destValue - 1}`] as HTMLElement
                    const offset = 78
                    const bodyRect = document.body.getBoundingClientRect().top
                    const elementRect = element.getBoundingClientRect().top
                    const elementPosition = elementRect - bodyRect
                    const offsetPosition = elementPosition - offset

                    window.scrollTo({
                        top: offsetPosition,
                        behavior: "smooth",
                    })
                })
            },
        },
    },
    computed: {
        ...mapWritableState(useGeneratorStore, ["template", "modLoaders", "mappings", "gradleLanguage", "gradleSystem", "mixin", "minecraftVersion"]),
        step() {
            let step = 1
            if (this.template === null) return step
            step++
            if (this.template === "architectury" || this.template === "multiloader") {
                if (this.modLoaders.length === 0) return step
                step++
            }
            if (this.minecraftVersion === null) return step
            step++
            if (this.gradleLanguage === null) return step
            if (((this.template !== "forge" && this.template !== "multiloader") || this.gradleSystem !== "fg") && this.mappings === null) return step
            step++
            step++
            if (this.mixin === null) return step
            step++
            step++
            return step
        },
        stepEntries(): StepEntry[] {
            let titles = []
            titles.push({
                title: this.$t("generator.template.base.title"),
                content: () => h(GeneratorTemplateBase),
            })
            if (this.template === "architectury" || this.template === "multiloader") {
                titles.push({
                    title: this.$t("generator.mod.loaders.title"),
                    content: () => h(GeneratorModLoaders),
                })
            }
            titles.push({
                title: this.$t("generator.versions.title"),
                content: () => h(GeneratorVersions),
            })
            titles.push({
                title: this.$t("generator.build.system.title"),
                content: () => h(GeneratorBuildSystem),
            })
            titles.push({
                title: this.$t("generator.mod.config.title"),
                content: () => h(GeneratorModConfiguration),
            })
            titles.push({
                title: this.$t("generator.mixins.config.title"),
                content: () => h(GeneratorMixinConfiguration),
            })
            let dependencies: Component
            switch (this.template) {
                case "fabric":
                    dependencies = GeneratorFabricDependencies
                    break
                case "architectury":
                    dependencies = GeneratorArchitecturyDependencies
                    break
                default:
                    dependencies = GeneratorDummyDependencies
                    break
            }
            titles.push({
                title: this.$t("generator.dependencies.title"),
                content: () => {
                    if (this.template === "forge") return h(GeneratorCommonDependencies)
                    const v = h("div", [h(dependencies), h(GeneratorCommonDependencies)])
                    v.props = {class: "flex flex-col gap-4"}
                    return v
                },
            })
            titles.push({
                title: "Generate",
                content: () => h(GeneratorGenerate),
            })

            return titles
        },
    },
    methods: {
        steps() {
            return this.stepEntries.length
        },
    },
})
</script>

<style scoped>
</style>