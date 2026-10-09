package com.localserve.user;

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

@WebServlet("/user/dashboard")
public class DashboardServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        HttpSession session = req.getSession(false);
        if (session == null || session.getAttribute("user_id") == null) {
            resp.sendRedirect(req.getContextPath() + "/auth/login");
            return;
        }

        int userId = (int) session.getAttribute("user_id");
        int total = 0, pending = 0, completed = 0;

        try (Connection conn = DBConnection.getConnection()) {
            total = count(conn, "SELECT COUNT(*) t FROM bookings WHERE user_id=?", userId);
            pending = count(conn, "SELECT COUNT(*) t FROM bookings WHERE user_id=? AND status='pending'", userId);
            completed = count(conn, "SELECT COUNT(*) t FROM bookings WHERE user_id=? AND status='accepted'", userId);
        } catch (Exception e) {
            req.setAttribute("error", e.getMessage());
        }

        req.setAttribute("total", total);
        req.setAttribute("pending", pending);
        req.setAttribute("completed", completed);
        req.getRequestDispatcher("/user/dashboard.jsp").forward(req, resp);
    }

    private int count(Connection conn, String sql, int userId) throws Exception {
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? rs.getInt("t") : 0;
            }
        }
    }
}
