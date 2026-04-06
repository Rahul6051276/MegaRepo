package com.mega

import android.content.Context
import com.lagradost.cloudstream3.app
import com.lagradost.cloudstream3.plugins.CloudstreamPlugin
import com.lagradost.cloudstream3.plugins.Plugin
import com.lagradost.cloudstream3.plugins.RepositoryManager
import com.lagradost.cloudstream3.ui.settings.extensions.RepositoryData
import com.lagradost.cloudstream3.utils.Coroutines.ioSafe
import org.json.JSONArray
import org.json.JSONObject

@CloudstreamPlugin
class MegaPlugin : Plugin() {
    override fun load(context: Context) {
        ioSafe {
            // आपका रायपुर वाला मास्टर लिंक
            val masterUrl = "https://raw.githubusercontent.com/Rahul6051276/cs-repos/refs/heads/master/repos-db.json"
            val response = app.get(masterUrl).text
            
            // JSON को एकदम देसी तरीके से पार्स करना (बिना किसी बाहरी लाइब्रेरी के)
            val jsonArray = JSONArray(response)
            val addedRepos = RepositoryManager.getRepositories()

            for (i in 0 until jsonArray.length()) {
                val item = jsonArray.get(i)
                val repoUrl = if (item is String) item else (item as JSONObject).optString("url")
                
                if (!repoUrl.isNullOrEmpty() && addedRepos.none { it.url == repoUrl }) {
                    try {
                        val parsed = RepositoryManager.parseRepository(repoUrl)
                        val data = RepositoryData(parsed?.iconUrl, parsed?.name ?: "Repo", repoUrl)
                        RepositoryManager.addRepository(data)
                    } catch (e: Exception) {
                        try {
                            RepositoryManager.addRepository(RepositoryData(null, "New Repo", repoUrl))
                        } catch (ex: Exception) {}
                    }
                }
            }
        }
    }
}
