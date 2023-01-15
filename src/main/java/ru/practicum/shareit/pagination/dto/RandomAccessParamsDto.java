package ru.practicum.shareit.pagination.dto;

import ru.practicum.shareit.pagination.RandomAccessParams;

import javax.validation.ValidationException;

public class RandomAccessParamsDto implements RandomAccessParams {
    private final int from;
    private final Integer size;

    public RandomAccessParamsDto(Integer from, Integer size, int defaultSize) {
        validate(from, size);

        this.from = (from != null) ? from : 0;
        this.size =  (size != null) ? size : defaultSize;
    }

    private static void validate(Integer offset, Integer size) {
        if (offset != null && offset < 0) {
            throw new ValidationException("'from' must be greater than or equal to 0");
        }
        if (size != null && size < 1) {
            throw new ValidationException("'size' must be greater than or equal to 1");
        }
    }

    @Override
    public int getFrom() {
        return from;
    }

    @Override
    public int getSize() {
        return size;
    }
}