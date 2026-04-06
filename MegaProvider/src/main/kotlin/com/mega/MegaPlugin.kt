package com.mega

import com.lagradost.cloudstream3.plugins.CloudstreamPlugin
import com.lagradost.cloudstream3.plugins.Plugin
import android.content.Context
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.node.ObjectNode
import com.fasterxml.jackson.databind.node.TextNode
import com.fasterxml.jackson.module.kotlin.treeToValue
import com.lagradost.cloudstream3.app
import com.lagradost.cloudstream3.mapper
import com.lagradost.cloudstream3.plugins.RepositoryManager
import com.lagradost.cloudstream3.ui.settings.extensions.RepositoryData
import com.lagradost.cloudstream3.utils.Coroutines.ioSafe

@CloudstreamPlugin
class MegaPlugin : Plugin() {
    override fun load(context: Context) {
        ioSafe {
            val repositories = getRepositories()
            addRepositories(repositories)
        }
    }

    private suspend fun addRepositories(repositories: List<String>) {
        val addedRepositories = RepositoryManager.getRepositories()
        repositories.forEach { url ->
            // अगर रेपो पहले से मौजूद है, तो उसे दोबारा न जोड़ें (Early exit)
            if (addedRepositories.any { it.url == url }) return@forEach

            val repo = RepositoryManager.parseRepository(url)
            val name = repo?.name ?: "No name"
            try {
                val iconUrl = repo?.iconUrl
                val data = RepositoryData(iconUrl, name, url)
                RepositoryManager.addRepository(data)
            } catch (_: Throwable) {
                val data = RepositoryData(name, url)
                RepositoryManager.addRepository(data)
            }
        }
    }

    private suspend fun getRepositories(): List<String> {
        data class VerifiedRepo(
            val url: String? = null,
            val verified: Boolean? = null
        )

        // यहाँ आपका रायपुर वाला मास्टर लिंक है जो अब "धक्का" मारेगा
        val text =
            app.get("https://raw.githubusercontent.com/Rahul6051276/cs-repos/refs/heads/master/repos-db.json").text

        // ऑब्जेक्ट्स और स्ट्रिंग्स दोनों को पार्स करने के लिए
        val tree = ObjectMapper().readTree(text)

        return tree.mapNotNull {
            when (it) {
                is TextNode -> mapper.treeToValue<String>(it)
                is ObjectNode -> mapper.treeToValue<VerifiedRepo>(it).url
                else -> null
            }
        }
    }
}
