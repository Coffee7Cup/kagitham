# Hey most important

in ZIP file, apk, metaData.json, apiInterface.kt(optional)

## metadata.json

```kotlin
data class MetaDataFile(
    val name: String,
    val author: String,
    val version: String,
    val id: String,
    val entryPoint: String,
    val widgets: List<String>,  // ✅ keep it a list here
    val apiClass: String,
    val apiInterface: String?
)

```
convert the above to json