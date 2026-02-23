package com.example.threatguard_demo.models.DTO;



public class AttackTypeCount {

    private String attackType;
    private Long count;

    public AttackTypeCount(String attackType, Long count) {
        this.attackType = attackType;
        this.count = count;
    }

    public String getAttackType() {
        return attackType;
    }

    public Long getCount() {
        return count;
    }
}
