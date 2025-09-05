package com.yash.kagitham.screens

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.Widgets
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.RestartAlt
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.yash.kagitham.db.PluginRepo.MetaDataPluginEntity
import com.yash.kagitham.db.PluginRepo.MetaDataPluginDB
import com.yash.kagitham.db.WidgetsRepo.WidgetDB
import com.yash.kagitham.db.WidgetsRepo.WidgetEntity
import com.yash.kagitham.utils.PaperInstanceRegistry
import com.yash.sdk.ContextRegistry
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.random.Random

class WidgetsViewModel : ViewModel() {
    var selectedWidgets by mutableStateOf<List<WidgetEntity>>(emptyList())
        private set

    var availablePlugins by mutableStateOf<List<MetaDataPluginEntity>>(emptyList())
        private set

    var showAvailable by mutableStateOf(false)
    var errorMessage by mutableStateOf<String?>(null)

    fun loadSelectedWidgets() {
        viewModelScope.launch {
            try {
                val dao = WidgetDB.getDatabase().widgetDao()
                selectedWidgets = dao.getAllWidgets()
            } catch (e: Exception) {
                errorMessage = "Failed to load widgets: ${e.localizedMessage}"
            }
        }
    }

    fun loadAvailableWidgets() {
        viewModelScope.launch {
            try {
                val dao = MetaDataPluginDB.getDatabase().metaDataPluginDao()
                availablePlugins = dao.getAllPlugins()
            } catch (e: Exception) {
                errorMessage = "Failed to load plugins: ${e.localizedMessage}"
            }
        }
    }

    fun removeWidget(widget: WidgetEntity) {
        viewModelScope.launch {
            try {
                WidgetDB.getDatabase().widgetDao().delete(widget)
                loadSelectedWidgets()
            } catch (e: Exception) {
                errorMessage = "Failed to remove widgetClass: ${e.localizedMessage}"
            }
        }
    }

    fun addWidget(owner: String, widgetClass: String, apkPath: String) {
        viewModelScope.launch {
            try {
                val entity = WidgetEntity(
                    owner = owner,
                    widgetClass = widgetClass.substringAfterLast("."),
                    apkPath = apkPath,
                )
                WidgetDB.getDatabase().widgetDao().insert(entity)
                loadSelectedWidgets()
                showAvailable = false
            } catch (e: Exception) {
                errorMessage = "Failed to add widgetClass: ${e.localizedMessage}"
            }
        }
    }

    fun clearError() {
        errorMessage = null
    }
}

@Composable
fun Widgets(viewModel: WidgetsViewModel = viewModel()) {
    val selected = viewModel.selectedWidgets
    val plugins = viewModel.availablePlugins
    val showAvailable = viewModel.showAvailable
    val errorMessage = viewModel.errorMessage

    LaunchedEffect(Unit) {
        viewModel.loadSelectedWidgets()
    }

    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp)
    ) {
        Spacer(modifier = Modifier.height(13.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("Widgets", fontSize = 32.sp)

            IconButton(onClick = {
                if (!showAvailable) viewModel.loadAvailableWidgets()
                viewModel.showAvailable = !showAvailable
            }) {
                Icon(
                    imageVector = if (showAvailable) Icons.Outlined.RestartAlt else Icons.Outlined.Add,
                    contentDescription = "Toggle Widgets",
                    tint = MaterialTheme.colorScheme.primary
                )
            }
        }

        Spacer(Modifier.height(8.dp))

        errorMessage?.let { msg ->
            Text(
                text = msg,
                color = Color.Red,
                modifier = Modifier.padding(8.dp)
            )
            LaunchedEffect(msg) {
                delay(3000)
                viewModel.clearError()
            }
        }

        if (showAvailable) {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                items(plugins) { plugin ->
                    Text(
                        plugin.name,
                        fontSize = 20.sp,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Spacer(Modifier.height(6.dp))

                    val widgetList: List<String> = remember(plugin.widgets) {
                        try {
                            val type = object : TypeToken<List<String>>() {}.type
                            Gson().fromJson(plugin.widgets, type) ?: emptyList()
                        } catch (e: Exception) {
                            emptyList()
                        }
                    }

                    WidgetGrid(
                        widgets = widgetList,
                        onClick = { widget ->
                            viewModel.addWidget(plugin.name, widget, plugin.path)
                        }
                    )
                }
            }
        } else {
            if (selected.isEmpty()) {
                Box(
                    Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text("No widgets selected", color = Color.Gray)
                }
            } else {
                LazyVerticalGrid(
                    columns = GridCells.Adaptive(minSize = 150.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    modifier = Modifier.fillMaxSize()
                ) {
                    items(selected) { widgetEntity ->
                        // ✅ One context per plugin
                        val pluginCtx = remember(widgetEntity.owner) {
                            ContextRegistry.getPluginContext(widgetEntity.owner)
                        }

                        // ✅ One instance per widget
                        val widgetInstance = remember(widgetEntity.id) {
                            PaperInstanceRegistry.getWidgetInstance(widgetEntity)
                        }

                        // 🔑 Render the widget with its own context
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            elevation = CardDefaults.cardElevation(6.dp),
                            onClick = {  }
                        ) {
                            widgetInstance.Content(pluginCtx)
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun WidgetGrid(
    widgets: List<String>,
    onClick: (String) -> Unit,
    onDelete: ((String) -> Unit)? = null
) {
    LazyVerticalGrid(
        columns = GridCells.Adaptive(minSize = 150.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp),
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        modifier = Modifier.fillMaxSize()
    ) {
        items(widgets) { widget ->
            WidgetItem(
                name = widget,
                onClick = { onClick(widget) },
                onDeleteClick = { onDelete?.invoke(widget) }
            )
        }
    }
}

@Composable
fun WidgetItem(
    name: String,
    onClick: () -> Unit,
    onDeleteClick: (() -> Unit)? = null
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.medium,
        elevation = CardDefaults.cardElevation(6.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
        onClick = { onClick() }
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.Widgets,
                contentDescription = "Widget",
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(32.dp)
            )
            Spacer(Modifier.width(12.dp))
            Text(
                text = name,
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.weight(1f)
            )
            onDeleteClick?.let {
                IconButton(onClick = it) {
                    Icon(
                        imageVector = Icons.Default.DeleteOutline,
                        contentDescription = "Remove",
                        tint = Color.Red
                    )
                }
            }
        }
    }
}
