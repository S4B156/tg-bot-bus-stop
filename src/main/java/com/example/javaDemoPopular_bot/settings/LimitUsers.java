package com.example.javaDemoPopular_bot.settings;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Component
@Slf4j
public class LimitUsers {
    public static Set<String> generatePassword(int count){
        Set<String> passwords = new HashSet<>();
        for(int i = 0; i <= count; i++){ passwords.add(UUID.randomUUID().toString()); }
        log.info(passwords.toString());
        return passwords;
    }
}
