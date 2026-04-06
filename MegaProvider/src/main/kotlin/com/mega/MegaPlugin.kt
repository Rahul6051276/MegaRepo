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
            try {
                val masterUrl = "https://raw.githubusercontent.com/Rahul6051276/cs-repos/refs/heads/master/repos-db.json"
                val response = app.get(masterUrl).text
                
                val jsonArray = JSONArray(response)
                val addedRepos = RepositoryManager.getRepositories()

                for (i in 0 until jsonArray.length()) {
                    val item = jsonArray.get(i)
                    val repoUrl = if (item is String) item else (item as JSONObject).optString("url")
                    
                    if (!repoUrl.isNullOrEmpty()) {
                        // चेक करें कि क्या पहले से एडेड है
                        val alreadyExists = addedRepos.any { it.url == repoUrl }
                        if (!alreadyExists) {
                            try {
                                val parsed = RepositoryManager.parseRepository(repoUrl)
                                RepositoryManager.addRepository(RepositoryData(parsed?.iconUrl, parsed?.name ?: "Repo", repoUrl))
                            } catch (e: Exception) {
                                RepositoryManager.addRepository(RepositoryData(null, "New Repo", repoUrl))
                            }
                        }
                    }
                }
            } catch (e: Exception) {
                // एरर होने पर कुछ न करें
            }
        }
    }
}
