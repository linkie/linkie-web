<template>
    <div class="flex flex-col">
        <h1 class="pt-40 pb-28 text-center text-6xl font-bold title-home text-white"> {{ $t("home.welcome") }} </h1>

        <PageWidthLimiter class="pt-8">
            <PageContent>
                <NewsProjectGenerator class="mb-8 hidden"/>
                <div class="mb-8 p-4 bg-base-400/50 ring-base-500 ring-offset-2 ring-2 rounded-lg flex flex-col hidden">
                    <span class="text-2xl font-bold mb-2">
                        News: Linkie Windows Client
                    </span>
                    <div>
                        There is a new desktop client available for download. It is still experimental, but it is already usable.
                        <br/>It is built with Tauri, a framework for building small web applications using existing platform libraries,
                        and runs a JVM server in the background for Linkie.
                        <br/><br/>The total size of the client is about <span class="underline underline-offset-2 decoration-2 font-semibold">50MB</span>, which is a lot smaller than any Electron-based client.
                        <br/>At idle, the client uses about <span class="underline underline-offset-2 decoration-2 font-semibold">600MB</span> of RAM, which is unfortunately due to the JVM.
                    </div>
                </div>

                <Header> {{ $t("home.quick.access") }}</Header>
                <a class="flex flex-nowrap gap-2 pb-2" href="/dependencies">
                    <SubHeader class="text-2xl">{{ $t("home.dependencies") }}</SubHeader>
                    <div class="flex flex-col">
                        <svg xmlns="http://www.w3.org/2000/svg" class="icon icon-tabler flex-1" width="20" height="20" viewBox="0 0 24 24"
                             stroke-width="2" stroke="currentColor" fill="none" stroke-linecap="round" stroke-linejoin="round">
                            <path stroke="none" d="M0 0h24v24H0z" fill="none"></path>
                            <path d="M11 7h-5a2 2 0 0 0 -2 2v9a2 2 0 0 0 2 2h9a2 2 0 0 0 2 -2v-5"></path>
                            <line x1="10" y1="14" x2="20" y2="4"></line>
                            <polyline points="15 4 20 4 20 9"></polyline>
                        </svg>
                    </div>
                </a>
                <HomeDependencies/>

                <a class="flex flex-nowrap gap-2 pt-6 pb-2" href="/mappings">
                    <SubHeader class="text-2xl">{{ $t("home.mappings") }}</SubHeader>
                    <div class="flex flex-col">
                        <svg xmlns="http://www.w3.org/2000/svg" class="icon icon-tabler flex-1" width="20" height="20" viewBox="0 0 24 24"
                             stroke-width="2" stroke="currentColor" fill="none" stroke-linecap="round" stroke-linejoin="round">
                            <path stroke="none" d="M0 0h24v24H0z" fill="none"></path>
                            <path d="M11 7h-5a2 2 0 0 0 -2 2v9a2 2 0 0 0 2 2h9a2 2 0 0 0 2 -2v-5"></path>
                            <line x1="10" y1="14" x2="20" y2="4"></line>
                            <polyline points="15 4 20 4 20 9"></polyline>
                        </svg>
                    </div>
                </a>
                <HomeMappings/>
            </PageContent>
        </PageWidthLimiter>
    </div>
</template>

<script lang="ts">
import {defineComponent} from "vue"
import Block from "../components/Block.vue"
import HomeDependencies from "../components/home/HomeDependencies.vue"
import HomeMappings from "../components/home/HomeMappings.vue"
import Header from "../components/dependencies/Header.vue"
import SubHeader from "../components/dependencies/SubHeader.vue"
import Navbar from "../components/Navbar.vue"
import PageContent from "../components/PageContent.vue"
import PageWidthLimiter from "../components/PageWidthLimiter.vue"
import NewsProjectGenerator from "../components/NewsProjectGenerator.vue"

export default defineComponent({
    name: "NotFound",
    components: {NewsProjectGenerator, PageWidthLimiter, PageContent, Navbar, SubHeader, Header, Block, HomeDependencies, HomeMappings},
})
</script>

<style scoped>
.title-home {
    background: linear-gradient(135deg, #1f005c, #5b0060, #870160, #ac255e, #ca485c, #e16b5c, #f39060, #ffb56b) fixed;
    -webkit-clip-path: url(#shape);
    clip-path: url(#shape);
}
</style>
<style>
.navbar-\/ {
    color: white !important;
}

.locale-dropdown {
    @apply text-base-content;
}
</style>