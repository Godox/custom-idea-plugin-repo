package fr.godox.customjetbrainspluginrepo.model

import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement
import fr.godox.customjetbrainspluginrepo.repository.DescriptorsTable
import fr.godox.customjetbrainspluginrepo.repository.IdeaVersion
import fr.godox.customjetbrainspluginrepo.repository.PluginVendor
import org.jetbrains.exposed.dao.EntityID
import org.jetbrains.exposed.dao.UUIDEntity
import org.jetbrains.exposed.dao.UUIDEntityClass
import java.io.Serializable
import java.util.*

@JacksonXmlRootElement(localName = "plugin")
class CustomIdePluginDescriptor(id: EntityID<UUID>) : UUIDEntity(id), Serializable {

    companion object : UUIDEntityClass<CustomIdePluginDescriptor>(DescriptorsTable)

    var downloads by DescriptorsTable.downloads

    @delegate:JacksonXmlProperty(localName = "plugin-id", isAttribute = true)
    var pluginId by DescriptorsTable.pluginId

    @delegate:JacksonXmlProperty(isAttribute = true)
    var url by DescriptorsTable.url

    @delegate:JacksonXmlProperty(isAttribute = true)
    var version by DescriptorsTable.version

    @delegate:JacksonXmlProperty(localName = "size", isAttribute = true)
    var size by DescriptorsTable.size

    @delegate:JacksonXmlProperty
    var name by DescriptorsTable.name

    @delegate:JacksonXmlProperty
    var vendor by PluginVendorEntity referencedOn DescriptorsTable.vendor

    @delegate:JacksonXmlProperty
    var description by DescriptorsTable.description

    @delegate:JacksonXmlProperty(localName = "change-notes")
    var changeNotes by DescriptorsTable.changeNotes

    @delegate:JacksonXmlProperty
//    @delegate:JacksonXmlElementWrapper(useWrapping = false)
    var depends by DescriptorsTable.depends

    @delegate:JacksonXmlProperty(localName = "download-url")
    var downloadUrl by DescriptorsTable.downloadUrl

    @delegate:JacksonXmlProperty(localName = "idea-version")
    var ideaVersion by IdeaVersionEntity referencedOn DescriptorsTable.ideaVersion

    @get:JsonIgnore
    var pluginZipFile by DescriptorsTable.zipFile

}

@JacksonXmlRootElement(localName = "vendor")
class PluginVendorEntity(id: EntityID<UUID>) : UUIDEntity(id), Serializable {

    companion object : UUIDEntityClass<PluginVendorEntity>(PluginVendor)

    var name by PluginVendor.name

    @delegate:JacksonXmlProperty(isAttribute = true)
    var url by PluginVendor.url

    @delegate:JacksonXmlProperty(isAttribute = true)
    var email by PluginVendor.email

    @delegate:JacksonXmlProperty(isAttribute = true)
    var logo by PluginVendor.logo

}

@JacksonXmlRootElement(localName = "idea-version")
class IdeaVersionEntity(id: EntityID<UUID>) : UUIDEntity(id), Serializable {

    companion object : UUIDEntityClass<IdeaVersionEntity>(IdeaVersion)

    @delegate:JacksonXmlProperty(isAttribute = true)
    var sinceBuild by IdeaVersion.sinceBuild

    @delegate:JacksonXmlProperty(isAttribute = true)
    var untilBuild by IdeaVersion.untilBuild

}