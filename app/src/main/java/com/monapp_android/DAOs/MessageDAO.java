package com.monapp_android.DAOs;

import com.monapp_android.DTOs.MessageDTO;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.Scanner;

public class MessageDAO {
    public MessageDAO() {
    }

    public void storeMessage(File file, MessageDTO messageDTO) {
        final List<String> lines = new LinkedList<>();
        try {
            final Scanner reader = new Scanner(new FileInputStream(file), "UTF-8");
            while (reader.hasNextLine()) {
                lines.add(reader.nextLine());
            }
            reader.close();

            final BufferedWriter writer;


            writer = new BufferedWriter(new FileWriter(file, false));
            writer.write(messageDTO.toString());
            writer.newLine();

            for (final String line : lines) {
                writer.write(line);
                writer.newLine();
            }

            writer.flush();
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public void removeMessage(File file, MessageDTO messageDTO) {
        final List<String> lines = new LinkedList<>();
        try {
            final Scanner reader = new Scanner(new FileInputStream(file), "UTF-8");
            String currentLine = null;

            while (reader.hasNextLine()) {
                currentLine = reader.nextLine();
                if (!messageDTO.toString().equals(currentLine)) {
                    lines.add(currentLine);
                }
            }
            reader.close();

            final BufferedWriter writer;

            writer = new BufferedWriter(new FileWriter(file, false));
            for (final String line : lines) {
                writer.write(line);
                writer.newLine();
            }
            writer.flush();
            writer.close();

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public void markMessageAsRead(File file, MessageDTO message){
        final List<String> lines = new LinkedList<>();
        try {
            final Scanner reader = new Scanner(new FileInputStream(file), "UTF-8");
            String currentLine = null;

            while (reader.hasNextLine()) {
                currentLine = reader.nextLine();
                if (!message.toString().equals(currentLine)) {
                    lines.add(currentLine);
                }
            }
            reader.close();

            final BufferedWriter writer;

            writer = new BufferedWriter(new FileWriter(file, false));
            for (final String line : lines) {
                writer.write(line);
                writer.newLine();
            }
            message.setRead(true);
            writer.write(message.toString());
            writer.newLine();

            writer.flush();
            writer.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void readFile(File file) {
        System.out.println("Start reading File -----------------: \n");
        try {
            final Scanner reader = new Scanner(new FileInputStream(file), "UTF-8");
            while (reader.hasNextLine()) {
                System.out.println("**" + reader.nextLine() + "**");
            }
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println("End reading File ---------------------\n");
    }


}