package com.example.backend.Model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "bucketList")
public class BucketList {
    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    private Long id;

    private String type;

    @OneToMany(mappedBy = "bucketList", fetch = FetchType.LAZY,
            cascade = CascadeType.ALL)
    private List<Destination> destinationList = new ArrayList<>();

    @JsonIgnore
    private Long userId;

    @JsonIgnore
    public Destination getDestination(Long destinationId) {
        for ( Destination var : destinationList){
            if (var.getId().equals(destinationId)){
                return var;
            }
        };
        return null;
    }

    @JsonIgnore
    public void addDestination(Destination destination) {
        destinationList.add(destination);
    }

    @JsonIgnore
    public void updateDestination(Destination destination) {
        destinationList.remove(destination);
        destinationList.add(destination);
    }

    @JsonIgnore
    public void removeDestination(Long destinationId) {
        destinationList.removeIf(var -> var.getId().equals(destinationId));
        ;
    }
}
