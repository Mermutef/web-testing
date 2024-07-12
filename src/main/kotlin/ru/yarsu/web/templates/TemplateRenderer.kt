package ru.yarsu.web.templates

fun cacheRenderer(cacheIt: Boolean = true): ContextAwareTemplateRenderer {
    if (cacheIt) {
        return ContextAwarePebbleTemplates().CachingClasspath("")
    }
    return ContextAwarePebbleTemplates().HotReload("src/main/resources")
}
