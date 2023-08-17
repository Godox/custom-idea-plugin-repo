package fr.godox.customjetbrainspluginrepo.config

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.PropertyName
import com.fasterxml.jackson.databind.introspect.AnnotatedClass
import com.fasterxml.jackson.databind.introspect.AnnotatedMember
import com.fasterxml.jackson.databind.introspect.JacksonAnnotationIntrospector
import com.fasterxml.jackson.dataformat.xml.XmlMapper
import fr.godox.customjetbrainspluginrepo.repository.DescriptorsTable
import fr.godox.customjetbrainspluginrepo.repository.IdeaVersion
import fr.godox.customjetbrainspluginrepo.repository.PluginVendor
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.transaction
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.ComponentScan
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Primary
import org.springframework.core.env.Environment
import org.springframework.http.converter.xml.MappingJackson2XmlHttpMessageConverter
import org.springframework.transaction.annotation.EnableTransactionManagement
import org.springframework.web.servlet.config.annotation.EnableWebMvc
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer
import java.io.File
import java.io.IOException
import java.io.Serializable
import java.nio.file.Files


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
        } catch (ignored: IOException) {
        }


        Database.connect("jdbc:sqlite:repository.db", "org.sqlite.JDBC")

        transaction { SchemaUtils.create(DescriptorsTable, IdeaVersion, PluginVendor) }

    }

    class IgnoreInheritedIntrospector : JacksonAnnotationIntrospector() {
        override fun findRootName(ac: AnnotatedClass?): PropertyName {
            return super.findRootName(ac)
        }



        override fun hasIgnoreMarker(m: AnnotatedMember): Boolean {
            return !m.declaringClass.interfaces.contains(Serializable::class.java) || super.hasIgnoreMarker(m)
        }
    }

//    @Bean
//    fun xmlMapper(): MappingJackson2XmlHttpMessageConverter {
//        return MappingJackson2XmlHttpMessageConverter(XmlMapper().apply {
//            setAnnotationIntrospector(IgnoreInheritedIntrospector())
//        })
//    }

    @Bean
    @Primary
    @Suppress("SpringJavaInjectionPointsAutowiringInspection")
    fun objectMapper(converter: MappingJackson2XmlHttpMessageConverter): ObjectMapper {
        return converter.objectMapper.also {
            it.setAnnotationIntrospector(IgnoreInheritedIntrospector())
        }
    }


//
//    @Bean
//    fun initMapper(): Jackson2ObjectMapperBuilder {
//        return Jackson2ObjectMapperBuilder().apply {
//            filters(SimpleFilterProvider().setDefaultFilter(SimpleBeanPropertyFilter.filterOutAllExcept()))
//            annotationIntrospector(IgnoreInheritedIntrospector())
//            createXmlMapper(true)
//        }
//    }

    companion object {
        lateinit var host: String
        var port: Int? = null
    }

}
