# PW Web Framework

📦 A minimal Java HTTP web framework built from scratch — with annotation-based routing, basic dispatcher, and
thread-per-request handling.

🚀 Features:

- Lightweight web server built on ServerSocket
- Annotation-based routing: @PwRoute, @PwController
- Support for:
    - Path variables (e.g., /user/{id})
    - Query parameters (e.g., ?size=10)
    - Automatic argument binding from request to controller methods
    - Uses SLF4J with Logback for structured logging

⚙ How it works

1. Define controllers with @PwController
2. Define methods with @PwRoute(method = "GET", path = "/users/{id}")
3. Bind method arguments automatically:
   - @PwPath("id") Integer id
   - @PwQuery("name") String name
4. The dispatcher:
   - Matches incoming requests to routes
   - Parses path/query params
   - Calls controller method with bound arguments
   - Serializes the returned HttpResponse
    
✏ Example controller
```java
@PwController(path = "/test")
public class UserController {

    @PwRoute(method = "GET", path = "/user/{id}")
    public HttpResponse getUser(HttpRequest request, @PwPath Integer id, @PwQuery("verbose") Boolean verbose) {
        // Some logic...
        return HttpResponse.ok(body);
    }
}
```
🛠 Example start up
```java
public static void main(String[] args) throws Exception {
    final Dispatcher dispatcher = new Dispatcher("com.petwal.demo"); // auto-scans controllers
    final Server server = new Server(5000, dispatcher);
    server.start();
}
```