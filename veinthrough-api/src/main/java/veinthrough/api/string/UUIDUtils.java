package veinthrough.api.string;

import java.util.Arrays;
import java.util.Optional;
import java.util.UUID;

import static veinthrough.api.string.Regex.delimOfMacAddress;
import static veinthrough.api.string.Regex.isMacAddress;

/**
 * {@link veinthrough.api.string.Regex#isMacAddress(String)}
 * {@link veinthrough.api.string.Regex#delimOfMacAddress(String)}: delim of mac address can be ":"/"-"
 */
public class UUIDUtils {
    private static final int DASHED_UUID_LENGTH = 32;
    private static final int DASHED_UUID_DIV1 = 8;
    private static final int DASHED_UUID_DIV2 = 12;
    private static final int DASHED_UUID_DIV3 = 16;
    private static final int DASHED_UUID_DIV4 = 20;

    public static Optional<UUID> macToUUID(String mac) {
        if (isMacAddress(mac)) {
            // noinspection OptionalGetWithoutIsPresent
            StringBuilder strBuilder = new StringBuilder(
                    // delim can be ":" or "-"
                    mac.replaceAll(delimOfMacAddress(mac).get().toString(), ""));

            char[] padding = new char[DASHED_UUID_LENGTH - strBuilder.length()];
            Arrays.fill(padding, '0');

            return Optional.of(
                    UUID.fromString(
                            strBuilder.reverse() // reverse
                                    .append(padding) // pad '0'
                                    .reverse() // reverse back
                                    .insert(DASHED_UUID_DIV1, '-')
                                    .insert(DASHED_UUID_DIV2 + 1, '-')
                                    .insert(DASHED_UUID_DIV3 + 2, '-')
                                    .insert(DASHED_UUID_DIV4 + 3, '-')
                                    .toString()));
        }
        return Optional.empty();
    }
}
