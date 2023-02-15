## Common Error Responses
* 200 OK          : If the operation is successful. The response should include a json payload in the body.
* 201 Created     : If the resource has been created. The response should not include any json payload in the body, but there should be a `Location` header pointing to the resource that has just been created.
* 204 No Content  : If the operation is successful. The response should not include any json payload in the body.
* 400 Bad Request : If the server does not understand the request payload, or the payload doesn't meet expectation.
* 401 Unauthorized: If the user is not authenticated.
* 403 Forbidden   : If the user is authenticated, but does not have permissions to perform the operation.
* 404 Not Found   : If the resource cannot be found.
