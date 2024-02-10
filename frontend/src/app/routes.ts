import Home from "../routes/Home.vue"
import Dependencies from "../routes/Dependencies.vue"
import Generator from "../routes/Generator.vue"
import Mappings from "../routes/Mappings.vue"
import OpenSourceLicenses from "../routes/OpenSourceLicenses.vue"
import SourcesStatus from "../routes/SourcesStatus.vue"
import NotFound from "../routes/NotFound.vue"
import {RouteRecordRaw} from "vue-router"

export const routes: RouteRecordRaw[] = [
    {path: "/", component: Home},
    {path: "/dependencies", component: Dependencies},
    {path: "/generator", component: Generator},
    {path: "/mappings", component: Mappings},
    {path: "/oss", component: OpenSourceLicenses},
    {path: "/status/sources", component: SourcesStatus},
    {path: "/:pathMatch(.*)*", component: NotFound},
]
