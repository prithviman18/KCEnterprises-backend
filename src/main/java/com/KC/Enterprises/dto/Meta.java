package com.KC.Enterprises.dto;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Meta {
    private LocalDateTime timestamp;
    private String version;
    private String apiVersion;
    private Pagination pagination;

    // Simple constructor
    public Meta(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Pagination {
        private int page;
        private int size;
        private long totalElements;
        private int totalPages;
        private boolean last;
    }
}