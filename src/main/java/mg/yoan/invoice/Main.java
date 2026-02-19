package mg.yoan.invoice;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class Main {
    List<InvoiceTotal> findInvoiceTotals() {
        ArrayList<InvoiceTotal> invoiceTotals = new ArrayList<>();
        DBConnection db = new DBConnection();
        Connection conn = db.getConnection();

        try {
            conn.setAutoCommit(false);

            String sql = """
            SELECT i.id,
                   i.customer_name,
                   COALESCE(SUM(il.unit_price * il.quantity), 0) AS total
            FROM invoice i
            LEFT JOIN invoice_line il ON il.invoice_id = i.id
            GROUP BY i.id, i.customer_name
            ORDER BY i.id
            """;

            PreparedStatement ps = conn.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                int id = rs.getInt("id");
                String customerName = rs.getString("customer_name");
                double total = rs.getDouble("total");

                invoiceTotals.add(new InvoiceTotal(id, customerName, total));
            }

            conn.commit();
            rs.close();
            ps.close();
            conn.close();

            return invoiceTotals;

        } catch (Exception e) {
            try { conn.rollback(); } catch (Exception ignored) {}
            throw new RuntimeException(e);
        }
        }
    };
