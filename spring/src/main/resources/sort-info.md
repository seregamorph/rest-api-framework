## Sorting

Depending on APIs, you may use a `sort` parameter to sort matching resources. The argument can be a single sort condition, or a semicolon-separated array of sort conditions. Each sort condition is specified in the format of `<fieldName> : <direction>` with `direction` being either `asc` or `desc`, where spaces before and after the colon may be omitted. In the case the sort direction is `asc`, then you only need to specify the field name.
E.g.
```
?sort=createdDate:desc;name;age:asc
```
