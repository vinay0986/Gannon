// 
// Decompiled by Procyon v0.5.36
// 

package com.gannon.entity;

import javax.persistence.Column;
import javax.persistence.GenerationType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Entity;
import java.io.Serializable;

@Entity
@Table(name = "admin_settings")
public class ConstantSettings implements Serializable
{
    private Integer settingsId;
    private String key;
    private String value;
    private String fActive;
    private String internalDescription;
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "settings_id", unique = true, nullable = false)
    public Integer getSettingsId() {
        return this.settingsId;
    }
    
    public void setSettingsId(final Integer settingsId) {
        this.settingsId = settingsId;
    }
    
    @Column(name = "key")
    public String getKey() {
        return this.key;
    }
    
    public void setKey(final String key) {
        this.key = key;
    }
    
    @Column(name = "value")
    public String getValue() {
        return this.value;
    }
    
    public void setValue(final String value) {
        this.value = value;
    }
    
    @Column(name = "f_active")
    public String getfActive() {
        return this.fActive;
    }
    
    public void setfActive(final String fActive) {
        this.fActive = fActive;
    }
    
    @Column(name = "internal_description")
    public String getInternalDescription() {
        return this.internalDescription;
    }
    
    public void setInternalDescription(final String internalDescription) {
        this.internalDescription = internalDescription;
    }
}
