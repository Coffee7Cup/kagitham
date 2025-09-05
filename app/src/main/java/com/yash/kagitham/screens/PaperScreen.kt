package com.yash.kagitham.screens

import android.content.Context
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.Extension
import androidx.compose.material.icons.outlined.RestartAlt
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.yash.kagitham.db.PluginRepo.MetaDataPluginDB
import com.yash.kagitham.db.PluginRepo.MetaDataPluginEntity
import com.yash.kagitham.db.PluginRepo.MetaDataPluginDao
import com.yash.kagitham.db.PluginRepo.PluginRepo
import com.yash.kagitham.utils.PaperInstanceRegistry
import com.yash.sdk.ContextRegistry
import com.yash.sdk.PaperEntryPoint
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class PaperViewModel(
    private val pluginDao: MetaDataPluginDao
) : ViewModel() {

    private val _plugins = MutableStateFlow<List<MetaDataPluginEntity>>(emptyList())
    val plugins: StateFlow<List<MetaDataPluginEntity>> = _plugins

    // Store plugin errors (pluginName → error message)
    private val _pluginErrors = MutableStateFlow<Map<String, String>>(emptyMap())
    val pluginErrors: StateFlow<Map<String, String>> = _pluginErrors

    // Currently loaded plugin instance + its context
    private val _loadedPlugin = MutableStateFlow<Pair<PaperEntryPoint, Context>?>(null)
    val loadedPlugin: StateFlow<Pair<PaperEntryPoint, Context>?> = _loadedPlugin

    init {
        refreshPlugins()
    }

    fun refreshPlugins() {
        viewModelScope.launch {
            val pluginsFromDb = pluginDao.getAllPlugins()
            _plugins.value = pluginsFromDb
        }
    }

    fun deletePlugin(plugin: MetaDataPluginEntity) {
        viewModelScope.launch {
            PluginRepo.deleteByName(plugin.name)
            _pluginErrors.value = _pluginErrors.value - plugin.name // remove old errors
            refreshPlugins()
        }
    }

    fun loadPlugin(plugin: MetaDataPluginEntity) {
        viewModelScope.launch {
            try {
                val pluginCtx = ContextRegistry.getPluginContext(plugin.name)
                val instance = PaperInstanceRegistry.getPaperInstance(plugin)

                if (instance is PaperEntryPoint) {
                    _loadedPlugin.value = instance to pluginCtx
                    _pluginErrors.value = _pluginErrors.value - plugin.name
                } else {
                    throw IllegalStateException("Plugin entry point is not a PaperEntryPoint")
                }
            } catch (e: Exception) {
                _pluginErrors.value = _pluginErrors.value + (plugin.name to (e.message ?: "Unknown error"))
            }
        }
    }

    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val db = MetaDataPluginDB.getDatabase()
                PaperViewModel(
                    pluginDao = db.metaDataPluginDao()
                )
            }
        }
    }
}

@Composable
fun Paper(viewModel: PaperViewModel = viewModel(factory = PaperViewModel.Factory)) {
    val plugins by viewModel.plugins.collectAsState()
    val errors by viewModel.pluginErrors.collectAsState()
    val loadedPlugin by viewModel.loadedPlugin.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Spacer(modifier = Modifier.height(13.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Paper",
                fontSize = 32.sp,
            )

            IconButton(onClick = { viewModel.refreshPlugins() }) {
                Icon(
                    imageVector = Icons.Outlined.RestartAlt,
                    contentDescription = "Refresh",
                    tint = MaterialTheme.colorScheme.primary
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(10.dp),
            modifier = Modifier.fillMaxSize()
        ) {
            items(plugins) { plugin ->
                FileItem(
                    plugin = plugin,
                    error = errors[plugin.name],
                    onDeleteClick = { viewModel.deletePlugin(plugin) },
                    onCardClick = { viewModel.loadPlugin(plugin) }
                )
            }
        }

        // 👉 Render currently loaded plugin (if any)
        loadedPlugin?.let { (instance, ctx) ->
            instance.renderWithHome(ctx)
        }
    }
}

@Composable
fun FileItem(
    plugin: MetaDataPluginEntity,
    error: String?,
    onDeleteClick: () -> Unit,
    onCardClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onCardClick() },
        shape = MaterialTheme.shapes.medium,
        elevation = CardDefaults.cardElevation(4.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.Extension,
                    contentDescription = "Plugin",
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(32.dp)
                )

                Spacer(modifier = Modifier.width(12.dp))

                Text(
                    text = plugin.name,
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier.weight(1f)
                )

                IconButton(onClick = onDeleteClick) {
                    Icon(
                        imageVector = Icons.Default.DeleteOutline,
                        contentDescription = "Delete",
                        tint = Color.Red,
                    )
                }
            }

            // Show error if present
            if (error != null) {
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = error,
                    color = Color.Red,
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }
    }
}
