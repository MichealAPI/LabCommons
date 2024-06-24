package it.mikeslab.commons.api.various;

import net.md_5.bungee.api.ChatColor;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class HexUtils {

    public static final Pattern HEX_PATTERN = Pattern.compile("#(\\w{5}[0-9a-f])");

    public static String translateHexCodes(String textToTranslate) {

        textToTranslate = textToTranslate.replace("&#", "#");

        Matcher matcher = HEX_PATTERN.matcher(textToTranslate);
        StringBuffer buffer = new StringBuffer();

        while(matcher.find()) {
            matcher.appendReplacement(buffer, ChatColor.of("#" + matcher.group(1)).toString());
        }

        return ChatColor.translateAlternateColorCodes('&', matcher.appendTail(buffer).toString());

    }

}
