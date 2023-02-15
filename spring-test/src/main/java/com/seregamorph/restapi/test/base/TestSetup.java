package com.seregamorph.restapi.test.base;

import com.seregamorph.restapi.test.base.setup.BaseSetup;
import com.seregamorph.restapi.test.base.setup.DeleteSetup;
import com.seregamorph.restapi.test.base.setup.GetAllSetup;
import com.seregamorph.restapi.test.base.setup.GetOneSetup;
import com.seregamorph.restapi.test.base.setup.PatchSetup;
import com.seregamorph.restapi.test.base.setup.PostSetup;
import com.seregamorph.restapi.test.base.setup.PutSetup;
import java.util.ArrayList;
import java.util.List;
import lombok.Getter;

/**
 * A hierarchical setup for MockMvc tests. Notice:
 * <ul>
 * <li>By default, all tests are disabled. Developers will need to enable them explicitly if they wish to use.</li>
 * <li>Tests are enabled or disabled because of developer's setup, NOT because whether those endpoints are available
 * or not available in controllers. That means, even if controllers have getAll, for example, but developers don't want
 * to use base tests for getAll, then they can simply turn them off.</li>
 * </ul>
 */
@Getter
public class TestSetup extends MockMvcTestSetup {

    private final List<GetAllSetup> getAllSetups = new ArrayList<>();
    private final List<GetOneSetup> getOneSetups = new ArrayList<>();
    private final List<PostSetup> postSetups = new ArrayList<>();
    private final List<PatchSetup> patchSetups = new ArrayList<>();
    private final List<PutSetup> putSetups = new ArrayList<>();
    private final List<DeleteSetup> deleteSetups = new ArrayList<>();

    public TestSetup(Class<?> controllerClass, String endpoint) {
        super(controllerClass, endpoint);
    }

    public TestSetup add(GetAllSetup getAllSetup) {
        return add(getAllSetups, getAllSetup);
    }

    public TestSetup add(GetOneSetup getOneSetup) {
        return add(getOneSetups, getOneSetup);
    }

    public TestSetup add(PostSetup postSetup) {
        return add(postSetups, postSetup);
    }

    public TestSetup add(PatchSetup patchSetup) {
        return add(patchSetups, patchSetup);
    }

    public TestSetup add(PutSetup putSetup) {
        return add(putSetups, putSetup);
    }

    public TestSetup add(DeleteSetup deleteSetup) {
        return add(deleteSetups, deleteSetup);
    }

    void add(BaseSetup<?, ?> setup) {
        if (setup instanceof GetAllSetup) {
            getAllSetups.add((GetAllSetup) setup);
        } else if (setup instanceof GetOneSetup) {
            getOneSetups.add((GetOneSetup) setup);
        } else if (setup instanceof PostSetup) {
            postSetups.add((PostSetup) setup);
        } else if (setup instanceof PatchSetup) {
            patchSetups.add((PatchSetup) setup);
        } else if (setup instanceof PutSetup) {
            putSetups.add((PutSetup) setup);
        } else if (setup instanceof DeleteSetup) {
            deleteSetups.add((DeleteSetup) setup);
        } else {
            throw new IllegalArgumentException("Unexpected " + setup);
        }
    }

    private <T extends BaseSetup<T, ?>> TestSetup add(List<T> setups, T setup) {
        setups.add(setup);
        // There is a reason for this workaround update of trace array. Without it the line number is not evaluated
        // correctly in chain calls inside of `initTest` method.
        setup.initTrace();
        return this;
    }

}
