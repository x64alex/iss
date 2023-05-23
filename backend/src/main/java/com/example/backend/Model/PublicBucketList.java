package com.example.backend.Model;


import javax.persistence.Entity;

@Entity
public class PublicBucketList extends BucketList {
    public PublicBucketList() {
        super();
        super.setType("public");
    }
}