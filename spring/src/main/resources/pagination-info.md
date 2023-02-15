### Pagination
Depending on APIs, collections are paginated using standard parameters:
* `page`: starting from 0
* `size`: size of the page returned, default value is {{default_page_size}}

Inspiration: https://docs.spring.io/spring-data/rest/docs/current/reference/html/#paging-and-sorting.sorting

Paginated results will include a `page` element with a few properties:

* `first`: Is this the first page?
* `last`: Is this the last page?
* `number`: Page number
* `numberOfElements`: Number of elements in the page
* `size`: Page size
* `totalElements`: Total number of elements
* `totalPages`: Total number of pages

Inspiration: https://docs.spring.io/spring-data/commons/docs/current/api/org/springframework/data/domain/Page.html
