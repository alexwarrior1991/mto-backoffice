package com.alejandro.mtobackoffice.client.configuration;

import com.alejandro.mtobackoffice.client.dto.master.ProfileDto;
import org.springframework.web.service.annotation.HttpExchange;

/** Los perfiles (postes). Solo pone la ruta y el tipo: los endpoints son los de {@link MasterClient}. */
@HttpExchange("/api/configuration/profiles")
public interface ProfileClient extends MasterClient<ProfileDto> {
}
