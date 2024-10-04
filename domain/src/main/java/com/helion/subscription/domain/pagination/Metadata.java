package com.helion.subscription.domain.pagination;

public record Metadata(
        int currentPage,
        int perPage,
        long total
) {
}
