package com.subscrybe.infrastructure.adapters.out.email;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.client.util.store.FileDataStoreFactory;
import com.google.api.services.gmail.Gmail;
import com.google.api.services.gmail.GmailScopes;
import com.google.api.services.gmail.model.ListMessagesResponse;
import com.google.api.services.gmail.model.Message;
import com.subscrybe.application.ports.out.IEmailScanner;
import com.subscrybe.domain.entities.Cycle;
import com.subscrybe.domain.entities.Subscription;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.google.api.client.util.Base64;
import java.time.Instant;
import java.time.ZoneId;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
public class GmailApiScannerAdapter implements IEmailScanner {

    private static final String APPLICATION_NAME = "Subscrybe";
    private static final GsonFactory JSON_FACTORY = GsonFactory.getDefaultInstance();
    private static final String TOKENS_DIRECTORY_PATH = "tokens";

    private Gmail getGmailService() throws Exception {
        InputStream in = GmailApiScannerAdapter.class.getResourceAsStream("/credentials.json");
        if (in == null) {
            throw new RuntimeException("No se encontró credentials.json en resources");
        }

        GoogleClientSecrets clientSecrets = GoogleClientSecrets.load(JSON_FACTORY, new InputStreamReader(in));

        GoogleAuthorizationCodeFlow flow = new GoogleAuthorizationCodeFlow.Builder(
                GoogleNetHttpTransport.newTrustedTransport(), JSON_FACTORY, clientSecrets, Collections.singletonList(GmailScopes.GMAIL_READONLY))
                .setDataStoreFactory(new FileDataStoreFactory(new File(TOKENS_DIRECTORY_PATH)))
                .setAccessType("offline")
                .build();

        LocalServerReceiver receiver = new LocalServerReceiver.Builder().setPort(8888).build();
        Credential credential = new AuthorizationCodeInstalledApp(flow, receiver).authorize("user");

        return new Gmail.Builder(GoogleNetHttpTransport.newTrustedTransport(), JSON_FACTORY, credential)
                .setApplicationName(APPLICATION_NAME)
                .build();
    }

    @Override
    public List<Subscription> scanInbox(String emailAccount) {
        List<Subscription> foundSubscriptions = new ArrayList<>();

        try {
            Gmail service = getGmailService();
            System.out.println("Conectado exitosamente a Gmail. Buscando correos de suscripciones...");

            // Buscamos correos de las plataformas objetivo
            ListMessagesResponse response = service.users().messages().list("me")
                    .setQ("Spotify OR Netflix OR Amazon Prime OR Disney")
                    .execute();

            List<Message> messages = response.getMessages();
            if (messages != null) {
                for (Message msg : messages) {
                    Message fullMessage = service.users().messages().get("me", msg.getId()).execute();
                    String snippet = fullMessage.getSnippet().toLowerCase();

                    String platformName = null;
                    double standardCost = 0.0;

                    if (snippet.contains("netflix") && !containsName(foundSubscriptions, "Netflix")) {
                        platformName = "Netflix";
                        standardCost = 250.0;
                    } else if (snippet.contains("spotify") && !containsName(foundSubscriptions, "Spotify")) {
                        platformName = "Spotify";
                        standardCost = 129.0;
                    } else if ((snippet.contains("amazon prime") || snippet.contains("prime video")) && !containsName(foundSubscriptions, "Amazon Prime")) {
                        platformName = "Amazon Prime";
                        standardCost = 99.0;
                    } else if (snippet.contains("disney") && !containsName(foundSubscriptions, "Disney+")) {
                        platformName = "Disney+";
                        standardCost = 179.0;
                    }

                    // Si se detectó la plataforma, extraemos el detalle
                    if (platformName != null) {
                        String fullBody = extractEmailBody(fullMessage);
                        double exactCost = extractCostFromText(fullBody, standardCost);
                        LocalDate paymentDate = extractDateFromMetadata(fullMessage);

                        foundSubscriptions.add(new Subscription(platformName, exactCost, Cycle.MONTHLY, paymentDate));
                        System.out.println("¡Suscripción detectada! " + platformName + " | Costo: $" + exactCost + " | Fecha: " + paymentDate);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return foundSubscriptions;
    }

    private boolean containsName(List<Subscription> subs, String name) {
        return subs.stream().anyMatch(sub -> sub.getName().equalsIgnoreCase(name));
    }

    // --- MÉTODOS DE APOYO (SINGLE RESPONSIBILITY) ---

    // 1. Extraer la fecha de recepción del correo
    private LocalDate extractDateFromMetadata(Message fullMessage) {
        Long timestamp = fullMessage.getInternalDate();
        if (timestamp != null) {
            return Instant.ofEpochMilli(timestamp)
                    .atZone(ZoneId.systemDefault())
                    .toLocalDate();
        }
        return LocalDate.now();
    }

    // 2. Extraer el cuerpo del correo (Maneja codificación Base64)
    private String extractEmailBody(Message fullMessage) {
        try {
            String encodedBody = "";

            if (fullMessage.getPayload().getParts() != null) {
                encodedBody = fullMessage.getPayload().getParts().get(0).getBody().getData();
            } else if (fullMessage.getPayload().getBody().getData() != null) {
                encodedBody = fullMessage.getPayload().getBody().getData();
            }

            if (encodedBody != null && !encodedBody.isEmpty()) {
                return new String(Base64.decodeBase64(encodedBody));
            }
        } catch (Exception e) {
            System.out.println("No se pudo extraer el cuerpo del correo.");
        }
        return "";
    }

    // 3. Buscar el precio usando Expresiones Regulares (Regex)
    private double extractCostFromText(String emailBody, double fallbackCost) {
        Pattern costPattern = Pattern.compile("\\$\\s*([0-9]+(?:\\.[0-9]{1,2})?)");
        Matcher matcher = costPattern.matcher(emailBody);

        if (matcher.find()) {
            try {
                return Double.parseDouble(matcher.group(1));
            } catch (NumberFormatException e) {
                // Ignorar y usar el fallback
            }
        }
        return fallbackCost;
    }
}