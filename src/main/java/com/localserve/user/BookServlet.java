package com.localserve.user;

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
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@WebServlet("/user/book")
public class BookServlet extends HttpServlet {

    private static final List<String> TIME_SLOTS = Arrays.asList(
            "9 AM - 11 AM", "11 AM - 1 PM", "1 PM - 3 PM", "3 PM - 5 PM", "5 PM - 7 PM");

    // providers whose saved service location equals the location the customer typed
    private static final String FIND_PROVIDERS_SQL =
            "SELECT p.user_id, p.service, p.price_per_hour, p.phone, p.experience, " +
            "p.service_city, p.service_state, p.service_pin_code " +
            "FROM provider_details p WHERE " + LocationUtil.MATCH_SQL + " " +
            "ORDER BY p.service, p.price_per_hour";

    // The provider check and the insert are ONE statement: if the provider does not exist or
    // is not in the requested location, the SELECT returns no row and nothing is inserted.
    private static final String INSERT_BOOKING_SQL =
            "INSERT INTO bookings (user_id, provider_id, status, booking_date, time_slot, " +
            "service_city, service_state, service_pin_code, service_address) " +
            "SELECT ?, p.user_id, 'pending', ?, ?, ?, ?, ?, ? " +
            "FROM provider_details p WHERE p.user_id = ? AND " + LocationUtil.MATCH_SQL;

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        if (getCustomerId(req, resp) == null) return;
        showProviders(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        req.setCharacterEncoding("UTF-8");

        Integer userId = getCustomerId(req, resp);
        if (userId == null) return;

        String providerParam = LocationUtil.clean(req.getParameter("provider_id"));
        String date = LocationUtil.clean(req.getParameter("date"));
        String time = LocationUtil.clean(req.getParameter("time"));
        String city = LocationUtil.clean(req.getParameter("city"));
        String state = LocationUtil.clean(req.getParameter("state"));
        String pin = LocationUtil.cleanPin(req.getParameter("pin_code"));
        String address = LocationUtil.clean(req.getParameter("address"));

        String error = validateBooking(providerParam, date, time, city, state, pin, address);

        if (error == null) {
            int providerId = Integer.parseInt(providerParam);
            try (Connection conn = DBConnection.getConnection();
                 PreparedStatement ps = conn.prepareStatement(INSERT_BOOKING_SQL)) {

                ps.setInt(1, userId);
                ps.setDate(2, java.sql.Date.valueOf(LocalDate.parse(date)));
                ps.setString(3, time);
                ps.setString(4, city);
                ps.setString(5, state);
                ps.setString(6, pin);
                if (address.isEmpty()) {
                    ps.setNull(7, java.sql.Types.VARCHAR);
                } else {
                    ps.setString(7, address);
                }
                ps.setInt(8, providerId);
                LocationUtil.bindMatch(ps, 9, city, state, pin);

                int rows = ps.executeUpdate();
                if (rows == 1) {
                    req.setAttribute("msg", "Booking Sent");
                } else {
                    error = "This provider is not available for the service location you entered. " +
                            "Please choose a provider from the list below.";
                }
            } catch (Exception e) {
                log("Could not save booking for user " + userId, e);
                error = "Something went wrong while saving your booking. Please try again.";
            }
        }

        if (error != null) {
            req.setAttribute("error", error);
        }
        showProviders(req, resp);
    }

    // returns an error message, or null when everything is valid
    private String validateBooking(String providerParam, String date, String time,
                                   String city, String state, String pin, String address) {

        String locationError = LocationUtil.checkLocation(city, state, pin);
        if (locationError != null) return locationError;

        String addressError = LocationUtil.checkAddress(address);
        if (addressError != null) return addressError;

        if (providerParam.isEmpty() || !providerParam.matches("[0-9]{1,9}")) {
            return "Please select a valid service provider.";
        }

        if (date.isEmpty()) return "Please select a booking date.";
        LocalDate bookingDate;
        try {
            bookingDate = LocalDate.parse(date);
        } catch (DateTimeParseException e) {
            return "The booking date is not valid.";
        }
        if (bookingDate.isBefore(LocalDate.now())) {
            return "The booking date cannot be in the past.";
        }

        if (!TIME_SLOTS.contains(time)) return "Please select a valid time slot.";

        return null;
    }

    // only logged in customers (role "user") may use this page
    private Integer getCustomerId(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        HttpSession session = req.getSession(false);
        if (session == null || session.getAttribute("user_id") == null
                || !"user".equals(session.getAttribute("role"))) {
            resp.sendRedirect(req.getContextPath() + "/auth/login");
            return null;
        }
        return (Integer) session.getAttribute("user_id");
    }

    private void showProviders(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        String city = LocationUtil.clean(req.getParameter("city"));
        String state = LocationUtil.clean(req.getParameter("state"));
        String pin = LocationUtil.cleanPin(req.getParameter("pin_code"));
        String address = LocationUtil.clean(req.getParameter("address"));

        boolean asked = req.getParameter("city") != null
                || req.getParameter("state") != null
                || req.getParameter("pin_code") != null;

        List<Provider> providers = new ArrayList<>();
        boolean searched = false;

        if (asked) {
            String locationError = LocationUtil.checkLocation(city, state, pin);
            if (locationError != null) {
                req.setAttribute("locationError", locationError);
            } else {
                try (Connection conn = DBConnection.getConnection();
                     PreparedStatement ps = conn.prepareStatement(FIND_PROVIDERS_SQL)) {

                    LocationUtil.bindMatch(ps, 1, city, state, pin);
                    try (ResultSet rs = ps.executeQuery()) {
                        while (rs.next()) {
                            providers.add(new Provider(
                                    rs.getInt("user_id"),
                                    rs.getString("service"),
                                    rs.getDouble("price_per_hour"),
                                    rs.getString("phone"),
                                    rs.getInt("experience"),
                                    rs.getString("service_city"),
                                    rs.getString("service_state"),
                                    rs.getString("service_pin_code")
                            ));
                        }
                    }
                    searched = true;
                } catch (Exception e) {
                    log("Could not load providers", e);
                    // keep an earlier error (e.g. a failed booking) if there already is one
                    if (req.getAttribute("error") == null) {
                        req.setAttribute("error", "Could not load service providers right now. Please try again.");
                    }
                }
            }
        }

        req.setAttribute("city", city);
        req.setAttribute("state", state);
        req.setAttribute("pinCode", pin);
        req.setAttribute("address", address);
        req.setAttribute("searched", searched);
        req.setAttribute("providers", providers);
        req.setAttribute("timeSlots", TIME_SLOTS);
        req.setAttribute("today", LocalDate.now().toString());
        req.getRequestDispatcher("/user/book.jsp").forward(req, resp);
    }
}
