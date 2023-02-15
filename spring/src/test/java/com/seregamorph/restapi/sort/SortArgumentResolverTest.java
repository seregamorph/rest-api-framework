package com.seregamorph.restapi.sort;

import com.seregamorph.restapi.test.common.AbstractUnitTest;
import org.junit.Test;
import org.springframework.beans.TypeMismatchException;

public class SortArgumentResolverTest extends AbstractUnitTest {

    @Test
    public void duplicatedSortFieldNamesShouldBeRejected() throws Exception {
        expectedException.expect(TypeMismatchException.class);
        expectedException.expectMessage("Argument [[id:asc, id:desc]] for parameter [sort] is invalid: "
                + "[Duplicated field names]");

        SortArgumentResolver.resolveArgument(sortParam(), new String[] {"id:asc", "id:desc"});
    }

    @Test
    public void nonSortableFieldsShouldBeRejected() throws Exception {
        expectedException.expect(TypeMismatchException.class);
        expectedException.expectMessage("Argument [name] for parameter [sort] is invalid: "
                + "[Field [name] is not sortable, allowed fields: [id]");

        SortArgumentResolver.resolveArgument(sortParam(), new String[] {"name"});
    }

    private static SortParam sortParam() throws Exception {
        return SortArgumentResolverTest.class
                .getDeclaredMethod("doSomething", Sort.class)
                .getParameters()[0]
                .getAnnotation(SortParam.class);
    }

    @SuppressWarnings("unused")
    private static void doSomething(
            @SortParam("id")
            final Sort sort
    ) {
        // Intentionally left blank
    }
}
