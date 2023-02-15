package com.seregamorph.restapi.search;

import static com.seregamorph.restapi.search.SearchOperator.EQUAL;
import static com.seregamorph.restapi.search.SearchOperator.GREATER_THAN_OR_EQUAL;
import static com.seregamorph.restapi.search.SearchOperator.IN;
import static com.seregamorph.restapi.search.SearchOperator.IS;
import static com.seregamorph.restapi.search.SearchOperator.LESS_THAN_OR_EQUAL;
import static com.seregamorph.restapi.search.SearchOperator.LIKE;
import static org.hamcrest.Matchers.equalTo;

import com.seregamorph.restapi.test.common.AbstractUnitTest;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.Arrays;
import java.util.Collections;
import org.junit.Test;

public class SearchArgumentResolverTest extends AbstractUnitTest {

    @Test
    public void shouldResolveSearchParam() throws Exception {
        String[] searchValues = new String[] {
                "name like %Smith",
                "name=ORANGE", // String starts with a logical operator
                "name=ERROR", // String ends with a logical operator
                "name=FORWARD", // String contains a logical operator
                "age>=18",
                "age <= 60",
                " registrationDate= 2020-02-02",
                " status=ACTIVE",
                " status=ERROR", // Enum ends with a logical operator
                "display_name$2=\" Display Name \"",
                "display_name$2=\" Display;Name \"",
                "display_name$2=\" Display|Name \"",
                "display_name$2=\" Display([{,}])Name \"",
                "display_name$2= Display$Name ",
                "display_name$2=\"Display'Name\"",
                "age >= 18 or group.id = 5"
        };

        Search search = SearchArgumentResolver.resolveArgument(searchParam(), searchValues);

        collector.checkThat(search, equalTo(Arrays.asList(
                new SingleSearchCondition("name", LIKE, "%Smith"),
                new SingleSearchCondition("name", EQUAL, "ORANGE"),
                new SingleSearchCondition("name", EQUAL, "ERROR"),
                new SingleSearchCondition("name", EQUAL, "FORWARD"),
                new SingleSearchCondition("age", GREATER_THAN_OR_EQUAL, 18),
                new SingleSearchCondition("age", LESS_THAN_OR_EQUAL, 60),
                new SingleSearchCondition("registrationDate", EQUAL, LocalDate.of(2020, 2, 2)),
                new SingleSearchCondition("status", EQUAL, Status.ACTIVE),
                new SingleSearchCondition("status", EQUAL, Status.ERROR),
                new SingleSearchCondition("display_name$2", EQUAL, " Display Name "),
                new SingleSearchCondition("display_name$2", EQUAL, " Display;Name "),
                new SingleSearchCondition("display_name$2", EQUAL, " Display|Name "),
                new SingleSearchCondition("display_name$2", EQUAL, " Display([{,}])Name "),
                new SingleSearchCondition("display_name$2", EQUAL, "Display$Name"),
                new SingleSearchCondition("display_name$2", EQUAL, "Display'Name"),
                new SearchConditionGroup(LogicalOperator.OR)
                        .addSearchCondition("age", GREATER_THAN_OR_EQUAL, 18)
                        .addSearchCondition("group.id", EQUAL, 5)
        )));
    }

    @Test
    public void shouldResolveComplexSearchParam() throws Exception {
        String[] searchValues = new String[] {
                "startDate<=2020-02-11T08:48:55+10:30 and (endDate>=2020-02-11T08:48:55+10:30 or endDate is null)"
        };

        Search search = SearchArgumentResolver.resolveArgument(searchParam(), searchValues);

        collector.checkThat(search, equalTo(Arrays.asList(
                new SingleSearchCondition("startDate", LESS_THAN_OR_EQUAL, OffsetDateTime.parse("2020-02-11T08:48:55+10:30")),
                new SearchConditionGroup(LogicalOperator.OR)
                        .addSearchCondition("endDate", GREATER_THAN_OR_EQUAL, OffsetDateTime.parse("2020-02-11T08:48:55+10:30"))
                        .addSearchCondition("endDate", IS, SearchValue.NULL)
        )));
    }

    @Test
    public void shouldResolveDefaultSearchParam() throws Exception {
        Search search = SearchArgumentResolver.resolveArgument(searchParam(), new String[0]);

        collector.checkThat(search, equalTo(Collections.singletonList(
                new SingleSearchCondition("status", IN, Arrays.asList(Status.ACTIVE, Status.PENDING))
        )));
    }

    @Test
    public void shouldResolveDefaultSearchParamWhenEmptyArgument() throws Exception {
        // Same behavior as Spring
        Search search = SearchArgumentResolver.resolveArgument(searchParam(), new String[] {""});

        collector.checkThat(search, equalTo(Collections.singletonList(
                new SingleSearchCondition("status", IN, Arrays.asList(Status.ACTIVE, Status.PENDING))
        )));
    }

    @Test
    public void shouldResolveSingleSearchParamContainingSpaces() throws Exception {
        Search search = SearchArgumentResolver.resolveArgument(searchParam(), new String[] {"name = Display Name"});

        collector.checkThat(search, equalTo(Collections.singletonList(
                new SingleSearchCondition("name", EQUAL, "Display Name"))
        ));
    }

    @Test
    public void shouldResolveCombinedSearchParamContainingSpaces() throws Exception {
        Search search = SearchArgumentResolver.resolveArgument(searchParam(), new String[] {"name = Display Name or name = Another Name"});

        collector.checkThat(search, equalTo(Collections.singletonList(
                new SearchConditionGroup(LogicalOperator.OR)
                        .addSearchCondition("name", EQUAL, "Display Name")
                        .addSearchCondition("name", EQUAL, "Another Name")
        )));
    }

    private static SearchParam searchParam() throws Exception {
        return SearchArgumentResolverTest.class
                .getDeclaredMethod("doSomething", Search.class)
                .getParameters()[0]
                .getAnnotation(SearchParam.class);
    }

    private enum Status {
        ACTIVE,
        PENDING,
        DELETED,
        ERROR
    }

    private static void doSomething(
            @SearchParam(
                    value = {
                            @SearchParam.Field(name = "age", type = int.class),
                            @SearchParam.Field(name = "group.id", type = int.class),
                            @SearchParam.Field(name = "name"),
                            @SearchParam.Field(name = "display_name$2"),
                            @SearchParam.Field(name = "registrationDate", type = LocalDate.class),
                            @SearchParam.Field(name = "status", type = Status.class),
                            @SearchParam.Field(name = "startDate", type = OffsetDateTime.class),
                            @SearchParam.Field(name = "endDate", type = OffsetDateTime.class)
                    },
                    defaultSearch = {
                            @SearchParam.DefaultField(name = "status", operator = IN, value = {"ACTIVE", "PENDING"})
                    }
            )
            final Search search
    ) {
        // Intentionally left blank
    }
}
