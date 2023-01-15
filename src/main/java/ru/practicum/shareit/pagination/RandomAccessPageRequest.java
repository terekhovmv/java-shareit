package ru.practicum.shareit.pagination;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

public class RandomAccessPageRequest implements Pageable {
    private final int offset;
    private final int limit;
    private final Sort sort;

    public static Pageable of(RandomAccessParams params, Sort sort) {
        return of(params.getFrom(), params.getSize(), sort);
    }

    public static Pageable of(int offset, int limit, Sort sort) {
        return new RandomAccessPageRequest(offset, limit, sort);
    }

    protected RandomAccessPageRequest(int offset, int limit, Sort sort) {
        if (offset < 0) {
            throw new IllegalStateException("Offset must be greater than or equal to 0");
        }
        if (limit < 1) {
            throw new IllegalStateException("Offset must be greater than or equal to 1");
        }

        this.offset = offset;
        this.limit = limit;
        this.sort = sort;
    }

    @Override
    public int getPageNumber() {
        return offset / limit;
    }

    @Override
    public int getPageSize() {
        return limit;
    }

    @Override
    public long getOffset() {
        return offset;
    }

    @Override
    public Sort getSort() {
        return sort;
    }

    @Override
    public Pageable next() {
        return of(getPageSize(), (int) (getOffset() + getPageSize()), getSort());
    }

    @Override
    public Pageable previousOrFirst() {
        return hasPrevious()
                ? of(getPageSize(), (int) (getOffset() - getPageSize()), getSort())
                : first();
    }

    @Override
    public Pageable first() {
        return of(getPageSize(), 0, getSort());
    }

    @Override
    public Pageable withPage(int pageNumber) {
        return of(getPageSize() * pageNumber, getPageSize(), getSort());
    }

    @Override
    public boolean hasPrevious() {
        return getOffset() >= getPageSize();
    }
}