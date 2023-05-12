<template>
    <div class="w-full flex flex-col gap-4">
        <generator-description>
            {{ $t("generator.mod.config.description") }}
        </generator-description>
        <div class="w-full sm:w-[60%] grid sm:input-grid grid-cols-1 sm:grid-cols-2 gap-x-3 gap-y-1.5 items-center min-w-10 max-w-full">
            <label for="mod-name"> {{ $t("generator.mod.config.mod.name") }} </label>
            <input type="text" v-model="name" id="mod-name"
                   class="text-field"/>
            
            <label for="mod-description"> {{ $t("generator.mod.config.mod.description") }} </label>
            <input type="text" v-model="description" id="mod-description"
                   class="text-field"/>
            
            <label for="mod-license"> {{ $t("generator.mod.config.mod.license") }} </label>
            <input type="text" v-model="license" id="mod-license"
                   class="text-field"/>

            <label for="main-class"> {{ $t("generator.mod.config.mod.main.class") }} </label>
            <div class="flex gap-1.5">
                <input type="text" :disabled="!mainClass" :value="mainClass ?? generateMainClassName()" id="main-class"
                       @change="mainClass = ($event.target as any).value"
                       class="text-field flex-1 min-w-0"/>
                <button class="lock" @click="mainClass = !mainClass ? generateMainClassName() : null">
                    <IconLock v-if="!mainClass"/>
                    <IconLockOpen v-else/>
                </button>
            </div>

            <label for="client-class"> {{ $t("generator.mod.config.mod.client.class") }} </label>
            <div class="flex gap-1.5">
                <input type="text" :disabled="!clientClass" :value="clientClass ?? generateClientClassName()" id="client-class"
                       @change="clientClass = ($event.target as any).value"
                       class="text-field flex-1 min-w-0"/>
                <button class="lock" @click="clientClass = !clientClass ? generateClientClassName() : null">
                    <IconLock v-if="!clientClass"/>
                    <IconLockOpen v-else/>
                </button>
            </div>

            <label for="mod-id"> {{ $t("generator.mod.config.mod.id") }} </label>
            <div class="flex gap-1.5">
                <input type="text" :disabled="!modId" :value="modId ?? generateModID()" id="mod-id"
                       @change="modId = ($event.target as any).value"
                       class="text-field flex-1 min-w-0"/>
                <button class="lock" @click="modId = !modId ? generateModID() : null">
                    <IconLock v-if="!modId"/>
                    <IconLockOpen v-else/>
                </button>
            </div>

            <label for="mod-version"> {{ $t("generator.mod.config.mod.version") }} </label>
            <input type="text" v-model="version" id="mod-version"
                   class="text-field"/>

            <label for="mod-package"> {{ $t("generator.mod.config.mod.package") }} </label>
            <input type="text" v-model="package" id="mod-package"
                   class="text-field"/>

            <span> {{ $t("generator.mod.config.mod.authors") }} </span>
            <button class="ml-auto w-9 h-9 bg-base-600 dark:bg-base-dark-500 bg-opacity-50 hover:bg-opacity-100 transition-all rounded-full font-bold"
                    @click="authors.push('')">+
            </button>
            <div class="w-0"/>
            <div class="flex flex-col gap-1.5">
                <div v-for="i in authors.length" class="flex gap-2">
                    <input type="text" v-model="authors[i - 1]"
                           class="flex-1 text-field min-w-0"/>
                    <button class="ml-auto w-9 h-9 bg-base-600 dark:bg-base-dark-500 bg-opacity-50 hover:bg-opacity-100 transition-all rounded-full font-bold flex-none"
                            @click="authors.splice(i - 1, 1)">-
                    </button>
                </div>
            </div>
        </div>
    </div>
</template>

<script lang="ts">
import {mapWritableState} from "pinia"
import {generateClientClassName, generateMainClassName, generateModID, useGeneratorStore} from "../../app/generator-store"
import GeneratorOptions from "./GeneratorOptions.vue"
import GeneratorOptionEntry from "./GeneratorOptionEntry.vue"
import {IconLock, IconLockOpen} from "@tabler/icons-vue"
import {defineComponent} from "vue"
import GeneratorDescription from "./GeneratorDescription.vue"

export default defineComponent({
    name: "GenertatorModConfiguration",
    components: {GeneratorDescription, GeneratorOptionEntry, GeneratorOptions, IconLock, IconLockOpen},
    computed: {
        ...mapWritableState(useGeneratorStore, ["name", "description", "license", "modId", "version", "mainClass", "clientClass", "package", "authors"]),
    },
    methods: {generateModID, generateMainClassName, generateClientClassName},
})
</script>

<style scoped>
.input-grid {
    grid-template-columns: auto 1fr;
}

.lock {
    @apply grid items-center justify-center transition-all flex-none;
    @apply w-9 h-9 bg-base-400 dark:bg-base-dark-300 bg-opacity-50 hover:bg-opacity-100 dark:hover:bg-base-dark-400 rounded;
}

.text-field {
    @apply h-9 bg-base-400 bg-opacity-50 hover:bg-opacity-100 transition-all p-3 rounded outline-0 border-0 text-base-dark-300;
    @apply dark:bg-base-dark-300 dark:hover:bg-base-dark-400 dark:text-base-dark-content;

    @apply disabled:bg-base-500 disabled:dark:bg-base-dark-200 disabled:dark:bg-opacity-80;
    @apply disabled:text-base-dark-800 disabled:dark:text-base-dark-900;
    @apply disabled:italic;
}

.text-field:focus {
    @apply ring-2 ring-base-dark-300 ring-offset-1;
    @apply dark:ring-base-dark-content;
}

</style>