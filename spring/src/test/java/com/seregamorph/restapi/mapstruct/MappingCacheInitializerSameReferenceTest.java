package com.seregamorph.restapi.mapstruct;

import static org.hamcrest.Matchers.is;

import com.seregamorph.restapi.base.BaseProjection;
import com.seregamorph.restapi.base.BaseResource;
import com.seregamorph.restapi.test.common.AbstractUnitTest;
import lombok.Data;
import org.junit.Test;

/**
 * <p>This class is dedicated to the case where a field is both required and redundant in the projection hierarchy.</p>
 * <ul>
 * <li>A now redundant field may later be required. We need to mark the field as required and 'unmark' the field
 * as redundant.</li>
 * <li>A now required field may later be redundant. We need to 'do nothing'.</li>
 * </ul>
 */
public class MappingCacheInitializerSameReferenceTest extends AbstractUnitTest {

    private static final Team TEAM = new Team();
    private static final Company COMPANY = new Company().setDefaultTeam(TEAM);
    private static final Contractor CONTRACTOR = new Contractor().setCompany(COMPANY).setTeam(TEAM);

    @Test
    public void initializeCacheShouldNotMarkRedundantIfPreviouslyRequired() {
        CachedMappingContext cache = MappingCacheInitializer.initializeCache(
                CONTRACTOR,
                ContractorResource.class,
                ContractorWithDirectTeamReferenceProjection.class);
        collector.checkThat(cache.size(), is(0));
    }

    @Test
    public void initializeImmutableCacheShouldMarkRequiredAndUnmarkRedundant() {
        CachedMappingContext cache = MappingCacheInitializer.initializeImmutableCache(
                CONTRACTOR,
                ContractorResource.class,
                ContractorWithIndirectTeamReferenceProjection.class);
        collector.checkThat(cache.size(), is(0));
    }

    @Data
    private static class Contractor {
        Team team;
        Company company;
    }

    @Data
    private static class Company {
        Team defaultTeam;
    }

    private static class Team {
    }

    @Data
    private static class ContractorResource implements BaseResource {
        @SuppressWarnings("unused")
        TeamResource team;
        @SuppressWarnings("unused")
        CompanyResource company;
    }

    @Data
    private static class CompanyResource implements BaseResource {
        @SuppressWarnings("unused")
        TeamResource defaultTeam;
    }

    private static class TeamResource implements BaseResource {
    }

    private interface ContractorWithIndirectTeamReferenceProjection extends BaseProjection {

        @SuppressWarnings("unused")
        CompanyWithDefaultTeamProjection getCompany();
    }

    private interface ContractorWithDirectTeamReferenceProjection extends BaseProjection {

        @SuppressWarnings("unused")
        TeamProjection getTeam();

        @SuppressWarnings("unused")
        CompanyWithoutDefaultTeamProjection getCompany();
    }

    private interface CompanyWithDefaultTeamProjection extends BaseProjection {

        @SuppressWarnings("unused")
        TeamProjection getDefaultTeam();
    }

    private interface CompanyWithoutDefaultTeamProjection extends BaseProjection {

    }

    private interface TeamProjection extends BaseProjection {

    }
}
