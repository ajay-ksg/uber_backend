package com.uber.uberapi.models;

import lombok.Getter;
import lombok.Setter;
import org.mockito.internal.matchers.Null;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;
@MappedSuperclass //don't create table for Auditable class
@EntityListeners(AuditingEntityListener.class)  // for proper working of Date fields
@Getter
@Setter
public abstract class Auditable implements Serializable {
    //database to provide id for us
    //autoincrement
    @javax.persistence.Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    //Temporal relating to time
    @Column(updatable = false)
    @Temporal(TemporalType.TIMESTAMP)  //from JPA
    @CreatedDate
    private Date createdAt;

    @Temporal(TemporalType.TIMESTAMP)
    @LastModifiedDate
    private Date updatedAt;

    @Override
    public int hashCode() {

        return id == null?0: id.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if(this == obj) return true; //same memory address
        if(!this.getClass().equals(obj.getClass())) return false;
        if(obj instanceof Auditable) {
            Auditable auditableObj = (Auditable) obj;
            if (id == null && auditableObj.id == null) return true;
            if(id == null || auditableObj.id == null) return false;
            return id == auditableObj.id;
        }else{
            return super.equals(obj);
        }
    }
}
/*compare objects in java
//==
primitive data type- int , float , bool, char - values comparison
objects - compare the memory address

//.equals()
object.equals()  ==
.hashCode()  also compare memory address for objects.




 */