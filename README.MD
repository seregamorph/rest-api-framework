# REST API framework

# Supported JDK
Supported JDK 8 and JDK 11.

# Framework features

## Jackson PATCH notation
The JSON format originates from the JavaScript programming language, which explicitly distinguishes `null` vs `undefined` object fields and it is pretty compatible with `HTTP PATCH` request notation: if the field is specified, it should be updated to either new value or `null`. But it contradicts jackson POJO class model and requires some additional configuration, out of the box jackson just suggests serialization of non-null or non-empty fields which does not solve the problem in general. The plain `Map` type for serialized fields can be also used, but it's a weak typing and also increases the size of code boilerplate. The framework introduces abstraction `PartialPayload` which is a base class for both serialized and parsed json payloads. The implementation is lombok-friendly, chain setters are also supported. The javassist dynamic class generation is used for client (serialization) code. Sample usage, POJO:
```java
import static com.seregamorph.restapi.partial.PartialPayloadFactory.partial;

import com.seregamorph.restapi.base.IdResource;
import lombok.Data;
import lombok.experimental.FieldNameConstants;

@Data
@Accessors(chain = true)
@FieldNameConstants
class SimplePartialResource extends IdResource<Long, SimplePartialResource> implements SimplePartial {

    private String name;

    private String title;

    private String description;

    private int version;

    private SimplePartialResource linked;

    public static SimplePartialResource create() {
        return partial(SimplePartialResource.class);
    }
}
```
and according client:
```java
ObjectMapper objectMapper = PartialPayloadMapperUtils.configure(new ObjectMapper());

SimplePartialResource resource = SimplePartialResource.create()
    .setName(VALUE_NAME)
    .setTitle(null)
    .setLinked(SimplePartialResource.create()
            .setId(10L));

    String str = objectMapper.writeValueAsString(resource);
```
and the `str` will be equal to
```json
{
  "name": "value",
  "title": null,
  "linked": {
    "id": 10
  }
}
```
where only specified fields are serialized - either null or non-null. Nested values are also supported.
This jackson extension works vice versa, such payload can be parsed to the object back:
```java
ObjectMapper objectMapper = PartialPayloadMapperUtils.configure(new ObjectMapper());

SimplePartialResource parsed = objectMapper.readValue(str, SimplePartialResource.class);
// root object
parsed.hasPartialProperty(SimplePartialResource.Fields.NAME);               // true
parsed.hasPartialProperty(SimplePartialResource.Fields.TITLE);              // true
parsed.hasPartialProperty(SimplePartialResource.Fields.DESCRIPTION);        // false
// nested object
parsed.hasPartialProperty(SimplePartialResource.Fields.LINKED);             // true
parsed.getLinked().hasPartialProperty(SimplePartialResource.FIELD_ID);      // true
parsed.getLinked().hasPartialProperty(SimplePartialResource.Fields.NAME);   // false
```

# Known issues
At the moment JDK 17 is not supported

