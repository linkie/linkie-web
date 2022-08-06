<template>
    <p class="mt-1" v-if="title != undefined">{{ title }}</p>
    <div class="rounded-lg bg-base-300 p-3 text-sm mt-2 mb-1">
        <div class="flex">
            <div class="flex-1">
                <CodeLanguageTab v-for="language in languages" :language="language"/>
            </div>
        </div>
        <pre class="pl-2 pb-1 overflow-x-auto"><code class="inline-block break-all whitespace-pre" id="code-block"><slot/></code></pre>
    </div>
</template>

<script lang="ts">
import CodeLanguageTab from "./tab/CodeLanguageTab.vue"
import {languages} from "../../app/preference-store"
import {defineComponent} from "vue"

export default defineComponent({
    name: "CodeBlock",
    components: {CodeLanguageTab},
    data() {
        return {
            languages,
        }
    },
    props: {
        title: String,
    },
})
</script>

<style scoped>
</style>