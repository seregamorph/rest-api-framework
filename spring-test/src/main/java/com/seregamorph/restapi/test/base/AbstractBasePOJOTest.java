package com.seregamorph.restapi.test.base;

import static com.seregamorph.restapi.test.utils.AcceptUtils.extractIllegalValues;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.isEmptyString;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.Assert.assertThat;

import com.openpojo.reflection.impl.PojoClassFactory;
import com.openpojo.validation.Validator;
import com.openpojo.validation.ValidatorBuilder;
import com.openpojo.validation.rule.impl.GetterMustExistRule;
import com.openpojo.validation.rule.impl.NoFieldShadowingRule;
import com.openpojo.validation.rule.impl.NoPublicFieldsExceptStaticFinalRule;
import com.openpojo.validation.rule.impl.SetterMustExistRule;
import com.openpojo.validation.test.impl.GetterTester;
import com.openpojo.validation.test.impl.SetterTester;
import com.seregamorph.restapi.partial.PartialPayload;
import com.seregamorph.restapi.test.common.AbstractUnitTest;
import com.seregamorph.restapi.utils.TypeUtils;
import com.seregamorph.restapi.validators.Accept;
import java.io.Serializable;
import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;
import javax.annotation.Nullable;
import lombok.val;
import org.apache.commons.lang3.reflect.FieldUtils;
import org.jeasy.random.EasyRandom;
import org.jeasy.random.EasyRandomParameters;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.Assert;

/**
 * This base test validates getters, setters and other rules over POJOs.
 */
public abstract class AbstractBasePOJOTest extends AbstractUnitTest {

    protected final Logger logger = LoggerFactory.getLogger(getClass());

    public void testClassShouldHaveRightName(Class<?> clazz) {
        collector.checkThat("Test class should have same package",
                clazz.getPackage(), equalTo(getClass().getPackage()));

        String expectedTestNamePrefix;
        if (clazz.isMemberClass()) {
            // nested classes test should include outer class name
            val packageName = clazz.getPackage().getName();
            val fullClassName = clazz.getName();
            Assert.isTrue(fullClassName.startsWith(packageName + "."), "Unexpected full class name " + fullClassName);
            expectedTestNamePrefix = fullClassName.substring(packageName.length() + 1)
                    .replace("$", "");
        } else {
            expectedTestNamePrefix = clazz.getSimpleName();
        }
        collector.checkThat("Test class should have correct name",
                getClass().getSimpleName(), equalTo(expectedTestNamePrefix + "Test"));
    }

    /**
     * This test validates getters, setters and other rules over POJOs through the OpenPOJO library.
     * See https://github.com/oshoukry/openpojo/
     * Refer to com.openpojo.validation.rule.impl.* for more validation rules.
     * Refer to com.openpojo.validation.test.impl.* for more testers.
     */
    public static void validatePOJOStructure(Class<?> clazz) {
        Validator validator = ValidatorBuilder.create()
                .with(new GetterMustExistRule())
                .with(new SetterMustExistRule())
                .with(new NoPublicFieldsExceptStaticFinalRule())
                .with(new NoFieldShadowingRule())
                .with(new SetterTester())
                .with(new GetterTester())
                .build();

        validator.validate(PojoClassFactory.getPojoClass(clazz));
    }

    public void validateToString(Class<?> clazz) {
        val instance = randomInstance(clazz, null);

        assertThat(instance.toString(), notNullValue());
        assertThat(instance.toString(), not(isEmptyString()));
    }

    public void validateEqualsAndHashCodeDefaultInstance(Class<?> clazz) throws Exception {
        val instance1 = newInstance(clazz);
        val instance2 = newInstance(clazz);

        assertThat(instance1, equalTo(instance2));
        assertThat(instance1.hashCode(), equalTo(instance2.hashCode()));
    }

    public void validateEqualsAndHashCodeSameRandomInstance(Class<?> clazz) {
        val seed = ThreadLocalRandom.current().nextLong();

        val instance1 = randomInstance(clazz, seed);
        // same seed - hence same object (mostly)
        val instance2 = randomInstance(clazz, seed);

        collector.checkThat(instance1, equalTo(instance2));
        collector.checkThat("hashCode() should be the same", instance1.hashCode(), equalTo(instance2.hashCode()));
    }

    public void validateNotEqualsAndHashCodeDifferentRandomInstances(Class<?> clazz) {
        val seed = ThreadLocalRandom.current().nextLong();

        val instance1 = randomInstance(clazz, seed);
        val instance2 = randomInstance(clazz, null);

        assertThat(instance1, not(equalTo(instance2)));
        assertThat(instance1.hashCode(), not(equalTo(instance2.hashCode())));
    }

    public static void validateAccept(Class<?> clazz) {
        Field[] fields = FieldUtils.getFieldsWithAnnotation(clazz, Accept.class);

        for (Field field : fields) {
            Accept accept = field.getAnnotation(Accept.class);
            Class<?> elementClass = TypeUtils.extractElementClass(field);

            if (Enum.class.isAssignableFrom(elementClass)) {
                // We want immediate feedback. No soft assertions here.
                assertThat("@Accept should not contain illegal values",
                        extractIllegalValues(accept, elementClass.asSubclass(Enum.class)), empty());
            }
        }
    }

    protected EasyRandomParameters prepareRandomParameters(@Nullable Long seed) {
        val random = seed == null ? new Random() : new Random(seed);
        return new EasyRandomParameters()
                .objectPoolSize(1)
                .seed(seed == null ? 0L : seed)
                .overrideDefaultInitialization(true)
                // Serializable mapping is for IdResource, should be handled via correct generic type randomization
                // https://github.com/j-easy/easy-random/issues/440
                // https://github.com/j-easy/easy-random/issues/441
                .randomize(Serializable.class, () -> (long) random.nextInt(1024))
                .randomize(Long.class, () -> (long) random.nextInt(1024))
                .randomize(Integer.class, () -> random.nextInt(1024))
                .randomize(Double.class, () -> random.nextInt(1024) / 256.0d)
                .randomize(BigDecimal.class, () -> new BigDecimal(random.nextInt(1024))
                        .divide(new BigDecimal(256), 4, RoundingMode.DOWN))
                .randomize(Object.class, () -> random.nextInt(1024))
                .stringLengthRange(3, 5)
                .collectionSizeRange(2, 3)
                .excludeField(field -> {
                    return field.getDeclaringClass() == PartialPayload.class
                            && (field.getName().equals("payloadClass")
                            || field.getName().equals("partialProperties"));
                });
    }

    private Object randomInstance(Class<?> type, @Nullable Long seed) {
        val easyRandom = new EasyRandom(prepareRandomParameters(seed));
        return easyRandom.nextObject(type);
    }

    /**
     * This method is meant for cases where the instantiation of the class is special.
     *
     * @return A new instance of the class
     * @throws InstantiationException If the class can not be instantiated to test the equals
     * @throws IllegalAccessException If the class can not be instantiated to test the equals
     */
    protected Object newInstance(Class<?> clazz) throws InstantiationException, IllegalAccessException {
        return clazz.newInstance();
    }
}
