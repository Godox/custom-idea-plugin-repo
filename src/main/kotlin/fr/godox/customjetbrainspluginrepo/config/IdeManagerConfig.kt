package fr.godox.customjetbrainspluginrepo.config

import com.jetbrains.plugin.structure.intellij.plugin.IdePluginManager
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.expression.EnvironmentAccessor
import org.springframework.core.env.Environment
import java.io.File
import java.nio.file.Files

var EXTRACT_PATH = File("./received/extracted")

var REPO_ROOT_FILE = File("./repo")

@Configuration
class IdeManagerConfig {

    @Autowired
    lateinit var env: Environment
    @Bean
    fun getHost(): String{
        return env.getProperty("server.host") ?: "localhost"
    }

    @Bean
    fun getPort(): String{
        return System.getenv("server.port") ?: "8080"
    }

    @Bean
    fun getIdeManager(): IdePluginManager {
        Files.createDirectories(EXTRACT_PATH.toPath())
        return IdePluginManager.createManager(EXTRACT_PATH.toPath())
    }

}