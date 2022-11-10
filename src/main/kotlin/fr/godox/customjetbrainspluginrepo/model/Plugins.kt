package fr.godox.customjetbrainspluginrepo.model

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement
import java.io.Serializable

@JacksonXmlRootElement(localName = "plugins")
class Plugins(
    @JacksonXmlProperty(localName = "plugin")
    @JacksonXmlElementWrapper(useWrapping = false)
    var plugin: Iterable<CustomIdePluginDescriptor> = mutableListOf()
) : Serializable {

    companion object {
        private const val serialVersionUID = 22L
    }

}