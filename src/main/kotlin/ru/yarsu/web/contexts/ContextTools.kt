package ru.yarsu.web.contexts

import org.http4k.core.RequestContexts
import org.http4k.lens.RequestContextKey
import org.http4k.lens.RequestContextLens
import ru.yarsu.domain.entities.AuthUser
import ru.yarsu.domain.entities.Category
import ru.yarsu.domain.entities.Permissions
import ru.yarsu.web.templates.ContextAwareViewRender

class ContextTools(
    htmlView: ContextAwareViewRender,
) {
    val appContexts = RequestContexts()
    val allCategoriesLens: RequestContextLens<List<Category>> =
        RequestContextKey
            .required(appContexts, "allCategories")
    val userAuthLens: RequestContextLens<AuthUser?> =
        RequestContextKey
            .optional(appContexts, "authUser")
    val userPermissionsLens: RequestContextLens<Permissions> =
        RequestContextKey
            .required(appContexts, "permissions")
    val roleNameLens: RequestContextLens<String?> =
        RequestContextKey.optional(appContexts, "roleName")
    val canEditLens: RequestContextLens<Boolean?> =
        RequestContextKey.optional(appContexts, "canEdit")
    val editModeLens: RequestContextLens<Boolean?> =
        RequestContextKey.optional(appContexts, "editMode")
    val htmlViewWithContexts =
        htmlView.associateContextLenses(
            mapOf(
                "allCategories" to allCategoriesLens,
                "authUser" to userAuthLens,
                "permissions" to userPermissionsLens,
                "roleName" to roleNameLens,
                "canEdit" to canEditLens,
                "editMode" to editModeLens,
            ),
        )
}
