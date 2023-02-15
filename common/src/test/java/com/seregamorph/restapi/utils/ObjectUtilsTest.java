package com.seregamorph.restapi.utils;

import com.seregamorph.restapi.test.common.AbstractUnitTest;
import java.util.Arrays;
import java.util.Collections;
import org.hamcrest.Matchers;
import org.junit.Test;

public class ObjectUtilsTest extends AbstractUnitTest {

    @Test
    public void objectShouldBeConvertedToCollection() {
        collector.checkThat(ObjectUtils.collection(new boolean[] {true, false}),
                Matchers.equalTo(Arrays.asList(Boolean.TRUE, Boolean.FALSE)));
        collector.checkThat(ObjectUtils.collection(new byte[] {Byte.MIN_VALUE, Byte.MAX_VALUE}),
                Matchers.equalTo(Arrays.asList(Byte.MIN_VALUE, Byte.MAX_VALUE)));
        collector.checkThat(ObjectUtils.collection(new char[] {'a', 'b'}),
                Matchers.equalTo(Arrays.asList('a', 'b')));
        collector.checkThat(ObjectUtils.collection(new short[] {Short.MIN_VALUE, Short.MAX_VALUE}),
                Matchers.equalTo(Arrays.asList(Short.MIN_VALUE, Short.MAX_VALUE)));
        collector.checkThat(ObjectUtils.collection(new int[] {Integer.MIN_VALUE, Integer.MAX_VALUE}),
                Matchers.equalTo(Arrays.asList(Integer.MIN_VALUE, Integer.MAX_VALUE)));
        collector.checkThat(ObjectUtils.collection(new long[] {Long.MIN_VALUE, Long.MAX_VALUE}),
                Matchers.equalTo(Arrays.asList(Long.MIN_VALUE, Long.MAX_VALUE)));
        collector.checkThat(ObjectUtils.collection(new float[] {Float.MIN_VALUE, Float.MAX_VALUE}),
                Matchers.equalTo(Arrays.asList(Float.MIN_VALUE, Float.MAX_VALUE)));
        collector.checkThat(ObjectUtils.collection(new double[] {Double.MIN_VALUE, Double.MAX_VALUE}),
                Matchers.equalTo(Arrays.asList(Double.MIN_VALUE, Double.MAX_VALUE)));
        collector.checkThat(ObjectUtils.collection(new Object[] {"foo", "bar"}),
                Matchers.equalTo(Arrays.asList("foo", "bar")));
        collector.checkThat(ObjectUtils.collection(Arrays.asList(1, 2)),
                Matchers.equalTo(Arrays.asList(1, 2)));
        collector.checkThat(ObjectUtils.collection("whatever"),
                Matchers.equalTo(Collections.singletonList("whatever")));
    }
}
