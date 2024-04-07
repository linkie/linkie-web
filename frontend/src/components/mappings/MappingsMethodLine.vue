<template>
    <div class="my-1 text-sm whitespace-nowrap flex items-center">
        <div class="mr-2 relative" v-if="!!entry.argsGuessed || !!entry.argsParchment">
            <IconAlertTriangleFilled v-if="!!entry.argsGuessed" class="peer" :size="20"/>
            <IconAlertCircleFilled v-else-if="!!entry.argsParchment" class="peer" :size="20"/>
            <Tooltip>
                <div v-if="!!entry.argsGuessed" class="flex flex-col gap-1">
                    <div class="flex items-center">
                        <IconAlertTriangle class="mr-1.5"/>
                        <span class="font-semibold">Warning</span>
                    </div>
                    The method argument mappings are aggregated from other mapping projects,<br>
                    and may not be accurate or fit the current context.<br><br>
                    This may be the result of the lack of a proper source for this mappings or this version.
                </div>
                <div v-else-if="!!entry.argsParchment" class="flex flex-col gap-1">
                    <div class="flex items-center">
                        <IconAlertCircle class="mr-1.5"/>
                        <span class="font-semibold">Info</span>
                    </div>
                    The method argument mappings are provided from a community project, ParchmentMC, <br>
                    and may not be accurate, or may be subject to a different license.
                </div>
            </Tooltip>
        </div>
        <div class="font-mono overflow-x-auto epic-scroller">
            <span class="font-bold">{{ methodReturnType(entry) }}</span>
            {{ onlyClass(getOptimumName(entry)) }}(<!---->
            <AutoBold class="[&_.bold]:font-bold">{{ methodArgs(entry) }}</AutoBold><!--
                -->)
        </div>
    </div>
</template>

<script setup lang="ts">
import Tooltip from "../Tooltip.vue"
import AutoBold from "./AutoBold.vue"
import {MappingEntry} from "../../app/mappings-data"
import {IconAlertCircle, IconAlertCircleFilled, IconAlertTriangle, IconAlertTriangleFilled} from "@tabler/icons-vue"

defineProps<{
    entry: MappingEntry,
    methodReturnType: (entry: MappingEntry) => string,
    methodArgs: (entry: MappingEntry) => string,
    getOptimumName: (entry: MappingEntry) => string,
    onlyClass: (name: string) => string,
}>()
</script>

<style scoped>
</style>