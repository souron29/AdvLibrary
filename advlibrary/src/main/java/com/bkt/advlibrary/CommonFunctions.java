package com.bkt.advlibrary;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Vibrator;
import android.text.SpannableString;
import android.text.style.StrikethroughSpan;
import android.util.SparseArray;
import androidx.core.graphics.drawable.DrawableCompat;
import java.io.File;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Random;

public class CommonFunctions {
    public static boolean isBlank(String value) {
        return value == null || value.equals("") || value.equals("null") || value.trim().equals("");
    }

    private static <T> T nvl(T first, T ifNull) {
        if (first == null) {
            return ifNull;
        }
        return first;
    }

    public static String[] nvl(String[] input, String whenNull) {
        if (input == null) {
            return null;
        }
        String[] output = new String[input.length];
        for (int i = 0; i < input.length; i++) {
            output[i] = (String) nvl(input[i], whenNull);
        }
        return output;
    }

    public static String getReferLink(String number) {
        return "https://foodtopia.co.in/?invitedBy=" + number;
    }

    public static Uri getResourceUri(Context context, int resource_id) {
        return Uri.parse("android.resource://" + context.getResources().getResourcePackageName(resource_id) + '/' + context.getResources().getResourceTypeName(resource_id) + '/' + context.getResources().getResourceEntryName(resource_id));
    }

    public static String clipString(String input, String[] args) {
        String toCheck = input;
        if (args == null || toCheck == null) {
            return null;
        }
        for (String c : args) {
            toCheck = toCheck.replace(c, "");
        }
        return toCheck;
    }

    public static int convertToPx(int dp) {
        return (int) (((float) dp) * Resources.getSystem().getDisplayMetrics().density);
    }

    public static int convertTodp(int px) {
        return (int) (((float) px) / Resources.getSystem().getDisplayMetrics().density);
    }

    /* access modifiers changed from: private */
    public static Object decode(Object code, Object... args) {
        for (int index = 0; index < args.length; index++) {
            if (args[index].equals(code)) {
                return args[index];
            }
        }
        return null;
    }

    public static <C> List<C> asList(SparseArray<C> sparseArray) {
        if (sparseArray == null) {
            return null;
        }
        List<C> arrayList = new ArrayList<>(sparseArray.size());
        for (int i = 0; i < sparseArray.size(); i++) {
            arrayList.add(sparseArray.valueAt(i));
        }
        return arrayList;
    }

    public static class Colors {
        public static int getInvertedColor(int pixel, float alphaValue) {
            return Color.argb((int) (255.0f * alphaValue), 255 - Color.red(pixel), 255 - Color.green(pixel), 255 - Color.blue(pixel));
        }

        public static int addTransparency(int color, double alphaValue) {
            return Color.argb((int) (255.0d * alphaValue), Color.red(color), Color.green(color), Color.blue(color));
        }

        public static void changeDrawableColor(Drawable drawable, int color, boolean removeColor) {
            if (!removeColor) {
                DrawableCompat.setTint(drawable, color);
            } else {
                DrawableCompat.setTintList(drawable, (ColorStateList) null);
            }
        }

        public static int getColor(int r, int g, int b, float alphaValue) {
            return Color.argb((int) (255.0f * alphaValue), Math.min(r, 255), Math.min(g, 255), Math.min(b, 255));
        }

        public static int getColor(Context context, int id) {
            if (context == null) {
                return 0;
            }
            try {
                if (Build.VERSION.SDK_INT >= 23) {
                    return context.getColor(id);
                }
                return context.getResources().getColor(id);
            } catch (Exception e) {
                if (Build.VERSION.SDK_INT >= 23) {
                    return context.getColor(id);
                }
                return context.getResources().getColor(id);
            }
        }

        public static Drawable changeColor(Drawable image, int color) {
            if (image != null) {
                image.setColorFilter(new PorterDuffColorFilter(color, PorterDuff.Mode.SRC_ATOP));
            }
            return image;
        }

        public static boolean isColorDark(int color) {
            return 1.0d - ((((((double) Color.red(color)) * 0.299d) + (((double) Color.green(color)) * 0.587d)) + (((double) Color.blue(color)) * 0.114d)) / 255.0d) >= 0.5d;
        }

        public static int getRandomColorBetween(int min, int max) {
            Random rand = new Random();
            return getColor(rand.nextInt((max - min) + 1) + min, rand.nextInt((max - min) + 1) + min, rand.nextInt((max - min) + 1) + min, 1.0f);
        }

