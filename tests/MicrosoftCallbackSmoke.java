import me.catzy44.struct.accounts.Microsoft;
import me.kotsu.microHTTPServer.HttpServer;
import me.kotsu.microHTTPServer.HttpServerAction;
import java.net.*;
import java.net.http.*;
import java.io.*;

class MicrosoftCallbackSmoke {
    static final HttpClient client = HttpClient.newBuilder().followRedirects(HttpClient.Redirect.NEVER).build();
    static HttpResponse<byte[]> request(String path, String method) throws Exception {
        return client.send(HttpRequest.newBuilder(URI.create("http://127.0.0.1:22420" + path))
            .method(method, HttpRequest.BodyPublishers.noBody()).build(), HttpResponse.BodyHandlers.ofByteArray());
    }
    static void check(boolean ok, String message) { if (!ok) throw new AssertionError(message); }
    public static void main(String[] args) throws Exception {
        Microsoft login = new Microsoft();
        try {
            login.startServer();
            check(request("/missing", "GET").statusCode() == 404, "404");
            check(request("/login", "GET").statusCode() == 400, "missing code");
            check(request("/login?code=Wrong", "HEAD").statusCode() == 405, "reject HEAD callback");
            var result = request("/login?code=AbC%2BDeF%3D%26Gh&state=123456", "GET");
            check(result.statusCode() == 307, "redirect");
            check(result.headers().firstValue("Location").orElse("").endsWith("/success"), "location");
            var code = Microsoft.class.getDeclaredField("code"); code.setAccessible(true);
            check("AbC+DeF=&Gh".equals(code.get(login)), "case and URL decoding");
            check(request("/success", "GET").headers().firstValue("Content-Type").orElse("").startsWith("text/html"), "HTML");
            check(request("/image.png", "GET").body()[0] == (byte)137, "PNG");
            check(request("/success", "HEAD").body().length == 0, "HEAD no body");
            Thread.sleep(11000);
            try (ServerSocket probe = new ServerSocket(22420, 50, InetAddress.getByName("127.0.0.1"))) {}
            login.startServer();
            check(request("/login?error=access_denied", "GET").statusCode() == 400, "OAuth denial");
            login.stopServer();
            try (ServerSocket occupied = new ServerSocket(22420)) {
                try { login.startServer(); throw new AssertionError("occupied port accepted"); }
                catch (IOException expected) {}
            }
            for (int i = 0; i < 10; i++) { login.startServer(); login.stopServer(); }
        } finally { login.stopServer(); }
        HttpServer legacy = new HttpServer(22420);
        legacy.addAction(new HttpServerAction("/legacy") {
            public Object run(String method, String ip, BufferedReader in) { return "compatible"; }
        });
        try {
            legacy.start();
            check(new String(request("/legacy", "GET").body()).contains("compatible"), "legacy action");
        } finally { legacy.shutdown(); }
        System.out.println("PASS: callback, resources, lifecycle, port conflict and legacy API");
    }
}

