package ru.yarsu.web.handlers.specialist

import org.http4k.core.HttpHandler
import org.http4k.core.Request
import org.http4k.core.Response
import org.http4k.core.Status
import org.http4k.core.with
import org.http4k.lens.RequestContextLens
import ru.yarsu.domain.entities.Degree
import ru.yarsu.domain.entities.Permissions
import ru.yarsu.domain.entities.SPECIALIST_ID
import ru.yarsu.domain.entities.Specialist
import ru.yarsu.domain.operations.degree.AddDegreeOperation
import ru.yarsu.domain.operations.degree.GetMainDegreesOperation
import ru.yarsu.domain.operations.specialist.CheckUniquenessOfLoginOperation
import ru.yarsu.domain.operations.specialist.UpdateSpecialistOperation
import ru.yarsu.web.lenses.DegreeLenses
import ru.yarsu.web.lenses.SpecialistLenses
import ru.yarsu.web.lenses.UniversalLenses
import ru.yarsu.web.models.NewSpecialistVM
import ru.yarsu.web.templates.ContextAwareViewRender
import java.time.LocalDateTime

class SaveSpecialistHandler(
    private val htmlView: ContextAwareViewRender,
    private val getAuthUser: RequestContextLens<Specialist?>,
    private val updateSpecialist: UpdateSpecialistOperation,
    private val addDegree: AddDegreeOperation,
    private val getMainDegrees: GetMainDegreesOperation,
    private val checkUniquenessOfLogin: CheckUniquenessOfLoginOperation,
    private val specialistLenses: SpecialistLenses,
    private val degreeLenses: DegreeLenses,
) : HttpHandler {
    override fun invoke(request: Request): Response {
        val specialist = getAuthUser(request) ?: return Response(Status.NOT_FOUND)
        val specialistId = UniversalLenses.lensOrNull(UniversalLenses.idLens, request)
        if (specialistId != specialist.id) {
            if (!Permissions(specialist.permissions).manageUsers) {
                return Response(Status.NOT_FOUND)
            }
        }
        var form = specialistLenses.allSpecialistFormLenses(request)
        var haveErrors = false
        val password = UniversalLenses.lensOrNull(SpecialistLenses.passwordField, form)
        if (password != null && password != UniversalLenses.lensOrNull(SpecialistLenses.passwordDuplicateField, form)) {
            form = form.plus("passwordsNotEquals" to "Пароли не совпадают")
            haveErrors = true
        }

        val login = UniversalLenses.lensOrNull(SpecialistLenses.loginField, form)
        if (!checkUniquenessOfLogin.checkUniqueness(login)) {
            form = form.plus("loginIsNotUnique" to "Пользователь с данным логином уже существует")
            haveErrors = true
        }

        if (form.errors.isNotEmpty() || password == null || login == null || haveErrors) {
            return Response(Status.OK).with(
                htmlView(request) of
                    NewSpecialistVM(
                        getMainDegrees.getMainDegrees(),
                        form,
                        true,
                    ),
            )
        }

        val registerDate = LocalDateTime.now()
        val mainDegree = degreeLenses.mainDegreeField(form)
        val degrees = mutableListOf<Int>()
        degrees.add(mainDegree)
        val courseDegree = DegreeLenses.courseDegreeField(form)

        if (courseDegree != null) {
            for (degree in courseDegree) {
                degrees.add(addDegree.add(Degree(-1, "course", degree)))
            }
        }

        return Response(Status.FOUND).header(
            "Location",
            "/users/${
                updateSpecialist.update(
                    Specialist(
                        specialistId ?: -1,
                        SpecialistLenses.fcsField(form),
                        degrees,
                        SpecialistLenses.phoneField(form),
                        SpecialistLenses.vkidField(form),
                        login,
                        password,
                        specialistId?.let { specialist.registerDate } ?: registerDate,
                        specialistId?.let { specialist.permissions } ?: SPECIALIST_ID,
                    ),
                )
            }",
        )
    }
}
