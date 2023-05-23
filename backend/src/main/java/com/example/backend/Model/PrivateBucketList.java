package com.example.backend.Model;


import javax.persistence.Entity;

@Entity
public class PrivateBucketList extends BucketList {
    public PrivateBucketList(Long userId) {
        super();
        super.setUserId(userId);
        super.setType("private");
    }

    public PrivateBucketList() {
        super();
        super.setType("private");
    }
}