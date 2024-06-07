package ru.yarsu.web.handlers.category

import org.http4k.core.Body
import org.http4k.core.HttpHandler
import org.http4k.core.Request
import org.http4k.core.Response
import org.http4k.core.Status
import org.http4k.core.with
import org.http4k.lens.Validator
import org.http4k.lens.webForm
import ru.yarsu.domain.operations.announcement.ClearCategoryAnnouncementsOperation
import ru.yarsu.domain.operations.category.DeleteCategoryOperation
import ru.yarsu.domain.operations.category.GetCategoryOperation
import ru.yarsu.web.lenses.UniversalLenses
import ru.yarsu.web.models.DeleteAnnouncementVM
import ru.yarsu.web.templates.ContextAwareViewRender

class DeleteCategoryHandler(
    private val htmlView: ContextAwareViewRender,
    private val clearCategoryAnnouncements: ClearCategoryAnnouncementsOperation,
    private val getCategory: GetCategoryOperation,
    private val deleteCategory: DeleteCategoryOperation,
) : HttpHandler {
    override fun invoke(request: Request): Response {
        val categoryId =
            UniversalLenses.lensOrNull(UniversalLenses.idLens, request)
                ?: return Response(Status.NOT_FOUND)

        val category =
            getCategory.get(categoryId)
                ?: return Response(Status.NOT_FOUND)

        val form =
            Body.webForm(
                Validator.Feedback,
                UniversalLenses.checkBoxField,
                UniversalLenses.checkEnteredIdLens(categoryId),
            ).toLens()(request)

        if (form.errors.isNotEmpty()) {
            return Response(Status.OK).with(
                htmlView(request) of
                    DeleteAnnouncementVM(
                        category.id,
                        form,
                    ),
            )
        }

        clearCategoryAnnouncements.clear(category.id)
        deleteCategory.delete(category.id)

        return Response(Status.FOUND).header("Location", "/")
    }
}
