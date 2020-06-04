package darkkronicle.advancedchat.filters;

import darkkronicle.advancedchat.config.ConfigFilter;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Environment(EnvType.CLIENT)
public class ReplaceFilter {

    public FilteredMessage filter(String message, ConfigFilter filter) {

        FilteredMessage.FilterResult filtered = FilteredMessage.FilterResult.UNKNOWN;
        if (!filter.isActive()) {
            return new FilteredMessage(message, false, false, FilteredMessage.FilterResult.UNKNOWN);

        }

        if (!filter.isRegex()) {
            if (filter.isIgnoreCase()) {
                if (message.toLowerCase().contains(filter.getTrigger().toLowerCase())) {
                    if (filter.getReplaceType() == ConfigFilter.ReplaceType.ONLYCHANGED) {
                        message = message.replaceAll("(?i)" + filter.getTrigger(), filter.getReplaceTo().replaceAll("%REPLACED%", filter.getTrigger()).replaceAll("&", "§"));
                        filtered = FilteredMessage.FilterResult.REPLACE;

                    } else {
                        message = filter.getReplaceTo().replaceAll("%REPLACED%", message).replaceAll("&", "§");
                        filtered = FilteredMessage.FilterResult.BLOCK;

                    }

                }
            } else {
                if (message.contains(filter.getTrigger())) {
                    if (filter.getReplaceType() == ConfigFilter.ReplaceType.ONLYCHANGED) {
                        message = message.replaceAll(filter.getTrigger(), filter.getReplaceTo().replaceAll("%REPLACED%", filter.getTrigger()).replaceAll("&", "§"));
                        filtered = FilteredMessage.FilterResult.REPLACE;

                    } else {
                        message = filter.getReplaceTo().replaceAll("%REPLACED%", filter.getTrigger()).replaceAll("&", "§");
                        filtered = FilteredMessage.FilterResult.BLOCK;

                    }
                }

            }
        } else {
            Pattern pattern = Pattern.compile(filter.getTrigger());
            Matcher matcher = pattern.matcher(message);

            if (matcher.find()) {
                if (filter.getReplaceType() == ConfigFilter.ReplaceType.ONLYCHANGED) {
                    message = matcher.replaceAll(filter.getReplaceTo().replaceAll("%REPLACED%", message).replaceAll("&", "§"));
                    filtered = FilteredMessage.FilterResult.REPLACE;

                } else {
                    message = filter.getReplaceTo().replaceAll("%REPLACED%", message).replaceAll("&", "§");
                    filtered = FilteredMessage.FilterResult.BLOCK;

                }
            }

        }
        if (filtered != FilteredMessage.FilterResult.UNKNOWN) {
            return new FilteredMessage(message, true, filter.isShowUnFilterInLog(), filtered);

        } else {
            return new FilteredMessage(message, false, false, FilteredMessage.FilterResult.UNKNOWN);

        }
    }

}
