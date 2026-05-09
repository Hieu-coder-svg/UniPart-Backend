package com.unipart.unipart_backend.controller;

import com.unipart.unipart_backend.dto.SystemLogDTO;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.BufferedReader;
import java.io.FileReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@RestController
@RequestMapping("/admin/logs")
@PreAuthorize("hasRole('ADMIN')")
public class SystemLogController {

    private static final Pattern LOG_PATTERN = Pattern.compile("^(\\d{4}-\\d{2}-\\d{2}T\\d{2}:\\d{2}:\\d{2}\\.\\d+[+-]\\d{2}:\\d{2})\\s+(\\w+)\\s+\\d+\\s+---\\s+\\[.*?\\]\\s+\\[.*?\\]\\s+(.*?)\\s+:\\s+(.*)$");

    @GetMapping
    public ResponseEntity<List<SystemLogDTO>> getLogs(@RequestParam(defaultValue = "500") int limit) {
        Path logPath = Paths.get("logs/unipart.log");
        if (!Files.exists(logPath)) {
            return ResponseEntity.ok(Collections.emptyList());
        }

        LinkedList<SystemLogDTO> logs = new LinkedList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(logPath.toFile()))) {
            String line;
            SystemLogDTO currentLog = null;
            StringBuilder detailsBuilder = new StringBuilder();

            while ((line = reader.readLine()) != null) {
                Matcher matcher = LOG_PATTERN.matcher(line);
                if (matcher.find()) {
                    if (currentLog != null) {
                        currentLog.setDetails(detailsBuilder.toString().trim());
                        logs.add(currentLog);
                        if (logs.size() > limit) {
                            logs.removeFirst();
                        }
                    }

                    String timestamp = matcher.group(1);
                    String level = matcher.group(2).toLowerCase();
                    String source = matcher.group(3).trim();
                    String message = matcher.group(4);

                    if (level.equals("warn")) level = "warning";

                    currentLog = SystemLogDTO.builder()
                            .id(UUID.randomUUID().toString())
                            .timestamp(timestamp.replace("T", " "))
                            .level(level)
                            .source(source)
                            .message(message)
                            .build();
                    detailsBuilder = new StringBuilder();
                } else {
                    if (currentLog != null) {
                        detailsBuilder.append(line).append("\n");
                    }
                }
            }

            if (currentLog != null) {
                currentLog.setDetails(detailsBuilder.toString().trim());
                logs.add(currentLog);
                if (logs.size() > limit) {
                    logs.removeFirst();
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().build();
        }

        List<SystemLogDTO> result = new ArrayList<>(logs);
        Collections.reverse(result);
        return ResponseEntity.ok(result);
    }
}
