package com.vuog.telebotmanager.infrastructure.bot.handler;

import org.springframework.stereotype.Service;

@Service
public class CommandHandler {

    public String showWeather(String[] args) {
        // logic here
        return "Weather: Sunny 25°C";
    }

    public String help(String[] args) {
        return "Available commands: /weather, /help";
    }

    // add more handlers as needed
}