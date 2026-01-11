package com.seregamorph.restapi.demo.controllers;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.empty;

import lombok.val;
import org.junit.jupiter.api.Test;

class AcceptControllerJUnit5Test {

    @Test
    void shouldReturnEmpty() {
        val controller = new AcceptController();

        val list = controller.list(null, null, null, null, null, null);

        assertThat(list, empty());
    }

}
