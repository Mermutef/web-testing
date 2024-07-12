package ru.yarsu.web.handlers.specialist

import org.http4k.core.HttpHandler
import org.http4k.core.Request
import org.http4k.core.Response
import org.http4k.core.Status
import org.http4k.core.cookie.invalidateCookie
import org.http4k.core.with
import org.http4k.lens.RequestContextLens
import ru.yarsu.domain.entities.AuthUser
import ru.yarsu.domain.entities.Degree
import ru.yarsu.domain.entities.EntitiesCheckAndHelpMethods
import ru.yarsu.domain.entities.Specialist
import ru.yarsu.domain.operations.degree.AddDegreeOperation
import ru.yarsu.domain.operations.degree.GetMainDegreesOperation
import ru.yarsu.domain.operations.specialist.CheckUniquenessOfLoginOperation
import ru.yarsu.domain.operations.specialist.GetSpecialistOperation
import ru.yarsu.domain.operations.specialist.UpdateSpecialistOperation
import ru.yarsu.web.lenses.DegreeLenses
import ru.yarsu.web.lenses.SpecialistLenses
import ru.yarsu.web.lenses.UniversalLenses
import ru.yarsu.web.models.NewSpecialistVM
import ru.yarsu.web.templates.ContextAwareViewRender
import java.time.LocalDateTime

class SaveSpecialistHandler(
    private val htmlView: ContextAwareViewRender,
    private val getUser: RequestContextLens<AuthUser?>,
    private val getSpecialist: GetSpecialistOperation,
    private val updateSpecialist: UpdateSpecialistOperation,
    private val addDegree: AddDegreeOperation,
    private val getMainDegrees: GetMainDegreesOperation,
    private val checkUniquenessOfLogin: CheckUniquenessOfLoginOperation,
    private val specialistLenses: SpecialistLenses,
    private val degreeLenses: DegreeLenses,
    private val salt: String,
) : HttpHandler {
    override fun invoke(request: Request): Response {
        var form = specialistLenses.allSpecialistFormLenses(request)
        val user = getUser(request)
        var haveErrors = false
        val password = UniversalLenses.lensOrNull(SpecialistLenses.passwordField, form)
        if (password != null &&
            password != UniversalLenses.lensOrNull(SpecialistLenses.passwordDuplicateField, form)
        ) {
            form = form.plus("passwordsNotEquals" to "Пароли не совпадают")
            haveErrors = true
        }

        val login = UniversalLenses.lensOrNull(SpecialistLenses.loginField, form)
        if (user == null) {
            if (login != null && !checkUniquenessOfLogin.checkUniqueness(login)) {
                form = form.plus("loginIsNotUnique" to "Пользователь с данным логином уже существует")
                haveErrors = true
            }
        }

        if (form.errors.isNotEmpty() ||
            password == null ||
            login == null ||
            haveErrors
        ) {
            return Response(Status.OK).with(
                htmlView(request) of
                    NewSpecialistVM(
                        getMainDegrees.getMainDegrees(),
                        form,
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

        val specialist = getSpecialist.get(user?.id ?: -1)

        updateSpecialist.update(
            Specialist(
                specialist?.id ?: -1,
                SpecialistLenses.fcsField(form),
                degrees,
                SpecialistLenses.phoneField(form),
                SpecialistLenses.vkidField(form),
                login,
                EntitiesCheckAndHelpMethods.saltPassword(password, salt),
                specialist?.registerDate ?: registerDate,
                SpecialistLenses.roleField(form),
            ),
        )

        return Response(Status.FOUND).invalidateCookie("auth").header(
            "Location",
            "/logout",
        )
    }
}
