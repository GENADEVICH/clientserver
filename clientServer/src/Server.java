import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

public class Server {
    private static final int PORT = 12345;
    private static final int HTTP_PORT = 8080;
    private static final Map<String, String> users = new HashMap<>();

    public static void main(String[] args) {
        new Thread(Server::startClientHandler).start();
        new Thread(Server::startHttpServer).start();
    }

    private static void startClientHandler() {
        try (ServerSocket serverSocket = new ServerSocket(Server.PORT)) {
            System.out.println("Client Server listening on port " + Server.PORT);

            while (true) {
                try (Socket clientSocket = serverSocket.accept();
                     BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                     PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true)) {

                    System.out.println("Client connected: " + clientSocket.getInetAddress().getHostAddress() + " (Non-HTTP)");

                    String login = in.readLine();
                    String password = in.readLine();
                    String action = in.readLine();

                    if ("login".equals(action)) {
                        String response = users.containsKey(login) && users.get(login).equals(password) ? "Success" : "Fail";
                        out.println(response);
                    } else if ("register".equals(action)) {
                        if (users.containsKey(login)) {
                            out.println("Fail: User already exists");
                        } else {
                            users.put(login, password);
                            out.println("Success");
                            System.out.println("New user is logged in: " + login);
                        }
                    }


                } catch (IOException e) {
                    System.out.println("Server exception: " + e.getMessage());
                    e.printStackTrace();
                }
            }
        } catch (IOException e) {
            System.out.println("Could not listen on port " + Server.PORT);
            e.printStackTrace();
        }
    }

    private static void startHttpServer() {
        try (ServerSocket httpServerSocket = new ServerSocket(Server.HTTP_PORT)) {
            System.out.println("HTTP Server started on port " + Server.HTTP_PORT);

            while (true) {
                try (Socket clientSocket = httpServerSocket.accept();
                     PrintWriter out = new PrintWriter(clientSocket.getOutputStream());
                     BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()))) {

                    System.out.println("Client connected: " + clientSocket.getInetAddress().getHostAddress() + " (HTTP)");

                    String line = in.readLine();
                    if (line != null) {
                        if (line.startsWith("GET /accounts ")) {
                            sendAccountsPage(out);
                        } else if (line.startsWith("GET / ")) { // Обработка запроса к корню
                            sendMainPage(out);
                        } else {
                            sendNotFoundPage(out);
                        }
                    }

                } catch (IOException e) {
                    System.out.println("HTTP Server exception: " + e.getMessage());
                    e.printStackTrace();
                }
            }
        } catch (IOException e) {
            System.out.println("Could not start HTTP server on port " + Server.HTTP_PORT);
            e.printStackTrace();
        }
    }

    private static void sendAccountsPage(PrintWriter out) {
        int totalAccounts = users.size();
        StringBuilder accountsListBuilder = new StringBuilder();
        users.forEach((login, password) -> accountsListBuilder.append("<li>").append("Login: ").append(login).append("</li>"));

        out.println("HTTP/1.1 200 OK");
        out.println("Content-Type: text/html; charset=utf-8");
        out.println();
        out.println("<!DOCTYPE html>");
        out.println("<html><head>");
        out.println("<title>Accounts List</title>");
        // Встроенные стили для простоты, но рекомендуется использовать внешние CSS файлы
        out.println("<style>");
        out.println("body { font-family: Arial, sans-serif; margin: 40px; }");
        out.println("h1 { color: #333; }");
        out.println("ul { list-style-type: none; padding: 0; }");
        out.println("li { background: #f9f9f9; margin: 5px 0; padding: 10px; border-radius: 5px; }");
        out.println(".account-bar { height: 20px; background-color: #4CAF50; color: white; text-align: center; border-radius: 5px; }");
        out.println("</style>");
        out.println("</head><body>");
        out.println("<h1>Account List</h1>");
        out.println("<p>Total accounts: " + totalAccounts + "</p>");

        // Добавление простой инфографики
        out.println("<div style='width: 100%; background-color: #f0f0f0; border-radius: 5px;'>");
        out.println("<div class='account-bar' style='width: " + Math.min(100, totalAccounts) + "%;'>" + totalAccounts + "</div>");
        out.println("</div>");

        out.println("<ul>");
        out.println(accountsListBuilder.toString());
        out.println("</ul>");
        out.println("</body></html>");
        out.flush();
    }



    private static void sendNotFoundPage(PrintWriter out) {
        out.println("HTTP/1.1 404 Not Found");
        out.println("Content-Type: text/html; charset=utf-8");
        out.println();
        out.println("<!DOCTYPE html><html lang='en'><head><meta charset='UTF-8'><meta name='viewport' content='width=device-width, initial-scale=1.0'><title>404 Page Not Found</title><style>body { font-family: 'Arial', sans-serif; text-align: center; padding: 50px; } h1 { color: #FF6347; } p { color: #555; } a { color: #007BFF; text-decoration: none; }</style></head><body><h1>Oops! Page not found.</h1><p>We can't seem to find the page you're looking for.</p><a href='/accounts'>Go Accounts</a></body></html>");
        out.flush();

    }

    private static void sendMainPage(PrintWriter out) {
        String html = """
                HTTP/1.1 200 OK\r
                Content-Type: text/html; charset=utf-8\r
                \r
                <!DOCTYPE html>\r
                <html lang="en">\r
                <head>\r
                    <meta charset="UTF-8">\r
                    <meta name="viewport" content="width=device-width, initial-scale=1.0">\r
                    <title>Welcome to Our Application</title>\r
                    <style>\r
                        body {\r
                            font-family: Arial, sans-serif;\r
                            text-align: center;\r
                            background-color: #f4f4f4;\r
                            padding: 50px;\r
                        }\r
                        h1 {\r
                            color: #333;\r
                        }\r
                        p {\r
                            color: #666;\r
                        }\r
                    </style>\r
                </head>\r
                <body>\r
                    <h1>Welcome to Our Application</h1>\r
                    <p>Use <a href="/accounts">/accounts</a> to view all accounts</p>\r
                </body>\r
                </html>""";

        out.print(html);
        out.flush();
    }
}
