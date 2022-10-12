package fr.godox.customjetbrainspluginrepo.service

import fr.godox.customjetbrainspluginrepo.model.PluginEntity
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import java.io.File
import javax.persistence.EntityManager
import javax.persistence.PersistenceContext
import javax.transaction.Transactional

@Service
class PluginsManagerCrud : PluginsManager {

    @Autowired
    @PersistenceContext
    lateinit var repo: EntityManager

    override fun getAllPluginList(): List<PluginEntity> {
        return repo.createQuery("Select e from PluginEntity e", PluginEntity::class.java).resultList
    }

    @Transactional
    override fun addPlugin(plugin: PluginEntity, file: File): Boolean {
        return runCatching {
            repo.persist(plugin)
            repo.flush()
            file.renameTo(File("./pluginsRepo/${plugin.pluginId}.zip"))
        }.isSuccess
    }

    override fun getPluginById(id: Long): PluginEntity? {
        return runCatching {
            repo.createQuery("select e from PluginEntity e where e.pluginId = :id", PluginEntity::class.java)
                .setParameter("id", id).singleResult
        }.getOrNull()
    }

}