<template>
    <div class="flex flex-col">
        <h1 class="pt-40 pb-28 text-center text-6xl font-bold title-home text-white"> {{ $t("home.welcome") }} </h1>

        <div class="max-w-[78rem] mx-auto pl-4 pr-10 flex-1 pt-8">
            <Header> {{ $t("home.quick.access") }} </Header>
            <SubHeader class="text-2xl">
                <a class="flex flex-nowrap gap-2" href="/dependencies">
                    {{ $t("home.dependencies") }}
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
            </SubHeader>
            <HomeDependencies/>

            <div class="mt-4"/>

            <SubHeader class="text-2xl">
                <a class="flex flex-nowrap gap-2" href="/mappings">
                    {{ $t("home.mappings") }}
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
            </SubHeader>
            <HomeMappings/>
        </div>
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

export default defineComponent({
    name: "NotFound",
    components: {Navbar, SubHeader, Header, Block, HomeDependencies, HomeMappings},
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