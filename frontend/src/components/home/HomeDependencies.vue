<template>
    <div class="flex gap-x-4 gap-y-3 flex-wrap">
        <router-link class="px-4 py-2 cursor-pointer border-2 border-base-content dark:border-base-dark-content
        font-medium rounded-xl whitespace-nowrap hover:scale-110 transition-all"
                     v-for="version in versions.slice(0, 16)" :to="select(version[0], version[1])">{{ version[0] }} {{ version[1] }}
        </router-link>
    </div>
</template>

<script setup lang="ts">
import {computed, onMounted} from "vue"
import {ensureDependencyData, updateDependencyData, useDependenciesDataStore} from "../../app/dependencies-data"
import {storeToRefs} from "pinia"
import {fullPath} from "../../app/backend"
import {useRoute} from "vue-router"

const {searchData} = storeToRefs(useDependenciesDataStore())

const route = useRoute()

const versions = computed(() => {
    let versions = searchData.value.versions
    let list = Object.keys(versions).map(loader => versions[loader].filter(entry => entry.stable).map(entry => [loader, entry.version])).flat()
    list = list.map(entry => [entry[0] == "fabric" ? "Fabric" : entry[0] == "neoforge" ? "NeoForge" : "Forge", entry[1]])
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
})

function select(loader: string, version: string): string {
    return `/dependencies?loader=${loader.toLowerCase()}&version=${version}`
}

onMounted(() => {
    updateDependencyData(fullPath(route))
    ensureDependencyData(fullPath(route))
})
</script>

<style scoped>
</style>