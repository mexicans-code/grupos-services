package com.idgs12.grupos.grupos.controller;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.idgs12.grupos.grupos.dto.GrupoDTO;
import com.idgs12.grupos.grupos.dto.GrupoListDTO;
import com.idgs12.grupos.grupos.dto.GrupoResponseDTO;
import com.idgs12.grupos.grupos.dto.ProfesorDTO;
import com.idgs12.grupos.grupos.entity.GruposEntity;
import com.idgs12.grupos.grupos.repository.GrupoRepository;
import com.idgs12.grupos.grupos.repository.GrupoUsuarioRepository;
import com.idgs12.grupos.grupos.services.GrupoService;
import com.idgs12.grupos.grupos.repository.GrupoProfesorRepository;

@RestController
@RequestMapping("/grupos")
@CrossOrigin(origins = "*")
public class GrupoController {

    @Autowired
    private GrupoService grupoService;

    @Autowired
    private GrupoRepository grupoRepository;

    @Autowired
    private GrupoUsuarioRepository grupoUsuarioRepository;

    @Autowired
    private GrupoProfesorRepository grupoProfesorRepository;

    // ================================
    // ENDPOINTS DE GRUPOS
    // ================================

    // Obtener todos los grupos
    @GetMapping("/all")
    public List<GrupoListDTO> getAllGrupos() {
        return grupoRepository.findAll()
                .stream()
                .map(grupo -> {
                    GrupoListDTO dto = new GrupoListDTO();
                    dto.setId(grupo.getId());
                    dto.setNombre(grupo.getNombre());
                    dto.setCuatrimestre(grupo.getCuatrimestre());
                    dto.setEstado(grupo.getEstado());

                    // Contar alumnos del grupo
                    int cantidadAlumnos = grupoUsuarioRepository.findByGrupo_Id(grupo.getId()).size();
                    dto.setCantidadAlumnos(cantidadAlumnos);

                    // Contar profesores del grupo
                    int cantidadProfesores = grupoProfesorRepository.findByGrupo_Id(grupo.getId()).size();
                    dto.setCantidadProfesores(cantidadProfesores);

                    return dto;
                })
                .collect(Collectors.toList());
    }

    // Ver grupo con sus alumnos y profesores
    @GetMapping("/{id}")
    public ResponseEntity<GrupoResponseDTO> getGrupoConAlumnos(@PathVariable int id) {
        GrupoResponseDTO grupo = grupoService.findByIdWithAlumnos(id);
        if (grupo != null) {
            return ResponseEntity.ok(grupo);
        }
        return ResponseEntity.notFound().build();
    }

    // Crear un nuevo grupo
    @PostMapping
    public ResponseEntity<GruposEntity> crearGrupo(@RequestBody GrupoDTO grupoDTO) {
        GruposEntity nuevoGrupo = grupoService.crearGrupo(grupoDTO);
        return ResponseEntity.ok(nuevoGrupo);
    }

    // Actualizar un grupo existente
    @PutMapping("/{id}")
    public ResponseEntity<GruposEntity> actualizarGrupo(@PathVariable int id, @RequestBody GrupoDTO grupoDTO) {
        grupoDTO.setId(id);
        GruposEntity grupoActualizado = grupoService.actualizarGrupo(grupoDTO);
        return ResponseEntity.ok(grupoActualizado);
    }
    
    //Funcionalidad de habilitar -- Maria Fernanda Rosas Briones IDGS12
    @PutMapping("/habilitar/{id}")
    public ResponseEntity<String> habilitarGrupo(@PathVariable Integer id) {
        boolean habilitado = grupoService.habilitarGrupo(id);

        if (!habilitado) {
            return ResponseEntity.badRequest().body("No se encontró el grupo o ya estaba habilitado.");
        }

        return ResponseEntity.ok("Grupo habilitado correctamente.");
    }
    
}