package ru.yarsu.domain.storages

import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import ru.yarsu.domain.entities.Announcement
import ru.yarsu.domain.entities.Category
import ru.yarsu.domain.entities.Degree
import ru.yarsu.domain.entities.Specialist
import ru.yarsu.domain.operations.announcement.AnnouncementDateTimeFilterOperationImpl
import ru.yarsu.domain.operations.announcement.ClearCategoryAnnouncementsOperationImpl
import ru.yarsu.domain.operations.announcement.DeleteAnnouncementOperationImpl
import ru.yarsu.domain.operations.announcement.GetAnnouncementOperationImpl
import ru.yarsu.domain.operations.announcement.GetByCategoryOperationImpl
import ru.yarsu.domain.operations.announcement.GetSpecialistIdOperationImpl
import ru.yarsu.domain.operations.announcement.UpdateAnnouncementOperationImpl
import ru.yarsu.domain.operations.category.DeleteCategoryOperationImpl
import ru.yarsu.domain.operations.category.GetCategoryOperationImpl
import ru.yarsu.domain.operations.category.GetLexiSortedCategoriesOperationImpl
import ru.yarsu.domain.operations.category.UpdateCategoryOperationImpl
import ru.yarsu.domain.operations.degree.AddDegreeOperationImpl
import ru.yarsu.domain.operations.degree.EncodeDegreesOperationImpl
import ru.yarsu.domain.operations.degree.GetDegreeOperationImpl
import ru.yarsu.domain.operations.degree.GetMainDegreesOperationImpl
import ru.yarsu.domain.operations.specialist.AuthorizationSpecialistOperationImpl
import ru.yarsu.domain.operations.specialist.CheckUniquenessOfPasswordOperationImpl
import ru.yarsu.domain.operations.specialist.ClearSpecialistAnnouncementsOperationImpl
import ru.yarsu.domain.operations.specialist.DeleteSpecialistOperationImpl
import ru.yarsu.domain.operations.specialist.EditSpecialistOperationImpl
import ru.yarsu.domain.operations.specialist.GetSpecialistOperationImpl
import ru.yarsu.domain.operations.specialist.SpecialistDateTimeFilterOperationImpl
import ru.yarsu.domain.operations.specialist.UpdateSpecialistOperationImpl
import java.io.File
import java.io.FileNotFoundException
import kotlin.concurrent.thread

class StoragesOperationsAndMethods(
    private val path: String,
    salt: String,
) {
    private val mapper = jacksonObjectMapper()
    private val announcementStorage: AnnouncementStorage
    private val specialistStorage: SpecialistStorage
    private val categoryStorage: CategoryStorage
    private val degreeStorage: DegreeStorage

    init {
        mapper.enable(SerializationFeature.INDENT_OUTPUT)
        try {
            announcementStorage =
                AnnouncementStorage(
                    mapper.readValue<List<Announcement>>(
                        File("$path/announcements.json"),
                    ),
                )
            specialistStorage =
                SpecialistStorage(
                    mapper.readValue<List<Specialist>>(
                        File("$path/specialists.json"),
                    ),
                )
            categoryStorage =
                CategoryStorage(
                    mapper.readValue<List<Category>>(
                        File("$path/categories.json"),
                    ),
                )
            degreeStorage =
                DegreeStorage(
                    mapper.readValue<List<Degree>>(
                        File("$path/degrees.json"),
                    ),
                )
        } catch (ex: Exception) {
            when (ex) {
                is FileNotFoundException,
                is IllegalArgumentException,
                is com.fasterxml.jackson.databind.exc.ValueInstantiationException,
                is com.fasterxml.jackson.core.JsonParseException,
                -> {
                    throw IllegalArgumentException(ex.message)
                }

                else -> throw ex
            }
        }
    }

    // announcement storage operations
    val getAnnouncement = GetAnnouncementOperationImpl(announcementStorage)
    val deleteAnnouncement = DeleteAnnouncementOperationImpl(announcementStorage)
    val updateAnnouncement = UpdateAnnouncementOperationImpl(announcementStorage)
    val announcementDateTimeFilter = AnnouncementDateTimeFilterOperationImpl(announcementStorage)
    val getByCategory = GetByCategoryOperationImpl(announcementStorage)
    val clearSpecialistAnnouncements = ClearSpecialistAnnouncementsOperationImpl(announcementStorage)
    val clearCategoryAnnouncements = ClearCategoryAnnouncementsOperationImpl(announcementStorage)
    val getSpecialistId = GetSpecialistIdOperationImpl(announcementStorage)

    // specialist storage operations
    val getSpecialist = GetSpecialistOperationImpl(specialistStorage)
    val updateSpecialist = UpdateSpecialistOperationImpl(specialistStorage, salt)
    val editSpecialist = EditSpecialistOperationImpl(specialistStorage)
    val deleteSpecialist = DeleteSpecialistOperationImpl(specialistStorage)
    val checkUniquenessOfPassword = CheckUniquenessOfPasswordOperationImpl(specialistStorage)
    val authorizationSpecialist = AuthorizationSpecialistOperationImpl(specialistStorage, salt)
    val specialistDateTimeFilter = SpecialistDateTimeFilterOperationImpl(specialistStorage)

    // category storage operations
    val getCategory = GetCategoryOperationImpl(categoryStorage)
    val deleteCategory = DeleteCategoryOperationImpl(categoryStorage)
    val updateCategory = UpdateCategoryOperationImpl(categoryStorage)
    val getLexiSortedCategories = GetLexiSortedCategoriesOperationImpl(categoryStorage)

    // degree storage operations
    val getDegree = GetDegreeOperationImpl(degreeStorage)
    val addDegree = AddDegreeOperationImpl(degreeStorage)
    val encodeDegrees = EncodeDegreesOperationImpl(degreeStorage)
    val getMainDegrees = GetMainDegreesOperationImpl(degreeStorage)

    fun specialistsByCategory(categoryId: Int): Map<Int, Specialist?> {
        return getByCategory
            .getByCategory(categoryId)
            .associateBy(
                { it.specialist },
                { getSpecialist.get(it.specialist) },
            )
            .toMap()
    }

    fun regSDHook() =
        Runtime.getRuntime().addShutdownHook(
            thread(start = false) {
                mapper.writeValue(File("$path/announcements.json"), announcementStorage.getAll())
                println("Объявления сохранены")
                mapper.writeValue(File("$path/specialists.json"), specialistStorage.getAll())
                println("Специалисты сохранены")
                mapper.writeValue(File("$path/categories.json"), categoryStorage.getAll())
                println("Категории сохранены")
                mapper.writeValue(File("$path/degrees.json"), degreeStorage.getAll())
                println("Образования сохранены")
                println("Данные успешно сохранены.")
            },
        )
}
