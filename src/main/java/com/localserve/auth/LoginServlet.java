package com.localserve.auth;

import com.localserve.util.DBConnection;
import com.localserve.util.PasswordUtil;

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

@WebServlet("/auth/login")
public class LoginServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        req.getRequestDispatcher("/auth/login.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        String email = req.getParameter("email");
        String password = req.getParameter("password");
        String msg = "";

        try (Connection conn = DBConnection.getConnection()) {

            String sql = "SELECT * FROM users WHERE email = ?";
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setString(1, email);
                try (ResultSet rs = ps.executeQuery()) {

                    if (rs.next()) {
                        String storedHash = rs.getString("password");

                        if (PasswordUtil.verify(password, storedHash)) {

                            int userId = rs.getInt("id");
                            String role = rs.getString("role");
                            String name = rs.getString("name");

                            HttpSession session = req.getSession();
                            session.setAttribute("user_id", userId);
                            session.setAttribute("role", role);
                            session.setAttribute("name", name);

                            if ("user".equals(role)) {
                                resp.sendRedirect(req.getContextPath() + "/user/dashboard");
                                return;
                            } else {
                                // provider -> check if profile already created
                                String checkSql = "SELECT id FROM provider_details WHERE user_id = ?";
                                try (PreparedStatement checkPs = conn.prepareStatement(checkSql)) {
                                    checkPs.setInt(1, userId);
                                    try (ResultSet checkRs = checkPs.executeQuery()) {
                                        if (checkRs.next()) {
                                            resp.sendRedirect(req.getContextPath() + "/provider/dashboard");
                                        } else {
                                            resp.sendRedirect(req.getContextPath() + "/provider/create-profile");
                                        }
                                        return;
                                    }
                                }
                            }
                        } else {
                            msg = "Wrong Password!";
                        }
                    } else {
                        msg = "User not found!";
                    }
                }
            }
        } catch (Exception e) {
            msg = "Database error: " + e.getMessage();
        }

        req.setAttribute("msg", msg);
        req.getRequestDispatcher("/auth/login.jsp").forward(req, resp);
    }
}
