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

@WebServlet("/provider/requests")
public class RequestsServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        HttpSession session = req.getSession(false);
        if (session == null || session.getAttribute("user_id") == null) {
            resp.sendRedirect(req.getContextPath() + "/auth/login");
            return;
        }

        int providerId = (int) session.getAttribute("user_id");

        // handle accept/reject action passed as query params: ?action=accepted&id=5
        String action = req.getParameter("action");
        String idParam = req.getParameter("id");
        if (action != null && idParam != null) {
            // only these two values are allowed, and a provider can only change their own bookings
            if ("accepted".equals(action) || "rejected".equals(action)) {
                try (Connection conn = DBConnection.getConnection();
                     PreparedStatement ps = conn.prepareStatement(
                             "UPDATE bookings SET status = ? WHERE id = ? AND provider_id = ?")) {
                    ps.setString(1, action);
                    ps.setInt(2, Integer.parseInt(idParam.trim()));
                    ps.setInt(3, providerId);
                    ps.executeUpdate();
                } catch (Exception e) {
                    log("Could not update booking " + idParam, e);
                }
            }
            resp.sendRedirect(req.getContextPath() + "/provider/requests");
            return;
        }

        List<BookingRequest> requests = new ArrayList<>();
        try (Connection conn = DBConnection.getConnection()) {
            String sql = "SELECT b.id, u.name, b.booking_date, b.time_slot, b.status, " +
                    "b.service_city, b.service_state, b.service_pin_code, b.service_address " +
                    "FROM bookings b JOIN users u ON b.user_id = u.id " +
                    "WHERE b.provider_id = ? ORDER BY b.id DESC";
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setInt(1, providerId);
                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        requests.add(new BookingRequest(
                                rs.getInt("id"),
                                rs.getString("name"),
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
            log("Could not load requests for provider " + providerId, e);
            req.setAttribute("error", "Could not load your requests right now. Please try again.");
        }

        req.setAttribute("requests", requests);
        req.getRequestDispatcher("/provider/requests.jsp").forward(req, resp);
    }
}