        public static int getRandomLightColor() {
            return getRandomColorBetween(170, 255);
        }

        public static int getRandomDarkColor() {
            return getRandomColorBetween(20, 100);
        }

        public static int getRandomColor() {
            return getRandomColorBetween(50, 200);
        }
    }

    public static class StringUtils {
        public static String toTitleCase(String input) {
            StringBuilder output = new StringBuilder();
            boolean nextTitleCase = true;
            for (char c : input.toCharArray()) {
                if (Character.isSpaceChar(c)) {
                    nextTitleCase = true;
                } else if (nextTitleCase) {
                    c = Character.toTitleCase(c);
                    nextTitleCase = false;
                }
                output.append(c);
            }
            return output.toString();
        }

        /*public static String getInitials(String input, int letters) {
            if (input == null || input.isEmpty()) {
                return "";
            }
            String input2 = input.replaceAll(RegularEx.NON_ALPHANUMERIC_SPACE, "").replace("  ", " ");
            ArrayList<String> words = getWords(input2);
            String output = "";
            if (words.size() == 1) {
                output = output + words.get(0).substring(0, NumUtils.min(input2.length(), letters));
            } else if (words.size() > 1) {
                boolean hasWords = true;
                int i = 0;
                while (hasWords) {
                    output = output + words.get(i).substring(0, 1);
                    i++;
                    hasWords = i < letters && i < words.size() && words.get(i) != null && !words.get(i).isEmpty();
                }
            }
            return toTitleCase(output);
        }*/

        public static SpannableString strikeThrough(String text) {
            SpannableString output = new SpannableString(text);
            output.setSpan(new StrikethroughSpan(), 0, text.length(), 33);
            return output;
        }

        /* JADX WARNING: Removed duplicated region for block: B:6:0x0015  */
        /* JADX WARNING: Removed duplicated region for block: B:7:0x0029  */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public static String toSentenceCase(String r6, boolean r7) {
            /*
                if (r6 == 0) goto L_0x00bc
                boolean r0 = r6.isEmpty()
                if (r0 != 0) goto L_0x00bc
                java.lang.StringBuilder r0 = new java.lang.StringBuilder
                java.lang.String r1 = r6.toLowerCase()
                r0.<init>(r1)
                r1 = -1
            L_0x0012:
                r2 = -1
                if (r1 != r2) goto L_0x0029
                int r2 = r1 + 1
                int r3 = r1 + 2
                int r4 = r1 + 1
                int r5 = r1 + 2
                java.lang.String r4 = r0.substring(r4, r5)
                java.lang.String r4 = r4.toUpperCase()
                r0.replace(r2, r3, r4)
                goto L_0x007a
            L_0x0029:
                int r2 = r1 + 1
                java.lang.String r2 = r0.substring(r1, r2)
                java.lang.String r3 = " "
                boolean r2 = java.util.Objects.equals(r2, r3)
                if (r2 == 0) goto L_0x004b
                int r2 = r1 + 1
                int r3 = r1 + 2
                int r4 = r1 + 1
                int r5 = r1 + 2
                java.lang.String r4 = r0.substring(r4, r5)
                java.lang.String r4 = r4.toUpperCase()
                r0.replace(r2, r3, r4)
                goto L_0x007a
            L_0x004b:
                int r2 = r1 + 1
                java.lang.StringBuilder r4 = new java.lang.StringBuilder
                r4.<init>()
                java.lang.StringBuilder r3 = r4.append(r3)
                int r4 = r1 + 1
                java.lang.String r4 = r0.substring(r1, r4)
                java.lang.StringBuilder r3 = r3.append(r4)
                java.lang.String r3 = r3.toString()
                r0.replace(r1, r2, r3)
                int r2 = r1 + 1
                int r3 = r1 + 2
                int r4 = r1 + 1
                int r5 = r1 + 2
                java.lang.String r4 = r0.substring(r4, r5)
                java.lang.String r4 = r4.toUpperCase()
                r0.replace(r2, r3, r4)
            L_0x007a:
                java.lang.String r2 = "."
                int r3 = r0.indexOf(r2, r1)
                int r1 = r3 + 1
                if (r1 <= 0) goto L_0x008a
                int r3 = r0.length()
                if (r1 < r3) goto L_0x0012
            L_0x008a:
                int r3 = r0.length()
                int r3 = r3 + -1
                char r3 = r0.charAt(r3)
                java.lang.Character r3 = java.lang.Character.valueOf(r3)
                int r4 = r0.length()
                int r4 = r4 + -1
                java.lang.String r4 = r0.substring(r4)
                boolean r4 = java.util.Objects.equals(r4, r2)
                if (r4 != 0) goto L_0x00b7
                char r4 = r3.charValue()
                boolean r4 = java.lang.Character.isAlphabetic(r4)
                if (r4 == 0) goto L_0x00b7
                if (r7 == 0) goto L_0x00b7
                r0.append(r2)
            L_0x00b7:
                java.lang.String r2 = r0.toString()
                return r2
            L_0x00bc:
                java.lang.String r0 = ""
                return r0
            */
            throw new UnsupportedOperationException("Method not decompiled: com.oneminute.wowfunctions.CommonFunctions.StringUtils.toSentenceCase(java.lang.String, boolean):java.lang.String");
        }

