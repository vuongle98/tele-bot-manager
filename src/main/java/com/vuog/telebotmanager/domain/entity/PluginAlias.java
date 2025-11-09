package com.vuog.telebotmanager.domain.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

/**
 * Domain entity representing plugin aliases
 * Allows plugins to be referenced by multiple names
 */
@Entity
@Table(name = "plugin_aliases")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EntityListeners({AuditingEntityListener.class})
public class PluginAlias {

    @Id
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "plugin_id", nullable = false)
    private BotPlugin botPlugin;

    @Id
    @Column(name = "alias", nullable = false)
    private String alias;

    @Column(name = "description")
    private String description;

    @Column(name = "is_primary")
    private Boolean isPrimary;

    /**
     * Domain method to set as primary alias
     */
    public void setAsPrimary() {
        this.isPrimary = true;
    }

    /**
     * Domain method to remove primary status
     */
    public void removePrimary() {
        this.isPrimary = false;
    }
}
