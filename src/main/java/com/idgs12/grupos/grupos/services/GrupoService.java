package com.idgs12.grupos.grupos.services;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.idgs12.grupos.grupos.dto.GrupoDTO;
import com.idgs12.grupos.grupos.dto.ProfesorDTO;
import com.idgs12.grupos.grupos.dto.GrupoResponseDTO;
import com.idgs12.grupos.grupos.dto.UsuarioDTO;
import com.idgs12.grupos.grupos.entity.GrupoUsuario;
import com.idgs12.grupos.grupos.entity.GruposEntity;
import com.idgs12.grupos.grupos.FeignClient.UsuarioFeignClient;
import com.idgs12.grupos.grupos.FeignClient.ProfesorFeignClient;
import com.idgs12.grupos.grupos.repository.GrupoRepository;
import com.idgs12.grupos.grupos.repository.GrupoUsuarioRepository;


@Service
public class GrupoService {

    @Autowired
    private GrupoUsuarioRepository grupoUsuarioRepository;

    @Autowired
    private GrupoRepository grupoRepository;

    @Autowired
    private UsuarioFeignClient usuarioFeignClient;

    @Autowired
    private ProfesorFeignClient profesorFeignClient;

    // Ver grupo con sus alumnos
    public GrupoResponseDTO findByIdWithAlumnos(int grupoId) {
        GruposEntity grupo = grupoRepository.findById(grupoId).orElse(null);

        if (grupo == null) {
            return null;
        }

        GrupoResponseDTO response = new GrupoResponseDTO();
        response.setId(grupo.getId());
        response.setNombre(grupo.getNombre());
        response.setCuatrimestre(grupo.getCuatrimestre());
        response.setEstado(grupo.getEstado());

        // Obtener alumnos
        List<GrupoUsuario> grupoUsuarios = grupoUsuarioRepository.findByGrupo_Id(grupoId);

        List<UsuarioDTO> alumnos = grupoUsuarios.stream()
                .map(gu -> {
                    try {
                        return usuarioFeignClient.getUsuarioById(gu.getUsuarioId());
                    } catch (Exception e) {
                        System.err.println("❌ Error al obtener usuario: " + e.getMessage());
                        return null;
                    }
                })
                .filter(u -> u != null)
                .collect(Collectors.toList());

        response.setAlumnos(alumnos);

        //  Obtener profesores del grupo
        List<ProfesorDTO> profesores = obtenerProfesoresDelGrupo(grupoId);
        response.setProfesores(profesores);

        return response;
    }

    // ================================
    // MÉTODOS PARA PROFESORES
    // ================================

    // OBTENER PROFESORES DE UN GRUPO
    public List<ProfesorDTO> obtenerProfesoresDelGrupo(Integer grupoId) {
        List<GrupoProfesor> relaciones = grupoProfesorRepository.findByGrupo_Id(grupoId);

        return relaciones.stream()
                .map(rel -> {
                    try {
                        return profesorFeignClient.getProfesorById(rel.getProfesorId());
                    } catch (Exception e) {
                        System.err.println("⚠️ Error al obtener profesor: " + e.getMessage());
                        return null;
                    }
                })
                .filter(p -> p != null)
                .collect(Collectors.toList());
    }

    // OBTENER GRUPOS DE UN PROFESOR
    public List<GruposEntity> obtenerGruposDelProfesor(Long profesorId) {
        List<GrupoProfesor> relaciones = grupoProfesorRepository.findByProfesorId(profesorId);

        return relaciones.stream()
                .map(rel -> rel.getGrupo())
                .collect(Collectors.toList());
    }

    // OBTENER PROFESORES ACTIVOS (para dropdown)
    public List<ProfesorDTO> obtenerProfesoresActivos() {
        try {
            return profesorFeignClient.getProfesoresActivos();
        } catch (Exception e) {
            System.err.println("❌ Error al obtener profesores activos: " + e.getMessage());
            throw new RuntimeException("No se pudo obtener la lista de profesores");
        }
    }

    @Transactional
    public GruposEntity crearGrupo(GrupoDTO grupoDTO) {
        GruposEntity grupo = new GruposEntity();
        grupo.setNombre(grupoDTO.getNombre());
        grupo.setCuatrimestre(grupoDTO.getCuatrimestre());
        grupo.setEstado(grupoDTO.getEstado());
        return grupoRepository.save(grupo);
    }

     // Actualizar grupo
    @Transactional
    public GruposEntity actualizarGrupo(GrupoDTO grupoDTO) {
        GruposEntity grupo = grupoRepository.findById(grupoDTO.getId()).orElse(new GruposEntity());
        grupo.setNombre(grupoDTO.getNombre());
        grupo.setCuatrimestre(grupoDTO.getCuatrimestre());
        grupo.setEstado(grupoDTO.getEstado());
        return grupoRepository.save(grupo);
    }
}
