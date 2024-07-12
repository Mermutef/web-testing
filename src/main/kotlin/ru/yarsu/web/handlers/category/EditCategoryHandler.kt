package ru.yarsu.web.handlers.category

import org.http4k.core.HttpHandler
import org.http4k.core.Request
import org.http4k.core.Response
import org.http4k.core.Status
import org.http4k.core.with
import org.http4k.lens.WebForm
import ru.yarsu.domain.operations.category.GetCategoryOperation
import ru.yarsu.web.lenses.CategoryLenses
import ru.yarsu.web.lenses.UniversalLenses
import ru.yarsu.web.models.NewCategoryVM
import ru.yarsu.web.templates.ContextAwareViewRender

class EditCategoryHandler(
    private val htmlView: ContextAwareViewRender,
    private val getCategory: GetCategoryOperation,
    private val categoryLenses: CategoryLenses,
) : HttpHandler {
    override fun invoke(request: Request): Response {
        val categoryId =
            UniversalLenses.lensOrNull(UniversalLenses.idLens, request)
                ?: return Response(Status.NOT_FOUND)

        val category =
            getCategory.get(categoryId)
                ?: return Response(Status.NOT_FOUND)

        val webForm =
            WebForm().with(
                categoryLenses.categoryField of category.id,
                CategoryLenses.categoryNameField of category.ru,
                CategoryLenses.needLicenceField of category.needLicense,
            )

        return Response(Status.OK).with(
            htmlView(request) of
                NewCategoryVM(
                    webForm,
                ),
        )
    }
}
