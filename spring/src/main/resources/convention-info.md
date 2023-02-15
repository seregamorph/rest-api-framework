## Coding Convention

* **Plural**: We use plural nouns for resource names, i.e. `GET /customers` instead of `GET /customer`. Example: `/customers`
* **Multiple words on paths**: We use this format for names consisting of several words: `{word1}-{word2}-{word3}`. Example: `/global-customers`
* **Resource properties**: We use camel case for resource properties. Example: `GET /users/12: { "firstName":"John" }`
* **Path parameters**: We use lowerCamelCase
* **Actions or Commands**: We use a hierarchy to represent actions or commands on specific resources. Reference: https://en.wikipedia.org/wiki/HATEOAS. Example: `/accounts/12345/deposit`
* **Relationships**: We represent relationships between Source and Target as `/sources/ID/targets`. Relationships are NOT retrieved by default. Note: You can get the relationship in just 1 shot by using a projection (see below).
* **Resource consistency**: A resource (e.g. User) ALWAYS has the same structure no mater if it is a GET/POST/PUT/PATH operation on the collection or the resource.
* **PUT vs PATCH**: PUT updates the entire resource. PATCH updates individual fields.
