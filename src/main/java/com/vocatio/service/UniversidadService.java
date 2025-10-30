package com.vocatio.service;

import com.vocatio.dto.response.PosiblesUniversidades;
import com.vocatio.dto.response.UniversidadesItemDTO;
import com.vocatio.repository.CarreraRepository;
import com.vocatio.repository.UniversidadCarreraRepository;
import org.springframework.stereotype.Service;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

@Service
public class UniversidadService {

    private final UniversidadCarreraRepository repo;
    private final CarreraRepository carreraRepo;

    public UniversidadService(UniversidadCarreraRepository repo, CarreraRepository carreraRepo) {
        this.repo = repo;
        this.carreraRepo = carreraRepo;
    }

    public PosiblesUniversidades getByCareer(Long careerId, int page, int size) {
        if (page < 1) page = 1;
        if (size < 1) size = 20;

        // 404 solo si la carrera NO existe
        if (!carreraRepo.existsById(careerId)) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND, "La carrera " + careerId + " no existe");
        }

        var rows = repo.findOffersByCareer(careerId);

        // Paginar aunque esté vacío
        int total = rows.size();
        int from = Math.min((page - 1) * size, total);
        int to   = Math.min(from + size, total);
        var slice = rows.subList(from, to);

        List<UniversidadesItemDTO> items = new ArrayList<>(slice.size());
        for (var r : slice) {
            var dto = new UniversidadesItemDTO();
            dto.id = r.getId();
            dto.name = r.getName();
            dto.city = r.getCity();
            dto.programDuration = (r.getProgramDurationYears() != null)
                    ? r.getProgramDurationYears() + " años" : null;
            dto.type = r.getType();
            dto.moreInfoUrl = r.getMoreInfoUrl();

            var t = new UniversidadesItemDTO.Tuition();
            t.currency = "PEN";
            if (r.getAnnualCost() != null) {
                t.approxMonthly = r.getAnnualCost()
                        .divide(BigDecimal.valueOf(12), 2, RoundingMode.HALF_UP);
            }
            dto.tuition = t;

            items.add(dto);
        }

        var resp = new PosiblesUniversidades();
        resp.items = items;   // puede quedar []
        resp.page = page;
        resp.size = size;
        resp.total = total;   // 0 si no hay resultados
        return resp;
    }
}
