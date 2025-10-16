package codes.alem.bffservice.service;

import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class TrackingService {

    public String generateTrackingId() {
        return "BFF-" + UUID.randomUUID().toString().substring(0, 8);
    }

}
