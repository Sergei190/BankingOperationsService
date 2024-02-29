package ru.effectivemobile.boperations.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import ru.effectivemobile.boperations.domain.core.model.DomainUser;

import java.util.UUID;

@AllArgsConstructor
@Getter
public class AppDomainUser implements DomainUser {
    private final UUID id;
}