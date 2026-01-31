package de.fred4jupiter.fredbet.avatar;

import de.fred4jupiter.fredbet.props.FredbetProperties;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

@Service
public class AvatarCreatorService {

    private final FredbetProperties fredbetProperties;

    public AvatarCreatorService(FredbetProperties fredbetProperties) {
        this.fredbetProperties = fredbetProperties;
    }

    public byte[] createAvatar(String username) {
        RestClient restClient = RestClient.builder().baseUrl(fredbetProperties.avatar().diceBearBaseUrl()).build();

        ResponseEntity<byte[]> entity = restClient.get()
            .uri(uriBuilder -> uriBuilder.path("/jpg").queryParam("seed", username).build())
            .accept(MediaType.IMAGE_JPEG)
            .retrieve()
            .toEntity(byte[].class);

        return entity.getBody();
    }
}
