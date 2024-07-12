package ru.yarsu.web.handlers.category

import org.http4k.core.HttpHandler
import org.http4k.core.Request
import org.http4k.core.Response
import org.http4k.core.Status
import org.http4k.core.with
import ru.yarsu.domain.entities.Category
import ru.yarsu.domain.operations.category.UpdateCategoryOperation
import ru.yarsu.web.lenses.CategoryLenses
import ru.yarsu.web.lenses.UniversalLenses
import ru.yarsu.web.models.NewCategoryVM
import ru.yarsu.web.templates.ContextAwareViewRender

class SaveCategoryHandler(
    private val htmlView: ContextAwareViewRender,
    private val updateCategory: UpdateCategoryOperation,
) : HttpHandler {
    override fun invoke(request: Request): Response {
        val form = CategoryLenses.categoryFormLenses(request)
        if (form.errors.isNotEmpty()) {
            return Response(Status.OK).with(
                htmlView(request) of
                    NewCategoryVM(
                        form,
                    ),
            )
        }

        val categoryName = CategoryLenses.categoryNameField(form)
        val needLicense = CategoryLenses.needLicenceField(form)

        val categoryId = UniversalLenses.lensOrNull(UniversalLenses.idLens, request) ?: -1

        return Response(Status.FOUND).header(
            "Location",
            "/categories/${
                updateCategory.update(
                    Category(
                        categoryId,
                        categoryName,
                        needLicense ?: false,
                    ),
                )
            }",
        )
    }
}
