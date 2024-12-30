package com.dkowalczyk;

import org.jetbrains.annotations.NotNull;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.zip.GZIPInputStream;
import java.util.zip.InflaterInputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class HttpResponseExtractor {

    // Helper method to decompress input streams
    private static InputStream getDecompressedInputStream(HttpURLConnection connection) throws Exception {
        String encoding = connection.getHeaderField("Content-Encoding");

        InputStream inputStream = connection.getInputStream();

        if ("gzip".equalsIgnoreCase(encoding)) {
            return new GZIPInputStream(inputStream);
        } else if ("deflate".equalsIgnoreCase(encoding)) {
            return new InflaterInputStream(inputStream);
        }

        return inputStream; // No compression, return the original input stream
    }

    public static String getResponseAsString(String urlString) throws Exception {
        URL url = new URL(urlString);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");

        connection.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64)");
        connection.setRequestProperty("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8");
        connection.setRequestProperty("Accept-Encoding", "gzip, deflate, br");
        connection.setRequestProperty("Cookie", "session=53616c7465645f5f161519bc75b349dcc4edcc26cdb3c9a6114a68b8e23f374737e2df18f71f04ecefccd5ad0274fd77b9bd86da32cb795240f97ea40222c4c7");

        // Check if the response code is 200 (OK)
        if (connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
            throw new Exception("Failed to fetch the data: " + connection.getResponseCode());
        }

        // Get the decompressed input stream (if applicable)
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(getDecompressedInputStream(connection), StandardCharsets.UTF_8))) {
            StringBuilder response = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                response.append(line).append("\n");
            }
            return response.toString();  // Return raw text
        }
    }

    public static void main(String[] args) {
        try {
            String url = "https://adventofcode.com/2024/day/1/input";
            String responseData = getResponseAsString(url);
            List<String> list1 = new ArrayList<>();
            List<String> list2 = new ArrayList<>();

            // Split the response into lines and process each line
            String[] lines = responseData.split("\n");
            for (String line : lines) {
                // Assuming data is separated by space or tab
                String[] columns = line.split("\\s+");  // Split by one or more spaces/tabs

                if (columns.length >= 2) {
                    list1.add(columns[0]);  // First column goes to list1
                    list2.add(columns[1]);  // Second column goes to list2
                }
            }

            // Printing the results for verification
            System.out.println("List 1: " + list1);
            System.out.println("List 2: " + list2);
            //FindDistance(list1, list2);
            FindSimilarity(list1, list2);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void FindDistance(@NotNull List<String> list1, List<String> list2) {
        int acc = 0;

        // Continue until one of the lists is empty
        while (!list1.isEmpty() && !list2.isEmpty()) {
            // Find the smallest value in list1
            int smallestFromList1 = Integer.MAX_VALUE;
            int smallestFromList2 = Integer.MAX_VALUE;

            for (String value : list1) {
                int number = Integer.parseInt(value.trim());
                smallestFromList1 = Math.min(smallestFromList1, number);
            }

            // Find the smallest value in list2
            for (String value : list2) {
                int number = Integer.parseInt(value.trim());
                smallestFromList2 = Math.min(smallestFromList2, number);
            }

            // Calculate the absolute difference
            int difference = Math.abs(smallestFromList1 - smallestFromList2);
            acc += difference; // Update the accumulator

            // Remove the smallest elements from both lists
            list1.remove(String.valueOf(smallestFromList1));
            list2.remove(String.valueOf(smallestFromList2));

            // Debug output to track progress
            System.out.printf("Smallest from List1: %d, Smallest from List2: %d, Difference: %d, Accumulator: %d%n",
                    smallestFromList1, smallestFromList2, difference, acc);
        }

        // Final result
        System.out.println("Final accumulated difference: " + acc);
    }

    public static void FindSimilarity(@NotNull List<String> list1, @NotNull List<String> list2) {
        int totalSimilarityScore = 0;

        // Count occurrences of each number in list2
        Map<Integer, Integer> occurrenceMap = new HashMap<>();
        for (String value : list2) {
            int number = Integer.parseInt(value.trim());
            occurrenceMap.put(number, occurrenceMap.getOrDefault(number, 0) + 1);
        }

        // Check for similarities and calculate the score
        for (String value : list1) {
            int number = Integer.parseInt(value.trim());

            // If number exists in list2, calculate its contribution to the score
            if (occurrenceMap.containsKey(number)) {
                int occurrences = occurrenceMap.get(number);
                totalSimilarityScore += number * occurrences;

                System.out.printf("Number %d occurs %d times. Contribution to score: %d%n",
                        number, occurrences, number * occurrences);
            }
        }

        // Output the total similarity score
        System.out.println("Total Similarity Score: " + totalSimilarityScore);
    }
}
