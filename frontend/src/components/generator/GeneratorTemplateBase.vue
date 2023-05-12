<template>
    <div class="w-full flex flex-col gap-4">
        <generator-description>
            {{ $t("generator.template.base.description") }}
        </generator-description>
        <GeneratorOptions :size="5">
            <template #slot-0>
                <GeneratorOptionEntry :selected="template === 'fabric'" @click="selectTemplate('fabric')">
                    <span class="flex flex-col text-left">
                        <span class="font-semibold">Fabric</span>
                        <span class="text-sm">for 1.14 to latest</span>
                        <a href="https://www.fabricmc.net/" class="text-sm font-semibold underline" tabindex="-1">fabricmc.net</a>
                    </span>
                    <span class="flex flex-col text-left">
                        <span class="font-semibold text-sm">{{ $t("generator.template.base.details.ide") }}</span>
                        <span class="text-sm">✔ IntelliJ, VSCode, Eclipse</span>
                    </span>
                    <span class="flex flex-col text-left">
                        <span class="font-semibold text-sm">{{ $t("generator.template.base.details.features") }}</span>
                        <span class="text-sm">✔ Comprehensive API</span>
                        <span class="text-sm">✔ Supports Groovy / Kotlin Gradle</span>
                        <span class="text-sm">✔ Supports Kotlin Language <span class="whitespace-nowrap">(via 1st party external library)</span></span>
                        <span class="text-sm">✔ Supports Mixin</span>
                        <span class="text-sm">✔ Only uses Official Gradle Plugins</span>
                        <span class="text-sm">✔ Trivial Data Generators</span>
                        <span class="text-sm">✔ Supports Split Client & Common Sources</span>
                    </span>
                </GeneratorOptionEntry>
            </template>
            <template #slot-1>
                <GeneratorOptionEntry :selected="template === 'forge'" @click="selectTemplate('forge')">
                    <span class="flex flex-col text-left">
                        <span class="font-semibold">Forge</span>
                        <span class="text-sm">for 1.13 to latest</span>
                        <a href="https://files.minecraftforge.net/" class="text-sm font-semibold underline" tabindex="-1">minecraftforge.net</a>
                    </span>
                    <span class="flex flex-col text-left">
                        <span class="font-semibold text-sm">{{ $t("generator.template.base.details.ide") }}</span>
                        <span class="text-sm">✔ IntelliJ, VSCode, Eclipse</span>
                    </span>
                    <span class="flex flex-col text-left">
                        <span class="font-semibold text-sm">{{ $t("generator.template.base.details.features") }}</span>
                        <span class="text-sm">✔ Comprehensive API</span>
                        <span class="text-sm">✔ Supports Groovy / Kotlin Gradle</span>
                        <span class="text-sm">✔ Supports Kotlin Language <span class="whitespace-nowrap">(via 3rd party external library)</span></span>
                        <span class="text-sm">✔ Supports Mixin</span>
                        <span class="text-sm">✔ Only uses Official Gradle Plugins</span>
                        <span class="text-sm">✔ Trivial Data Generators</span>
                        <span class="text-sm">✘ Supports Split Client & Common Sources</span>
                    </span>
                </GeneratorOptionEntry>
            </template>
            <template #slot-2>
