package com.example.backend.Controller;

import com.example.backend.Model.BucketList;
import com.example.backend.Model.Destination;
import com.example.backend.Model.PendingBucketList;
import com.example.backend.Model.PublicBucketList;
import com.example.backend.Repository.BucketListRepository;
import com.example.backend.Repository.DestinationRepository;
import com.example.backend.Security.Payload.response.MessageResponse;
import com.example.backend.Security.Services.UserDetailsImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import javax.transaction.Transactional;


@RestController
@RequestMapping("/api")
public class BucketListController {
    @Autowired
    private BucketListRepository bucketListRepository;


    @Autowired
    private DestinationRepository destinationRepository;

    @GetMapping("/bucketList/public")
    public BucketList getPublicBucketList() {
        BucketList publicBucketList = bucketListRepository.getPublicBucketList();
        if(publicBucketList == null){
            publicBucketList = new PublicBucketList();
            bucketListRepository.save(publicBucketList);
        }
        return bucketListRepository.getPublicBucketList();
    }
    @GetMapping("/bucketList/private")
    @PreAuthorize("hasRole('ROLE_REGULAR')")
    public BucketList getPrivateBucketList() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Long userId = ((UserDetailsImpl) principal).getId();

        System.out.println(bucketListRepository.findAll());

        return bucketListRepository.getPrivateBucketList(userId);
    }
    @GetMapping("/bucketList/pending")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public BucketList getPendingBucketList() {
        BucketList pendingBucketList = bucketListRepository.getPendingBucketList();
        if(pendingBucketList == null){
            pendingBucketList = new PendingBucketList();
            bucketListRepository.save(pendingBucketList);
        }
        return bucketListRepository.getPendingBucketList();
    }

    @PostMapping("/bucketList/private")
    @PreAuthorize("hasRole('ROLE_REGULAR') or hasRole('ROLE_ADMIN')")
    public ResponseEntity<?> addPrivateDestination(@RequestBody Destination destination) {
        try{
            BucketList privateBucketList = this.getPrivateBucketList();
            privateBucketList.addDestination(destination);
            destination.setBucketList(privateBucketList);

            bucketListRepository.save(privateBucketList);
            return ResponseEntity.ok(new MessageResponse("Destination added successfully to private list!"));
        }catch(Exception e){
            return ResponseEntity.badRequest().body(new MessageResponse("Error: " + e.getMessage()));
        }

    }

    @PostMapping("/bucketList/public")
    @PreAuthorize("hasRole('ROLE_REGULAR') or hasRole('ROLE_ADMIN')")
    public ResponseEntity<?> addPublicDestination(@RequestBody Destination destination) {
        try{
            BucketList pendingBucketList = this.getPendingBucketList();
            if(pendingBucketList == null){
                pendingBucketList = new PendingBucketList();
            }
            destination.setBucketList(pendingBucketList);
            pendingBucketList.addDestination(destination);
            bucketListRepository.save(pendingBucketList);
            return ResponseEntity.ok(new MessageResponse("Destination added to pending destinations!"));
        }catch(Exception e){
            return ResponseEntity.badRequest().body(new MessageResponse("Error: " + e.getMessage()));
        }

    }

    @PostMapping("/bucketList/approve")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<?> approveDestination(@RequestParam Long destinationId) {
        try{
            BucketList pendingBucketList = this.getPendingBucketList();
            if(pendingBucketList == null){
                pendingBucketList = new PendingBucketList();
            }
            BucketList publicBucketList = this.getPublicBucketList();
            if(publicBucketList == null){
                publicBucketList = new PublicBucketList();
            }
            Destination destination = pendingBucketList.getDestination(destinationId);
            pendingBucketList.removeDestination(destinationId);
            publicBucketList.addDestination(destination);

            destination.setBucketList(publicBucketList);
            bucketListRepository.save(publicBucketList);
            bucketListRepository.save(pendingBucketList);
            return ResponseEntity.ok(new MessageResponse("Destination approved to be added to public bucket list!"));
        }catch(Exception e){
            return ResponseEntity.badRequest().body(new MessageResponse("Error: " + e.getMessage()));
        }

    }

    @Transactional
    @DeleteMapping("/bucketList/public")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<?> removePublicDestination(@RequestParam Long destinationId) {
        try{
            BucketList publicBucketList = this.getPublicBucketList();

            publicBucketList.removeDestination(destinationId);

            bucketListRepository.save(publicBucketList);
            destinationRepository.deleteById(destinationId);
            System.out.println(bucketListRepository.getPublicBucketList().getDestinationList());
            return ResponseEntity.ok(new MessageResponse("Destination deleted fromm public bucket list!"));
        }catch(Exception e){
            return ResponseEntity.badRequest().body(new MessageResponse("Error: " + e.getMessage()));
        }

    }


    @Transactional
    @DeleteMapping("/bucketList/private")
    @PreAuthorize("hasRole('ROLE_REGULAR')")
    public ResponseEntity<?> removePrivateDestination(@RequestParam Long destinationId) {
        try{
            BucketList privateBucketList = this.getPrivateBucketList();

            privateBucketList.removeDestination(destinationId);

            bucketListRepository.save(privateBucketList);
            destinationRepository.deleteById(destinationId);
            return ResponseEntity.ok(new MessageResponse("Destination deleted fromm private bucket list!"));
        }catch(Exception e){
            return ResponseEntity.badRequest().body(new MessageResponse("Error: " + e.getMessage()));
        }

    }

}
