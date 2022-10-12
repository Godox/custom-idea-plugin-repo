package fr.godox.customjetbrainspluginrepo.model

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement
import java.io.Serializable
import java.sql.Blob
import java.util.*
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.Id

@JacksonXmlRootElement(localName = "plugin")
@Entity
class PluginEntity() : Serializable {

    @Id
    @JacksonXmlProperty(localName = "plugin-id", isAttribute = true)
//    @GeneratedValue
    var pluginId: Long? = null

    @JacksonXmlProperty(isAttribute = true)
    lateinit var url: String

    @JacksonXmlProperty(isAttribute = true)
    lateinit var version: String

    @JacksonXmlProperty
    var name: String? = null

    @JacksonXmlProperty
    var vendor: String? = null

    @JacksonXmlProperty
    @Column(length=1000000)
    var description: String? = null

    @JacksonXmlProperty(localName = "change-notes")
    var changeNotes: String? = null

    @JacksonXmlProperty
    var depends: String? = null

    @JacksonXmlProperty(localName = "download-url")
    var downloadUrl: String? = null
        get() = field ?: url


    @JacksonXmlProperty(localName = "idea-version")
    var ideaVersion: String? = null

}