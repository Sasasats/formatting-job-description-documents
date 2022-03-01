package utils;

import utils.enums.RegexPatterns;
import utils.enums.Separators;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TxtUtils {
    /**
     * Форматирует и сохраняет текст из импортируемого документа в экспортируемый.
     *
     * @param importFileName Файл в котором нужно отформатировать текст
     * @param exportFileName Файл в который нужно записать отформатированный текст
     */
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

                if (textLine.trim().length() != 0) {
                    //Убрать некорректные символы
                    if (textLine.indexOf('\u001F') != -1) {
                        textLine = textLine.replaceAll(
                                "\u001F", // Unit Separator. Escaped Unicode ()
                                Separators.NOTHING.getValue());
                    }
                    text.add(textLine);
                }
            }

            List<String> formattedText = new ArrayList<>();
            for (int i = 0; i < text.size() - 1; i++) {
                formattedTextLine = formatText(text.get(i), text.get(i + 1));
                formattedText.add(formattedTextLine);
            }

            List<String> neededFormattedText = getNeededText(formattedText, "1.", "5.");
            for (String line : neededFormattedText) {
                fileWriter.write(line + "\n");
            }

            fileReader.close();
            fileWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Выделяет из всего текста документа нужную часть с помощью нахождения нужной последовательности символов.
     *
     * @param formattedText Текстовый список.
     * @return Обрезанный текстовый список.
     */
    private static List<String> getNeededText(List<String> formattedText, String startRegex, String endRegex) {
        //Получение начального индекса для отбора списка текста должностных инструкций
        int startIndex = 0;
        for (; startIndex < formattedText.size() - 1; startIndex++) {
            if (formattedText.get(startIndex).contains(startRegex)) {
                break;
            }
        }

        //Получение конечного индекса для отбора списка текста должностных инструкций
        int endIndex = formattedText.size() - 1;
        for (; endIndex > 0; endIndex--) {
            if (formattedText.get(endIndex).contains(endRegex)) {
                endIndex++;
                break;
            }
        }

        return formattedText.subList(startIndex, endIndex);
    }

    /**
     * Форматирование текста.
     *
     * @param textLine     Текстовая строка.
     * @param nextTextLine Следующая текстовая строка.
     * @return Форматированная текстовая строка.
     */
    private static String formatText(String textLine, String nextTextLine) {
        //Удаление пробелов в начале и конце строки
        textLine = textLine.trim();
        //Замена знаков табуляции на пробелы
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

        textLine = formatNumbers(textLine);
        textLine = setPunctuationMarks(firstLetter1, firstLetter2, textLine, nextTextLine);

        return textLine;
    }


    /**
     * Находит первую букву в строке.
     *
     * @param textLine Текстовая строка
     * @return Первая буква, если не найдена возвращает 0 (ничего)
     */
    public static char getFirstLetter(String textLine) {
        char firstLetter = 0;
        for (char character : textLine.toCharArray()) {
            if (isLetter(character)) {
                firstLetter = character;
                break;
            }
        }
        return firstLetter;
    }

    /**
     * Находит индекс первой буквы в строке.
     *
     * @param textLine Текстовая строка
     * @return Индекс первой буквы, если не найден возвращает 0
     */
    public static int getFirstLetterIndex(String textLine) {
        int counter = 0;
        for (char character : textLine.toCharArray()) {
            if (isLetter(character)) {
                break;
            }
            counter++;
        }
        return counter;
    }

    /**
     * Находит первую цифру в строке.
     *
     * @param textLine Текстовая строка
     * @return Первая цифра, если не найдена возвращает 0
     */
    public static char getFirstNumber(String textLine) {
        char firstNumber = 0;
        for (char character : textLine.toCharArray()) {
            if (isLetter(character)) {
                firstNumber = character;
                break;
            }
        }
        return firstNumber;
    }

    /**
     * Находит индекс первой цифры в строке.
     *
     * @param textLine Текстовая строка
     * @return Индекс первой цифры, если не найден возвращает 0
     */
    public static int getFirstNumberIndex(String textLine) {
        int counter = 0;
        for (char character : textLine.toCharArray()) {
            if (Character.isDigit(character)) {
                break;
            }
            counter++;
        }
        return counter;
    }

    /**
     * Находит последнее число.
     *
     * @param textLine Текстовая строка
     * @return Первое число, если не найдено вернет "" (ничего)
     */
    public static int getLastNumber(String textLine) {
        String number = "";
        for (int i = getLastNumberIndex(textLine) - 1; i >= 0; i--) {
            char ch = textLine.charAt(i);
            if (Character.isDigit(ch)) {
                number += ch;
            } else {
                break;
            }
        }
        return Integer.parseInt(new StringBuilder(number).reverse().toString());
    }

    /**
     * Находит индекс последнего числа в строке.
     *
     * @param textLine Текстовая строка
     * @return Индекс последнего числа, если не найден возвращает 0
     */
    public static int getLastNumberIndex(String textLine) {
        textLine = new StringBuilder(textLine).reverse().toString();
        int counter = 0;
        for (char character : textLine.toCharArray()) {
            if (Character.isDigit(character)) {
                break;
            }
            counter++;
        }
        return textLine.length() - counter;
    }

    /**
     * Убирает символ по заданной позиции в строке.
     *
     * @param textLine Текстовая строка.
     * @param position Позиция удаления.
     * @return Обновленная текстовая строка.
     */
    private static String removeCharAt(String textLine, int position) {
        return textLine.substring(0, position) + textLine.substring(position + 1);
    }

    /**
     * Вставляет или заменяет знаки препинания в конце строки.
     *
     * @param firstLetterTextLine     Первая буква текущей строки.
     * @param firstLetterNextTextLine Первая буква следующей строки.
     * @param textLine                Текущая строка.
     * @param nextTextLine            Следующая строка.
     * @return Обновленная текстовая строка.
     */
    private static String setPunctuationMarks(char firstLetterTextLine, char firstLetterNextTextLine, String textLine,
                                              String nextTextLine) {
        //Расстановка ";" в подсписках
        if (isLowercaseLetter(firstLetterTextLine) && isLowercaseLetter(firstLetterNextTextLine)) {
            if (isTextLineEndsWithPunctuationMark(textLine)) {
                replaceLastCharacter(textLine, Separators.SEMICOLON.getValue());
            } else {
                textLine = textLine + Separators.SEMICOLON.getValue();
            }
        }

        //Расстановка "." в подсписках
        if (isLowercaseLetter(firstLetterTextLine) && isUppercaseLetter(firstLetterNextTextLine)) {
            if (isTextLineEndsWithPunctuationMark(textLine)) {
                replaceLastCharacter(textLine, Separators.DOT.getValue());
            } else {
                textLine = textLine + Separators.DOT.getValue();
            }
        }

        //Расстановка "." в конце строк
        if (isUppercaseLetter(firstLetterTextLine) && isUppercaseLetter(firstLetterNextTextLine)) {
            if (isTextLineEndsWithPunctuationMark(textLine)) {
                replaceLastCharacter(textLine, Separators.DOT.getValue());
            } else {
                textLine = textLine + Separators.DOT.getValue();
            }
        }
        if (isLetter(firstLetterTextLine) && nextTextLine.isEmpty()) {
            if (isTextLineEndsWithPunctuationMark(textLine)) {
                replaceLastCharacter(textLine, Separators.DOT.getValue());
            } else {
                textLine = textLine + Separators.DOT.getValue();
            }
        }

        //Расстановка ":" перед подсписками
        if (isUppercaseLetter(firstLetterTextLine) && isLowercaseLetter(firstLetterNextTextLine)) {
            if (isTextLineEndsWithPunctuationMark(textLine)) {
                textLine = replaceLastCharacter(textLine, Separators.COLON.getValue());
            } else {
                textLine = textLine + Separators.COLON.getValue();
            }
        }
        return textLine;
    }

    /**
     * Проверяет, заканчивается ли строка на знак препинания.
     *
     * @param textLine Текстовая строка
     * @return Булевый результат
     */
    public static boolean isTextLineEndsWithPunctuationMark(String textLine) {
        return textLine.endsWith(Separators.DOT.getValue())
                || textLine.endsWith(Separators.COMMA.getValue())
                || textLine.endsWith(Separators.COLON.getValue())
                || textLine.endsWith(Separators.SEMICOLON.getValue());
    }

    /**
     * Заменить последний символ в строке.
     *
     * @param textLine     Текстовая строка.
     * @param newCharacter Новый символ на который происходит замена.
     * @return Форматированная строка.
     */
    public static String replaceLastCharacter(String textLine, String newCharacter) {
        return textLine.substring(0, textLine.length() - 1) + newCharacter;
    }

    /**
     * Проверка на то, что символ является буквой.
     *
     * @param letter Символ.
     * @return Булево значение в зависимости от результата.
     */
    public static boolean isLetter(char letter) {
        return isUppercaseLetter(letter) || isLowercaseLetter(letter);
    }

    /**
     * Проверка на то, что символ является буквой верхнего регистра.
     *
     * @param letter Символ.
     * @return Булево значение в зависимости от результата.
     */
    public static boolean isUppercaseLetter(char letter) {
        return Character.isUpperCase(letter);
    }

    /**
     * Проверка на то, что символ является буквой нижнего регистра.
     *
     * @param letter Символ.
     * @return Булево значение в зависимости от результата.
     */
    public static boolean isLowercaseLetter(char letter) {
        return Character.isLowerCase(letter);
    }

    /**
     * Форматирование нумерации. Расставление точек, уборка пробелов и прочее.
     *
     * @param textLine Текстовая строка.
     * @return Форматированная строка.
     */
    private static String formatNumbers(String textLine) {
        //Форматирование нумерации
        if (textLine.length() > 2) {
            StringBuilder stringBuffer = new StringBuilder(textLine);
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
            }

            if (matcher1.matches()) {
                stringBuffer.insert(substring.length() - 2, Separators.DOT.getValue());
                textLine = stringBuffer.toString();
                textLine = removeLastPunctuationMark(textLine);
            }

            if (matcher2.matches()) {
                stringBuffer.insert(substring.length() - 1, Separators.SPACE.getValue());
                textLine = stringBuffer.toString();
                textLine = removeLastPunctuationMark(textLine);
            }

            if (matcher3.matches()) {
                stringBuffer.insert(substring.length() - 2,
                        Separators.DOT.getValue() + Separators.SPACE.getValue());
                textLine = stringBuffer.toString();
                textLine = removeLastPunctuationMark(textLine);
            }
        }

        if (textLine.length() > 4) {
            StringBuilder stringBuffer = new StringBuilder(textLine);
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
                stringBuffer.insert(substring.length() - 2,
                        Separators.DOT.getValue() + Separators.SPACE.getValue());
                textLine = stringBuffer.toString();
            }
        }

        if (textLine.length() > 5) {
            StringBuilder stringBuffer = new StringBuilder(textLine);
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
                stringBuffer.insert(substring.length() - 2,
                        Separators.DOT.getValue() + Separators.SPACE.getValue());
                textLine = stringBuffer.toString();
            }
        }

        if (textLine.length() > 6) {
            StringBuilder stringBuffer = new StringBuilder(textLine);
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
                stringBuffer.insert(substring.length() - 2,
                        Separators.DOT.getValue() + Separators.SPACE.getValue());
                textLine = stringBuffer.toString();
            }
        }

        if (textLine.length() > 6) {
            StringBuilder stringBuffer = new StringBuilder(textLine);
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
                stringBuffer.insert(substring.length() - 2,
                        Separators.DOT.getValue() + Separators.SPACE.getValue());
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
                stringBuffer.insert(substring.length() - 2,
                        Separators.DOT.getValue() + Separators.SPACE.getValue());
                textLine = stringBuffer.toString();
            }
        }

        if (textLine.length() > 8) {
            StringBuilder stringBuffer = new StringBuilder(textLine);
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
                stringBuffer.insert(substring.length() - 2,
                        Separators.DOT.getValue() + Separators.SPACE.getValue());
                textLine = stringBuffer.toString();
            }
        }
        return textLine;
    }

    /**
     * Удаление знака препинания на конце строки.
     *
     * @param textLine Текстовая строка
     * @return Текстовая строка без знака препинания на конце.
     */
    private static String removeLastPunctuationMark(String textLine) {
        if (isTextLineEndsWithPunctuationMark(textLine)) {
            textLine = textLine.substring(0, textLine.length() - 1);
        }
        return textLine;
    }

    /**
     * Получает массив строк из содержимого указанного файла.
     *
     * @param fileName Имя файла (опционально путь)
     * @return Массив строк, если строк нет возвращает null
     */
    public static List<String> getTextFromFileTxt(String fileName) {
        try {
            Scanner scanner = new Scanner(new File(fileName));
            List<String> text = new ArrayList<>();

            while (scanner.hasNextLine()) {
                text.add(scanner.nextLine());
            }
            return text;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return null;
        }
    }
}
