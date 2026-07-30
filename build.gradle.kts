plugins {
    id("gg.meza.stonecraft")
}

modSettings {
    clientOptions {
        fov = 90
        guiScale = 3
        narrator = false
        darkBackground = true
        musicVolume = 0.0
    }
}

val datagen: SourceSet by sourceSets.creating {
    java.setSrcDirs(listOf(rootProject.file("src/datagen/java")))
    resources.setSrcDirs(emptyList<Any>())
}

afterEvaluate {
    val main = sourceSets.getByName("main")
    datagen.compileClasspath += main.compileClasspath + main.output
    datagen.runtimeClasspath += main.runtimeClasspath + main.output
}

tasks.register<JavaExec>("generateModels") {
    group = "fasterblockentities"
    description = "Regenerates block models from vanilla entity model layers"
    mainClass = "org.kvxd.fasterblockentities.datagen.ModelGenerator"
    classpath = files(provider { datagen.runtimeClasspath })
    dependsOn(datagen.classesTaskName)
    args(rootProject.file("src/main/resources").absolutePath)

    val workDirectory = layout.buildDirectory.dir("datagen")
    doFirst { workDirectory.get().asFile.mkdirs() }
    workingDir = workDirectory.get().asFile
}
