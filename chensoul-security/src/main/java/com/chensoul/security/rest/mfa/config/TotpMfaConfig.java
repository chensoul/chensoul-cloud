package com.chensoul.security.rest.mfa.config;

import com.chensoul.security.rest.mfa.provider.MfaProviderType;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

@Data
@EqualsAndHashCode(callSuper = true)
public class TotpMfaConfig extends MfaConfig {

	@NotBlank
	@Pattern(regexp = "otpauth://totp/(\\S+?):(\\S+?)\\?issuer=(\\S+?)&secret=(\\w+?)", message = "is invalid")
	private String authUrl;

	@Override
	public MfaProviderType getProviderType() {
		return MfaProviderType.TOTP;
	}

}

