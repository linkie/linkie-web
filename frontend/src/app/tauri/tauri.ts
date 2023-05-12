import {defineStore} from "pinia"
import {listen} from "@tauri-apps/api/event"

declare global {
    interface Window {
        __TAURI__?: any;
    }
}

export function isTauri(): boolean {
    // If window.__TAURI__ is defined, we're running in a Tauri app
    return typeof window !== "undefined" && window.__TAURI__ !== undefined
}

export interface State {
    consoleMessages: string[],
}

function newState(): State {
    return {
        consoleMessages: [],
    }
}

export const useTauriStore = defineStore({
    id: "tauri",
    state: newState,
    persist: true,
})

export function tauriInit() {
    listen("console", (event) => {
        // @ts-ignore
        useTauriStore().consoleMessages = event.payload.messages
    })
}
