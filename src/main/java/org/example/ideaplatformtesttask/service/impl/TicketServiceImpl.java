package org.example.ideaplatformtesttask.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.ideaplatformtesttask.model.Ticket;
import org.example.ideaplatformtesttask.model.TicketsFile;
import org.example.ideaplatformtesttask.service.TicketService;

import java.io.File;
import java.io.IOException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class TicketServiceImpl implements TicketService {
    private final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("dd.MM.yy H:mm");
    private final ObjectMapper objectMapper = new ObjectMapper();
    @Override
    public void processingTicket(String filePath) throws IOException {
        if (filePath == null || filePath.isEmpty()) {
            throw new IllegalArgumentException("File path cannot be empty");
        }

        TicketsFile ticketsFile = objectMapper.readValue(new File(filePath), TicketsFile.class);
        List<Ticket> filteredTickets = ticketsFile.getTickets().stream()
                .filter(ticket -> "VVO".equals(ticket.getOrigin()) && "TLV".equals(ticket.getDestination()))
                .toList();

        if (filteredTickets.isEmpty()) {
            System.out.println("No tickets found for route VVO-TLV");
            return;
        }

        Map<String, Duration> carrierMinDurations = calculateMinDurations(filteredTickets);
        List<Integer> prices = extractPrices(filteredTickets);
        double median = calculateMedian(prices);
        double average = calculateAverage(prices);
        double diff = average - median;

        printResults(carrierMinDurations, diff);
    }
    private Map<String, Duration> calculateMinDurations(List<Ticket> tickets) {
        Map<String, Duration> minDurations = new HashMap<>();
        for (Ticket ticket : tickets) {
            LocalDateTime departure = parseDateTime(
                    ticket.getDeparture_date(),
                    ticket.getDeparture_time()
            );
            LocalDateTime arrival = parseDateTime(
                    ticket.getArrival_date(),
                    ticket.getArrival_time()
            );
            Duration duration = Duration.between(departure, arrival);
            minDurations.compute(ticket.getCarrier(), (key, currentMin) ->
                    (currentMin == null || duration.compareTo(currentMin) < 0) ? duration : currentMin
            );
        }
        return minDurations;
    }

    private LocalDateTime parseDateTime(String date, String time) {
        return LocalDateTime.parse(date + " " + time, DATE_TIME_FORMATTER);
    }

    private List<Integer> extractPrices(List<Ticket> tickets) {
        return tickets.stream()
                .map(Ticket::getPrice)
                .toList();
    }

    private double calculateMedian(List<Integer> prices) {
        List<Integer> sortedPrices = new ArrayList<>(prices);
        Collections.sort(sortedPrices);
        int size = sortedPrices.size();
        if (size % 2 == 0) {
            return (sortedPrices.get(size/2 - 1) + sortedPrices.get(size/2)) / 2.0;
        } else {
            return sortedPrices.get(size/2);
        }
    }

    private double calculateAverage(List<Integer> prices) {
        return prices.stream()
                .mapToInt(Integer::intValue)
                .average()
                .orElse(0.0);
    }

    private void printResults(Map<String, Duration> minDurations, double priceDiff) {
        System.out.println("Min time flying between Vladivistok and Tel-Aviv:");
        minDurations.forEach((carrier, duration) -> {
            long hours = duration.toHours();
            long minutes = duration.toMinutesPart();
            System.out.printf("%s: %d hours %d minutes%n", carrier, hours, minutes);
        });
        System.out.printf("%nDifference between avg price and median: %.2f%n", priceDiff);
    }
}
