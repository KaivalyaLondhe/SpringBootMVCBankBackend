package com.aurionpro.bank.util;

import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

import com.aurionpro.bank.entity.Transaction;
import com.opencsv.CSVWriter;

public class CsvUtil {

    public static void writeTransactionsToCsv(List<Transaction> transactions, String filePath) {
        try (CSVWriter writer = new CSVWriter(new FileWriter(filePath))) {
            // Header
            String[] header = { "Date", "Amount", "Transaction Type" };
            writer.writeNext(header);

            // Data
            for (Transaction transaction : transactions) {
                String[] data = {
                    transaction.getDate().toString(),
                    transaction.getAmount().toString(),
                    transaction.getType().toString()
                };
                writer.writeNext(data);
            }
        } catch (IOException e) {
            throw new RuntimeException("Failed to write CSV", e);
        }
    }
}
