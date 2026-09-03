package com.saloni.banking.repository;
import com.saloni.banking.entity.AuditLog; import org.springframework.data.jpa.repository.JpaRepository; import java.util.List;
public interface AuditLogRepository extends JpaRepository<AuditLog,Long>{List<AuditLog> findTop50ByOrderByCreatedAtDesc();}
