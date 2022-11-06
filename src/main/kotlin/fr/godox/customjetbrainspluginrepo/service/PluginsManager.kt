package fr.godox.customjetbrainspluginrepo.service

import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.dataformat.xml.XmlMapper
import com.jetbrains.plugin.structure.intellij.beans.IdeaVersionBean
import fr.godox.customjetbrainspluginrepo.config.REPO_ROOT_FILE
import fr.godox.customjetbrainspluginrepo.model.CustomIdePluginDescriptor
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.core.env.Environment
import org.springframework.stereotype.Service
import java.io.File
import java.nio.file.Files
import kotlin.io.path.Path
import kotlin.io.path.createDirectory

@Service
class PluginsFileManager {

    @Autowired
    lateinit var env: Environment

    val urlRoot
        get() = "https://${env.getProperty("server.host")}${env.getProperty("server.port")?.let { ":$it" }}/plugins/"

    init {
        runCatching { REPO_ROOT_FILE.toPath().createDirectory() }
    }

    fun save(descriptor: CustomIdePluginDescriptor): CustomIdePluginDescriptor {
        return descriptor.apply {
            val path = "${REPO_ROOT_FILE}/${pluginId.split(".").joinToString("/")}/$version/"
            Files.createDirectories(Path(path))
            var fileName = "${name.replace(" ", "_")}_$version"
            var i = 1
            while (listOf("zip", "xml").any { File("$path$fileName.$it").exists() }) fileName = "${fileName}_${i++}"
            val newFile = File("$path/$fileName.zip")
            downloadUrl = "$urlRoot$fileName.zip?id=$pluginId&version=$version"
            File("$path/$fileName.xml").also {
                pluginZipFile.renameTo(newFile)
                it.createNewFile()
                it.writeText(XmlMapper().writeValueAsString(descriptor))
            }
        }
    }

    fun saveAll(entities: Iterable<CustomIdePluginDescriptor>): Collection<CustomIdePluginDescriptor> {
        return entities.map { save(it) }
    }

    fun findByIdAndVersion(id: String, version: String): CustomIdePluginDescriptor? {
        val dir = id.split(".").fold(REPO_ROOT_FILE) { acc, s ->
            acc.listFiles()?.firstOrNull { it.isDirectory && it.name == s } ?: return null
        }
        val xmlFile = dir.listFiles()?.firstOrNull {
            it.isDirectory && it.name == version
        }?.listFiles()?.last { it.extension == "xml" }
        return xmlFile?.let { XmlMapper().readValue(it, CustomIdePluginDescriptor::class.java).also { desc ->
            desc.pluginZipFile = File(xmlFile.parentFile.path + "/" +  xmlFile.nameWithoutExtension + ".zip")
        } }
    }

    private fun findRecursive(directory: File, files: MutableCollection<File> = mutableListOf()): Collection<File> {
        directory.listFiles()?.forEach {
            if (it.isDirectory) {
                findRecursive(it, files)
            } else if (it.extension == "xml") {
                files.add(it)
            }
        }
        return files
    }

    fun findAll(): Collection<CustomIdePluginDescriptor> {
        return findRecursive(REPO_ROOT_FILE).map {
            XmlMapper().configure(
                DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES,
                false
            ).readValue(it.readText(), CustomIdePluginDescriptor::class.java)
        }
    }

    fun count(): Long {
        TODO("Not yet implemented")
    }

    fun deleteAll() {
        TODO("Not yet implemented")
    }

    fun deleteAll(entities: MutableIterable<CustomIdePluginDescriptor>) {
        entities.forEach { delete(it) }
    }

    fun delete(entity: CustomIdePluginDescriptor) {
        TODO("Not yet implemented")
    }

}

private operator fun IdeaVersionBean.compareTo(apply: IdeaVersionBean): Int {
    return 0 //TODO
}
