package org.bukkit.craftbukkit.util;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMap.Builder;

public final class CraftChatMessage {
    private static class StringMessage {
        private static final Map<Character, net.minecraft.util.EnumChatFormatting> formatMap;
        private static final Pattern INCREMENTAL_PATTERN = Pattern.compile("(" + String.valueOf(org.bukkit.ChatColor.COLOR_CHAR) + "[0-9a-fk-or])|(\\n)|((?:(?:https?)://)?(?:[-\\w_\\.]{2,}\\.[a-z]{2,4}.*?(?=[\\.\\?!,;:]?(?:[ \\n]|$))))", Pattern.CASE_INSENSITIVE);

        static {
            Builder<Character, net.minecraft.util.EnumChatFormatting> builder = ImmutableMap.builder();
            for (net.minecraft.util.EnumChatFormatting format : net.minecraft.util.EnumChatFormatting.values()) {
                builder.put(Character.toLowerCase(format.func_96298_a()), format);
            }
            formatMap = builder.build();
        }

        private final List<net.minecraft.util.IChatComponent> list = new ArrayList<net.minecraft.util.IChatComponent>();
        private net.minecraft.util.IChatComponent currentChatComponent = new net.minecraft.util.ChatComponentText("");
        private net.minecraft.util.ChatStyle modifier = new net.minecraft.util.ChatStyle();
        private final net.minecraft.util.IChatComponent[] output;
        private int currentIndex;
        private final String message;

        private StringMessage(String message) {
            this.message = message;
            if (message == null) {
                output = new net.minecraft.util.IChatComponent[] { currentChatComponent };
                return;
            }
            list.add(currentChatComponent);

            Matcher matcher = INCREMENTAL_PATTERN.matcher(message);
            String match = null;
            while (matcher.find()) {
                int groupId = 0;
                while ((match = matcher.group(++groupId)) == null) {
                    // NOOP
                }
                appendNewComponent(matcher.start(groupId));
                switch (groupId) {
                case 1:
                    net.minecraft.util.EnumChatFormatting format = formatMap.get(match.toLowerCase().charAt(1));
                    if (format == net.minecraft.util.EnumChatFormatting.RESET) {
                        modifier = new net.minecraft.util.ChatStyle();
                    } else if (format.func_96301_b()) {
                        switch (format) {
                        case BOLD:
                            modifier.func_150227_a(Boolean.TRUE);
                            break;
                        case ITALIC:
                            modifier.func_150217_b(Boolean.TRUE);
                            break;
                        case STRIKETHROUGH:
                            modifier.func_150225_c(Boolean.TRUE);
                            break;
                        case UNDERLINE:
                            modifier.func_150228_d(Boolean.TRUE);
                            break;
                        case OBFUSCATED:
                            modifier.func_150237_e(Boolean.TRUE);
                            break;
                        default:
                            throw new AssertionError("Unexpected message format");
                        }
                    } else { // Color resets formatting
                        modifier = new net.minecraft.util.ChatStyle().func_150238_a(format);
                    }
                    break;
                case 2:
                    currentChatComponent = null;
                    break;
                case 3:
                    if ( !( match.startsWith( "http://" ) || match.startsWith( "https://" ) ) ) {
                        match = "http://" + match;
                    }
                    modifier.func_150241_a(new net.minecraft.event.ClickEvent(net.minecraft.event.ClickEvent.Action.OPEN_URL, match)); // Should be setChatClickable
                    appendNewComponent(matcher.end(groupId));
                    modifier.func_150241_a((net.minecraft.event.ClickEvent) null);
                }
                currentIndex = matcher.end(groupId);
            }

            if (currentIndex < message.length()) {
                appendNewComponent(message.length());
            }

            output = list.toArray(new net.minecraft.util.IChatComponent[list.size()]);
        }

        private void appendNewComponent(int index) {
            if (index <= currentIndex) {
                return;
            }
            net.minecraft.util.IChatComponent addition = new net.minecraft.util.ChatComponentText(message.substring(currentIndex, index)).func_150255_a(modifier);
            currentIndex = index;
            modifier = modifier.func_150232_l();
            if (currentChatComponent == null) {
                currentChatComponent = new net.minecraft.util.ChatComponentText("");
                list.add(currentChatComponent);
            }
            currentChatComponent.func_150257_a(addition);
        }

        private net.minecraft.util.IChatComponent[] getOutput() {
            return output;
        }
    }

    public static net.minecraft.util.IChatComponent[] fromString(String message) {
        return new StringMessage(message).getOutput();
    }

    private CraftChatMessage() {
    }
}
