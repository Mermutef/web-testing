package ru.yarsu.web.contexts

import org.http4k.core.RequestContexts
import org.http4k.lens.RequestContextKey
import org.http4k.lens.RequestContextLens
import ru.yarsu.domain.entities.Category
import ru.yarsu.domain.entities.Permissions
import ru.yarsu.domain.entities.Specialist
import ru.yarsu.web.templates.ContextAwareViewRender

class ContextTools(
    htmlView: ContextAwareViewRender,
) {
    val appContexts = RequestContexts()
    val allCategoriesLens: RequestContextLens<List<Category>> =
        RequestContextKey
            .required(appContexts, "allCategories")
    val userAuthLens: RequestContextLens<Specialist?> =
        RequestContextKey
            .optional(appContexts, "authUser")
    val userPermissionsLens: RequestContextLens<Permissions> =
        RequestContextKey
            .required(appContexts, "permissions")
    val roleNameLens: RequestContextLens<String?> =
        RequestContextKey.optional(appContexts, "roleName")
    val htmlViewWithContexts =
        htmlView.associateContextLenses(
            mapOf(
                "allCategories" to allCategoriesLens,
                "authUser" to userAuthLens,
                "permissions" to userPermissionsLens,
                "roleName" to roleNameLens,
            ),
        )
}
