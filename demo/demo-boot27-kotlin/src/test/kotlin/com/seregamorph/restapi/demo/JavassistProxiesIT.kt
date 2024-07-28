package com.seregamorph.restapi.demo

import com.seregamorph.restapi.base.BasePayload
import com.seregamorph.restapi.demo.resources.UserResource
import com.seregamorph.restapi.partial.PartialPayload
import com.seregamorph.restapi.partial.PartialPayloadFactory
import com.seregamorph.restapi.test.AbstractSpringIT
import com.seregamorph.restapi.test.base.JsonMatcher
import com.seregamorph.restapi.test.base.JsonMatcher.matching
import com.seregamorph.restapi.test.base.setup.common.payload.GenericPayloads
import com.seregamorph.restapi.test.base.setup.common.payload.GenericPayloads.required
import org.hamcrest.Matchers.*
import org.junit.Test
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest
class JavassistProxiesIT : AbstractSpringIT() {

    @Test
    fun partialResourceShouldBeSerialized() {
        val user: UserResource = partial {
            name = "name"
            status = null
            group = partial {
                id = 2L
            }
        }

        val payload = writeJson(user)

        collector.checkThat(readJson(payload), equalTo(`object`()
            .set("name", "name")
            .set("status", null)
            .set("group", `object`()
                .set("id", 2))))
    }

    @Test
    fun jsonMatchingShouldHaveCorrectToString() {
        val userMatcher: UserResource = jsonMatching {
            name = matching(startsWith("name"))
            status = null
            group = jsonMatching {
                id = matching(notNullValue())
            }
        }

        collector.checkThat(userMatcher, hasToString("JsonMatcherProxy" +
                "<com.seregamorph.restapi.demo.resources.UserResource>" +
                "(name, status, group)"))
    }

    @Test
    fun genericResourceShouldHaveCorrectToString() {
        val genericUser: UserResource = generic {
            name = required("new-name")
            status = null
            group = generic {
                id = 2L
            }
        }

        collector.checkThat(genericUser, hasToString("GenericPayloadProxy" +
                "<com.seregamorph.restapi.demo.resources.UserResource>" +
                "(required name, optional status, optional group)"))
    }
}

inline fun <reified T : PartialPayload> partial(block: T.() -> Unit): T {
    val partial = PartialPayloadFactory.partial(T::class.java)
    block(partial)
    return partial
}

inline fun <reified T : BasePayload> jsonMatching(block: T.() -> Unit): T {
    val jsonMatching = JsonMatcher.jsonMatching(T::class.java)
    block(jsonMatching)
    return jsonMatching
}

inline fun <reified T : PartialPayload> generic(block: T.() -> Unit): T {
    val generic = GenericPayloads.generic(T::class.java)
    block(generic)
    return generic
}
