package com.seregamorph.restapi.mapstruct;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;

import com.seregamorph.restapi.base.BaseProjection;
import com.seregamorph.restapi.base.BaseResource;
import com.seregamorph.restapi.test.common.AbstractUnitTest;
import lombok.Data;
import lombok.experimental.FieldNameConstants;
import org.junit.Test;

/**
 * This class is dedicated to test the behavior of {@link MappingCacheInitializer} with proxy methods.
 */
public class MappingCacheInitializerProductionProxyTest extends AbstractUnitTest {

    // With this class, we expect to cover the following actual case:
    //
    // E.g. We have a resource hierarchy like this:
    // AssignmentResource.SelectionResource.MarketplaceMemberResource.ApplicationResource.CandidateResource
    //
    // in AssignmentResource:
    // @Proxy({
    //     AssignmentResource.Fields.SELECTION,
    //     SelectionResource.Fields.MARKETPLACE_MEMBER,
    //     MarketplaceMemberResource.Fields.APPLICATION,
    //     ApplicationResource.Fields.CANDIDATE
    // })
    // public CandidateResource getCandidate() {
    //     return this.selection.getMarketplaceMember().getApplication().getCandidate();
    // }
    //
    // in projection AssignmentProjection:
    // CandidateProjection getCandidate();
    //
    // When init the cache with projection AssignmentProjection for AssignmentResource:
    // - All fields in AssignmentResource should be marked redundant based on the currently processed projection
    // - All fields in SelectionResource should be marked redundant
    // except for SelectionResource.Fields.MARKETPLACE_MEMBER
    // - All fields in MarketplaceMemberResource should be marked redundant
    // except for MarketplaceMemberResource.Fields.APPLICATION
    // - All fields in ApplicationResource should be marked redundant
    // except for ApplicationResource.Fields.CANDIDATE
    // - All fields in CandidateResource should be marked redundant based on the projection declared
    // (CandidateProjection)

    private static final AlwaysIncluded FOR_ASSIGNMENT = new AlwaysIncluded()
            .setId("For Assignment");
    private static final AlwaysIncluded FOR_SELECTION = new AlwaysIncluded()
            .setId("For Selection");
    private static final AlwaysIncluded FOR_MARKETPLACE_MEMBER = new AlwaysIncluded()
            .setId("For Marketplace Member");
    private static final AlwaysIncluded FOR_APPLICATION = new AlwaysIncluded()
            .setId("For Application");
    private static final AlwaysIncluded FOR_CANDIDATE = new AlwaysIncluded()
            .setId("For Candidate");
    private static final Candidate CANDIDATE = new Candidate()
            .setId("Candidate")
            .setAlwaysIncluded(FOR_CANDIDATE);
    private static final Application APPLICATION = new Application()
            .setId("Application")
            .setCandidate(CANDIDATE)
            .setAlwaysIncluded(FOR_APPLICATION);
    private static final MarketplaceMember MARKETPLACE_MEMBER = new MarketplaceMember()
            .setId("Marketplace Member")
            .setApplication(APPLICATION)
            .setAlwaysIncluded(FOR_MARKETPLACE_MEMBER);
    private static final Selection SELECTION = new Selection()
            .setId("Selection")
            .setMarketplaceMember(MARKETPLACE_MEMBER)
            .setAlwaysIncluded(FOR_SELECTION);
    private static final Assignment ASSIGNMENT = new Assignment()
            .setId("Assignment")
            .setSelection(SELECTION)
            .setAlwaysIncluded(FOR_ASSIGNMENT);

    @Test
    public void initializeCacheShouldProcessAllFieldsInProxy() {
        CachedMappingContext cache = MappingCacheInitializer.initializeCache(
                ASSIGNMENT,
                AssignmentResource.class,
                AssignmentResource.Projection.class);
        collector.checkThat(cache.size(), is(5));
        AlwaysIncludedResource object = new AlwaysIncludedResource();
        collector.checkThat(cache.getMappedInstance(FOR_ASSIGNMENT, AlwaysIncludedResource.class), equalTo(object));
        collector.checkThat(cache.getMappedInstance(FOR_SELECTION, AlwaysIncludedResource.class), equalTo(object));
        collector.checkThat(cache.getMappedInstance(FOR_MARKETPLACE_MEMBER, AlwaysIncludedResource.class),
                equalTo(object));
        collector.checkThat(cache.getMappedInstance(FOR_APPLICATION, AlwaysIncludedResource.class), equalTo(object));
        collector.checkThat(cache.getMappedInstance(FOR_CANDIDATE, AlwaysIncludedResource.class), equalTo(object));
    }

    @Data
    private static class AlwaysIncluded {

        private String id;
    }

    @Data
    private static class Assignment {

        private String id;

        private Selection selection;

        private AlwaysIncluded alwaysIncluded;
    }

    @Data
    private static class Selection {

        private String id;

        private MarketplaceMember marketplaceMember;

        private AlwaysIncluded alwaysIncluded;
    }

    @Data
    private static class MarketplaceMember {

        private String id;

        private Application application;

        private AlwaysIncluded alwaysIncluded;
    }

    @Data
    private static class Application {

        private String id;

        private Candidate candidate;

        private AlwaysIncluded alwaysIncluded;
    }

    @Data
    private static class Candidate {

        private String id;

        private AlwaysIncluded alwaysIncluded;
    }

    @Data
    private static class AlwaysIncludedResource implements BaseResource {

        private String id;
    }

    @Data
    @FieldNameConstants
    private static class AssignmentResource implements BaseResource {

        private String id;

        private SelectionResource selection;

        private AlwaysIncludedResource alwaysIncluded;

        @Proxy({
                AssignmentResource.Fields.SELECTION,
                SelectionResource.Fields.MARKETPLACE_MEMBER,
                MarketplaceMemberResource.Fields.APPLICATION,
                ApplicationResource.Fields.CANDIDATE
        })
        @SuppressWarnings("unused")
        public CandidateResource getCandidate() {
            return selection.getMarketplaceMember().getApplication().getCandidate();
        }

        private interface Projection extends IdProjection {

            @SuppressWarnings("unused")
            IdProjection getCandidate();
        }
    }

    @Data
    @FieldNameConstants
    private static class SelectionResource implements BaseResource {

        private String id;

        private MarketplaceMemberResource marketplaceMember;

        private AlwaysIncludedResource alwaysIncluded;
    }

    @Data
    @FieldNameConstants
    private static class MarketplaceMemberResource implements BaseResource {

        private String id;

        private ApplicationResource application;

        private AlwaysIncludedResource alwaysIncluded;
    }

    @Data
    @FieldNameConstants
    private static class ApplicationResource implements BaseResource {

        private String id;

        private CandidateResource candidate;

        private AlwaysIncludedResource alwaysIncluded;
    }

    @Data
    @FieldNameConstants
    private static class CandidateResource implements BaseResource {

        private String id;

        private AlwaysIncludedResource alwaysIncluded;
    }

    private interface IdProjection extends BaseProjection {

        @SuppressWarnings("unused")
        String getId();
    }
}
