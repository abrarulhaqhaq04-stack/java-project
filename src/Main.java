import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.sql.*;

public class Main {

    private static final String DB_URL =
            "jdbc:mysql://mysql:3306/studentdb";

    private static final String DB_USER = "root";
    private static final String DB_PASSWORD = "root123";

    public static void main(String[] args) throws IOException {

        createTable();

        HttpServer server = HttpServer.create(
                new InetSocketAddress(8080), 0
        );

        server.createContext("/", Main::home);

        server.createContext("/students", Main::students);

        server.start();

        System.out.println(
                "Java application running on port 8080"
        );
    }

    private static void home(HttpExchange exchange)
            throws IOException {

        String response =
                "<html>" +
                "<body>" +
                "<h1>Java + MySQL + Docker</h1>" +
                "<p>Application is running successfully.</p>" +
                "<a href='/students'>View Students</a>" +
                "</body>" +
                "</html>";

        sendResponse(exchange, response);
    }

    private static void students(HttpExchange exchange)
            throws IOException {

        StringBuilder html = new StringBuilder();

        html.append("<html><body>");
        html.append("<h1>Student List</h1>");
        html.append("<table border='1' cellpadding='10'>");
        html.append("<tr><th>ID</th><th>Name</th><th>Email</th></tr>");

        String query = "SELECT * FROM students";

        try (
                Connection connection = DriverManager.getConnection(
                        DB_URL,
                        DB_USER,
                        DB_PASSWORD
                );

                Statement statement = connection.createStatement();

                ResultSet resultSet =
                        statement.executeQuery(query)
        ) {

            while (resultSet.next()) {

                html.append("<tr>");

                html.append("<td>")
                        .append(resultSet.getInt("id"))
                        .append("</td>");

                html.append("<td>")
                        .append(resultSet.getString("name"))
                        .append("</td>");

                html.append("<td>")
                        .append(resultSet.getString("email"))
                        .append("</td>");

                html.append("</tr>");
            }

        } catch (SQLException e) {

            html.append("<tr><td colspan='3'>");
            html.append("Database connection error: ");
            html.append(e.getMessage());
            html.append("</td></tr>");
        }

        html.append("</table>");
        html.append("</body></html>");

        sendResponse(exchange, html.toString());
    }

    private static void createTable() {

        String sql =
                "CREATE TABLE IF NOT EXISTS students (" +
                "id INT AUTO_INCREMENT PRIMARY KEY," +
                "name VARCHAR(100)," +
                "email VARCHAR(100)" +
                ")";

        try {

            Connection connection =
                    DriverManager.getConnection(
                            DB_URL,
                            DB_USER,
                            DB_PASSWORD
                    );

            Statement statement =
                    connection.createStatement();

            statement.executeUpdate(sql);

            statement.close();
            connection.close();

            System.out.println("Database table ready.");

        } catch (SQLException e) {

            System.out.println(
                    "Database connection failed."
            );

            System.out.println(e.getMessage());
        }
    }

    private static void sendResponse(
            HttpExchange exchange,
            String response
    ) throws IOException {

        exchange.getResponseHeaders()
                .set("Content-Type", "text/html");

        exchange.sendResponseHeaders(
                200,
                response.getBytes().length
        );

        OutputStream output =
                exchange.getResponseBody();

        output.write(response.getBytes());

        output.close();
    }
}