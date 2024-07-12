package ru.yarsu.domain.storages

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

class StoragesOperationsAndMethods(
    announcementStorage: AnnouncementStorage,
    specialistStorage: SpecialistStorage,
    categoryStorage: CategoryStorage,
    degreeStorage: DegreeStorage,
    val salt: String,
) {
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
    val updateSpecialist = UpdateSpecialistOperationImpl(specialistStorage)
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

    companion object {
        fun initAnnouncementStorage(announcements: List<Announcement>): AnnouncementStorage {
            val storage = AnnouncementStorage(announcements)
            return storage
        }

        fun initSpecialistStorage(specialists: List<Specialist>): SpecialistStorage {
            val storage = SpecialistStorage(specialists)
            return storage
        }

        fun initCategoryStorage(categories: List<Category>): CategoryStorage {
            val storage = CategoryStorage(categories)
            return storage
        }

        fun initDegreeStorage(degrees: List<Degree>): DegreeStorage {
            val storage = DegreeStorage(degrees)
            return storage
        }
    }
}
