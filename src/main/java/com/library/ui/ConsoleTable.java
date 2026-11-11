package com.library.ui;

import java.util.List;

public class ConsoleTable {

    public static void print(List<String> headers, List<List<String>> rows) {
        int columns = headers.size();
        int[] widths = new int[columns];

        // Calculate column widths
        for (int i = 0; i < columns; i++) {
            widths[i] = headers.get(i).length();

            for (List<String> row : rows) {
                widths[i] = Math.max(widths[i], row.get(i).length());
            }
        }

        System.out.println();
        printSeparator(widths);
        printRow(headers, widths);
        printSeparator(widths);

        for (List<String> row : rows) {
            printRow(row, widths);
        }

        printSeparator(widths);
    }

    private static void printRow(List<String> values, int[] widths) {
        System.out.print("|");

        for (int i = 0; i < widths.length; i++) {
            System.out.printf(" %-" + widths[i] + "s |", values.get(i));

        }

        System.out.println();
    }

    private static void printSeparator(int[] widths) {
        for (int width : widths) {
            System.out.print("+" + "-".repeat(width + 2));
        }

        System.out.println("+");
    }
}