<!--                <GeneratorOptionEntry :selected="template === 'quilt'" @click="selectTemplate('quilt')">-->
<!--                    <span class="flex flex-col text-left">-->
<!--                        <span class="font-semibold">Quilt (Beta)</span>-->
<!--                        <span class="text-sm">for 1.18 to latest</span>-->
<!--                        <a href="https://quiltmc.org/" class="text-sm font-semibold underline" tabindex="-1">quiltmc.org</a>-->
<!--                    </span>-->
<!--                    <span class="flex flex-col text-left">-->
<!--                        <span class="font-semibold text-sm">{{ $t("generator.template.base.details.ide") }}</span>-->
<!--                        <span class="text-sm">✔ IntelliJ, VSCode, Eclipse</span>-->
<!--                    </span>-->
<!--                    <span class="flex flex-col text-left">-->
<!--                        <span class="font-semibold text-sm">{{ $t("generator.template.base.details.features") }}</span>-->
<!--                        <span class="text-sm">✔ Comprehensive API</span>-->
<!--                        <span class="text-sm">✔ Supports Groovy / Kotlin Gradle</span>-->
<!--                        <span class="text-sm">✔ Supports Kotlin Language <span class="whitespace-nowrap">(via 3rd party external library)</span></span>-->
<!--                        <span class="text-sm">✔ Supports Mixin</span>-->
<!--                        <span class="text-sm">✔ Only uses Official Gradle Plugins</span>-->
<!--                        <span class="text-sm">✔ Trivial Data Generators</span>-->
<!--                        <span class="text-sm">✘ Supports Split Client & Common Sources</span>-->
<!--                    </span>-->
<!--                </GeneratorOptionEntry>-->
            </template>
            <template #slot-3>
                <GeneratorOptionEntry :selected="template === 'architectury'" @click="selectTemplate('architectury')">
                    <span class="flex flex-col text-left">
                        <span class="font-semibold">Architectury</span>
                        <span class="text-sm">for 1.16 to latest</span>
                    </span>
                    <span class="flex flex-col text-left w-full">
                        <span class="font-semibold text-sm">{{ $t("generator.template.base.details.ide") }}</span>
                        <span class="text-sm w-full grid grid-cols-2 gap-2"><span>✔ IntelliJ, VSCode</span><span>✘ Eclipse</span></span>
                    </span>
                    <span class="flex flex-col text-left">
                        <span class="font-semibold text-sm">{{ $t("generator.template.base.details.features") }}</span>
                        <span class="text-sm">✔ Comprehensive API <span class="whitespace-nowrap">(via optional external library)</span></span>
                        <span class="text-sm">✔ Supports Groovy / Kotlin Gradle</span>
                        <span class="text-sm">✔ Supports Kotlin Language <span class="whitespace-nowrap">(via 3rd party external library)</span></span>
                        <span class="text-sm">✔ Supports Mixin</span>
                        <span class="text-sm">✘ Only uses Official Gradle Plugins <span class="whitespace-nowrap">(Architectury Loom used)</span></span>
                        <span class="text-sm">✘ Trivial Data Generators <span class="whitespace-nowrap">(only via Fabric / Forge setups)</span></span>
                        <span class="text-sm">✘ Supports Split Client & Common Sources</span>
                    </span>
                </GeneratorOptionEntry>
            </template>
            <template #slot-4>
<!--                <GeneratorOptionEntry :selected="template === 'multiloader'" @click="selectTemplate('multiloader')">-->
<!--                    <span class="flex flex-col text-left">-->
<!--                        <span class="font-semibold">Jared's MultiLoader</span>-->
<!--                        <span class="text-sm">for 1.17 to latest</span>-->
<!--                    </span>-->
<!--                    <span class="flex flex-col text-left">-->
<!--                        <span class="font-semibold text-sm">{{ $t("generator.template.base.details.ide") }}</span>-->
<!--                        <span class="text-sm w-full grid grid-cols-2 gap-2"><span>✔ IntelliJ, VSCode</span><span>✘ Eclipse</span></span>-->
<!--                    </span>-->
<!--                    <span class="flex flex-col text-left">-->
<!--                        <span class="font-semibold text-sm">{{ $t("generator.template.base.details.features") }}</span>-->
<!--                        <span class="text-sm">✘ Comprehensive API (common libraries do exist but not widely used)</span>-->
<!--                        <span class="text-sm">✔ Supports Groovy / Kotlin Gradle</span>-->
<!--                        <span class="text-sm">✔ Supports Kotlin Language <span class="whitespace-nowrap">(via 3rd party external library)</span></span>-->
<!--                        <span class="text-sm">✔ Supports Mixin</span>-->
<!--                        <span class="text-sm">✔ Only uses Official Gradle Plugins</span>-->
<!--                        <span class="text-sm">✘ Trivial Data Generators <span class="whitespace-nowrap">(only via Fabric / Forge setups)</span></span>-->
<!--                        <span class="text-sm">✘ Supports Split Client & Common Sources</span>-->
<!--                    </span>-->
<!--                </GeneratorOptionEntry>-->
            </template>
        </GeneratorOptions>
    </div>
</template>

<script lang="ts">
import {mapWritableState} from "pinia"
import {useGeneratorStore, afterSelectTemplate} from "../../app/generator-store"
import GeneratorOptions from "./GeneratorOptions.vue"
import GeneratorOptionEntry from "./GeneratorOptionEntry.vue"
import {defineComponent} from "vue"
import GeneratorDescription from "./GeneratorDescription.vue"

export default defineComponent({
    name: "GeneratorTemplateBase",
    components: {GeneratorDescription, GeneratorOptionEntry, GeneratorOptions},
    computed: {
        ...mapWritableState(useGeneratorStore, ["template"]),
    },
    methods: {
        selectTemplate(template: string) {
            if (this.template === template) {
                this.template = null
            } else {
                this.template = template
            }
            afterSelectTemplate()
        },
    },
})
</script>

<style scoped>

</style>