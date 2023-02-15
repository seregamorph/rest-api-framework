### Projections
Sometimes you need only a few attributes from a resource or some additional ones not retrieved by default (e.g. collections). In that case you can use a projection.
You need to add the header: `Accept-Projection: XXXX` to the `POST`/`PUT`/`PATCH` endpoints or `projection=XXXXX` query parameter for `GET` endpoints accordingly.

Projection names are written in snake uppercase (e.g `FIRST_NAME`).

Inspiration: https://docs.spring.io/spring-data/rest/docs/current/reference/html/#projections-excerpts
