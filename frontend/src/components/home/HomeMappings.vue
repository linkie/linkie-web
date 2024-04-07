<template>
    <div class="flex flex-col gap-4">
        <div v-for="vers in versions">
            <div class="font-bold text-xl mb-2">{{ namespaceLocalizations[vers[0][0]] ?? vers[0][0] }}</div>
            <div class="flex gap-x-4 gap-y-3 flex-wrap">
                <router-link class="px-4 py-2 cursor-pointer border-2 border-base-content dark:border-base-dark-content
                 font-medium rounded-xl whitespace-nowrap hover:scale-110 transition-all"
                             v-for="version in vers.slice(0, 16)" :to="select(version[0], version[1])">{{ version[1] }}
                </router-link>
            </div>
        </div>
    </div>
</template>

<script setup lang="ts">
import {computed, onMounted} from "vue"
import {useMappingsDataStore} from "../../app/mappings-data"
import {storeToRefs} from "pinia"
import {fullPath, namespaceLocalizations} from "../../app/backend"
import {ensureDependencyData, updateDependencyData} from "../../app/dependencies-data"
import {useRoute} from "vue-router"

const {mappingsData} = storeToRefs(useMappingsDataStore())

const route = useRoute()

const versions = computed(() => {
    let namespaces = mappingsData.value.namespaces
    .filter(namespace => ["yarn", "mojang", "mojang_srg"].includes(namespace.id))
    return namespaces.map(namespace => {
        let l = namespace.versions.filter(entry => entry.stable).map(entry => [namespace.id, entry.version])
        l.sort((b, a) => {
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
        return l
    })
})

function select(namespace: string, version: string): string {
    return `/mappings?namespace=${namespace.toLowerCase()}&version=${version}`
}

onMounted(() => {
    updateDependencyData(fullPath(route))
    ensureDependencyData(fullPath(route))
})
</script>

<style scoped>
</style>