        public static String trim(String input) {
            return input.replaceAll("\\s+", " ");
        }

        public static String superTrim(String input, String... subStrings) {
            String output = input.trim().replace(" ", "");
            if (subStrings != null && subStrings.length > 0) {
                for (String str : subStrings) {
                    output = output.replace(str, "");
                }
            }
            return output;
        }

        public static String getCode(String input, int distance) {
            String output = "";
            char[] chars = trim(input).toCharArray();
            int i = 0;
            while (i < input.length()) {
                output = output + chars[i];
                i += distance;
            }
            return output;
        }

        public static ArrayList<String> getWords(String input) {
            int wordCount = 0;
            int charCount = 0;
            ArrayList<String> words = new ArrayList<>();
            StringBuilder word = new StringBuilder();
            for (char c : input.toCharArray()) {
                charCount++;
                if (Character.isSpaceChar(c)) {
                    wordCount++;
                    words.add(word.toString());
                    word = new StringBuilder();
                } else {
                    word.append(c);
                }
                if (charCount == input.length()) {
                    words.add(word.toString());
                    wordCount++;
                }
            }
            return words;
        }

        public static ArrayList<String> getWords(String text, String delimiter) {
            if (text == null || text.isEmpty()) {
                return new ArrayList<>();
            }
            if (delimiter == null || delimiter.isEmpty()) {
                ArrayList<String> list = new ArrayList<>();
                list.add(text);
                return list;
            }
            StringBuilder word = new StringBuilder();
            ArrayList<String> words = new ArrayList<>();
            while (text.length() > 0) {
                if (!text.startsWith(delimiter)) {
                    word.append(text.substring(0, 1));
                    text = text.substring(1);
                } else {
                    words.add(word.toString());
                    word = new StringBuilder();
                    text = text.substring(delimiter.length());
                }
            }
            if (word.length() > 0) {
                words.add(word.toString());
            }
            return words;
        }

        /*static boolean compareString(String main, String substring, boolean alphaNumeric, boolean keepCase) {
            if (alphaNumeric) {
                main = main.replaceAll(RegularEx.NON_ALPHANUMERIC, "");
                substring = substring.replaceAll(RegularEx.NON_ALPHANUMERIC, "");
            }
            if (!keepCase) {
                main = main.toLowerCase();
                substring = substring.toLowerCase();
            }
            return main.contains(substring);
        }*/

        /*public static boolean compareString(ArrayList<String> main, String substring, boolean alphaNumeric, boolean keepCase) {
            for (int i = 0; i < main.size(); i++) {
                if (compareString(main.get(i), substring, alphaNumeric, keepCase)) {
                    return true;
                }
            }
            return false;
        }*/

        public static int count(String full, String occurrenceOf) {
            return full.length() - full.replace(occurrenceOf, "").length();
        }

        public static String ellipsize(String text, int maxLength) {
            if (text == null || text.isEmpty() || text.length() < maxLength) {
                return text;
            }
            return text.substring(0, maxLength - 5) + " ...";
        }

        public static String replaceAt(String text, String sub, int indexOf) {
            return text.substring(0, indexOf) + sub + text.substring(indexOf + 2);
        }

