package fr.godox.customjetbrainspluginrepo.service

import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.ser.BeanSerializerModifier
import com.fasterxml.jackson.dataformat.xml.XmlMapper
import com.fasterxml.jackson.module.jaxb.JaxbAnnotationModule
import com.jetbrains.plugin.structure.intellij.beans.IdeaVersionBean
import fr.godox.customjetbrainspluginrepo.config.ClientWebConfig
import fr.godox.customjetbrainspluginrepo.config.REPO_ROOT_FILE
import fr.godox.customjetbrainspluginrepo.model.CustomIdePluginDescriptor
import org.springframework.stereotype.Service
import java.io.File
import java.nio.file.Files
import kotlin.io.path.Path
import kotlin.io.path.createDirectory

@Service
class PluginsFileManager {

    init {
        runCatching { REPO_ROOT_FILE.toPath().createDirectory() }
    }

    fun save(descriptor: CustomIdePluginDescriptor, urlRoot: String): CustomIdePluginDescriptor {
        return descriptor.apply {
            val path = "${REPO_ROOT_FILE}/${pluginId.split(".").joinToString("/")}/$version/"
            Files.createDirectories(Path(path))
            val fileName = "${name.replace(" ", "_")}_$version"
            var i = 1
            var newFileName = fileName
            while (listOf("zip", "xml").any { File("$path$newFileName.$it").exists() }) newFileName =
                "${fileName}_${i++}"
            val newFile = File("$path/$newFileName.zip")
            downloadUrl = "${urlRoot}&version=$version"
            File("$path/$fileName.xml").also {
                pluginZipFile.renameTo(newFile)
                it.createNewFile()
                it.writeText(XmlMapper().registerModule(JaxbAnnotationModule()).writeValueAsString(descriptor))
            }
        }
    }

    fun findByIdAndVersion(id: String, version: String): CustomIdePluginDescriptor? {
        val dir = id.split(".").fold(REPO_ROOT_FILE) { acc, s ->
            acc.listFiles()?.firstOrNull { it.isDirectory && it.name == s } ?: return null
        }
        val xmlFile = dir.listFiles()?.firstOrNull {
            it.isDirectory && it.name == version
        }?.listFiles()?.last { it.extension == "xml" }
        return xmlFile?.let {
            XmlMapper().registerModule(JaxbAnnotationModule()).readValue(it, CustomIdePluginDescriptor::class.java)
                .also { desc ->
                    desc.pluginZipFile = File(xmlFile.parentFile.path + "/" + xmlFile.nameWithoutExtension + ".zip")
                }
        }
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
            XmlMapper()
                .registerModule(JaxbAnnotationModule()).configure(
                    DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES,
                    false
                ).readValue(it.readText(), CustomIdePluginDescriptor::class.java)
        }
    }

}
