package utils;

import utils.enums.RegexPatterns;
import utils.enums.Separators;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TXTUtils {
    public static void readFile(String importFileName, String exportFileName) {
        try {
            FileWriter fileWriter = new FileWriter(exportFileName);
            FileReader fileReader = new FileReader(importFileName);

            Scanner scanner = new Scanner(fileReader);
            String textLine;
            String formattedTextLine;

            List<String> text = new ArrayList<>();
            while (scanner.hasNextLine()) {
                textLine = scanner.nextLine();
                text.add(textLine);
            }

            List<String> formattedText = new ArrayList<>();
            for (int i = 0; i < text.size() - 1; i++) {
                formattedTextLine = formatText(text.get(i), text.get(i + 1));
                formattedText.add(formattedTextLine);
            }

            List<String> neededFormattedText = getNeededText(formattedText);
            for(String line : neededFormattedText){
                fileWriter.write(line + "\n");
            }

            fileReader.close();
            fileWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static List<String> getNeededText(List<String> formattedText) {
        //Получение начального индекса для отбора списка текста должностных инструкций
        int startIndex = 0;
        for(; startIndex < formattedText.size() - 1; startIndex++){
            if(formattedText.get(startIndex).contains("1. ")){
                break;
            }
        }

        //Получение конечного индекса для отбора списка текста должностных инструкций
        int endIndex = formattedText.size() - 1;
        for(; endIndex > 0; endIndex--){
            if(formattedText.get(endIndex).contains("5.")){
                endIndex++;
                break;
            }
        }

        return formattedText.subList(startIndex, endIndex);
    }

    private static String formatText(String textLine, String nextTextLine) {
        //Удаление пробелов в начале и конце строки
        textLine = textLine.trim();
        textLine = textLine.replaceAll("\t", " ");

        //Удаление двойных пробелов
        for (int i = 0; i < textLine.length() - 1; i++) {
            if (textLine.charAt(i) == ' ' && textLine.charAt(i + 1) == ' ') {
                textLine = removeCharAt(textLine, i);
                i--;
            }
        }

        //Нахождение первой буквы в строке
        char firstLetter1 = getFirstLetter(textLine);
        char firstLetter2 = getFirstLetter(nextTextLine);

        textLine = setPunctuationMarks(firstLetter1, firstLetter2, textLine, nextTextLine);
        textLine = formatNumbers(textLine, nextTextLine);

        return textLine;
    }

    private static char getFirstLetter(String textLine) {
        char firstLetter = 0;
        for (char character : textLine.toCharArray()) {
            if ((character >= 'а' && character <= 'я') || (character >= 'А' && character <= 'Я')) {
                firstLetter = character;
                break;
            }
        }
        return firstLetter;
    }

    private static String removeCharAt(String s, int pos) {
        return s.substring(0, pos) + s.substring(pos + 1);
    }

    private static String setPunctuationMarks(char firstLetter1, char firstLetter2, String textLine, String nextTextLine) {
        //Расстановка ";" в подсписках
        if ((firstLetter1 >= 'а' && firstLetter1 <= 'я') && (firstLetter2 >= 'a' && firstLetter2 <= 'я') &&
                (textLine.endsWith(".") || textLine.endsWith(":") || textLine.endsWith(","))) {
            textLine = textLine.substring(0, textLine.length() - 1) + ";";
        }
        if ((firstLetter1 >= 'а' && firstLetter1 <= 'я') &&
                (!textLine.endsWith(";") && !textLine.endsWith(".") && !textLine.endsWith(","))) {
            textLine = textLine + ";";
        }

        //Расстановка "." в подсписках
        if ((firstLetter1 >= 'а' && firstLetter1 <= 'я') && (firstLetter2 >= 'А' && firstLetter2 <= 'Я') &&
                (textLine.endsWith(",") || textLine.endsWith(":") || (textLine.endsWith(".")
                        || textLine.endsWith(";")))) {
            textLine = textLine.substring(0, textLine.length() - 1) + ".";
        }

        //Расстановка "." в конце строк
        if ((firstLetter1 >= 'А' && firstLetter1 <= 'Я') && (firstLetter2 >= 'А' && firstLetter2 <= 'Я') &&
                (textLine.endsWith(",") || textLine.endsWith(":"))) {
            textLine = textLine.substring(0, textLine.length() - 1) + ".";
        }
        if ((firstLetter1 >= 'А' && firstLetter1 <= 'я') &&
                (!textLine.endsWith(",") && !textLine.endsWith(":") && !textLine.endsWith(";") && !textLine.endsWith(".")) &&
                nextTextLine.isEmpty()) {
            textLine = textLine + ".";
        }
        if ((firstLetter1 >= 'А' && firstLetter1 <= 'я') &&
                (textLine.endsWith(",") || textLine.endsWith(":") || textLine.endsWith(";") || textLine.endsWith(".")) &&
                nextTextLine.isEmpty()) {
            textLine = textLine.substring(0, textLine.length() - 1) + ".";
        }

        //Расстановка ":" перед подсписками
        if ((firstLetter1 >= 'А' && firstLetter1 <= 'Я') && (firstLetter2 >= 'а' && firstLetter2 <= 'я') &&
                (textLine.endsWith(",") || textLine.endsWith(".") || textLine.endsWith(";"))) {
            textLine = textLine.substring(0, textLine.length() - 1) + ":";
        }

        return textLine;
    }


    private static String formatNumbers(String textLine, String nextTextLine) {
        //Форматирование нумерации
        if (textLine.length() > 2) {
            StringBuffer stringBuffer = new StringBuffer(textLine);
            String substring = textLine.substring(0, 3);

            Pattern pattern = Pattern.compile(RegexPatterns.CORRECT_LIST_LVL_1.getValue());
            Matcher matcher = pattern.matcher(substring);

            Pattern pattern1 = Pattern.compile(RegexPatterns.LIST_LVL_11.getValue());
            Matcher matcher1 = pattern1.matcher(substring);

            Pattern pattern2 = Pattern.compile(RegexPatterns.LIST_LVL_12.getValue());
            Matcher matcher2 = pattern2.matcher(substring);

            Pattern pattern3 = Pattern.compile(RegexPatterns.LIST_LVL_13.getValue());
            Matcher matcher3 = pattern3.matcher(substring);

            if (matcher.matches()) {
                textLine = stringBuffer.toString();
                textLine = removeLastPunctuationMark(textLine);
                if (!nextTextLine.isEmpty()) {
                    textLine = textLine + "\n";
                }
            }

            if (matcher1.matches()) {
                stringBuffer.insert(substring.length() - 2, Separators.DOT.getValue());
                textLine = stringBuffer.toString();
                textLine = removeLastPunctuationMark(textLine);
                if (!nextTextLine.isEmpty()) {
                    textLine = textLine + "\n";
                }
            }

            if (matcher2.matches()) {
                stringBuffer.insert(substring.length() - 1, Separators.SPACE.getValue());
                textLine = stringBuffer.toString();
                textLine = removeLastPunctuationMark(textLine);
                if (!nextTextLine.isEmpty()) {
                    textLine = textLine + "\n";
                }
            }

            if (matcher3.matches()) {
                stringBuffer.insert(substring.length() - 2, Separators.DOT_AND_SPACE.getValue());
                textLine = stringBuffer.toString();
                textLine = removeLastPunctuationMark(textLine);
                if (!nextTextLine.isEmpty()) {
                    textLine = textLine + "\n";
                }
            }
        }

        if (textLine.length() > 4) {
            StringBuffer stringBuffer = new StringBuffer(textLine);
            String substring = textLine.substring(0, 5);

            Pattern pattern1 = Pattern.compile(RegexPatterns.LIST_LVL_21.getValue());
            Matcher matcher1 = pattern1.matcher(substring);

            Pattern pattern2 = Pattern.compile(RegexPatterns.LIST_LVL_22.getValue());
            Matcher matcher2 = pattern2.matcher(substring);

            Pattern pattern3 = Pattern.compile(RegexPatterns.LIST_LVL_23.getValue());
            Matcher matcher3 = pattern3.matcher(substring);

            if (matcher1.matches()) {
                stringBuffer.insert(substring.length() - 2, Separators.DOT.getValue());
                textLine = stringBuffer.toString();
            }

            if (matcher2.matches()) {
                stringBuffer.insert(substring.length() - 1, Separators.SPACE.getValue());
                textLine = stringBuffer.toString();
            }

            if (matcher3.matches()) {
                stringBuffer.insert(substring.length() - 2, Separators.DOT_AND_SPACE.getValue());
                textLine = stringBuffer.toString();
            }
        }

        if (textLine.length() > 5) {
            StringBuffer stringBuffer = new StringBuffer(textLine);
            String substring = textLine.substring(0, 6);

            Pattern pattern1 = Pattern.compile(RegexPatterns.LIST_LVL_211.getValue());
            Matcher matcher1 = pattern1.matcher(substring);

            Pattern pattern2 = Pattern.compile(RegexPatterns.LIST_LVL_221.getValue());
            Matcher matcher2 = pattern2.matcher(substring);

            Pattern pattern3 = Pattern.compile(RegexPatterns.LIST_LVL_231.getValue());
            Matcher matcher3 = pattern3.matcher(substring);

            if (matcher1.matches()) {
                stringBuffer.insert(substring.length() - 2, Separators.DOT.getValue());
                textLine = stringBuffer.toString();
            }

            if (matcher2.matches()) {
                stringBuffer.insert(substring.length() - 1, Separators.SPACE.getValue());
                textLine = stringBuffer.toString();
            }

            if (matcher3.matches()) {
                stringBuffer.insert(substring.length() - 2, Separators.DOT_AND_SPACE.getValue());
                textLine = stringBuffer.toString();
            }
        }

        if (textLine.length() > 6) {
            StringBuffer stringBuffer = new StringBuffer(textLine);
            String substring = textLine.substring(0, 7);

            Pattern pattern1 = Pattern.compile(RegexPatterns.LIST_LVL_31.getValue());
            Matcher matcher1 = pattern1.matcher(substring);

            Pattern pattern2 = Pattern.compile(RegexPatterns.LIST_LVL_32.getValue());
            Matcher matcher2 = pattern2.matcher(substring);

            Pattern pattern3 = Pattern.compile(RegexPatterns.LIST_LVL_33.getValue());
            Matcher matcher3 = pattern3.matcher(substring);

            if (matcher1.matches()) {
                stringBuffer.insert(substring.length() - 2, Separators.DOT.getValue());
                textLine = stringBuffer.toString();
            }

            if (matcher2.matches()) {
                stringBuffer.insert(substring.length() - 1, Separators.SPACE.getValue());
                textLine = stringBuffer.toString();
            }

            if (matcher3.matches()) {
                stringBuffer.insert(substring.length() - 2, Separators.DOT_AND_SPACE.getValue());
                textLine = stringBuffer.toString();
            }
        }

        if (textLine.length() > 6) {
            StringBuffer stringBuffer = new StringBuffer(textLine);
            String substring = textLine.substring(0, 8);

            Pattern pattern1 = Pattern.compile(RegexPatterns.LIST_LVL_311.getValue());
            Matcher matcher1 = pattern1.matcher(substring);

            Pattern pattern2 = Pattern.compile(RegexPatterns.LIST_LVL_321.getValue());
            Matcher matcher2 = pattern2.matcher(substring);

            Pattern pattern3 = Pattern.compile(RegexPatterns.LIST_LVL_331.getValue());
            Matcher matcher3 = pattern3.matcher(substring);

            Pattern pattern4 = Pattern.compile(RegexPatterns.LIST_LVL_312.getValue());
            Matcher matcher4 = pattern4.matcher(substring);

            Pattern pattern5 = Pattern.compile(RegexPatterns.LIST_LVL_322.getValue());
            Matcher matcher5 = pattern5.matcher(substring);

            Pattern pattern6 = Pattern.compile(RegexPatterns.LIST_LVL_332.getValue());
            Matcher matcher6 = pattern6.matcher(substring);

            if (matcher1.matches()) {
                stringBuffer.insert(substring.length() - 2, Separators.DOT.getValue());
                textLine = stringBuffer.toString();
            }

            if (matcher2.matches()) {
                stringBuffer.insert(substring.length() - 1, Separators.SPACE.getValue());
                textLine = stringBuffer.toString();
            }

            if (matcher3.matches()) {
                stringBuffer.insert(substring.length() - 2, Separators.DOT_AND_SPACE.getValue());
                textLine = stringBuffer.toString();
            }

            if (matcher4.matches()) {
                stringBuffer.insert(substring.length() - 2, Separators.DOT.getValue());
                textLine = stringBuffer.toString();
            }

            if (matcher5.matches()) {
                stringBuffer.insert(substring.length() - 1, Separators.SPACE.getValue());
                textLine = stringBuffer.toString();
            }

            if (matcher6.matches()) {
                stringBuffer.insert(substring.length() - 2, Separators.DOT_AND_SPACE.getValue());
                textLine = stringBuffer.toString();
            }
        }

        if (textLine.length() > 8) {
            StringBuffer stringBuffer = new StringBuffer(textLine);
            String substring = textLine.substring(0, 9);

            Pattern pattern1 = Pattern.compile(RegexPatterns.LIST_LVL_313.getValue());
            Matcher matcher1 = pattern1.matcher(substring);

            Pattern pattern2 = Pattern.compile(RegexPatterns.LIST_LVL_323.getValue());
            Matcher matcher2 = pattern2.matcher(substring);

            Pattern pattern3 = Pattern.compile(RegexPatterns.LIST_LVL_333.getValue());
            Matcher matcher3 = pattern3.matcher(substring);

            if (matcher1.matches()) {
                stringBuffer.insert(substring.length() - 2, Separators.DOT.getValue());
                textLine = stringBuffer.toString();
            }

            if (matcher2.matches()) {
                stringBuffer.insert(substring.length() - 1, Separators.SPACE.getValue());
                textLine = stringBuffer.toString();
            }

            if (matcher3.matches()) {
                stringBuffer.insert(substring.length() - 2, Separators.DOT_AND_SPACE.getValue());
                textLine = stringBuffer.toString();
            }
        }
        return textLine;
    }

    private static String removeLastPunctuationMark(String textLine) {
        if (textLine.endsWith(",") || textLine.endsWith(".") || textLine.endsWith(";") || textLine.endsWith(":")) {
            textLine = textLine.substring(0, textLine.length() - 1);
        }
        return textLine;
    }
}
