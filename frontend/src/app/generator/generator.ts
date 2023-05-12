import {generateClientClassName, generateMainClassName, generateModID, useGeneratorStore} from "../generator-store"
import {generateFabric} from "./fabric/fabric"
import {gradlew, gradlewBat} from "./gradle"

export type Files = { [path: string]: string }

export function generate(): Promise<Files> {
    let state = useGeneratorStore()
    state = JSON.parse(JSON.stringify(state))
    state.mainClass = state.mainClass || generateMainClassName()
    state.clientClass = state.clientClass || generateClientClassName()
    state.modId = state.modId || generateModID()
    return new Promise<Files>((resolve, reject) => {
        let files: Files = {}
        if (state.template === "fabric") {
            generateFabric(state, files)
        }
        files["gradlew"] = gradlew()
        files["gradlew.bat"] = gradlewBat()
        resolve(files)
    }).finally(() => {
        useGeneratorStore().generating = false
    });
}