package com.idgs12.grupos.grupos.FeignClient;

import java.util.List;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import com.idgs12.grupos.grupos.dto.ProfesorDTO;

@FeignClient(name = "PROFESORES", url = "https://profesores-repository-production.up.railway.app")
public interface ProfesorFeignClient {

    @GetMapping("/api/profesores/activos")
    List<ProfesorDTO> getProfesoresActivos();

    @GetMapping("/api/profesores/{id}")
    ProfesorDTO getProfesorById(@PathVariable("id") Long id);
}