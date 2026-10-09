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

@WebServlet("/user/chat")
public class ChatServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        loadChat(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        HttpSession session = req.getSession(false);
        if (session == null || session.getAttribute("user_id") == null) {
            resp.sendRedirect(req.getContextPath() + "/auth/login");
            return;
        }

        int userId = (int) session.getAttribute("user_id");
        int bookingId = Integer.parseInt(req.getParameter("booking_id"));
        String msg = req.getParameter("msg");

        try (Connection conn = DBConnection.getConnection()) {
            int providerId = 0;
            try (PreparedStatement ps = conn.prepareStatement(
                    "SELECT provider_id FROM bookings WHERE id = ?")) {
                ps.setInt(1, bookingId);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) providerId = rs.getInt("provider_id");
                }
            }

            try (PreparedStatement ps = conn.prepareStatement(
                    "INSERT INTO messages (booking_id, sender_id, receiver_id, message) VALUES (?,?,?,?)")) {
                ps.setInt(1, bookingId);
                ps.setInt(2, userId);
                ps.setInt(3, providerId);
                ps.setString(4, msg);
                ps.executeUpdate();
            }
        } catch (Exception e) {
            req.setAttribute("error", e.getMessage());
        }

        loadChat(req, resp);
    }

    private void loadChat(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        HttpSession session = req.getSession(false);
        if (session == null || session.getAttribute("user_id") == null) {
            resp.sendRedirect(req.getContextPath() + "/auth/login");
            return;
        }

        int bookingId = Integer.parseInt(req.getParameter("booking_id"));
        List<ChatMessage> messages = new ArrayList<>();

        try (Connection conn = DBConnection.getConnection()) {
            try (PreparedStatement ps = conn.prepareStatement(
                    "SELECT sender_id, message FROM messages WHERE booking_id = ? ORDER BY id ASC")) {
                ps.setInt(1, bookingId);
                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        messages.add(new ChatMessage(rs.getInt("sender_id"), rs.getString("message")));
                    }
                }
            }
        } catch (Exception e) {
            req.setAttribute("error", e.getMessage());
        }

        req.setAttribute("messages", messages);
        req.setAttribute("bookingId", bookingId);
        req.getRequestDispatcher("/user/chat.jsp").forward(req, resp);
    }
}
