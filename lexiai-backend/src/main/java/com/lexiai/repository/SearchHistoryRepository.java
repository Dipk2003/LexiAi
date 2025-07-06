package com.lexiai.repository;

import com.lexiai.model.SearchHistory;
import com.lexiai.model.Lawyer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface SearchHistoryRepository extends JpaRepository<SearchHistory, Long> {
    
    List<SearchHistory> findByLawyer(Lawyer lawyer);
    
    List<SearchHistory> findByLawyerId(Long lawyerId);
    
    @Query("SELECT sh FROM SearchHistory sh WHERE sh.lawyer.id = :lawyerId ORDER BY sh.searchDate DESC")
    List<SearchHistory> findByLawyerIdOrderBySearchDateDesc(@Param("lawyerId") Long lawyerId);
    
    @Query("SELECT sh FROM SearchHistory sh WHERE sh.lawyer.id = :lawyerId AND sh.searchDate BETWEEN :startDate AND :endDate")
    List<SearchHistory> findByLawyerIdAndDateRange(@Param("lawyerId") Long lawyerId, 
                                                  @Param("startDate") LocalDateTime startDate, 
                                                  @Param("endDate") LocalDateTime endDate);
    
    @Query("SELECT sh.searchQuery, COUNT(sh) as count FROM SearchHistory sh WHERE sh.lawyer.id = :lawyerId GROUP BY sh.searchQuery ORDER BY count DESC")
    List<Object[]> findTopSearchQueriesByLawyer(@Param("lawyerId") Long lawyerId);
    
    @Query("SELECT COUNT(sh) FROM SearchHistory sh WHERE sh.lawyer.id = :lawyerId")
    Long countByLawyerId(@Param("lawyerId") Long lawyerId);
    
    @Query("SELECT sh FROM SearchHistory sh WHERE sh.lawyer.firm.id = :firmId ORDER BY sh.searchDate DESC")
    List<SearchHistory> findByFirmIdOrderBySearchDateDesc(@Param("firmId") Long firmId);
    
    @Query("SELECT COUNT(sh) FROM SearchHistory sh WHERE sh.lawyer.firm.id = :firmId")
    Long countByFirmId(@Param("firmId") Long firmId);
}
