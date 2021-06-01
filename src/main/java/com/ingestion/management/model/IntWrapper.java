package com.ingestion.management.model;

public final class IntWrapper {
    private final int count;

    public IntWrapper(int count) {
        this.count = count;
    }

    public int getCount() {
        return count;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;

        IntWrapper that = (IntWrapper) o;

        return count == that.count;
    }

    @Override
    public int hashCode() {
        return count;
    }
}
