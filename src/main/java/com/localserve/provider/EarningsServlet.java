package com.localserve.provider;

import com.localserve.util.DBConnection;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

@WebServlet("/provider/earnings")
public class EarningsServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        loadEarnings(req, resp, null);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        // "withdraw" is a placeholder action, same as the original PHP version
        loadEarnings(req, resp, "Withdrawal Request Sent!");
    }

    private void loadEarnings(HttpServletRequest req, HttpServletResponse resp, String msg)
            throws ServletException, IOException {

        HttpSession session = req.getSession(false);
        if (session == null || session.getAttribute("user_id") == null) {
            resp.sendRedirect(req.getContextPath() + "/auth/login");
            return;
        }

        int providerId = (int) session.getAttribute("user_id");
        double total = 0;
        List<EarningEntry> entries = new ArrayList<>();

        try (Connection conn = DBConnection.getConnection()) {

            String totalSql = "SELECT SUM(p.price_per_hour) total FROM bookings b " +
                    "JOIN provider_details p ON b.provider_id = p.user_id " +
                    "WHERE b.provider_id = ? AND b.status = 'accepted'";
            try (PreparedStatement ps = conn.prepareStatement(totalSql)) {
                ps.setInt(1, providerId);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) total = rs.getDouble("total");
                }
            }

            String listSql = "SELECT b.id, b.booking_date, b.time_slot, p.price_per_hour FROM bookings b " +
                    "JOIN provider_details p ON b.provider_id = p.user_id " +
                    "WHERE b.provider_id = ? AND b.status = 'accepted' ORDER BY b.id DESC";
            try (PreparedStatement ps = conn.prepareStatement(listSql)) {
                ps.setInt(1, providerId);
                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        entries.add(new EarningEntry(
                                rs.getInt("id"),
                                rs.getString("booking_date"),
                                rs.getString("time_slot"),
                                rs.getDouble("price_per_hour")
                        ));
                    }
                }
            }
        } catch (Exception e) {
            req.setAttribute("error", e.getMessage());
        }

        req.setAttribute("total", total);
        req.setAttribute("entries", entries);
        if (msg != null) req.setAttribute("msg", msg);
        req.getRequestDispatcher("/provider/earnings.jsp").forward(req, resp);
    }
}
