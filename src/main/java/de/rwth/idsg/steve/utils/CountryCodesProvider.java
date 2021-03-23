package de.rwth.idsg.steve.utils;

import com.neovisionaries.i18n.CountryCode;

import java.util.Arrays;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.Map;

import static de.rwth.idsg.steve.utils.ControllerHelper.EMPTY_OPTION;

/**
 * @author Sevket Goekay <sevketgokay@gmail.com>
 * @since 23.03.2021
 */
public class CountryCodesProvider {

    public static Map<String, String> getCountryCodes() {
        CountryCode[] codes = CountryCode.values();
        Arrays.sort(codes, Comparator.comparing(CountryCode::getName));

        Map<String, String> map = new LinkedHashMap<>(codes.length + 1);
        map.put("", EMPTY_OPTION);

        for (CountryCode c : codes) {
            if (shouldInclude(c)) {
                map.put(c.getAlpha2(), c.getName());
            }
        }
        return map;
    }

    /**
     * There are some invalid codes like {@link CountryCode#UNDEFINED} and {@link CountryCode#EU},
     * or some countries are listed twice {@link CountryCode#FI} - {@link CountryCode#SF} and
     * {@link CountryCode#GB} - {@link CountryCode#UK} which are confusing. We filter these out.
     */
    private static boolean shouldInclude(CountryCode c) {
        return c.getAssignment() == CountryCode.Assignment.OFFICIALLY_ASSIGNED;
    }
}
