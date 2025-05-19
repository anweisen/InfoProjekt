plugins {
  id("java")
  id("application")
  id("org.openjfx.javafxplugin") version "0.0.14"
  id("org.beryx.jlink") version "2.24.0"
}

group = "de.amberg.gmg"
version = "1.0"

repositories {
  mavenCentral()
}

dependencies {
}

javafx {
  version = "21"
  modules = listOf("javafx.controls", "javafx.graphics")
}

application {
  mainClass.set("game.Game")
  mainModule.set("game")
  applicationDefaultJvmArgs = listOf(
    "-Dprism.verbose=true",
    "-Dprism.forceGPU=true",
  )
}

jlink {
//  options.addAll("--strip-debug", "--compress", "2", "--no-header-files", "--no-man-pages")

  launcher {
    name = "game"
  }
  jpackage {
    installerType = "exe"
  }
}
