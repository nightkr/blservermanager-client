package blservermanager.build;

import org.gradle.api.*;
import org.gradle.api.tasks.*;

class JarLibs implements Plugin {
	def void apply(Object project) { apply((Project)project) }
	def void apply(Project project) {
		project.task(type: Copy, "copyToLib") {
			into "${project.buildDir}/libs"
			from project.configurations.runtime
		}
		project.jar.dependsOn(project.copyToLib)
		project.jar.doFirst {
			String classPath = null;
			for (file in project.fileTree("${project.buildDir}/libs")) {
				if (classPath == null)
					classPath = "" + file.getName()
				else
					classPath += " " + file.getName()
			}
			if (classPath != null)
				project.manifest.attributes("Class-Path": classPath)
		}
		//project.manifest.attributes("Class-Path": "${project.sourceSets.main.runtimeClasspath.asPath}")
	}
}
