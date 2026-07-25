package pro.eng.yui.oss.osm.lib.jppostalcore.parser;

import java.util.Map;

public class AddressParser {

    public String getView(Map<String, String> tags){
        StringBuilder result = new StringBuilder();

        // Add postal code if available
        String postalCode = tags.get("addr:postcode");
        if (postalCode != null && !postalCode.isEmpty()) {
            result.append("〒").append(postalCode).append(" ");
        }

        // Address components in order
        String[] components = {
                "addr:province",
                "addr:county",
                "addr:city",
                "addr:suburb",
                "addr:quarter",
                "addr:neighbourhood"
        };

        for (String key : components) {
            String value = tags.get(key);
            if (value != null && !value.isEmpty()) {
                result.append(value).append(" ");
            }
        }

        // Handle block_number and housenumber with hyphen
        String blockNumber = tags.get("addr:block_number");
        String houseNumber = tags.get("addr:housenumber");
        if (blockNumber != null && !blockNumber.isEmpty() && houseNumber != null && !houseNumber.isEmpty()) {
            result.append(blockNumber).append("-").append(houseNumber).append(" ");
        } else if (blockNumber != null && !blockNumber.isEmpty()) {
            result.append(blockNumber).append(" ");
        } else if (houseNumber != null && !houseNumber.isEmpty()) {
            result.append(houseNumber).append(" ");
        }

        // Remaining components
        String[] remainingComponents = {
                "addr:housename",
                "addr:floor",
                "addr:room"
        };

        for (String key : remainingComponents) {
            String value = tags.get(key);
            if (value != null && !value.isEmpty()) {
                result.append(value).append(" ");
            }
        }

        // Remove trailing space if exists
        return result.toString().trim();
    }

}
