package fr.godox.customjetbrainspluginrepo.service

import fr.godox.customjetbrainspluginrepo.model.PluginEntity
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Service
import java.io.File

interface PluginsManager {

    fun getAllPluginList(): List<PluginEntity>

    fun addPlugin(plugin: PluginEntity, file: File): Boolean

    fun getPluginById(id: Long): PluginEntity?

}