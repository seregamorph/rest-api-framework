package com.seregamorph.restapi.test;

import com.github.seregamorph.testsmartcontext.SmartDirtiesTestsSorter;
import com.seregamorph.restapi.test.base.FrameworkRunner;
import org.junit.runner.RunWith;
import org.junit.runner.Runner;

public class FrameworkSmartDirtiesTestsSorter extends SmartDirtiesTestsSorter {

    @Override
    protected boolean isReorderTestJUnit4(Class<?> testClass) {
        if (super.isReorderTestJUnit4(testClass)) {
            return true;
        }
        RunWith runWith = testClass.getAnnotation(RunWith.class);
        if (runWith == null) {
            return false;
        }
        Class<? extends Runner> runner = runWith.value();
        return FrameworkRunner.class.isAssignableFrom(runner);
    }
}
