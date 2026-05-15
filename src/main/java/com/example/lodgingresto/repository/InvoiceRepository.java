package com.example.lodgingresto.repository;

import com.example.lodgingresto.model.Invoice;
import org.springframework.data.jpa.repository.JpaRepository;

public interface InvoiceRepository extends JpaRepository<Invoice, Long> {
}

