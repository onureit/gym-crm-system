package kz.olzhas.service;

import org.springframework.stereotype.Service;

@Service
public class GymService {
    public String getWelcomeMessage() {
        return "Welcome to the Gym CRM System";
    }
}
