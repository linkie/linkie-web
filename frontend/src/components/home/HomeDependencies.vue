<template>
    <div class="flex gap-x-4 gap-y-3 flex-wrap">
        <a class="px-4 py-2 cursor-pointer border-2 border-base-content font-medium rounded-xl whitespace-nowrap hover:scale-110 transition-all" 
               v-for="version in versions.slice(0, 16)" :href="select(version[0], version[1])">{{ version[0] }} {{ version[1] }}</a>
    </div>
</template>

<script lang="ts">
import {defineComponent} from "vue"
import Block from "../Block.vue"
import {ensureDependencyData, updateDependencyData, useDependenciesDataStore} from "../../app/dependencies-data"
import {mapState} from "pinia"

export default defineComponent({
    name: "HomeDependencies",
    components: {Block},
    methods: {
        select(loader: string, version: string): string {
            return `/dependencies?loader=${loader.toLowerCase()}&version=${version}`
        },
    },
    computed: {
        versions() {
            let versions = useDependenciesDataStore().searchData.versions
            let list = Object.keys(versions).map(loader => versions[loader].filter(entry => entry.stable).map(entry => [loader, entry.version])).flat()
            list = list.map(entry => [entry[0] == "fabric" ? "Fabric" : "Forge", entry[1]])
            list.sort((b, a) => {
                let aVersion = a[1]
                let bVersion = b[1]
                if (aVersion === bVersion) return 0
                let aSplit = aVersion.split(".")
                let bSplit = bVersion.split(".")
                for (let i = 0; i < aSplit.length; i++) {
                    if (bSplit.length <= i) return 1
                    let aNum = parseInt(aSplit[i])
                    let bNum = parseInt(bSplit[i])
                    if (aNum === bNum) continue
                    return aNum - bNum
                }
                return -1
            })
            return list
        },
        ...mapState(useDependenciesDataStore, ["searchData"]),
    },
    mounted() {
        updateDependencyData()
        ensureDependencyData()
    },
})
</script>

<style scoped>
</style>