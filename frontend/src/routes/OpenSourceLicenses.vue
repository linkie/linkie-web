<template>
    <div class="pt-20"/>
    <PageWidthLimiter v-if="entries.length !== 0">
        <PageSidebar>
            <Block>
                <SubHeader>Table of Contents</SubHeader>
                <ol class="list-decimal pl-6 mt-2">
                    <li v-for="entry in entries">
                        <a :href="'#oss-' + entries.indexOf(entry)">{{ entry.name }}</a>
                    </li>
                </ol>
            </Block>
        </PageSidebar>

        <PageContent class="flex flex-col gap-y-6">
            <Block v-for="entry in entries" :title="entry.name" :id="'oss-' + entries.indexOf(entry)">
                <Header>{{ entry.name }}</Header>
                <span class="whitespace-pre-line">
                    {{ entry.license }}
                </span>
                <br/>
                <br/>
                <a class="underline" :href="entry.link">More about {{ entry.name }}</a>
            </Block>
        </PageContent>
    </PageWidthLimiter>

    <div v-else class="text-center h-[calc(100vh-56px-24px-5rem)] items-center justify-center grid">
        <div class="flex gap-4 items-center justify-center animate-pulse animate-bounce">
            <svg xmlns="http://www.w3.org/2000/svg" class="animate-spin" width="24" height="24" viewBox="0 0 24 24" stroke-width="2" stroke="currentColor"
                 fill="none" stroke-linecap="round" stroke-linejoin="round">
                <path stroke="none" d="M0 0h24v24H0z" fill="none"></path>
                <path d="M10 17a3 3 0 1 1 -1.543 -2.623l2.087 -3.754a3 3 0 0 1 1.456 -5.623a3 3 0 0 1 1.457 5.623l2.087 3.754a3 3 0 1 1 -1.538 2.8l-.006 -.177h-4z"></path>
                <path d="M17 17v.01"></path>
                <path d="M7 17v.01"></path>
                <path d="M12 8v.01"></path>
            </svg>
            <p class="font-medium text-xl">Loading...</p>
        </div>
    </div>
</template>

<script lang="ts">
import {reqOss} from "../app/backend"
import Block from "../components/Block.vue"
import SubHeader from "../components/dependencies/SubHeader.vue"
import {defineComponent} from "vue"
import {addAlert} from "../app/alerts"
import PageWidthLimiter from "../components/PageWidthLimiter.vue"
import PageSidebar from "../components/PageSidebar.vue"
import PageContent from "../components/PageContent.vue"
import Header from "../components/dependencies/Header.vue"

interface OssEntry {
    name: string,
    link: string,
    license: string,
}

export default defineComponent({
    name: "Dependencies",
    components: {Header, PageContent, PageSidebar, PageWidthLimiter, SubHeader, Block},
    data() {
        return {
            entries: [] as OssEntry[],
        }
    },
    mounted() {
        reqOss().then(value => {
            this.entries = value.data
        }).catch(reason => {
            addAlert({
                type: "error",
                message: `Failed to fetch entries: ${reason.message}`,
            })
        })
    },
})
</script>

<style scoped>
</style>