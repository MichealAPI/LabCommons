package it.mikeslab.commons.api.various;

import net.md_5.bungee.api.ChatColor;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class HexUtils {

    public static final Pattern HEX_PATTERN = Pattern.compile("#(\\w{5}[0-9a-f])");

    private static boolean ARE_HEXES_SUPPORTED = true;

    public static String translateHexCodes(String textToTranslate) {

        String result = null;

        if(!ARE_HEXES_SUPPORTED) {
            return textToTranslate;
        }

        try {
            textToTranslate = textToTranslate.replace("&#", "#");

            Matcher matcher = HEX_PATTERN.matcher(textToTranslate);
            StringBuffer buffer = new StringBuffer();

            while (matcher.find()) {
                matcher.appendReplacement(buffer, ChatColor.of("#" + matcher.group(1)).toString());
            }

            result = ChatColor
                    .translateAlternateColorCodes('&', matcher.appendTail(buffer).toString());

        } catch (Exception e) {
            ARE_HEXES_SUPPORTED = false;
        }

        return result;
    }


}
