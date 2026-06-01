package com.visura.di

import com.visura.data.repositories.network.authentication.DefaultFireBaseClientRepository
import com.visura.data.repositories.network.authentication.DefaultGoogleClientRepository
import com.visura.data.repositories.network.inspection.DefaultInspectionRepository
import com.visura.data.repositories.network.inspector.DefaultInspectorRepository
import com.visura.data.repositories.network.location.DefaultLocationRepository
import com.visura.data.repositories.network.property.DefaultPropertyRepository
import com.visura.domain.repositories.authentication.FireBaseClientRepository
import com.visura.domain.repositories.authentication.GoogleClientRepository
import com.visura.domain.repositories.inspection.InspectionRepository
import com.visura.domain.repositories.inspector.InspectorRepository
import com.visura.domain.repositories.location.LocationRepository
import com.visura.domain.repositories.property.PropertyRepository
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton
import com.visura.data.repositories.network.address.DefaultAddressRepository
import com.visura.domain.repositories.address.AddressRepository
import com.visura.data.repositories.network.owner.DefaultOwnerRepository
import com.visura.domain.repositories.owner.OwnerRepository
import com.visura.data.repositories.network.tenant.DefaultTenantRepository
import com.visura.domain.repositories.tenant.TenantRepository
import com.visura.data.repositories.network.realstate.DefaultRealStateRepository
import com.visura.domain.repositories.realstate.RealStateRepository
import com.visura.data.repositories.network.environment.DefaultEnvironmentRepository
import com.visura.domain.repositories.environment.EnvironmentRepository
import com.visura.data.repositories.network.checklist.DefaultChecklistRepository
import com.visura.domain.repositories.checklist.ChecklistRepository
import com.visura.data.repositories.network.report.DefaultReportRepository
import com.visura.domain.repositories.report.ReportRepository
import com.visura.data.repositories.network.evidence.DefaultPhotoEvidenceRepository
import com.visura.domain.repositories.evidence.PhotoEvidenceRepository
import com.visura.data.repositories.network.feature.DefaultPropertyFeatureRepository
import com.visura.domain.repositories.feature.PropertyFeatureRepository
import com.visura.data.repositories.network.user.DefaultUserRepository
import com.visura.domain.repositories.user.UserRepository

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds //ADICIONADO
    abstract fun UserRepository(defaultUserRepository: DefaultUserRepository): UserRepository

    @Binds //ADICIONADO
    abstract fun PropertyFeatureRepository(defaultPropertyFeatureRepository: DefaultPropertyFeatureRepository): PropertyFeatureRepository

    @Binds //ADICIONADO
    abstract fun PhotoEvidenceRepository(defaultPhotoEvidenceRepository: DefaultPhotoEvidenceRepository): PhotoEvidenceRepository

    @Binds //ADICIONADO
    abstract fun ReportRepository(defaultReportRepository: DefaultReportRepository): ReportRepository

    @Binds //ADICIONADO
    abstract fun ChecklistRepository(defaultChecklistRepository: DefaultChecklistRepository): ChecklistRepository

    @Binds //ADICIONADO
    abstract fun EnvironmentRepository(defaultEnvironmentRepository: DefaultEnvironmentRepository): EnvironmentRepository

    @Binds //ADICIONADO
    abstract fun RealStateRepository(defaultRealStateRepository: DefaultRealStateRepository): RealStateRepository

    @Binds
    abstract fun TenantRepository(defaultTenantRepository: DefaultTenantRepository): TenantRepository

    @Binds //ADICIONADO
    abstract fun OwnerRepository(defaultOwnerRepository: DefaultOwnerRepository): OwnerRepository

    @Binds //ADICIONADO
    abstract fun AddressRepository(defaultAddressRepository: DefaultAddressRepository): AddressRepository

    @Binds //ADICIONADO
    abstract fun InspectorRepository(defaultInspectorRepository: DefaultInspectorRepository): InspectorRepository

    @Binds //ADICIONADO
    abstract fun InspectionRepository(defaultInspectionRepository: DefaultInspectionRepository): InspectionRepository

    @Binds //ADICIONADO
    abstract fun PropertyRepository(defaultPropertyRepository: DefaultPropertyRepository): PropertyRepository

    @Binds
    abstract fun FirebaseRepository(defaultFireBaseClientRepository: DefaultFireBaseClientRepository): FireBaseClientRepository

    @Binds
    abstract fun GoogleRepository(defaultGoogleClientRepository: DefaultGoogleClientRepository): GoogleClientRepository

    @Binds
    abstract fun LocationRepository(defaultLocationRepository: DefaultLocationRepository): LocationRepository
}