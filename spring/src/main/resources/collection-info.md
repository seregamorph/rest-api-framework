### Collections Structure
An API may return more than 1 resource objects. In that case, the elements are transformed into a JSON array and either returned directly (if the API does not support pagination) or wrapped inside a `content` element and returned (if the API supports pagination). 
