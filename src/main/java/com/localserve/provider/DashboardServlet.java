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

@WebServlet("/provider/dashboard")
public class DashboardServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        HttpSession session = req.getSession(false);
        if (session == null || session.getAttribute("user_id") == null) {
            resp.sendRedirect(req.getContextPath() + "/auth/login");
            return;
        }

        int providerId = (int) session.getAttribute("user_id");
        int total = 0, pending = 0, completed = 0;
        boolean needsLocation = false;

        try (Connection conn = DBConnection.getConnection()) {
            total = count(conn, "SELECT COUNT(*) t FROM bookings WHERE provider_id=?", providerId);
            pending = count(conn, "SELECT COUNT(*) t FROM bookings WHERE provider_id=? AND status='pending'", providerId);
            completed = count(conn, "SELECT COUNT(*) t FROM bookings WHERE provider_id=? AND status='accepted'", providerId);

            // a provider without a full service location is not shown to any customer
            needsLocation = count(conn, "SELECT COUNT(*) t FROM provider_details WHERE user_id=? " +
                    "AND service_city IS NOT NULL AND service_city <> '' " +
                    "AND service_state IS NOT NULL AND service_state <> '' " +
                    "AND service_pin_code IS NOT NULL AND service_pin_code <> ''", providerId) == 0;
        } catch (Exception e) {
            log("Could not load dashboard for provider " + providerId, e);
            req.setAttribute("error", "Could not load dashboard figures right now.");
        }

        req.setAttribute("total", total);
        req.setAttribute("pending", pending);
        req.setAttribute("completed", completed);
        req.setAttribute("status", "Active");
        req.setAttribute("needsLocation", needsLocation);
        req.getRequestDispatcher("/provider/dashboard.jsp").forward(req, resp);
    }

    private int count(Connection conn, String sql, int providerId) throws Exception {
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, providerId);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? rs.getInt("t") : 0;
            }
        }
    }
}
