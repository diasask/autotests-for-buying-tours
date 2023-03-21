package interactions;

import lombok.SneakyThrows;
import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.handlers.ScalarHandler;

import java.io.FileReader;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Scanner;

public class DBInteractions {

    @SneakyThrows
    public static String getUrlDb() {
        FileReader fr = new FileReader("artifacts/application.properties");
        Scanner scan = new Scanner(fr);
        scan.nextLine();
        scan.nextLine();
        String url = scan.nextLine().substring(22);
        fr.close();
        return url;
    }


    @SneakyThrows
    public static void cleanData() {
        var runner = new QueryRunner();
        var creditPayment = "DELETE FROM credit_request_entity;";
        var order = "DELETE FROM order_entity;";
        var debitPayment = "DELETE FROM payment_entity;";

        try {
            var connection = DriverManager.getConnection(
                    getUrlDb(), "app", "pass");
            runner.update(connection, creditPayment);
            runner.update(connection, order);
            runner.update(connection, debitPayment);

        } catch (SQLException ex) {
            System.out.println("SQLException message: " + ex.getMessage());
        }
    }

    @SneakyThrows
    public static String[] paymentRequest() {
        var statusPaymentSQL = "SELECT status FROM payment_entity ORDER BY created DESC LIMIT 1;";// выбрать записи из колонки status из таблицы payment_entity и отсортировать по добавлению и оставить последний
        var sumPaymentSQL = "SELECT amount FROM payment_entity ORDER BY created DESC LIMIT 1;";// выбрать записи из колонки amount из таблицы payment_entity и отсортировать по добавлению и оставить последний
        var runner = new QueryRunner();

        try (
                var conn = DriverManager.getConnection(
                        getUrlDb(), "app", "pass"
                );
        ) {
            String[] request = new String[2];
            var status = runner.query(conn, statusPaymentSQL, new ScalarHandler<>());//status
            var sum = runner.query(conn, sumPaymentSQL, new ScalarHandler<>());//amount
            request[0] = String.valueOf(status);
            request[1] = String.valueOf(sum);
            return request;
        }
    }

    @SneakyThrows
    public static String creditPaymentRequest() {
        var statusPaymentSQL = "SELECT status FROM credit_request_entity ORDER BY created DESC LIMIT 1;";// выбрать записи из колонки status из таблицы payment_entity и отсортировать по добавлению и оставить последний
        //var sumPaymentSQL = "SELECT amount FROM payment_entity ORDER BY created DESC LIMIT 1;";// выбрать записи из колонки amount из таблицы payment_entity и отсортировать по добавлению и оставить последний
        var runner = new QueryRunner();

        try (
                var conn = DriverManager.getConnection(
                        getUrlDb(), "app", "pass"
                );
        ) {
            //String[] request = new String[2];
            var status = runner.query(conn, statusPaymentSQL, new ScalarHandler<>());//status
            //var sum = runner.query(conn, sumPaymentSQL, new ScalarHandler<>());//amount
            //request[0] = String.valueOf(status);
            //request[1] = String.valueOf(sum);
            return String.valueOf(status);
        }
    }
}