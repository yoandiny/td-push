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

    List<InvoiceTotal> findConfirmedAndPaidInvoiceTotals() {
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
            WHERE i.status = 'CONFIRMED' AND i.status = 'PAID'
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

    InvoiceStatusTotal computeStatusTotals() {
        DBConnection db = new DBConnection();
        Connection conn = db.getConnection();

        try {
            conn.setAutoCommit(false);

            String sql = """
            SELECT
                COALESCE(SUM(CASE WHEN i.status = 'PAID'
                    THEN il.quantity * il.unit_price END), 0) AS total_paid,

                COALESCE(SUM(CASE WHEN i.status = 'CONFIRMED'
                    THEN il.quantity * il.unit_price END), 0) AS total_confirmed,

                COALESCE(SUM(CASE WHEN i.status = 'DRAFT'
                    THEN il.quantity * il.unit_price END), 0) AS total_draft
            FROM invoice i
            LEFT JOIN invoice_line il ON il.invoice_id = i.id
            """;

            PreparedStatement ps = conn.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();

            InvoiceStatusTotal totals = null;

            if (rs.next()) {
                double totalPaid = rs.getDouble("total_paid");
                double totalConfirmed = rs.getDouble("total_confirmed");
                double totalDraft = rs.getDouble("total_draft");

                totals = new InvoiceStatusTotal(totalPaid, totalConfirmed, totalDraft);
            } else {
                // cas théorique où rien n'est retourné
                totals = new InvoiceStatusTotal(0, 0, 0);
            }

            conn.commit();

            rs.close();
            ps.close();
            conn.close();

            return totals;

        } catch (Exception e) {
            try { conn.rollback(); } catch (Exception ignored) {}
            throw new RuntimeException(e);
        }
    }

    Double computeWeightedTurnover (){
        throw new UnsupportedOperationException("Not implemented yet");
    };


};
