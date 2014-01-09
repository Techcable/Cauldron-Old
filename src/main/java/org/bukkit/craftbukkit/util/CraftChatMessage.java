package org.bukkit.craftbukkit.util;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMap.Builder;

public final class CraftChatMessage {
    private static class FromString {
        private static final Map<Character, net.minecraft.util.EnumChatFormatting> formatMap;

        static {
            Builder<Character, net.minecraft.util.EnumChatFormatting> builder = ImmutableMap.builder();
            for (net.minecraft.util.EnumChatFormatting format : net.minecraft.util.EnumChatFormatting.values()) {
                builder.put(format.func_96298_a(), format);
            }
            formatMap = builder.build();
        }

        private final List<net.minecraft.util.IChatComponent> list = new ArrayList<net.minecraft.util.IChatComponent>();
        private net.minecraft.util.IChatComponent currentChatComponent = new net.minecraft.util.ChatComponentText("");
        private net.minecraft.util.ChatStyle modifier = new net.minecraft.util.ChatStyle();
        private StringBuilder builder = new StringBuilder();
        private final net.minecraft.util.IChatComponent[] output;
        private static final Pattern url = Pattern.compile("^(\u00A7.)*?((?:(https?)://)?([-\\w_\\.]{2,}\\.[a-z]{2,4})(/\\S*?)?)(\u00A7.)*?$");
        private int lastWord = 0;

        private FromString(String message) {
            if (message == null) {
                output = new net.minecraft.util.IChatComponent[] { currentChatComponent };
                return;
            }
            list.add(currentChatComponent);

            net.minecraft.util.EnumChatFormatting format = null;
            Matcher matcher = url.matcher(message);
            lastWord = 0;

            for (int i = 0; i < message.length(); i++) {
                char currentChar = message.charAt(i);
                if (currentChar == '\u00A7' && (i < (message.length() - 1)) && (format = formatMap.get(message.charAt(i + 1))) != null) {
                    checkUrl(matcher, message, i);
                    lastWord++;
                    if (builder.length() > 0) {
                        appendNewComponent();
                    }

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
                    i++;
                } else if (currentChar == '\n') {
                    checkUrl(matcher, message, i);
                    lastWord = i + 1;
                    if (builder.length() > 0) {
                        appendNewComponent();
                    }
                    currentChatComponent = null;
                } else {
                    if (currentChar == ' ' || i == message.length() - 1) {
                        if (checkUrl(matcher, message, i)) {
                            break;
                        }
                    }
                    builder.append(currentChar);
                }
            }

            if (builder.length() > 0) {
                appendNewComponent();
            }

            output = list.toArray(new net.minecraft.util.IChatComponent[0]);
        }

        private boolean checkUrl(Matcher matcher, String message, int i) {
            Matcher urlMatcher = matcher.region(lastWord, i == message.length() - 1 ? message.length() : i);
            lastWord = i + 1;
            if (urlMatcher.find()) {
                String fullUrl = urlMatcher.group(2);
                String protocol = urlMatcher.group(3);
                String url = urlMatcher.group(4);
                String path = urlMatcher.group(5);
                builder.delete(builder.length() - fullUrl.length() + (i == message.length() - 1 ? 1 : 0), builder.length());
                if (builder.length() > 0) {
                    appendNewComponent();
                }
                builder.append(fullUrl);
                net.minecraft.event.ClickEvent link = new net.minecraft.event.ClickEvent(net.minecraft.event.ClickEvent.Action.OPEN_URL,
                        (protocol!=null?protocol:"http") + "://" + url + (path!=null?path:""));
                modifier.func_150241_a(link);
                appendNewComponent();
                modifier.func_150241_a((net.minecraft.event.ClickEvent) null);
                if (i == message.length() - 1) {
                    return true;
                }
            }
            return false;
        }

        private void appendNewComponent() {
            net.minecraft.util.IChatComponent addition = new net.minecraft.util.ChatComponentText(builder.toString()).func_150255_a(modifier);
            builder = new StringBuilder();
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
        return new FromString(message).getOutput();
    }

    private CraftChatMessage() {
    }
}
