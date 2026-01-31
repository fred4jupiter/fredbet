package de.fred4jupiter.fredbet.avatar;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

@Service
public class AvatarCreatorService {

    public byte[] createAvatar(String username) {
        RestClient restClient = RestClient.builder().baseUrl("https://api.dicebear.com/6.x/avataaars").build();

        ResponseEntity<byte[]> entity = restClient.get()
            .uri(uriBuilder -> uriBuilder.path("/jpg").queryParam("seed", username).build())
            .accept(MediaType.IMAGE_JPEG)
            .retrieve()
            .toEntity(byte[].class);

        return entity.getBody();
    }
}
