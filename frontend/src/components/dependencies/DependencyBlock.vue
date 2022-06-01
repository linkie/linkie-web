<template>
    <Block>
        <Header>{{ title }}</Header>
        <slot></slot>
    </Block>
</template>

<script lang="ts">
import Header from "./Header.vue"
import Block from "../Block.vue"
import {defineComponent} from "vue"

export default defineComponent({
    name: "DependencyBlock",
    components: {Block, Header},
    props: {
        title: String,
    },
})
</script>

<style scoped>
</style>