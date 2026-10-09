package com.localserve.util;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.regex.Pattern;

/**
 * Helpers for the city / state / PIN code location matching.
 * Matching is exact on city + state + PIN code. It does NOT measure distance.
 */
public class LocationUtil {

    public static final int MAX_ADDRESS_LENGTH = 255;

    private static final Pattern PIN_PATTERN = Pattern.compile("[0-9]{6}");
    // letters (any language), spaces, dot, apostrophe, hyphen
    private static final Pattern NAME_PATTERN = Pattern.compile("\\p{L}[\\p{L}\\p{M} .'\\-]*");

    /**
     * WHERE condition shared by the provider search and the booking insert, so both
     * always use the same rule. The table alias must be "p" (provider_details).
     * Parameters, in order: pin code, city, state.
     * A provider with NULL location columns can never satisfy this, so providers
     * without a service location are never eligible.
     */
    public static final String MATCH_SQL =
            "p.service_pin_code = ? " +
            "AND LOWER(TRIM(p.service_city)) = LOWER(?) " +
            "AND LOWER(TRIM(p.service_state)) = LOWER(?)";

    /** Trims the text and collapses repeated spaces. null becomes "". */
    public static String clean(String s) {
        if (s == null) return "";
        return s.trim().replaceAll("\\s+", " ");
    }

    /** PIN codes are only trimmed (kept as a string so leading zeros survive). */
    public static String cleanPin(String s) {
        return s == null ? "" : s.trim();
    }

    /**
     * Checks city, state and PIN (already cleaned).
     * Returns an error message, or null if everything is fine.
     */
    public static String checkLocation(String city, String state, String pin) {
        if (city.isEmpty()) return "Please enter the service city.";
        if (city.length() < 2 || city.length() > 100 || !NAME_PATTERN.matcher(city).matches()) {
            return "City can only contain letters, spaces, dots, hyphens and apostrophes (2-100 characters).";
        }
        if (state.isEmpty()) return "Please enter the state.";
        if (state.length() < 2 || state.length() > 100 || !NAME_PATTERN.matcher(state).matches()) {
            return "State can only contain letters, spaces, dots, hyphens and apostrophes (2-100 characters).";
        }
        if (pin.isEmpty()) return "Please enter the PIN code.";
        if (!PIN_PATTERN.matcher(pin).matches()) return "PIN code must be exactly 6 digits.";
        return null;
    }

    /** Address is optional, only the length is checked. */
    public static String checkAddress(String address) {
        if (address.length() > MAX_ADDRESS_LENGTH) {
            return "Full address must be at most " + MAX_ADDRESS_LENGTH + " characters.";
        }
        return null;
    }

    /**
     * Fills the three placeholders of MATCH_SQL starting at parameter index "start".
     * Returns the next free parameter index.
     */
    public static int bindMatch(PreparedStatement ps, int start, String city, String state, String pin)
            throws SQLException {
        ps.setString(start, pin);
        ps.setString(start + 1, city);
        ps.setString(start + 2, state);
        return start + 3;
    }

    /** "Dehradun, Uttarakhand - 248001", or "" when nothing is stored (old data). */
    public static String format(String city, String state, String pin) {
        StringBuilder sb = new StringBuilder();
        if (city != null && !city.trim().isEmpty()) sb.append(city.trim());
        if (state != null && !state.trim().isEmpty()) {
            if (sb.length() > 0) sb.append(", ");
            sb.append(state.trim());
        }
        if (pin != null && !pin.trim().isEmpty()) {
            if (sb.length() > 0) sb.append(" - ");
            sb.append(pin.trim());
        }
        return sb.toString();
    }
}
