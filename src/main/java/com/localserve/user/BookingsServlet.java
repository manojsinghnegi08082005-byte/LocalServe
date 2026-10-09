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
import java.util.ArrayList;
import java.util.List;

@WebServlet("/user/bookings")
public class BookingsServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        HttpSession session = req.getSession(false);
        if (session == null || session.getAttribute("user_id") == null) {
            resp.sendRedirect(req.getContextPath() + "/auth/login");
            return;
        }

        int userId = (int) session.getAttribute("user_id");
        List<Booking> bookings = new ArrayList<>();

        try (Connection conn = DBConnection.getConnection()) {
            String sql = "SELECT b.id, p.service, b.booking_date, b.time_slot, b.status, " +
                    "b.service_city, b.service_state, b.service_pin_code, b.service_address " +
                    "FROM bookings b JOIN provider_details p ON b.provider_id = p.user_id " +
                    "WHERE b.user_id = ? ORDER BY b.id DESC";
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setInt(1, userId);
                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        bookings.add(new Booking(
                                rs.getInt("id"),
                                rs.getString("service"),
                                rs.getString("booking_date"),
                                rs.getString("time_slot"),
                                rs.getString("status"),
                                rs.getString("service_city"),
                                rs.getString("service_state"),
                                rs.getString("service_pin_code"),
                                rs.getString("service_address")
                        ));
                    }
                }
            }
        } catch (Exception e) {
            log("Could not load bookings for user " + userId, e);
            req.setAttribute("error", "Could not load your bookings right now. Please try again.");
        }

        req.setAttribute("bookings", bookings);
        req.getRequestDispatcher("/user/bookings.jsp").forward(req, resp);
    }
}
