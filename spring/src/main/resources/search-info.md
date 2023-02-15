## Searching

Depending on APIs, you may use a `search` parameter to search for matching resources. The argument can be a single search condition or a combination of them using logical operators. Each search condition is specified in the format of `<fieldName> <operator> <value>`, where spaces before and after the operator may be omitted in the case the operator consists of only special characters.
E.g.
```
?search=phone is not null;age>=18;age<=60
```
**Separators:**
- Logical operators are used to separate atomic clauses
- Commas are used to separate elements in a collection
- Spaces are used to separate `operator` from `fieldName` and `value`, which is not necessary if the operator consists of only special characters
- Brackets (round, square, curly) are used to group a collection of values
- Round brackets are used to identify a set of clauses

**Logical operators:**
- Logical AND: `and` or `;`
- Logical OR: `or` or `|`

E.g. The following query:
```
?search=startDate<='2020-02-02' and (endDate>='2020-02-02' or endDate is null)
```
Can be written as follows:
```
?search=startDate<='2020-02-02';(endDate >= '2020-02-02'|endDate is null)
```
{{additional_search_info}}
