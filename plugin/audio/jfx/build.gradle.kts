plugins {
	id("common")
	id("kt")
	id("jfx")
	id("lib")
	id("testable")
}

group = "$group.plugin.audio"
description = "JavaFX AudioFactory implementation"

dependencies {
	implementation(projects.platform)
}

javafx {
	modules("javafx.base", "javafx.graphics", "javafx.media")
}

tasks.jar {
	archiveBaseName.set("${parent?.name}-${project.name}")
}
