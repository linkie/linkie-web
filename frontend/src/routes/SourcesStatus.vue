<template>
    <PageWidthLimiter v-if="mappingsData.namespaces.length !== 0">
        <PageSidebar>
            <Block>
                <NamespaceFilterBlock :ns="namespace ?? undefined" :set-ns="(ns: string | null) => namespace = ns"
                                      :snapshots="snapshots"
                                      :set-snapshots="(ss: boolean) => snapshots = ss"/>
            </Block>
        </PageSidebar>
        <PageContent>
            <div v-if="!!current" class="text-xl font-bold">{{ current }}</div>
            <div v-if="!!next">
                <div class="mb-4">{{ next }}</div>
                Queue:
                <ul class="list-inside list-disc">
                    <li v-for="ni in nextList">
                        {{ ni }}
                    </li>
                </ul>
            </div>
            <div v-if="!!data">
                <div v-for="item in filteredData">
                    <div class="mb-2">
                        <SubHeader>{{ item[0] }}</SubHeader>
                        Cached: {{ item[1].curr }} out of {{ item[1].total }}<br>
                        Ever cached: {{ item[1].everCached }}
                    </div>
                </div>
            </div>
        </PageContent>
    </PageWidthLimiter>

    <div v-else class="text-center h-[calc(100vh-56px-24px-5rem)] items-center justify-center grid">
        <div class="flex gap-4 items-center justify-center animate-pulse animate-bounce">
            <svg xmlns="http://www.w3.org/2000/svg" class="animate-spin" width="24" height="24" viewBox="0 0 24 24"
                 stroke-width="2" stroke="currentColor"
                 fill="none" stroke-linecap="round" stroke-linejoin="round">
                <path stroke="none" d="M0 0h24v24H0z" fill="none"></path>
                <path
                    d="M10 17a3 3 0 1 1 -1.543 -2.623l2.087 -3.754a3 3 0 0 1 1.456 -5.623a3 3 0 0 1 1.457 5.623l2.087 3.754a3 3 0 1 1 -1.538 2.8l-.006 -.177h-4z"></path>
                <path d="M17 17v.01"></path>
                <path d="M7 17v.01"></path>
                <path d="M12 8v.01"></path>
            </svg>
            <p class="font-medium text-xl">Loading...</p>
        </div>
    </div>
</template>

<script setup lang="ts">
import Block from "../components/Block.vue"
import SubHeader from "../components/dependencies/SubHeader.vue"
import {computed, onMounted, ref, watch} from "vue"
import PageWidthLimiter from "../components/PageWidthLimiter.vue"
import PageSidebar from "../components/PageSidebar.vue"
import PageContent from "../components/PageContent.vue"
import {ensureMappingsData, updateMappingsData, useMappingsDataStore} from "../app/mappings-data"
import {storeToRefs} from "pinia"
import NamespaceFilterBlock from "../components/statuses/NamespaceFilterBlock.vue"
import {fullPath, reqStatusSource} from "../app/backend"
import {useRoute} from "vue-router"

interface Cache {
    stable: boolean,
    curr: number,
    total: number,
    everCached: boolean,
}

const namespace = ref<string | null>(null)
const snapshots = ref(false)
const data = ref<{ [version: string]: Cache }>({})
const current = ref<string | null>(null)
const next = ref<string | null>(null)
const nextList = ref<string[]>([])

const { mappingsData } = storeToRefs(useMappingsDataStore())

const filteredData = computed(() => {
    return Object.entries(data.value).filter(([version, cache]) => snapshots.value || cache.stable)
})

const route = useRoute()

watch(namespace, (newValue) => {
    reqStatusSource("current").then(value => {
        if (value.data.namespace !== "null") {
            current.value = `Currently remapping ${value.data.namespace} ${value.data.version}`
        } else {
            current.value = null
        }
    })
    reqStatusSource(newValue ?? "next").then(value => {
        if (namespace.value === newValue) {
            next.value = null
            nextList.value = []
            if (!newValue) {
                data.value = {}
                let nextJson = value.data.next
                if (nextJson.namespace !== "null") {
                    next.value = `Next in line for remapper daemon is ${nextJson.namespace} ${nextJson.version}`
                } else {
                    next.value = "No namespaces in queue"
                }
                nextList.value = value.data.list.map((obj: {
                    namespace: string,
                    version: string
                }) => `${obj.namespace} ${obj.version}`)
            } else {
                data.value = value.data as unknown as { [version: string]: Cache }
            }
        }
    })
})

onMounted(() => {
    updateMappingsData(fullPath(route))
    ensureMappingsData(fullPath(route))
})
</script>

<style scoped>
</style>