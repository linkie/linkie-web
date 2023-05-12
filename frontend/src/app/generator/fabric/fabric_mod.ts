import {State} from "../../generator-store"

export function fabricModJson(state: State, loader: string, javaVersion: number) {
    return `{
\t"schemaVersion": 1,
\t"id": "${state.modId}",
\t"version": "\${version}",
\t"name": "${state.name}",
\t"description": "${state.description}",
\t"authors": ${JSON.stringify(state.authors)},
\t"license": "${state.license}",
\t"icon": "assets/${state.modId}/icon.png",
\t"environment": "*",
\t"entrypoints": {
\t\t"main": [
\t\t\t"${state.package}.${state.mainClass}"
\t\t],
\t\t"client": [
\t\t\t"${state.package}.client.${state.clientClass}"
\t\t]
\t},
\t${state.mixin ? `"mixins": [
\t\t"modid.mixins.json"
\t],\n` : ``}
\t"depends": {
\t\t"fabricloader": ">=${loader}",${state.dependencies?.includes("fabric-api") ? `\n\t\t"fabric-api": "*",` : ``}
\t\t"minecraft": "~${state.minecraftVersion}",
\t\t"java": ">=${javaVersion}"
\t}
}
`
}