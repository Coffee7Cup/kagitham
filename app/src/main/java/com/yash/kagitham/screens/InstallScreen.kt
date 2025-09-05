package com.yash.kagitham.screens

import android.util.Log
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Extension
import androidx.compose.material.icons.outlined.RestartAlt
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import com.google.gson.Gson
import com.yash.kagitham.db.PluginRepo.MetaDataPluginEntity
import com.yash.kagitham.db.PluginRepo.MetaDataPluginDB
import com.yash.kagitham.db.WidgetsRepo.WidgetDB
import com.yash.kagitham.db.WidgetsRepo.WidgetEntity
import com.yash.sdk.AppRegistry
import com.yash.sdk.unzipPaper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.File
import java.io.FileOutputStream
import java.io.FileReader

// ---------- Data ----------
data class GitHubFile(val name: String, val download_url: String)
data class MetaDataFile(
    val name: String,
    val author: String,
    val version: String,
    val id: String,
    val entryPoint: String,
    val widgets: List<String>,  // ✅ keep it a list here
    val apiClass: String,
)

private const val GITHUB_PLUGINS_URL =
    "https://api.github.com/repos/Coffee7Cup/plugins/contents?ref=main"
private const val RAW_PLUGINS_URL =
    "https://raw.githubusercontent.com/Coffee7Cup/plugins/main"

// ---------- Plugin Downloader ----------
object PluginDownloader {
    private val client = OkHttpClient()
    private val appContext = AppRegistry.getAppContext()
    private val pluginsDir = File(appContext.filesDir, "zippedFiles")

    suspend fun download(
        fileName: String,
        onProgress: (Int) -> Unit
    ): String = withContext(Dispatchers.IO) {
        val request = Request.Builder()
            .url("$RAW_PLUGINS_URL/$fileName")
            .build()

        client.newCall(request).execute().use { response ->
            if (!response.isSuccessful) throw Exception("HTTP ${response.code}: ${response.message}")

            val body = response.body ?: throw Exception("Empty body")
            val totalBytes = body.contentLength()
            val inputStream = body.byteStream()

            val file = File(pluginsDir, fileName).apply { parentFile?.mkdirs() }

            FileOutputStream(file).use { output ->
                val buffer = ByteArray(8 * 1024)
                var bytesRead: Int
                var downloaded = 0L

                while (inputStream.read(buffer).also { bytesRead = it } != -1) {
                    output.write(buffer, 0, bytesRead)
                    downloaded += bytesRead
                    if (totalBytes > 0) {
                        onProgress(((downloaded * 100) / totalBytes).toInt())
                    }
                }
            }
            inputStream.close()
            unzipPaper(file.absolutePath, fileName) // returns extracted dir path
        }
    }
}

// ---------- Setup Plugin ----------
suspend fun setupPlugin(path: String) {
    val metaFile = File(path, "metaData.json")
    val fileMeta = Gson().fromJson(FileReader(metaFile), MetaDataFile::class.java)

    // Save plugin metadata in MetaDataPluginDB
    val pluginMeta = MetaDataPluginEntity(
        name = fileMeta.name,
        author = fileMeta.author,
        version = fileMeta.version,
        id = fileMeta.id,
        entryPoint = fileMeta.entryPoint,
        widgets = Gson().toJson(fileMeta.widgets), // store as JSON string
        apiClass = fileMeta.apiClass,
        path = path
    )

    MetaDataPluginDB.getDatabase()
        .metaDataPluginDao()
        .insert(pluginMeta)

    // ---------- Setup widgets ----------
    val widgetDb = WidgetDB.getDatabase()
    val widgetDao = widgetDb.widgetDao()

    fileMeta.widgets.forEach { widgetClass ->
        val widgetEntity = WidgetEntity(
            owner = fileMeta.name,
            widgetClass = widgetClass,
            apkPath = path,
        )
        widgetDao.insert(widgetEntity)
    }
}

// ---------- ViewModel ----------
class InstallViewModel : ViewModel() {
    var plugins by mutableStateOf<List<GitHubFile>>(emptyList())
        private set
    var error by mutableStateOf<String?>(null)
    var isLoading by mutableStateOf(false)
        private set
    var installedPlugins by mutableStateOf<Set<String>>(emptySet())
        private set

    private var loadedOnce = false

    init {
        viewModelScope.launch {
            refreshInstalled()
        }
    }

    private suspend fun refreshInstalled() {
        val dao = MetaDataPluginDB.getDatabase().metaDataPluginDao()
        installedPlugins = dao.getAllPlugins().map { it.name }.toSet()
    }

    suspend fun markInstalled(pluginName: String) {
        refreshInstalled()
    }

