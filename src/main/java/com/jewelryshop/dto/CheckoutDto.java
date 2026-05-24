package com.jewelryshop.dto;

import jakarta.validation.constraints.*;
import lombok.*;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class CheckoutDto {

    @NotBlank(message = "Họ tên không được để trống")
    private String shippingName;

    @NotBlank(message = "Số điện thoại không được để trống")
    @Pattern(regexp = "^[0-9]{10,11}$", message = "Số điện thoại phải là 10-11 chữ số")
    private String shippingPhone;

    @NotBlank(message = "Địa chỉ không được để trống")
    private String shippingAddress;

    @NotBlank(message = "Vui lòng chọn phương thức thanh toán")
    private String paymentMethod = "COD";

    private String couponCode;
    private String note;
}
