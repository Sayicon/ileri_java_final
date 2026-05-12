package com.tbl324.event.domain;

public final class Venue {
    private final Long id;
    private final String name;
    private final String address;
    private final int capacity;

    private Venue(Builder b) {
        this.id = b.id;
        this.name = b.name;
        this.address = b.address;
        this.capacity = b.capacity;
    }

    public Long getId()       { return id; }
    public String getName()   { return name; }
    public String getAddress(){ return address; }
    public int getCapacity()  { return capacity; }

    public static Builder builder() { return new Builder(); }

    public static final class Builder {
        private Long id;
        private String name;
        private String address;
        private int capacity;

        public Builder id(Long id)          { this.id = id; return this; }
        public Builder name(String name)    { this.name = name; return this; }
        public Builder address(String a)    { this.address = a; return this; }
        public Builder capacity(int c)      { this.capacity = c; return this; }
        public Venue build()                { return new Venue(this); }
    }
}
