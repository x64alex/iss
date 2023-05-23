package com.example.backend.Model;


import javax.persistence.Entity;

@Entity
public class PendingBucketList extends BucketList {
    public PendingBucketList() {
        super();
        super.setType("pending");
    }
}
