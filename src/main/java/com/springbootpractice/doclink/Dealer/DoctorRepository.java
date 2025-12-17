package com.springbootpractice.doclink.Dealer;

import com.springbootpractice.doclink.Kernel.Entity.Doctor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DoctorRepository extends JpaRepository<Doctor,Long> {
    List<Doctor> findAll();

    // Existing: paged search by full name or specialization
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

    // Existing: non-paged list search
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

    // NEW: paged search with hospital filter (by id or name) + existing filters
    @Query(value = """
        SELECT DISTINCT d
        FROM Doctor d
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
          AND
          (
            (:hospitalId IS NULL AND (:hospitalName IS NULL OR :hospitalName = ''))
            OR EXISTS (
                SELECT 1
                FROM Doctors_in_Hospital dih
                JOIN dih.hospital h
                WHERE dih.doctor = d
                  AND (:hospitalId IS NULL OR h.hospitalId = :hospitalId)
                  AND (:hospitalName IS NULL OR :hospitalName = '' OR
                       LOWER(h.hospitalName) LIKE LOWER(CONCAT('%', :hospitalName, '%')))
            )
          )
        """,
            countQuery = """
        SELECT COUNT(DISTINCT d)
        FROM Doctor d
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
          AND
          (
            (:hospitalId IS NULL AND (:hospitalName IS NULL OR :hospitalName = ''))
            OR EXISTS (
                SELECT 1
                FROM Doctors_in_Hospital dih
                JOIN dih.hospital h
                WHERE dih.doctor = d
                  AND (:hospitalId IS NULL OR h.hospitalId = :hospitalId)
                  AND (:hospitalName IS NULL OR :hospitalName = '' OR
                       LOWER(h.hospitalName) LIKE LOWER(CONCAT('%', :hospitalName, '%')))
            )
          )
        """)
    Page<Doctor> searchByNameSpecAndHospital(
            @Param("q") String q,
            @Param("specialization") String specialization,
            @Param("hospitalId") Long hospitalId,
            @Param("hospitalName") String hospitalName,
            Pageable pageable
    );

    // NEW: non-paged list with hospital filter
    @Query("""
        SELECT DISTINCT d
        FROM Doctor d
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
          AND
          (
            (:hospitalId IS NULL AND (:hospitalName IS NULL OR :hospitalName = ''))
            OR EXISTS (
                SELECT 1
                FROM Doctors_in_Hospital dih
                JOIN dih.hospital h
                WHERE dih.doctor = d
                  AND (:hospitalId IS NULL OR h.hospitalId = :hospitalId)
                  AND (:hospitalName IS NULL OR :hospitalName = '' OR
                       LOWER(h.hospitalName) LIKE LOWER(CONCAT('%', :hospitalName, '%')))
            )
          )
        ORDER BY d.doctorId ASC
        """)
    List<Doctor> searchListByNameSpecAndHospital(
            @Param("q") String q,
            @Param("specialization") String specialization,
            @Param("hospitalId") Long hospitalId,
            @Param("hospitalName") String hospitalName
    );

    @Query("select distinct d.specialization from Doctor d where d.specialization is not null order by d.specialization")
    List<String> findAllSpecializations();
}
