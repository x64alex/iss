package com.example.backend.Repository;

import com.example.backend.Model.BucketList;
import com.example.backend.Model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BucketListRepository extends JpaRepository<BucketList, Long> {
    @Query("select bl from BucketList bl where bl.type = 'private' and bl.userId = :userId")
    BucketList getPrivateBucketList(@Param("userId") Long userId);

    @Query("select bl from BucketList bl where bl.type = 'public'")
    BucketList getPublicBucketList();

    @Query("select bl from BucketList bl where bl.type = 'pending'")
    BucketList getPendingBucketList();



}
