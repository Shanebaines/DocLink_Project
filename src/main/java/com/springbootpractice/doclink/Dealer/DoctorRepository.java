package com.springbootpractice.doclink.Dealer;

import com.springbootpractice.doclink.Kernal.Entity.Doctor;
import com.springbootpractice.doclink.Kernal.Relations.Doctor_availability;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DoctorRepository extends JpaRepository<Doctor, Long> {

    // Paged: search by full name (first + ' ' + last) or by specialization
    @Query(value = """
    SELECT d FROM Doctor d
    JOIN d.user u
    WHERE
      (
        :q IS NULL OR :q = '' OR
        LOWER(CONCAT(u.firstName, ' ', u.lastName)) LIKE LOWER(CONCAT('%', :q, '%')) OR
        LOWER(u.firstName) LIKE LOWER(CONCAT('%', :q, '%')) OR
        LOWER(u.lastName)  LIKE LOWER(CONCAT('%', :q, '%'))
      )
      AND
      (
        :specialization IS NULL OR :specialization = '' OR
        LOWER(d.specialization) LIKE LOWER(CONCAT('%', :specialization, '%'))
      )
    """,
            countQuery = """
    SELECT COUNT(d) FROM Doctor d
    JOIN d.user u
    WHERE
      (
        :q IS NULL OR :q = '' OR
        LOWER(CONCAT(u.firstName, ' ', u.lastName)) LIKE LOWER(CONCAT('%', :q, '%')) OR
        LOWER(u.firstName) LIKE LOWER(CONCAT('%', :q, '%')) OR
        LOWER(u.lastName)  LIKE LOWER(CONCAT('%', :q, '%'))
      )
      AND
      (
        :specialization IS NULL OR :specialization = '' OR
        LOWER(d.specialization) LIKE LOWER(CONCAT('%', :specialization, '%'))
      )
    """)
    Page<Doctor> searchByNameOrSpecialization(
            @Param("q") String q,
            @Param("specialization") String specialization,
            Pageable pageable
    );

    // Non-paged list: same criteria, ordered by id (useful for returning same shape as /viewAll)
    @Query("""
    SELECT d FROM Doctor d
    JOIN d.user u
    WHERE
      (
        :q IS NULL OR :q = '' OR
        LOWER(CONCAT(u.firstName, ' ', u.lastName)) LIKE LOWER(CONCAT('%', :q, '%')) OR
        LOWER(u.firstName) LIKE LOWER(CONCAT('%', :q, '%')) OR
        LOWER(u.lastName)  LIKE LOWER(CONCAT('%', :q, '%'))
      )
      AND
      (
        :specialization IS NULL OR :specialization = '' OR
        LOWER(d.specialization) LIKE LOWER(CONCAT('%', :specialization, '%'))
      )
    ORDER BY d.doctorId ASC
    """)
    List<Doctor> searchListByNameOrSpecialization(
            @Param("q") String q,
            @Param("specialization") String specialization
    );

    @Query("select distinct d.specialization from Doctor d where d.specialization is not null order by d.specialization")
    List<String> findAllSpecializations();
}