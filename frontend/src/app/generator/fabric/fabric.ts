import {State} from "../../generator-store"
import {fabricModJson} from "./fabric_mod"
import {settingsGradle} from "../gradle"
import {Files} from "../generator"

export function generateFabric(state: State, files: Files) {
    let loader = "0.14.17"
    let javaVersion = 17
    files[`src/main/resources/assets/${state.modId}/fabric.mod.json`] = fabricModJson(state, loader, javaVersion)
    files["gradle.properties"] = gradleProperties(state, loader)
    files[state.gradleLanguage === "groovy" ? "settings.gradle" : "settings.gradle.kts"] = fabricSettingsGradle(state.gradleLanguage === "groovy")
    files[state.gradleLanguage === "groovy" ? "build.gradle" : "build.gradle.kts"] = buildGradle(state, javaVersion)
}

function gradleProperties(state: State, loader: string) {
    return `# Done to increase the memory available to gradle.
org.gradle.jvmargs=-Xmx1G
org.gradle.parallel=true

# Fabric Properties
\t# check these on https://fabricmc.net/develop
\tminecraft_version=${state.minecraftVersion}
\tyarn_mappings=1.19.4+build.1
\tloader_version=${loader}

# Mod Properties
\tmod_version = ${state.version}
\tmaven_group = ${state.package}
\tarchives_base_name = ${state.modId}

# Dependencies
\tfabric_version=0.75.3+1.19.4
`
}

function fabricSettingsGradle(groovy: boolean): string {
    return settingsGradle([
        {name: "FabricMC", url: "https://maven.fabricmc.net/"}
    ], groovy)
}

function buildGradle(state: State, javaVersion: number): string {
    const groovy = state.gradleLanguage === "groovy"
    return `plugins {${groovy ? `
\tid "fabric-loom" version "1.1-SNAPSHOT"
\tid "maven-publish"` : `
id("fabric-loom") version "1.1-SNAPSHOT"
\t\`maven-publish\``}
}

${groovy ? `version = project.mod_version` : `version = project.property("mod_version")!!`}
${groovy ? `group = project.maven_group` : `group = project.property("maven_group")!!`}

repositories {
\t// Add repositories to retrieve artifacts from in here.
\t// You should only use this when depending on other mods because
\t// Loom adds the essential maven repositories to download Minecraft and libraries from automatically.
\t// See https://docs.gradle.org/current/userguide/declaring_repositories.html
\t// for more information about repositories.
}

dependencies {
\t// To change the versions see the gradle.properties file
\tminecraft${groovy ? ` ` : `(`}"com.mojang:minecraft:\${${groovy ? `project.minecraft_version` : `project.property("minecraft_version")`}}"${groovy ? `` : `)`}
\tmappings${groovy ? ` ` : `(`}"net.fabricmc:yarn:\${${groovy ? `project.yarn_mappings` : `project.property("yarn_mappings")`}}:v2"${groovy ? `` : `)`}
\tmodImplementation${groovy ? ` ` : `(`}"net.fabricmc:fabric-loader:\${${groovy ? `project.loader_version` : `project.property("loader_version")`}}"${groovy ? `` : `)`}

\t// Fabric API. This is technically optional, but you probably want it anyway.
\tmodImplementation${groovy ? ` ` : `(`}"net.fabricmc.fabric-api:fabric-api:\${${groovy ? `project.fabric_version` : `project.property("fabric_version")`}}"${groovy ? `` : `)`}

\t// Uncomment the following line to enable the deprecated Fabric API modules. 
\t// These are included in the Fabric API production distribution and allow you to update your mod to the latest modules at a later more convenient time.

\t// modImplementation${groovy ? ` ` : `(`}"net.fabricmc.fabric-api:fabric-api-deprecated:\${${groovy ? `project.fabric_version` : `project.property("fabric_version")`}}"${groovy ? `` : `)`}
}

base {
\tarchivesName${groovy ? ` = project.archives_base_name` : `.set(project.property("archives_base_name")!!)`}
}

processResources {
\tinputs.property "version", project.version

\tfilesMatching("fabric.mod.json") {
\t\texpand "version": project.version
\t}
}

tasks.withType(JavaCompile).configureEach {
\tit.options.release = ${javaVersion}
}

java {
\t// Loom will automatically attach sourcesJar to a RemapSourcesJar task and to the "build" task
\t// if it is present.
\t// If you remove this line, sources will not be generated.
\twithSourcesJar()

\tsourceCompatibility = JavaVersion.VERSION_${javaVersion}
\ttargetCompatibility = JavaVersion.VERSION_${javaVersion}
}

jar {
\tfrom("LICENSE") {
\t\trename { "\${it}_\${base.archivesName.get()}"}
\t}
}

// configure the maven publication
publishing {
\tpublications {
\t\tmavenJava(MavenPublication) {
\t\t\tfrom${groovy ? ` ` : `(`}components.java${groovy ? `` : `)`}
\t\t}
\t}

\t// See https://docs.gradle.org/current/userguide/publishing_maven.html for information on how to set up publishing.
\trepositories {
\t\t// Add repositories to publish to here.
\t\t// Notice: This block does NOT have the same function as the block in the top level.
\t\t// The repositories here will be used for publishing your artifact, not for
\t\t// retrieving dependencies.
\t}
}
`
}