        public static ArrayList<String> getSentences(String text, String separator) {
            if (text == null || text.isEmpty()) {
                return null;
            }
            ArrayList<String> output = new ArrayList<>();
            if (separator == null || separator.isEmpty() || !text.contains(separator)) {
                output.add(text);
                return output;
            }
            boolean containsSeparator = text.contains(separator);
            while (containsSeparator) {
                String temp = text.substring(0, text.indexOf(separator));
                text = text.substring(text.indexOf(separator) + separator.length());
                output.add(temp);
                containsSeparator = text.contains(separator);
                if (!containsSeparator && !text.isEmpty()) {
                    output.add(text);
                }
            }
            return output;
        }

        public static int findWord(String str, String findStr) {
            int lastIndex = 0;
            int count = 0;
            while (lastIndex != -1) {
                lastIndex = str.indexOf(findStr, lastIndex);
                if (lastIndex != -1) {
                    count++;
                    lastIndex += findStr.length();
                }
            }
            return count;
        }

        public static String leftPad(int code, int padding, String whatToAdd) {
            String output = String.valueOf(code);
            if (output.isEmpty()) {
                return output;
            }
            return String.format(Locale.US, "%" + whatToAdd + padding + "d", new Object[]{Integer.valueOf(code)});
        }

        public static String getLink(String link, String name) {
            return "<a href=\"" + link + "\">" + name + "</a>";
        }

        public static boolean contains(String text, String... sub_texts) {
            if (sub_texts != null) {
                for (String sub_text : sub_texts) {
                    if (text.contains(sub_text)) {
                        return true;
                    }
                }
            }
            return false;
        }

        public static String extractOTP(String text) {
            Iterator<String> it = getWords(text).iterator();
            while (it.hasNext()) {
                String word = it.next();
                if (NumUtils.isNumeric(word)) {
                    return word;
                }
            }
            return "";
        }

        public static boolean isSimilar(String text1, String text2, boolean ignoreWhitespace) {
            if (text1 == null || text2 == null) {
                return false;
            }
            String text12 = text1.toUpperCase();
            String text22 = text2.toUpperCase();
            if (ignoreWhitespace) {
                text12 = text12.replace(" ", "");
                text22 = text22.replace(" ", "");
            }
            if (text12.contains(text22) || text22.contains(text12)) {
                return true;
            }
            return false;
        }
    }

    public static class NumUtils {
        public static int getRandomInt(int min, int max) {
            return new Random().nextInt((max - min) + 1) + min;
        }

        public static int min(int... args) {
            if (args == null) {
                return 0;
            }
            if (args.length <= 1) {
                return args[0];
            }
            int min = args[0];
            for (int arg : args) {
                min = Math.min(min, arg);
            }
            return min;
        }

        public static double min(double... args) {
            if (args == null) {
                return 0.0d;
            }
            if (args.length <= 1) {
                return args[0];
            }
            double min = args[0];
            for (double arg : args) {
                min = Math.min(min, arg);
            }
            return min;
        }

        public static int max(int... args) {
            if (args == null) {
                return 0;
            }
            if (args.length <= 1) {
                return args[0];
            }
            int max = 0;
            for (int arg : args) {
                max = Math.max(max, arg);
            }
            return max;
        }

        public static double max(double... args) {
            if (args == null) {
                return 0.0d;
            }
            if (args.length <= 1) {
                return args[0];
            }
            double max = 0.0d;
            for (double arg : args) {
                max = Math.max(max, arg);
            }
            return max;
        }

        public static int clip(int input, int min, int max) {
            if (input < min) {
                return min;
            }
            if (input > max) {
                return max;
            }
            return input;
        }

        public static double truncate(double number, int decimal_places) {
            int cast = (int) Math.pow(10.0d, (double) decimal_places);
            return ((double) ((int) (number * ((double) cast)))) / ((double) cast);
        }

        public static double round(double number, int decimal_places) {
            return ((double) Math.round(Math.pow(10.0d, (double) decimal_places) * number)) / Math.pow(10.0d, (double) decimal_places);
        }

        public static float round(float number, int decimal_places) {
            return (float) (((double) Math.round(((double) number) * Math.pow(10.0d, (double) decimal_places))) / Math.pow(10.0d, (double) decimal_places));
        }

        public static String getActualValue(Double value) {
            return new DecimalFormat("0.##").format(value);
        }

        public static String getActualValue(String value) {
            try {
                return new DecimalFormat("0.##").format(Double.parseDouble(value));
            } catch (Exception e) {
                e.printStackTrace();
                return "";
            }
        }

