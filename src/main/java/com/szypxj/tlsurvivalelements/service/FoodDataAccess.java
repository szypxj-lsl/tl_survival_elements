package com.szypxj.tlsurvivalelements.service;

public interface FoodDataAccess {
    int tlse$getMaxFood();
    void tlse$setMaxFood(int value);
    float tlse$getSaturationLevel();
    void tlse$addExhaustion(float amount);
    int tlse$getNaturalHealTimer();
    void tlse$setNaturalHealTimer(int value);
}
