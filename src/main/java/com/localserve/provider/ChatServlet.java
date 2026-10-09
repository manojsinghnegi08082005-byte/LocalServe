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

@WebServlet("/provider/chat")
public class ChatServlet extends HttpServlet {

    public static class Msg {
        public int senderId;
        public String text;
        public Msg(int senderId, String text) { this.senderId = senderId; this.text = text; }
    }

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

        int providerId = (int) session.getAttribute("user_id");
        int bookingId = Integer.parseInt(req.getParameter("booking_id"));
        String msg = req.getParameter("msg");

        try (Connection conn = DBConnection.getConnection()) {
            int userId = 0;
            try (PreparedStatement ps = conn.prepareStatement(
                    "SELECT user_id FROM bookings WHERE id = ?")) {
                ps.setInt(1, bookingId);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) userId = rs.getInt("user_id");
                }
            }

            try (PreparedStatement ps = conn.prepareStatement(
                    "INSERT INTO messages (booking_id, sender_id, receiver_id, message) VALUES (?,?,?,?)")) {
                ps.setInt(1, bookingId);
                ps.setInt(2, providerId);
                ps.setInt(3, userId);
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
        List<Msg> messages = new ArrayList<>();

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(
                     "SELECT sender_id, message FROM messages WHERE booking_id = ? ORDER BY id ASC")) {
            ps.setInt(1, bookingId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    messages.add(new Msg(rs.getInt("sender_id"), rs.getString("message")));
                }
            }
        } catch (Exception e) {
            req.setAttribute("error", e.getMessage());
        }

        req.setAttribute("messages", messages);
        req.setAttribute("bookingId", bookingId);
        req.getRequestDispatcher("/provider/chat.jsp").forward(req, resp);
    }
}
