<template>
    <div class="p-4 bg-base-400/50 dark:bg-base-dark-400/40 ring-base-500 dark:ring-base-dark-500 ring-offset-2 ring-2 rounded-lg flex flex-col">
        <span class="text-2xl font-bold mb-2">
            News: Project Generator
        </span>
        <generator-description>
            Linkie now has a project generator!
            <br/>It is tailored to be the best way to get started with Minecraft modding, with tons of different options,
            because everyone has different preferences.
            <br/><br/>You can create projects with *Fabric*, *Forge*, or *Architectury*, at the moment, with more options coming soon.
            <br/>You also get options for *Build Language* (Kotlin or Groovy), *Mixins*, *Build Plugins* (Loom or ForgeGradle), *Mappings* (Yarn or Official),
            Parchment, and more!
            <br/><br/>Try it out now! ⊂◉‿◉つ {{ (current === "/" ? "It is accessible on the navigation bar at the top of the page." : "") }}
        </generator-description>
    </div>
</template>

<script lang="ts">
import {defineComponent} from "vue"
import GeneratorDescription from "./generator/GeneratorDescription.vue"

export default defineComponent({
    name: "NewsProjectGenerator",
    components: {GeneratorDescription},
    data() {
        return {
            current: window.location.pathname as string,
        }
    },
})
</script>

<style scoped>

</style>