package com.psikolojikdanismanlik.randevusistemi.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "email_attachment_contents")
public class EmailAttachment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "filename", nullable = false)
    private String filename;

    @Column(name = "content_type")
    private String contentType;

    @Column(name = "content", nullable = false, columnDefinition = "BYTEA")
    private byte[] content;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "email_id", nullable = false)
    private Email email;
}
