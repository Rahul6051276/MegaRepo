package com.mega

import android.content.Context
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.node.ObjectNode
import com.fasterxml.jackson.databind.node.TextNode
import com.lagradost.cloudstream3.app
import com.lagradost.cloudstream3.plugins.CloudstreamPlugin
import com.lagradost.cloudstream3.plugins.Plugin
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
            // अगर रेपो पहले से है तो छोड़ दें
            if (addedRepositories.any { it.url == url }) return@forEach

            try {
                val repo = RepositoryManager.parseRepository(url)
                val name = repo?.name ?: "No name"
                val iconUrl = repo?.iconUrl
                val data = RepositoryData(iconUrl, name, url)
                RepositoryManager.addRepository(data)
            } catch (e: Throwable) {
                // फेल होने पर बिना आइकन के कोशिश करें
                try {
                    RepositoryManager.addRepository(RepositoryData(null, "Repo", url))
                } catch (_: Throwable) {}
            }
        }
    }

    private suspend fun getRepositories(): List<String> {
        return try {
            // आपका रायपुर वाला मास्टर लिंक
            val text = app.get("https://raw.githubusercontent.com/Rahul6051276/cs-repos/refs/heads/master/repos-db.json").text
            val mapper = ObjectMapper()
            val tree = mapper.readTree(text)

            tree.mapNotNull { node ->
                when (node) {
                    is TextNode -> node.asText()
                    is ObjectNode -> node.get("url")?.asText()
                    else -> null
                }
            }
        } catch (e: Throwable) {
            emptyList()
        }
    }
}
