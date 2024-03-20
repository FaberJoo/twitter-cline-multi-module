package org.twitter.db.common.util;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;

public class PagingUtil {

    public static Pageable applyPageableForNonOffset(int size, String property, String direction) {
        return PageRequest.of(0, size, direction.equals("desc") ? Direction.DESC : Direction.ASC, property);
    }

    public static Pageable applyPageableForNonOffset(int size, Sort sort) {
        return PageRequest.of(0, size, sort);
    }

    public static Pageable applyPageableForOffset(int page, String property, String direction, int size) {
        return PageRequest.of(page, size, direction.equals("desc") ? Direction.DESC : Direction.ASC, property);
    }
}
