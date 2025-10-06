package org.habittracker.services;

import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.client.util.store.FileDataStoreFactory;
import com.google.api.services.calendar.Calendar;
import com.google.api.services.calendar.CalendarScopes;
import com.google.api.services.calendar.model.Event;
import com.google.api.services.calendar.model.EventDateTime;
import com.google.api.client.util.DateTime;

import java.io.FileReader;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Collections;
import java.util.Date;

public class GoogleCalendarService {
    private static final String APPLICATION_NAME = "Habit Tracker";
    private static final JsonFactory JSON_FACTORY = GsonFactory.getDefaultInstance();
    private static final String TOKENS_DIRECTORY_PATH = "tokens";

    private static Calendar getCalendarService() throws IOException, GeneralSecurityException {
        var httpTransport = GoogleNetHttpTransport.newTrustedTransport();
        var clientSecrets = GoogleClientSecrets.load(JSON_FACTORY, new FileReader("src/main/resources/credentials.json"));

        var flow = new GoogleAuthorizationCodeFlow.Builder(
                httpTransport, JSON_FACTORY, clientSecrets,
                Collections.singleton(CalendarScopes.CALENDAR))
                .setDataStoreFactory(new FileDataStoreFactory(new java.io.File(TOKENS_DIRECTORY_PATH)))
                .setAccessType("offline")
                .build();

        var receiver = new com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver();
        var credential = new com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp(flow, receiver)
                .authorize("user");

        return new Calendar.Builder(httpTransport, JSON_FACTORY, credential)
                .setApplicationName(APPLICATION_NAME)
                .build();
    }

    // Add event and return Event
    public static Event addEvent(String summary, String description, Date startTime, int durationMinutes)
            throws IOException, GeneralSecurityException {

        Calendar service = getCalendarService();

        DateTime startDateTime = new DateTime(startTime);
        DateTime endDateTime = new DateTime(new Date(startTime.getTime() + durationMinutes * 60000));

        Event event = new Event()
                .setSummary(summary)
                .setDescription(description)
                .setStart(new EventDateTime().setDateTime(startDateTime))
                .setEnd(new EventDateTime().setDateTime(endDateTime));

        event = service.events().insert("primary", event).execute();
        System.out.println("✅ Event created: " + event.getHtmlLink());
        return event;
    }

    // Delete an event by ID
    public static void deleteEvent(String eventId) throws IOException, GeneralSecurityException {
        if (eventId == null || eventId.isEmpty()) return;
        Calendar service = getCalendarService();
        service.events().delete("primary", eventId).execute();
        System.out.println("🗑 Event deleted: " + eventId);
    }

    // Update event summary/description (optional)
    public static Event updateEvent(String eventId, String newSummary, String newDescription) throws IOException, GeneralSecurityException {
        if (eventId == null || eventId.isEmpty()) return null;
        Calendar service = getCalendarService();
        Event event = service.events().get("primary", eventId).execute();
        event.setSummary(newSummary);
        event.setDescription(newDescription);
        event = service.events().update("primary", event.getId(), event).execute();
        System.out.println("✏️ Event updated: " + event.getHtmlLink());
        return event;
    }
}
