package com.idgs12.grupos.grupos.repository;

import com.idgs12.grupos.grupos.entity.GrupoProfesor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface GrupoProfesorRepository extends JpaRepository<GrupoProfesor, Integer> {

    List<GrupoProfesor> findByGrupo_Id(Integer grupoId);

    List<GrupoProfesor> findByProfesorId(Long profesorId);

    boolean existsByGrupo_IdAndProfesorId(Integer grupoId, Long profesorId);

    void deleteByGrupo_Id(Integer grupoId);
}