    fun loadPlugins(force: Boolean = false) {
        if (loadedOnce && !force) return

        isLoading = true
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val request = Request.Builder().url(GITHUB_PLUGINS_URL).build()
                val client = OkHttpClient()

                val res = client.newCall(request).execute()
                res.use {
                    if (!it.isSuccessful) throw Exception("HTTP ${it.code}")

                    val json = it.body?.string() ?: throw Exception("Empty body")
                    val gson = Gson()
                    val element = com.google.gson.JsonParser.parseString(json)

                    val parsed = if (element.isJsonArray) {
                        gson.fromJson(element, Array<GitHubFile>::class.java)
                            .filter { f -> f.name.endsWith(".zip") }
                    } else {
                        listOf(gson.fromJson(element, GitHubFile::class.java))
                            .filter { f -> f.name.endsWith(".zip") }
                    }

                    withContext(Dispatchers.Main) {
                        plugins = parsed
                        error = null
                        loadedOnce = true
                        isLoading = false
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    error = e.message
                    isLoading = false
                }
                Log.e("LP", "Error loading plugins", e)
            }
        }
    }
}

// ---------- UI ----------
@Composable
fun Install(viewModel: InstallViewModel = viewModel()) {
    val scope = rememberCoroutineScope()
    var stateMap by rememberSaveable {
        mutableStateOf<Map<String, Pair<Int, String?>>>(emptyMap())
    }

    LaunchedEffect(Unit) { viewModel.loadPlugins() }

    Column(
        modifier = Modifier.fillMaxSize().padding(20.dp, 16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().height(80.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("Install", fontSize = 32.sp)
            IconButton(onClick = { viewModel.loadPlugins(force = true) }) {
                Icon(Icons.Outlined.RestartAlt, "Refresh", tint = MaterialTheme.colorScheme.primary)
            }
        }

        when {
            viewModel.isLoading -> CenterMessage("Loading...")
            viewModel.error != null -> CenterMessage(viewModel.error!!, Color.Red)
            viewModel.plugins.isEmpty() -> CenterMessage("No plugins found")
            else -> LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(viewModel.plugins) { plugin ->
                    val (progress, error) = stateMap[plugin.name] ?: (0 to null)
                    val pluginName = plugin.name.removeSuffix(".zip")
                    val isInstalled = pluginName in viewModel.installedPlugins

                    PluginCard(
                        plugin = plugin,
                        progress = progress,
                        error = error,
                        isInstalled = isInstalled,
                        onInstallClick = {
                            scope.launch {
                                try {
                                    val path = PluginDownloader.download(
                                        fileName = plugin.name,
                                        onProgress = { value ->
                                            stateMap = stateMap.toMutableMap().apply {
                                                this[plugin.name] = value to null
                                            }
                                        }
                                    )
                                    setupPlugin(path)
                                    viewModel.markInstalled(pluginName) // ✅ mark as installed
                                } catch (e: Exception) {
                                    stateMap = stateMap.toMutableMap().apply {
                                        this[plugin.name] =
                                            progress to (e.message ?: "Unknown error")
                                    }
                                    Log.e("Install", "Error installing ${plugin.name}", e)
                                }
                            }
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun CenterMessage(msg: String, color: Color = Color.Unspecified) {
    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text(msg, fontSize = 18.sp, color = color)
    }
}

@Composable
fun PluginCard(
    plugin: GitHubFile,
    progress: Int,
    error: String?,
    isInstalled: Boolean,
    onInstallClick: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = if (isInstalled) Color(0xFFDFFFD6) // ✅ green tint
            else MaterialTheme.colorScheme.surfaceVariant
        ),
        elevation = CardDefaults.cardElevation(4.dp),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(Modifier.fillMaxWidth().padding(10.dp)) {
            Row(
                Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        Icons.Default.Extension,
                        "Plugin",
                        Modifier.size(36.dp),
                        tint = if (isInstalled) Color.Green
                        else MaterialTheme.colorScheme.primary
                    )
                    Spacer(Modifier.width(12.dp))
                    Text(plugin.name.removeSuffix(".zip"), fontSize = 20.sp)
                }

                if (isInstalled) {
                    Text(
                        "Installed",
                        color = Color.Green,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                } else {
                    IconButton(onClick = onInstallClick) {
                        Icon(
                            Icons.Default.Download,
                            "Install",
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(30.dp)
                        )
                    }
                }
            }

            error?.let {
                Spacer(Modifier.height(8.dp))
                Text("Error: $it", color = Color.Red, fontSize = 14.sp)
            }

            if (progress in 1..99 && !isInstalled) {
                Spacer(Modifier.height(10.dp))
                val totalHashes = 20
                val filled = (progress / 5).coerceAtMost(totalHashes)
                val bar = "#".repeat(filled) + "_".repeat(totalHashes - filled)
                Text(
                    "$bar $progress%",
                    color = Color.Green,
                    fontSize = 18.sp,
                    fontStyle = FontStyle.Italic
                )
            }
        }
    }
}