        public static Double opPercentage(Double mrp, String operator, Double deal) {
            double output = mrp.doubleValue();
            if (operator.equalsIgnoreCase("-")) {
                output = (mrp.doubleValue() * (100.0d - deal.doubleValue())) / 100.0d;
            }
            return Double.valueOf(output);
        }

        public static float truncate(float number, int decimal_places) {
            int cast = (int) Math.pow(10.0d, (double) decimal_places);
            return ((float) ((int) (number * ((float) cast)))) / ((float) cast);
        }

        public static String extractFrom(String message) {
            return message != null ? message.replaceAll("[^0-9]", "") : "";
        }

        public static boolean isNumeric(String text) {
            try {
                Double.valueOf(Double.parseDouble(text));
                return true;
            } catch (NumberFormatException e) {
                return false;
            }
        }
    }

    public static class Validations {
        public static boolean isPhoneNumber(String number) {
            boolean output = true;
            if (!isNumber(number, new String[]{"+91"})) {
                return false;
            }
            String temp = CommonFunctions.clipString(number, new String[]{"+91"});
            if (temp.length() < 10) {
                output = false;
            }
            if (Long.valueOf(temp).longValue() <= 7000000000L) {
                return false;
            }
            return output;
        }

        public static boolean isAlphabetic(String name, String[] exclude) {
            if (name == null || name.isEmpty()) {
                return false;
            }
            if (exclude != null) {
                for (String c : exclude) {
                    name = name.replace(c, "");
                }
            }
            for (int i = 0; i < name.length(); i++) {
                if (!Character.isAlphabetic(name.charAt(i))) {
                    return false;
                }
            }
            return true;
        }

        public static boolean isNumber(String value, String[] exclude) {
            String toCheck = value;
            if (value == null || value.isEmpty()) {
                return false;
            }
            if (exclude != null) {
                for (String c : exclude) {
                    toCheck = toCheck.replace(c, "");
                }
            }
            if (!CommonFunctions.isBlank(toCheck)) {
                return toCheck.matches("^[0-9]+$");
            }
            return false;
        }

        public static boolean isPasswordField(int input_type) {
            return CommonFunctions.decode(Integer.valueOf(input_type), 18, 129, 145, 225) != null;
        }

        public static boolean isProperPassword(String password) {
            if (password == null || password.isEmpty()) {
                return false;
            }
            boolean upper = false;
            boolean lower = false;
            boolean symbol = false;
            for (char valueOf : password.toCharArray()) {
                Character letter = Character.valueOf(valueOf);
                if (Character.isUpperCase(letter.charValue())) {
                    upper = true;
                }
                if (Character.isLowerCase(letter.charValue())) {
                    lower = true;
                }
                if (!Character.isDigit(letter.charValue()) && !Character.isLetter(letter.charValue())) {
                    symbol = true;
                }
            }
            if (!upper || !lower || !symbol) {
                return false;
            }
            return true;
        }

        public static boolean isEmail(String email) {
            return false;
        }

        public static boolean isNonEmpty(String... texts) {
            if (texts == null || texts.length <= 0) {
                return false;
            }
            for (String text : texts) {
                if (text == null || text.isEmpty()) {
                    return false;
                }
            }
            return true;
        }
    }

    public static class GeneralUtils {
        public static Uri getUriForFIle(Context context, File file) {
            return null;
        }

        public static boolean isMarshmallow() {
            return Build.VERSION.SDK_INT >= 23;
        }

        static boolean isNougat() {
            return Build.VERSION.SDK_INT >= 24;
        }

        public static boolean isOreo() {
            return Build.VERSION.SDK_INT >= 26;
        }

        public static void sendToClipboard(Context context, String label, String text) {
            ((ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE)).setPrimaryClip(ClipData.newPlainText(label, text));
        }

        public static void vibrate(Context context, long time) {
            if (context != null) {
                Vibrator vibrator = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
            }
        }

        public static String generateCode(String code) {
            String output = "";
            Iterator<String> it = StringUtils.getWords(code).iterator();
            while (it.hasNext()) {
                output = output + it.next().substring(0, 1);
            }
            return output;
        }

        public static String generateCode(String... codes) {
            return new StringBuilder("").toString();
        }

        public static String[] decipherCode(String code) {
            return null;
        }
    }
}
