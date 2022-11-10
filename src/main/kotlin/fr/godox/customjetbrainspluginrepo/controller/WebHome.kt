package fr.godox.customjetbrainspluginrepo.controller

import com.fasterxml.jackson.dataformat.xml.XmlMapper
import com.fasterxml.jackson.module.jaxb.JaxbAnnotationModule
import com.jetbrains.plugin.structure.base.plugin.PluginCreationFail
import com.jetbrains.plugin.structure.base.plugin.PluginCreationSuccess
import com.jetbrains.plugin.structure.intellij.beans.PluginBean
import com.jetbrains.plugin.structure.intellij.extractor.PluginBeanExtractor
import com.jetbrains.plugin.structure.intellij.plugin.IdePluginManager
import fr.godox.customjetbrainspluginrepo.config.ClientWebConfig
import fr.godox.customjetbrainspluginrepo.model.CustomIdePluginDescriptor
import fr.godox.customjetbrainspluginrepo.model.Plugins
import fr.godox.customjetbrainspluginrepo.model.toCustomPluginDescriptor
import fr.godox.customjetbrainspluginrepo.service.PluginsFileManager
import org.jetbrains.intellij.pluginRepository.model.PluginUpdateBean
import org.jetbrains.intellij.pluginRepository.model.PluginUserBean
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import org.springframework.web.multipart.MultipartFile
import java.io.File
import java.net.URLEncoder
import java.nio.file.Files
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse


@RestController
@RequestMapping("/")
class WebHome {

    @Autowired
    lateinit var pluginsManager: PluginsFileManager

    @Autowired
    lateinit var idePluginManager: IdePluginManager

    @Autowired

    @GetMapping("", produces = [MediaType.APPLICATION_XML_VALUE])
    fun getPluginsList(request: HttpServletRequest): ResponseEntity<String> {
        return ResponseEntity.ok(
            XmlMapper()
                .registerModules(JaxbAnnotationModule())
                .writeValueAsString(
                    Plugins(pluginsManager.findAll())
                )
        )
    }

    @GetMapping("/plugins/*")
    fun getPlugin(
        @RequestParam id: String,
        @RequestParam version: String,
        response: HttpServletResponse
    ): ResponseEntity<CustomIdePluginDescriptor> {
        return pluginsManager.findByIdAndVersion(id, version)?.let {
            response.outputStream.write(it.pluginZipFile.readBytes())
            ResponseEntity.ok().build()
        } ?: ResponseEntity.notFound().build()
    }

    @PostMapping("/api/updates/upload", produces = [MediaType.APPLICATION_JSON_VALUE])
    fun uploadPlugin(@RequestParam("file") file: MultipartFile, request: HttpServletRequest): ResponseEntity<*> {
        val receivedFile = saveFile(file)
        val plugin = when (val result = idePluginManager.createPlugin(receivedFile.toPath())) {
            is PluginCreationSuccess -> result.plugin
            is PluginCreationFail -> return ResponseEntity.internalServerError().body(result.toString())
        }
        val pluginBean = PluginBeanExtractor.extractPluginBean(plugin.underlyingDocument)
        val pluginDescriptor = pluginsManager.save(pluginBean.toCustomPluginDescriptor().also {
            it.pluginZipFile = receivedFile
        }, generatePluginUrl(pluginBean.name, pluginBean.id, request))

        return ResponseEntity.ok(generatePluginUpdateBean(pluginBean, pluginDescriptor, request))
    }

    private fun generatePluginUpdateBean(
        pluginBean: PluginBean,
        pluginDescriptor: CustomIdePluginDescriptor,
        request: HttpServletRequest
    ): PluginUpdateBean {
        return pluginBean.run {
            PluginUpdateBean(
                (hashCode() + System.currentTimeMillis()).toInt(),
                author = PluginUserBean(id, vendor.name, vendor.url),
                pluginId = id.hashCode(),
                version = pluginVersion,
                cdate = LocalDateTime.now().format(DateTimeFormatter.ISO_DATE),
                channel = "",
                downloads = 0, // TODO
                downloadUrl = generatePluginUrl(name, id, request),
                modules = modules.toSet(),
                notes = changeNotes,
                since = ideaVersion.sinceBuild,
                sinceUntil = ideaVersion.untilBuild,
                size = pluginDescriptor.size.toInt(),
                until = ideaVersion.untilBuild
            )
        }
    }

    private fun generatePluginUrl(pluginName: String, pluginId: String, request: HttpServletRequest): String {
        val pluginUrl = generatePluginPath(pluginName, pluginId)
        return "http${if (request.isSecure) "s" else ""}://${ClientWebConfig.host}:${ClientWebConfig.port}$pluginUrl"
    }

    private fun generatePluginPath(pluginName: String, pluginId: String): String {
        val encodedName = URLEncoder.encode(pluginName, "UTF-8").replace("+", "%20")
        return "/plugins/$encodedName.zip?id=${pluginId}"
    }

    fun saveFile(file: MultipartFile): File {
        val receivedFile = File("./received/${file.resource.filename}").also {
            Files.createDirectories(it.parentFile.toPath())
            file.transferTo(File(it.absolutePath))
        }
        return receivedFile
    }

}