package com.localserve.auth;

import com.localserve.util.DBConnection;
import com.localserve.util.PasswordUtil;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

@WebServlet("/auth/register")
public class RegisterServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        req.getRequestDispatcher("/auth/register.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        String name = req.getParameter("name");
        String email = req.getParameter("email");
        String password = req.getParameter("password");
        String role = req.getParameter("role");
        String msg;

        try (Connection conn = DBConnection.getConnection()) {

            String checkSql = "SELECT id FROM users WHERE email = ?";
            try (PreparedStatement check = conn.prepareStatement(checkSql)) {
                check.setString(1, email);
                try (ResultSet rs = check.executeQuery()) {
                    if (rs.next()) {
                        msg = "Email already exists!";
                    } else {
                        String insertSql = "INSERT INTO users (name, email, password, role) VALUES (?,?,?,?)";
                        try (PreparedStatement insert = conn.prepareStatement(insertSql)) {
                            insert.setString(1, name);
                            insert.setString(2, email);
                            insert.setString(3, PasswordUtil.hash(password));
                            insert.setString(4, role);
                            insert.executeUpdate();
                        }
                        msg = "Registered Successfully! Please login.";
                    }
                }
            }
        } catch (Exception e) {
            msg = "Database error: " + e.getMessage();
        }

        req.setAttribute("msg", msg);
        req.getRequestDispatcher("/auth/register.jsp").forward(req, resp);
    }
}
