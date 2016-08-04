package com.softjourn.vending.utils;


import java.lang.reflect.Field;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Stream;

/**
 * Class for merging entities.
 * Takes already stored entity and replace it's fields
 * by fields from another entity called updater.
 * Can ignore specified fields and fields that is null in updater;
 * @param <T> class for which this util is constructed
 */
public class ReflectionMergeUtil<T> {

    private final Class<T> paramClass;

    private final Set<String> ignoreFields;

    private final boolean ignoreNull;

    private ReflectionMergeUtil(Class<T> clazz, Set<String> ignoreFields, boolean ignoreNull) {
        paramClass = clazz;
        this.ignoreFields = ignoreFields;
        this.ignoreNull = ignoreNull;
    }

    public static class Builder<T> {

        private final Class<T> paramClass;
        private final Set<String> ignoreFields = new HashSet<>();
        private boolean ignoreNull;

        public Builder(Class<T> paramClass) {
            this.paramClass = paramClass;
        }

        public Builder<T> ignoreField(String field) {
            ignoreFields.add(field);
            return this;
        }

        public Builder<T> ignoreNull(boolean ignoreNull) {
            this.ignoreNull = ignoreNull;
            return this;
        }

        public ReflectionMergeUtil<T> build() {
            return new ReflectionMergeUtil<>(paramClass, ignoreFields, ignoreNull);
        }
    }

    public static <T> Builder<T> forClass(Class<T> clazz) {
        return new Builder<>(clazz);
    }

    public T merge(T fromDB, T updater) {
        Stream.of(paramClass.getDeclaredFields())
                .peek(field -> field.setAccessible(true))
                .filter(field -> ! ignoreFields.contains(field.getName()))
                .filter(field -> (! ignoreNull ) || isNotNull(field, updater))
                .forEach(field -> update(field, fromDB, updater));

        return fromDB;
    }

    private boolean isNotNull(Field field, T o) {
        try {
            return field.get(o) != null;
        } catch (IllegalAccessException e) {
            //Should never happen
            throw new RuntimeException(e);
        }
    }

    private void update(Field field, T acceptor, T updater) {
        try {
            field.set(acceptor, field.get(updater));
        } catch (IllegalAccessException e) {
            //Should never happen
            throw new RuntimeException(e);
        }
    }

}
