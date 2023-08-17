package fr.godox.customjetbrainspluginrepo.repository

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement
import org.jetbrains.exposed.dao.UUIDTable

internal object DescriptorsTable : UUIDTable("descriptors") {

    var downloads = integer("downloads")

    var zipFile = blob("zip_file")

    var pluginId = text("plugin_id")


    var url = text("url")


    var version = text("version")


    var size = long("size")

    var name = text("name")

    var vendor = reference("vendor", PluginVendor)

    var description = text("description").nullable()

    var changeNotes = text("changeNotes").nullable()

    var depends = text("depends") //TODO LIST


    var downloadUrl = text("downloadUrl").nullable()


    var ideaVersion = reference("ideaVersion", IdeaVersion)

}

object PluginVendor : UUIDTable("vendors") {

    @JacksonXmlProperty(isAttribute = true)

    var url = text("url")

    @JacksonXmlProperty(isAttribute = true)
    var email = text("email")

    @JacksonXmlProperty(isAttribute = true)
    var logo = text("logo").nullable()

    val name = text("name")

}

object IdeaVersion : UUIDTable("versions") {

    var sinceBuild = text("sinceBuild").nullable()

    var untilBuild = text("untilBuild").nullable()

}