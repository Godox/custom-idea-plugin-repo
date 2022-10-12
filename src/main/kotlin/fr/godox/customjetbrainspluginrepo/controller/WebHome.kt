package fr.godox.customjetbrainspluginrepo.controller

import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.dataformat.xml.XmlMapper
import fr.godox.customjetbrainspluginrepo.model.PluginEntity
import fr.godox.customjetbrainspluginrepo.model.Plugins
import fr.godox.customjetbrainspluginrepo.service.PluginsManager
import org.hibernate.id.UUIDGenerator
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import org.springframework.web.multipart.MultipartFile
import java.io.File
import java.io.IOException
import java.net.URI
import java.net.URL
import java.net.URLEncoder
import java.nio.file.FileSystems
import java.nio.file.Files
import java.nio.file.Path
import java.util.*
import java.util.zip.ZipEntry
import java.util.zip.ZipFile
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse
import kotlin.text.Charsets.UTF_8


@RestController
@RequestMapping("/")
class WebHome {

    @Autowired
    lateinit var pluginsManager: PluginsManager

    @GetMapping("", produces = [MediaType.APPLICATION_XML_VALUE])
    fun getPluginsList(): ResponseEntity<Plugins> {
        return ResponseEntity.ok(Plugins(pluginsManager.getAllPluginList()))
    }

    @GetMapping("/plugin/{fileName}")
    fun getPlugin(@RequestParam id: Long, response: HttpServletResponse): ResponseEntity<PluginEntity> {
        return pluginsManager.getPluginById(id)?.let {
            response.outputStream.write(File("./pluginsRepo/${it.pluginId!!}.zip").readBytes())
            ResponseEntity.ok().build()
        } ?: ResponseEntity.notFound().build()
    }

    @PostMapping("/api/updates/upload")
    fun uploadPlugin(@RequestParam("file") file: MultipartFile, request: HttpServletRequest): ResponseEntity<String> {
        val receivedFile = File("./received/${file.resource.filename}").also {
            Files.createDirectories(it.parentFile.toPath())
            file.transferTo(File(it.absolutePath))
        }
        val zipFile = ZipFile(receivedFile)
        val jarName = receivedFile.name.replaceAfterLast(".", "jar")
        val jarEntry: ZipEntry? = zipFile.entries().asSequence().firstOrNull { it.name.substringAfterLast("/") == jarName }

        val bytes = jarEntry?.let {
            zipFile.getInputStream(it)
        }?.readAllBytes()
        zipFile.close()
        val jarFile = File("./received/$jarName").apply {
            this.createNewFile()
            this.writeBytes(bytes!!)
        }
        val url = URL("jar:file:${jarFile.absolutePath}/!/META-INF/plugin.xml")
        val xmlContent: String = url.openStream().readAllBytes().decodeToString()

        val pluginEntity = XmlMapper()
            .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
            .readValue(xmlContent, PluginEntity::class.java)
        pluginEntity.pluginId = Random().nextLong(1) //TODO ID SYSTEM
        val pluginUrl = request.contextPath + "/plugin/${URLEncoder.encode(pluginEntity.name, UTF_8)}.zip?id=${pluginEntity.pluginId}"
        val location = URI("http://${request.remoteHost}:${request.serverPort}${request.contextPath}$pluginUrl")
        pluginEntity.url = location.toString()
        pluginsManager.addPlugin(pluginEntity, receivedFile)
        return ResponseEntity.created(location).build()
    }

    @Throws(IOException::class)
    private fun extractAll(fromZip: URI, toDirectory: Path) {
        FileSystems.newFileSystem(fromZip, Collections.emptyMap<String, Any>())
            .rootDirectories
            .forEach { root ->
                // in a full implementation, you'd have to
                // handle directories
                Files.walk(root)
                    .forEach { path: Path? ->
                        Files.copy(
                            path,
                            toDirectory
                        )
                    }
            }
    }

}