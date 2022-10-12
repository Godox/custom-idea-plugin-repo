package fr.godox.customjetbrainspluginrepo.repository

import fr.godox.customjetbrainspluginrepo.model.PluginEntity
import org.springframework.data.repository.CrudRepository

interface PluginRepository : CrudRepository<PluginEntity, String> {
}