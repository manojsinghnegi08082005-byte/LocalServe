package com.localserve.provider;

import com.localserve.util.DBConnection;
import com.localserve.util.LocationUtil;

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
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

@WebServlet("/provider/create-profile")
public class CreateProfileServlet extends HttpServlet {

    private static final Pattern PHONE_PATTERN = Pattern.compile("[0-9+\\-() ]{7,20}");

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        Integer providerId = getProviderId(req, resp);
        if (providerId == null) return;

        // load the saved profile (if any) so the form shows the current values
        Map<String, String> form = new HashMap<>();
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(
                     "SELECT name, service, price_per_hour, phone, experience, " +
                     "service_city, service_state, service_pin_code " +
                     "FROM provider_details WHERE user_id = ?")) {
            ps.setInt(1, providerId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    form.put("name", rs.getString("name"));
                    form.put("service", rs.getString("service"));
                    form.put("price", rs.getBigDecimal("price_per_hour").toPlainString());
                    form.put("phone", rs.getString("phone"));
                    form.put("exp", String.valueOf(rs.getInt("experience")));
                    form.put("city", rs.getString("service_city"));
                    form.put("state", rs.getString("service_state"));
                    form.put("pin_code", rs.getString("service_pin_code"));
                }
            }
        } catch (Exception e) {
            log("Could not load profile for provider " + providerId, e);
            req.setAttribute("error", "Could not load your profile right now. Please try again.");
        }

        req.setAttribute("form", form);
        req.getRequestDispatcher("/provider/create_profile.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        req.setCharacterEncoding("UTF-8");

        Integer providerId = getProviderId(req, resp);
        if (providerId == null) return;

        Map<String, String> form = new HashMap<>();
        form.put("name", LocationUtil.clean(req.getParameter("name")));
        form.put("service", LocationUtil.clean(req.getParameter("service")));
        form.put("price", LocationUtil.clean(req.getParameter("price")));
        form.put("phone", LocationUtil.clean(req.getParameter("phone")));
        form.put("exp", LocationUtil.clean(req.getParameter("exp")));
        form.put("city", LocationUtil.clean(req.getParameter("city")));
        form.put("state", LocationUtil.clean(req.getParameter("state")));
        form.put("pin_code", LocationUtil.cleanPin(req.getParameter("pin_code")));

        String error = validate(form);

        if (error == null) {
            double price = Double.parseDouble(form.get("price"));
            int exp = Integer.parseInt(form.get("exp"));

            try (Connection conn = DBConnection.getConnection()) {
                // requires a UNIQUE constraint on provider_details.user_id (see schema.sql)
                String sql = "INSERT INTO provider_details " +
                        "(user_id, name, service, price_per_hour, phone, experience, " +
                        "service_city, service_state, service_pin_code) " +
                        "VALUES (?,?,?,?,?,?,?,?,?) " +
                        "ON DUPLICATE KEY UPDATE name=VALUES(name), service=VALUES(service), " +
                        "price_per_hour=VALUES(price_per_hour), phone=VALUES(phone), experience=VALUES(experience), " +
                        "service_city=VALUES(service_city), service_state=VALUES(service_state), " +
                        "service_pin_code=VALUES(service_pin_code)";
                try (PreparedStatement ps = conn.prepareStatement(sql)) {
                    ps.setInt(1, providerId);
                    ps.setString(2, form.get("name"));
                    ps.setString(3, form.get("service"));
                    ps.setDouble(4, price);
                    ps.setString(5, form.get("phone"));
                    ps.setInt(6, exp);
                    ps.setString(7, form.get("city"));
                    ps.setString(8, form.get("state"));
                    ps.setString(9, form.get("pin_code"));
                    ps.executeUpdate();
                }
                req.setAttribute("msg", "Profile Saved");
            } catch (Exception e) {
                log("Could not save profile for provider " + providerId, e);
                error = "Could not save your profile right now. Please try again.";
            }
        }

        if (error != null) {
            req.setAttribute("error", error);
        }
        req.setAttribute("form", form);
        req.getRequestDispatcher("/provider/create_profile.jsp").forward(req, resp);
    }

    // returns an error message, or null if the form is valid
    private String validate(Map<String, String> f) {
        String name = f.get("name");
        if (name.isEmpty() || name.length() > 100) return "Please enter your name (up to 100 characters).";

        String service = f.get("service");
        if (service.isEmpty() || service.length() > 100) return "Please select a service.";

        double price;
        try {
            price = Double.parseDouble(f.get("price"));
        } catch (NumberFormatException e) {
            return "Please enter a valid price per hour.";
        }
        if (Double.isNaN(price) || price <= 0 || price > 100000) {
            return "Price per hour must be more than 0 and at most 100000.";
        }

        if (!PHONE_PATTERN.matcher(f.get("phone")).matches()) {
            return "Please enter a valid phone number (7 to 20 digits, + - ( ) and spaces allowed).";
        }

        int exp;
        try {
            exp = Integer.parseInt(f.get("exp"));
        } catch (NumberFormatException e) {
            return "Experience must be a whole number of years.";
        }
        if (exp < 0 || exp > 60) return "Experience must be between 0 and 60 years.";

        return LocationUtil.checkLocation(f.get("city"), f.get("state"), f.get("pin_code"));
    }

    // only a logged in provider may open or save the profile, and always their own
    private Integer getProviderId(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        HttpSession session = req.getSession(false);
        if (session == null || session.getAttribute("user_id") == null
                || !"provider".equals(session.getAttribute("role"))) {
            resp.sendRedirect(req.getContextPath() + "/auth/login");
            return null;
        }
        return (Integer) session.getAttribute("user_id");
    }
}
