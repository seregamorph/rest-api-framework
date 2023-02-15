package com.seregamorph.restapi.exceptions;

import static com.seregamorph.restapi.exceptions.NotFoundException.ERROR_TEMPLATE;
import static org.hamcrest.Matchers.equalTo;

import com.google.common.collect.ImmutableMap;
import com.seregamorph.restapi.annotations.ResourceName;
import com.seregamorph.restapi.test.common.AbstractUnitTest;
import org.junit.Test;

public class ExceptionUtilsTest extends AbstractUnitTest {

    // Be careful with class names (case insensitive)
    // to avoid failures on macOS and Windows.
    private static final String NAME_USER = "User";
    private static final String NAME_ANOTHER_USER = "AnotherUser";

    @Test
    public void getMessageShouldReturnCorrectMessage() {
        getMessageShouldReturnCorrectMessage(UserResource.class, NAME_USER);
        getMessageShouldReturnCorrectMessage(UserPartial.class, NAME_USER);
        getMessageShouldReturnCorrectMessage(UserDTO.class, NAME_USER);
        getMessageShouldReturnCorrectMessage(AnotherUserDto.class, NAME_ANOTHER_USER);
        getMessageShouldReturnCorrectMessage(UserVO.class, NAME_USER);
        getMessageShouldReturnCorrectMessage(AnotherUserVo.class, NAME_ANOTHER_USER);
        getMessageShouldReturnCorrectMessage(User.class, NAME_USER);
        getMessageShouldReturnCorrectMessage(UvcsUser.class, NAME_USER);
    }

    private void getMessageShouldReturnCorrectMessage(Class<?> clazz, String name) {
        collector.checkThat(
                ExceptionUtils.getMessage(ERROR_TEMPLATE, clazz, 1, 2),
                equalTo(String.format(ERROR_TEMPLATE, name, "1, 2")));

        collector.checkThat(
                ExceptionUtils.getMessage(ERROR_TEMPLATE, clazz, "field 1", 1, "field 2", 2),
                equalTo(String.format(ERROR_TEMPLATE, name, "field 1 (1), field 2 (2)")));

        collector.checkThat(
                ExceptionUtils.getMessage(ERROR_TEMPLATE, clazz, ImmutableMap.of("field 1", 1, "field 2", 2)),
                equalTo(String.format(ERROR_TEMPLATE, name, "field 1 (1), field 2 (2)")));
    }

    private static class UserResource {

        // Intentionally left blank
    }

    private static class UserPartial {

        // Intentionally left blank
    }

    private static class UserDTO {

        // Intentionally left blank
    }

    private static class AnotherUserDto {

        // Intentionally left blank
    }

    private static class UserVO {

        // Intentionally left blank
    }

    private static class AnotherUserVo {

        // Intentionally left blank
    }

    private static class User {

        // Intentionally left blank
    }

    @ResourceName(NAME_USER)
    private static class UvcsUser {

        // Intentionally left blank
    }

}
