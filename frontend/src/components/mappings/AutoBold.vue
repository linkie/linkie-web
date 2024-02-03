<script lang="ts">
import {defineComponent, h} from "vue"
import {VNodeArrayChildren} from "@vue/runtime-core"

export default defineComponent({
    name: "AutoBold",
    render() {
        let text: string = this.$slots.default!!().map(value => {
            if (value.type === "br") return "\n"
            return (value.children ?? "undefined").toString()
        }).join(" ")
                .replace("<br>", "\n")
                .replace("<br\>", "\n")
        let children = [] as VNodeArrayChildren
        // the text as * is bold, \n is newline
        let bold = false
        let current = ""
        for (let i = 0; i < text.length; i++) {
            let char = text.charAt(i)
            if (char === "\n") {
                if (bold) {
                    children.push(h("span", {class: "bold"}, current))
                } else {
                    children.push(current)
                }
                bold = false
                current = ""
                children.push(h("br"))
            } else if (char === "*") {
                if (bold) {
                    children.push(h("span", {class: "bold"}, current))
                } else {
                    children.push(current)
                }
                current = ""
                bold = !bold
            } else {
                current += char
            }
        }
        if (current.length > 0) {
            if (bold) {
                children.push(h("span", {class: "bold"}, current))
            } else {
                children.push(current)
            }
        }

        return h("span", children)
    },
})
</script>

<style scoped>
</style>