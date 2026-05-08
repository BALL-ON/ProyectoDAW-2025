package com.ballon.backend.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO para tokenQr que se usa en la reserva
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TokenQrDTO {
	
	public String token;

}
