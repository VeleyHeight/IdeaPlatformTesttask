package org.example.ideaplatformtesttask;


import org.example.ideaplatformtesttask.service.TicketService;
import org.example.ideaplatformtesttask.service.impl.TicketServiceImpl;

import java.io.IOException;

public class IdeaPlatformTestTaskApplication {

    public static void main(String[] args) {
        TicketService ticketService = new TicketServiceImpl();
        try {
            ticketService.processingTicket(args[0]);
        }
        catch (IOException | IllegalArgumentException e){
            System.err.println("Ошибка обработки файла: " + e.getMessage());
            e.printStackTrace();
            System.exit(1);
        }
    }

}
