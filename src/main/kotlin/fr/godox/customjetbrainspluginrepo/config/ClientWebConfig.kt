package fr.godox.customjetbrainspluginrepo.config

import org.springframework.context.annotation.ComponentScan
import org.springframework.context.annotation.Configuration
import org.springframework.core.env.Environment
import org.springframework.transaction.annotation.EnableTransactionManagement
import org.springframework.web.servlet.config.annotation.EnableWebMvc
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer
import java.io.File
import java.io.IOException
import java.nio.file.Files
import kotlin.properties.Delegates

@EnableWebMvc
@Configuration
@ComponentScan(basePackages = ["fr.godox.customjetbrainspluginrepo.controller", "fr.godox.customjetbrainspluginrepo.service"])
@EnableTransactionManagement
class ClientWebConfig(env: Environment) : WebMvcConfigurer {
    init {
        try {
            Files.createDirectories(File("./received").toPath())
            port = env.getProperty("server.port", Int::class.java)
            host = env.getProperty("server.address") ?: "localhost"
        } catch (ignored: IOException) { }
    }

    companion object {
        lateinit var host: String
        var port : Int? = null
    }

}
