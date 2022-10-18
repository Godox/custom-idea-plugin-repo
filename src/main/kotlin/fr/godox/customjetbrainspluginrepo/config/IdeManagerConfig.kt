package fr.godox.customjetbrainspluginrepo.config

import com.jetbrains.plugin.structure.intellij.plugin.IdePluginManager
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import java.io.File
import java.nio.file.Files

var EXTRACT_PATH = File("./received/extracted")

var REPO_ROOT_FILE = File("./repo")

@Configuration
class IdeManagerConfig {

    @Bean
    fun getIdeManager(): IdePluginManager {
        Files.createDirectories(EXTRACT_PATH.toPath())
        return IdePluginManager.createManager(EXTRACT_PATH.toPath())
    }

}