package com.jewelryshop.enums;

public enum DiscountType {
    PERCENTAGE("Phần trăm (%)"),
    FIXED_AMOUNT("Số tiền cố định (đ)");

    private final String displayName;

    DiscountType(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
