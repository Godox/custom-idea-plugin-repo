package fr.godox.customjetbrainspluginrepo.model

import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement
import com.jetbrains.plugin.structure.intellij.beans.IdeaVersionBean
import com.jetbrains.plugin.structure.intellij.beans.PluginBean
import com.jetbrains.plugin.structure.intellij.beans.PluginVendorBean
import fr.godox.customjetbrainspluginrepo.config.REPO_ROOT_FILE
import java.io.File
import java.io.Serializable
import javax.persistence.Column
import javax.persistence.Convert
import javax.persistence.Id
import javax.xml.bind.annotation.*

@JacksonXmlRootElement(localName = "plugin")
class CustomIdePluginDescriptor : Serializable {

    @Id
    @JacksonXmlProperty(localName = "plugin-id", isAttribute = true)
    lateinit var pluginId: String

    @JacksonXmlProperty(isAttribute = true)
    lateinit var url: String

    @JacksonXmlProperty(isAttribute = true)
    lateinit var version: String

    @JacksonXmlProperty(localName = "size", isAttribute = true)
    var size : Long = 0

    @JacksonXmlProperty
    lateinit var name: String

    @JacksonXmlProperty
    var vendor: PluginVendorBean? = null

    @JacksonXmlProperty
    @Column(length = 1000000)
    var description: String? = null

    @JacksonXmlProperty(localName = "change-notes")
    var changeNotes: String? = null

    @JacksonXmlProperty
    @JacksonXmlElementWrapper(useWrapping = false)
    var depends: List<String> = emptyList()

    @JacksonXmlProperty(localName = "download-url")
    var downloadUrl: String? = null

    @JacksonXmlProperty(localName = "idea-version")
    var ideaVersion: IdeaVersionBean? = null

    @JsonIgnore
    var pluginZipFile = File("")
        set(value) {
            field = value
            size = value.length()
        }

}

fun PluginBean.toCustomPluginDescriptor(): CustomIdePluginDescriptor {
    return CustomIdePluginDescriptor().also {
        it.pluginId = id
        it.url = url
        it.changeNotes = changeNotes
        it.depends = dependencies.map { bean -> bean.dependencyId }
        it.description = description
        it.name = name
        it.ideaVersion = ideaVersion
        it.vendor = vendor
        it.version = pluginVersion
    }
}