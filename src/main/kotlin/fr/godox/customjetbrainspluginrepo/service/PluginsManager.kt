package fr.godox.customjetbrainspluginrepo.service

import com.jetbrains.plugin.structure.intellij.beans.PluginBean
import fr.godox.customjetbrainspluginrepo.config.REPO_ROOT_FILE
import fr.godox.customjetbrainspluginrepo.model.*
import fr.godox.customjetbrainspluginrepo.repository.DescriptorsTable
import fr.godox.customjetbrainspluginrepo.repository.PluginVendor
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.transactions.transaction
import org.springframework.stereotype.Service
import java.io.File
import javax.sql.rowset.serial.SerialBlob
import kotlin.io.path.createDirectory

@Service
class PluginsFileManager {

    init {
        runCatching { REPO_ROOT_FILE.toPath().createDirectory() }
    }

    fun insertFromXml(bean: PluginBean, receivedFile: File, pluginUrl: String): CustomIdePluginDescriptor {
        return transaction {
            CustomIdePluginDescriptor.new {
                pluginId = bean.id
                url = bean.url
                downloads = 0
                changeNotes = bean.changeNotes
                depends = bean.dependencies.joinToString { bean -> bean.dependencyId }
                description = bean.description
                name = bean.name
                ideaVersion = IdeaVersionEntity.new {
                    sinceBuild = bean.ideaVersion.sinceBuild
                    untilBuild = bean.ideaVersion.untilBuild
                }
                vendor = PluginVendorEntity.find { PluginVendor.name eq name }.firstOrNull() ?: PluginVendorEntity.new {
                    email = bean.vendor.email
                    url = bean.vendor.url
                    name = bean.vendor.name
                    logo = bean.vendor.logo
                }
                version = bean.pluginVersion
                pluginZipFile = SerialBlob(receivedFile.readBytes())
                size = receivedFile.length()
                downloadUrl = pluginUrl
            }
        }
//        return descriptor.apply {
//            val path = "${REPO_ROOT_FILE}/${pluginId.split(".").joinToString("/")}/$version/"
//            Files.createDirectories(Path(path))
//            val fileName = "${name.replace(" ", "_")}_$version"
//            var i = 1
//            var newFileName = fileName
//            while (listOf("zip", "xml").any { File("$path$newFileName.$it").exists() }) newFileName =
//                "${fileName}_${i++}"
//            val newFile = File("$path/$newFileName.zip")
//            downloadUrl = "${receivedFile}&version=$version"
//            File("$path/$fileName.xml").also {
//                pluginZipFile.renameTo(newFile)
//                it.createNewFile()
//                it.writeText(XmlMapper().registerModule(JaxbAnnotationModule()).writeValueAsString(descriptor))
//            }
//        }
    }

    fun findByIdAndVersion(id: String, version: String): CustomIdePluginDescriptor? {
        return CustomIdePluginDescriptor.find { (DescriptorsTable.pluginId eq id) and (DescriptorsTable.version eq version) }
            .firstOrNull()
    }

    fun findAll(): Collection<CustomIdePluginDescriptor> {
        return transaction { CustomIdePluginDescriptor.all().toList() }
    }

}
